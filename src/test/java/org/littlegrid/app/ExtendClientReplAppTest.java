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

package org.littlegrid.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.littlegrid.support.SystemUtils;

import java.util.Properties;

import static org.littlegrid.ClusterMemberGroup.Builder.BUILDER_SYSTEM_PROPERTY_PREFIX_KEY;

/**
 * Extend client REPL application tests.
 */
public class ExtendClientReplAppTest {
    private Properties systemProperties;

    @Before
    public void beforeTest() {
        systemProperties = SystemUtils.snapshotSystemProperties();
    }

    @After
    public void afterTest() {
        System.setProperties(systemProperties);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void construct() {
        new ExtendClientReplApp();
    }

    @Test(expected = IllegalStateException.class)
    public void startWithEmptyStringClientCacheConfiguration() {
        System.setProperty(BUILDER_SYSTEM_PROPERTY_PREFIX_KEY + "ClientCacheConfiguration", " ");

        ExtendClientReplApp.main(new String[]{});
    }

    @Test
    public void runMain() {
        System.setProperty(BUILDER_SYSTEM_PROPERTY_PREFIX_KEY + "ClientCacheConfiguration", "some-file.xml");

        ExtendClientReplApp.main(new String[]{"commands=# Extend client REPL test; bye"});
    }

    @Test(expected = IllegalStateException.class)
    public void startWithNullArguments() {
        ExtendClientReplApp.main(null);
    }
}
