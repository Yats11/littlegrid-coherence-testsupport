package org.littlegrid.group.jxm_monitor;

import com.tangosol.net.CacheFactory;
import org.junit.Test;
import org.littlegrid.AbstractAfterTestShutdownIntegrationTest;
import org.littlegrid.ClusterMemberGroupUtils;

import static org.littlegrid.ClusterMemberGroupTestSupport.CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;
import static org.littlegrid.ClusterMemberGroupTestSupport.SINGLE_TEST_CLUSTER_SIZE;
import static org.littlegrid.ClusterMemberGroupTestSupport.TCMP_CLUSTER_MEMBER_CACHE_CONFIG_FILE;
import static org.littlegrid.ClusterMemberGroupTestSupport.assertThatClusterIsExpectedSize;

/**
 * JMX monitor member tests.
 */
public final class JmxMonitorIntegrationTest extends AbstractAfterTestShutdownIntegrationTest {
    @Test
    public void jmxMonitorClusterMember() {
        final int numberOfMembers = SINGLE_TEST_CLUSTER_SIZE;
        final int expectedClusterSize = numberOfMembers + CLUSTER_SIZE_WITHOUT_CLUSTER_MEMBER_GROUP;

        memberGroup = ClusterMemberGroupUtils.newBuilder()
                .setCacheConfiguration(TCMP_CLUSTER_MEMBER_CACHE_CONFIG_FILE)
                .setJmxMonitorCount(numberOfMembers)
                .build();

        assertThatClusterIsExpectedSize(CacheFactory.ensureCluster(), expectedClusterSize);
    }
}