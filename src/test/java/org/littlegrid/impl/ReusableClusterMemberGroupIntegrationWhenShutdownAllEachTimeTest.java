/*
 * Copyright (c) 2010-2014 Jonathan Hall.
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

package org.littlegrid.impl;

import com.tangosol.net.CacheFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.littlegrid.ClusterMemberGroup;
import org.littlegrid.ClusterMemberGroupUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.littlegrid.ClusterMemberGroupTestSupport.KNOWN_TEST_CACHE;

/**
 * Reusable cluster member group integration tests which shutdown all each time to force
 * a new instance to be created - i.e. the instance in the registry cannot be used.
 */
public class ReusableClusterMemberGroupIntegrationWhenShutdownAllEachTimeTest {
    private ClusterMemberGroup testMemberGroup;

    @Before
    public void beforeTest() {
        startMemberGroup();
        testMemberGroup.shutdownAll();
        testMemberGroup.shutdownAll();
        testMemberGroup.shutdownAll();
        testMemberGroup.shutdownAll();
        testMemberGroup.shutdownAll();

        startMemberGroup();
        CacheFactory.getCache(KNOWN_TEST_CACHE);
    }

    @After
    public void afterTest() {
        final UsageCountingClusterMemberGroup instance = (UsageCountingClusterMemberGroup) testMemberGroup;
        assertThat(instance.getCurrentUsageCount(), is(1));

        ClusterMemberGroupUtils.shutdownCacheFactoryThenClusterMemberGroups(testMemberGroup);
    }

    private void startMemberGroup() {
        testMemberGroup = ClusterMemberGroupUtils.newBuilder()
                .setClusterMemberGroupInstanceClassName(UsageCountingClusterMemberGroup.class.getName())
                .setStorageEnabledCount(1)
                .setLogLevel(0)
                .setFastStartJoinTimeoutMilliseconds(100)
                .setOverrideConfiguration("littlegrid/littlegrid-fast-start-coherence-override.xml")
                .buildAndConfigureForStorageDisabledClient();
    }

    @Test
    public void test1() {
    }

    @Test
    public void test2() {
    }
}
