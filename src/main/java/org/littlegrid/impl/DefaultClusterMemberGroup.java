/*
 * Copyright (c) 2010-2012 Jonathan Hall.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the LittleGrid nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.littlegrid.impl;

import com.tangosol.net.CacheFactory;
import org.littlegrid.ClusterMemberGroup;
import org.littlegrid.ClusterMemberGroupBuildException;
import org.littlegrid.support.LoggerPlaceHolder;
import org.littlegrid.support.SystemUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.String.format;

/**
 * Default local process cluster member group implementation.
 */
public final class DefaultClusterMemberGroup implements ClusterMemberGroup {
    private static final LoggerPlaceHolder LOGGER =
            new LoggerPlaceHolder(DefaultClusterMemberGroup.class.getName());

    private final List<Future<DelegatingClusterMemberWrapper>> memberFutures =
            new ArrayList<Future<DelegatingClusterMemberWrapper>>();

    private boolean startInvoked;
    private Properties systemPropertiesBeforeStartInvoked;
    private Properties systemPropertiesToBeApplied;
    private int numberOfMembers;
    private URL[] classPathUrls;
    private String clusterMemberInstanceClassName;
    private int numberOfThreadsInStartUpPool;
    private int sleepAfterStopDuration35x;
    private int sleepAfterStopDuration36x;
    private int sleepAfterStopDurationDefault;


    /**
     * Constructor with reduced scope.
     *
     * @param sleepAfterStopDuration35x     Sleep duration for 3.5.x.
     * @param sleepAfterStopDuration36x     Sleep duration for 3.6.x.
     * @param sleepAfterStopDurationDefault Default sleep duration.
     */
    DefaultClusterMemberGroup(final int sleepAfterStopDuration35x,
                              final int sleepAfterStopDuration36x,
                              final int sleepAfterStopDurationDefault) {

        this.sleepAfterStopDuration35x = sleepAfterStopDuration35x;
        this.sleepAfterStopDuration36x = sleepAfterStopDuration36x;
        this.sleepAfterStopDurationDefault = sleepAfterStopDurationDefault;

        systemPropertiesBeforeStartInvoked = SystemUtils.snapshotSystemProperties();
    }

    /**
     * Constructor.
     *
     * @param numberOfMembers                Number of members.
     * @param systemPropertiesToBeApplied    System properties to be applied.
     * @param classPathUrls                  Class path.
     * @param clusterMemberInstanceClassName Class name of cluster member instance.
     * @param numberOfThreadsInStartUpPool   Number of threads in start-up pool.
     */
    public DefaultClusterMemberGroup(final int numberOfMembers,
                                     final Properties systemPropertiesToBeApplied,
                                     final URL[] classPathUrls,
                                     final String clusterMemberInstanceClassName,
                                     final int numberOfThreadsInStartUpPool) {

        if (numberOfMembers < 1) {
            throw new IllegalArgumentException("Number of members must be 1 or more");
        }

        if (systemPropertiesToBeApplied == null || systemPropertiesToBeApplied.size() == 0) {
            throw new IllegalArgumentException("No system properties specified, cannot setup cluster");
        }

        if (classPathUrls == null || classPathUrls.length == 0) {
            throw new IllegalArgumentException("No class path URLs specified - will not be able to necessary classes");
        }

        if (clusterMemberInstanceClassName == null || clusterMemberInstanceClassName.trim().length() == 0) {
            throw new IllegalArgumentException("No cluster member instance class name, cannot setup cluster");
        }

        if (numberOfThreadsInStartUpPool < 1) {
            throw new IllegalArgumentException("Invalid number of threads specified for start-up pool, cannot start");
        }

        this.numberOfMembers = numberOfMembers;
        this.classPathUrls = classPathUrls;
        this.clusterMemberInstanceClassName = clusterMemberInstanceClassName;

        if (numberOfThreadsInStartUpPool > numberOfMembers) {
            this.numberOfThreadsInStartUpPool = numberOfMembers;
        } else {
            this.numberOfThreadsInStartUpPool = numberOfThreadsInStartUpPool;
        }

        this.systemPropertiesToBeApplied = systemPropertiesToBeApplied;

        systemPropertiesBeforeStartInvoked = SystemUtils.snapshotSystemProperties();
    }

    /**
     * Reduced scope method to merge in a cluster member group with this cluster member group.
     *
     * @param memberGroup Cluster member group to be merged.
     * @return new size of combined member group.
     */
    int merge(final ClusterMemberGroup memberGroup) {
        final DefaultClusterMemberGroup defaultClusterMemberGroup = (DefaultClusterMemberGroup) memberGroup;

        memberFutures.addAll(defaultClusterMemberGroup.getMemberFutures());
        startInvoked = true;

        numberOfMembers = memberFutures.size();

        return memberFutures.size();
    }

    /**
     * Reduced scope method to get this members list of futures, the references to the
     * cluster members.
     *
     * @return list of member's futures.
     */
    List<Future<DelegatingClusterMemberWrapper>> getMemberFutures() {
        return memberFutures;
    }

    /**
     * Starts all the cluster members in the group.
     *
     * @return member group.
     */
    public ClusterMemberGroup startAll() {
        if (startInvoked) {
            return this;
        }

        SystemUtils.applyToSystemProperties(systemPropertiesToBeApplied);
        startInvoked = true;
        outputStartAllMessages();

        try {
            final List<Callable<DelegatingClusterMemberWrapper>> tasks =
                    new ArrayList<Callable<DelegatingClusterMemberWrapper>>(numberOfMembers);

            for (int i = 0; i < numberOfMembers; i++) {
                tasks.add(new ClusterMemberCallable(clusterMemberInstanceClassName, classPathUrls));
            }

            final Callable<DelegatingClusterMemberWrapper> taskForSeniorMember = tasks.remove(0);

            final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreadsInStartUpPool);

            LOGGER.debug("About to establish a cluster using a single member initially");
            final Future<DelegatingClusterMemberWrapper> futureForSeniorMember =
                    executorService.submit(taskForSeniorMember);

            futureForSeniorMember.get();

            LOGGER.debug("First cluster member up, starting any remaining members to join established cluster");
            final List<Future<DelegatingClusterMemberWrapper>> futuresForOtherMembers =
                    executorService.invokeAll(tasks);

            memberFutures.add(futureForSeniorMember);
            memberFutures.addAll(futuresForOtherMembers);

            executorService.shutdown();

            LOGGER.debug(format("This group of cluster member(s) started, member Ids: %s",
                    Arrays.toString(getStartedMemberIds())));
        } catch (Exception e) {
            LOGGER.error(format(
                    "Failed to start cluster member group - check Coherence system applied for misconfiguration: %s",
                    systemPropertiesToBeApplied));

//            System.setProperties(systemPropertiesBeforeStartInvoked);

            throw new ClusterMemberGroupBuildException(e, systemPropertiesBeforeStartInvoked,
                    systemPropertiesToBeApplied, numberOfMembers, classPathUrls,
                    clusterMemberInstanceClassName, numberOfThreadsInStartUpPool);
        } finally {
            System.setProperties(systemPropertiesBeforeStartInvoked);
        }

        return this;
    }

    private void outputStartAllMessages() {
        final int oneMB = 1024 * 1024;

        LOGGER.debug(format("About to start '%d' cluster member(s) in group, using '%d' threads in pool",
                numberOfMembers, numberOfThreadsInStartUpPool));

        LOGGER.debug(format("Class path (after exclusions)..: %s", Arrays.deepToString(classPathUrls)));
        LOGGER.info(format("System properties to be set.: %s", systemPropertiesToBeApplied));
        LOGGER.info(format("Max memory: %sMB, current: %sMB, free memory: %sMB",
                Runtime.getRuntime().maxMemory() / oneMB,
                Runtime.getRuntime().totalMemory() / oneMB,
                Runtime.getRuntime().freeMemory() / oneMB));
    }

    private DelegatingClusterMemberWrapper getClusterMemberWrapper(final int memberId) {
        if (!startInvoked) {
            throw new IllegalStateException("Cluster member group never started");
        }

        try {
            for (int i = 0; i < memberFutures.size(); i++) {
                final Future<DelegatingClusterMemberWrapper> task = memberFutures.get(i);

                final DelegatingClusterMemberWrapper memberWrapper = task.get();

                if (memberWrapper.getLocalMemberId() == memberId) {
                    return memberWrapper;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getStartedMemberIds() {
        try {
            final List<Integer> memberIds = new ArrayList<Integer>();

            for (int i = 0; i < memberFutures.size(); i++) {
                final Future<DelegatingClusterMemberWrapper> task = memberFutures.get(i);

                final DelegatingClusterMemberWrapper memberWrapper = task.get();
                memberIds.add(memberWrapper.getLocalMemberId());
            }

            int[] memberIdsArray = new int[memberIds.size()];

            for (int i = 0; i < memberIds.size(); i++) {
                memberIdsArray[i] = memberIds.get(i);
            }

            return memberIdsArray;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuggestedSleepAfterStopDuration() {
        return getSuggestedSleepDurationBasedUponVersion(getMajorMinorVersion());
    }

    /**
     * Gets the suggested sleep duration based upon the version.
     *
     * @param majorMinorVersion Version of Coherence.
     * @return returns the suggested sleep duration.
     */
    public int getSuggestedSleepDurationBasedUponVersion(final float majorMinorVersion) {
        final float coherenceVersionNumber36x = 3.6f;
        final float coherenceVersionNumber370 = 3.7f;

        if (majorMinorVersion < coherenceVersionNumber36x) {
            return sleepAfterStopDuration35x;

        } else if (majorMinorVersion < coherenceVersionNumber370) {
            return sleepAfterStopDuration36x;
        }

        return sleepAfterStopDurationDefault;
    }

    private static float getMajorMinorVersion() {
        final String majorMinorVersionString = CacheFactory.VERSION.substring(0, 3);

        return Float.parseFloat(majorMinorVersionString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterMember getClusterMember(final int memberId) {
        if (!startInvoked) {
            LOGGER.warn(format("Cluster member group never started - cannot get member '%s'", memberId));

            return null;
        }

        LOGGER.debug(format("About to get cluster member '%d'", memberId));

        return getClusterMemberWrapper(memberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterMemberGroup shutdownMember(final int... memberIds) {
        if (!startInvoked) {
            LOGGER.warn("Cluster member group never started - nothing to shutdown");

            return this;
        }

        if (memberIds.length > 1) {
            throw new UnsupportedOperationException("Shutting down multiple members is not supported currently");
        }

        final int memberId = memberIds[0];

        LOGGER.info(format("About to shutdown cluster member '%d'", memberId));

        final DelegatingClusterMemberWrapper memberWrapper = getClusterMemberWrapper(memberId);

        if (memberWrapper != null) {
            memberWrapper.shutdown();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterMemberGroup shutdownAll() {
        LOGGER.info("Restoring system properties back to their original state before member group started");

        System.setProperties(systemPropertiesBeforeStartInvoked);

        if (!startInvoked) {
            LOGGER.warn("Cluster member group never started - nothing to shutdown");

            return this;
        }

        LOGGER.info(format("Shutting down '%d' cluster member(s) in group", numberOfMembers));

        try {
            for (int i = 0; i < memberFutures.size(); i++) {
                final Future<DelegatingClusterMemberWrapper> task = memberFutures.get(i);

                final DelegatingClusterMemberWrapper memberWrapper = task.get();

                memberWrapper.shutdown();
            }

            memberFutures.clear();

            LOGGER.info("Group of cluster member(s) shutdown");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterMemberGroup stopMember(final int... memberIds) {
        if (!startInvoked) {
            LOGGER.warn("Cluster member group never started - nothing to do");

            return this;
        }

        if (memberIds.length > 1) {
            throw new UnsupportedOperationException("Stopping multiple members is not supported currently");
        }

        final int memberId = memberIds[0];

        LOGGER.info(format("About to stop cluster member with id '%d'", memberId));

        final DelegatingClusterMemberWrapper memberWrapper = getClusterMemberWrapper(memberId);

        if (memberWrapper != null) {
            memberWrapper.stop();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterMemberGroup stopAll() {
        if (!startInvoked) {
            LOGGER.warn("Cluster member group never started - nothing to stop");

            return this;
        }

        LOGGER.info(format("Stopping '%d' cluster member(s) in this group", numberOfMembers));

        try {
            for (int i = 0; i < memberFutures.size(); i++) {
                final Future<DelegatingClusterMemberWrapper> task = memberFutures.get(i);

                final DelegatingClusterMemberWrapper memberWrapper = task.get();

                memberWrapper.stop();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return this;
    }
}