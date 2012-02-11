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

package org.littlegrid;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import org.junit.After;
import org.junit.Test;

/**
 * Cluster member group baseline tests, a set of simple tests to quickly check the basic
 * behaviour.
 */
public final class BaselineClusterMemberGroupIntegrationTest
        extends AbstractAfterTestShutdownIntegrationTest {

    private ClusterMemberGroup memberGroup;


    @After
    public void afterTest() {
        ClusterMemberGroupUtils.shutdownCacheFactoryThenClusterMemberGroups(memberGroup);
    }

    @Test
    public void startAndShutdownSingleMemberGroup() {
        final int numberOfMembers = ClusterMemberGroupTestSupport.SINGLE_TEST_CLUSTER_SIZE;
        final int expectedClusterSize = numberOfMembers + ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;

        final ClusterMemberGroup memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setStorageEnabledCount(numberOfMembers).build();
        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), expectedClusterSize);

        final NamedCache cache = CacheFactory.getCache("test");
        cache.put("key", "value");

        memberGroup.shutdownAll();

        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP);
    }

    @Test
    public void simpleMemberGroupWithCacheConfigurationAndKnownCache() {
        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setCacheConfiguration(ClusterMemberGroupTestSupport.TCMP_CLUSTER_MEMBER_CACHE_CONFIG_FILE)
                .build();

        final NamedCache cache = CacheFactory.getCache(ClusterMemberGroupTestSupport.KNOWN_TEST_CACHE);
        cache.put("key", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleMemberGroupWithCacheConfigurationAndUnknownCache() {
        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setCacheConfiguration(ClusterMemberGroupTestSupport.TCMP_CLUSTER_MEMBER_CACHE_CONFIG_FILE)
                .build();

        final NamedCache cache = CacheFactory.getCache("this-cache-will-not-be-found-in-cache-configuration");
        cache.put("key", "value");
    }

    @Test
    public void startAndShutdownInvokedTwice() {
        final int numberOfMembers = ClusterMemberGroupTestSupport.SINGLE_TEST_CLUSTER_SIZE;
        final int expectedClusterSize = numberOfMembers + ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;

        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setStorageEnabledCount(numberOfMembers)
                .build();

        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), expectedClusterSize);

        memberGroup.shutdownAll();
        memberGroup.shutdownAll();

        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP);
    }

    @Test
    public void startAndStopInvokedTwice() {
        final int numberOfMembers = ClusterMemberGroupTestSupport.SINGLE_TEST_CLUSTER_SIZE;
        final int expectedClusterSize = numberOfMembers + ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;

        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setStorageEnabledCount(numberOfMembers)
                .build();

        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), expectedClusterSize);

        memberGroup.stopAll();
        memberGroup.stopAll();

        ClusterMemberGroupTestSupport.sleepForSeconds(memberGroup.getSuggestedSleepAfterStopDuration());

        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP);
    }

    @Test
    public void startAndShutdownWithKnownRequiredJarBeingExcluded() {
        final int numberOfMembers = ClusterMemberGroupTestSupport.SINGLE_TEST_CLUSTER_SIZE;
        final int expectedClusterSize = numberOfMembers + ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;
        final String jarToExclude = "junit-4.8.2.jar";

        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setJarsToExcludeFromClassPath(jarToExclude)
                .build();

        ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), expectedClusterSize);
    }
}