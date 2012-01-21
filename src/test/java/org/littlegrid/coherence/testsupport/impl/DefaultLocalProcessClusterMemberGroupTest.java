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

package org.littlegrid.coherence.testsupport.impl;

import org.junit.Test;
import org.littlegrid.coherence.testsupport.ClusterMemberGroup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Direct (i.e. not going through ClusterMemberGroupUtils) default local process cluster member
 * group tests.
 */
public class DefaultLocalProcessClusterMemberGroupTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructWithInvalidNumberOfMembers() {
        new DefaultLocalProcessClusterMemberGroup(0, null, null, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNullSystemProperties() {
        new DefaultLocalProcessClusterMemberGroup(1, null, null, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNoSystemProperties() {
        new DefaultLocalProcessClusterMemberGroup(1, new Properties(), null, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNullClassPath() {
        new DefaultLocalProcessClusterMemberGroup(1, getPopulatedProperties(), null, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNoClassPath() {
        new DefaultLocalProcessClusterMemberGroup(1, getPopulatedProperties(), new URL[]{}, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithNullInstanceClassName()
            throws MalformedURLException {

        new DefaultLocalProcessClusterMemberGroup(1, getPopulatedProperties(), getPopulatedUrls(), null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithEmptyInstanceClassName()
            throws MalformedURLException {

        new DefaultLocalProcessClusterMemberGroup(1, getPopulatedProperties(), getPopulatedUrls(), " ", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithInvalidNumberOfThreads()
            throws MalformedURLException {

        new DefaultLocalProcessClusterMemberGroup(1, getPopulatedProperties(), getPopulatedUrls(), "SomeClass", 0);
    }

    @Test
    public void shutdownAllRestoreOfSystemProperties()
            throws MalformedURLException {

        final String key = "this-is-a-key-of-a-new-property";

        Properties propertiesBefore = SystemUtils.snapshotSystemProperties();

        final ClusterMemberGroup memberGroup = new DefaultLocalProcessClusterMemberGroup(1,
                getPopulatedProperties(), getPopulatedUrls(), "SomeClass", 1);

        System.setProperty(key, "Adding a new system property");

        assertThat(System.getProperty(key), notNullValue());

        memberGroup.shutdownAll();

        assertThat(System.getProperty(key), nullValue());
    }

    private static Properties getPopulatedProperties() {
        Properties properties = new Properties();
        properties.setProperty("key", "value");

        return properties;
    }

    private static URL[] getPopulatedUrls()
            throws MalformedURLException {

        return new URL[]{new URL("file://url")};
    }
}
