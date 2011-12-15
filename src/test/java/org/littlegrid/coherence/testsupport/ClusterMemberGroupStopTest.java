/*
 * Copyright (c) 2011, Jonathan Hall.
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

package org.littlegrid.coherence.testsupport;

import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Cluster member group stop tests.
 */
public class ClusterMemberGroupStopTest extends AbstractStorageDisabledClientClusterMemberGroupTest {
    private ClusterMemberGroup memberGroup;

    @After
    public void afterTest() {
        ClusterMemberGroupUtils.shutdownCacheFactoryThenClusterMemberGroups(memberGroup);
    }

    @Test
    public void startAndStopSpecificMemberOfGroup() {
        final int numberOfMembers = MEDIUM_TEST_CLUSTER_SIZE;
        final int expectedClusterSizeBeforeStop = numberOfMembers + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;
        final int expectedClusterSizeAfterStop = (numberOfMembers + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP) - 1;
        final int memberIdToStop = 3;

        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setStorageEnabledCount(numberOfMembers).build();
        assertThatClusterIsExpectedSize(expectedClusterSizeBeforeStop);
        assertThat(doesMemberExist(memberIdToStop), is(true));

        memberGroup.stopMember(memberIdToStop);
        ClusterMemberGroupUtils.sleepAfterPerformingMemberStop();
        assertThat(doesMemberExist(memberIdToStop), is(false));
        assertThatClusterIsExpectedSize(expectedClusterSizeAfterStop);

        memberGroup.shutdownAll();
    }

    @Test
    public void startAndStopNonExistentMemberOfGroup() {
        int numberOfMembers = MEDIUM_TEST_CLUSTER_SIZE;
        int memberIdToStop = 12;
        int expectedClusterSize = numberOfMembers + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;

        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setStorageEnabledCount(numberOfMembers).build();
        assertThatClusterIsExpectedSize(expectedClusterSize);
        assertThat(doesMemberExist(memberIdToStop), is(false));

        memberGroup.stopMember(memberIdToStop);
        // No need to wait - it never existed
        assertThatClusterIsExpectedSize(expectedClusterSize);

        memberGroup.shutdownAll();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void attemptToStopMoreThanOneMemberWhichIsNotSupported() {
        memberGroup = ClusterMemberGroupUtils.newClusterMemberGroupBuilder()
                .setStorageEnabledCount(3).build().stopMember(1, 2);
    }
}
