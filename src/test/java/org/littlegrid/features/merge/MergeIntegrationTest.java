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
 * Neither the name of the littlegrid nor the names of its contributors may
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

package org.littlegrid.features.merge;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import org.junit.Test;
import org.littlegrid.AbstractAfterTestShutdownIntegrationTest;
import org.littlegrid.ClusterMemberGroup;
import org.littlegrid.ClusterMemberGroupUtils;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.littlegrid.ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;
import static org.littlegrid.ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize;
import static org.littlegrid.ClusterMemberGroupTestSupport.doesMemberExist;

/**
 * Merge cluster member group integration tests.
 */
public class MergeIntegrationTest extends AbstractAfterTestShutdownIntegrationTest {
    @Test
    public void incrementallyMergeInNewMemberGroups() {
        final int numberOfMembersToStartWith = 1;
        final int numberOfMembersToAddEachTime = 1;
        final int totalNumberOfMembersToMergeIn = 3;

        final ClusterMemberGroup.Builder builder = ClusterMemberGroupUtils.newBuilder()
                .setStorageEnabledCount(numberOfMembersToStartWith);

        memberGroup = builder.buildAndConfigureForStorageDisabledClient();

        final Cluster cluster = CacheFactory.ensureCluster();

        assertThat(cluster.getMemberSet().size(), is(numberOfMembersToStartWith
                + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP));

        for (int i = 1; i <= totalNumberOfMembersToMergeIn; i++) {
            final ClusterMemberGroup newMemberGroup = builder.buildAndConfigureForStorageDisabledClient();
            final int currentNumberOfMembers = memberGroup.merge(newMemberGroup);

            final int expectedCurrentNumberOfMembers = numberOfMembersToStartWith + (numberOfMembersToAddEachTime * i);

            assertThat(currentNumberOfMembers, is(expectedCurrentNumberOfMembers));
            assertThatClusterIsExpectedSize(cluster, expectedCurrentNumberOfMembers
                    + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP);
        }
    }

    @Test
    public void rollingRestart()
            throws InterruptedException {

        final int numberOfMembersToStartWith = 2;
        final int numberOfMembersToAddEachTime = 1;
        final int totalNumberOfMembersToMergeIn = 4;

        final ClusterMemberGroup.Builder builder = ClusterMemberGroupUtils.newBuilder()
                .setStorageEnabledCount(numberOfMembersToStartWith);

        memberGroup = builder.buildAndConfigureForStorageDisabledClient();

        final Cluster cluster = CacheFactory.ensureCluster();

        assertThat(cluster.getMemberSet().size(), is(numberOfMembersToStartWith
                + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP));

        builder.setStorageEnabledCount(numberOfMembersToAddEachTime);

        int idOfNextMemberToRollOutOfCluster = memberGroup.getStartedMemberIds()[0];

        for (int i = 1; i <= totalNumberOfMembersToMergeIn; i++) {
            final ClusterMemberGroup newMemberGroup = builder.buildAndConfigureForNoClient();

            // Roll a member in
            memberGroup.merge(newMemberGroup);

            assertThatClusterIsExpectedSize(cluster, numberOfMembersToStartWith + numberOfMembersToAddEachTime
                    + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP);

            // Roll the oldest member out
            memberGroup.shutdownMember(idOfNextMemberToRollOutOfCluster);

            TimeUnit.SECONDS.sleep(1);
            assertThat(doesMemberExist(cluster, idOfNextMemberToRollOutOfCluster), is(false));

            assertThatClusterIsExpectedSize(cluster, numberOfMembersToStartWith
                    + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP);

            idOfNextMemberToRollOutOfCluster = newMemberGroup.getStartedMemberIds()[0];
        }

        assertThat(cluster.getMemberSet().size(), is(numberOfMembersToStartWith
                + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP));
    }
}