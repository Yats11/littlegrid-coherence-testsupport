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

import org.junit.Test;
import org.littlegrid.ClusterMemberGroupBuildException;
import org.littlegrid.IdentifiableException;

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.littlegrid.IdentifiableException.ReasonEnum.REUSABLE_MEMBER_GROUP_CANNOT_BE_MERGED;

/**
 * Default builder exception reporter tests.
 */
public class DefaultBuildExceptionReporterTest {
    @Test
    public void standardExceptionWithoutMemberGroupClassName() {
        new DefaultBuildExceptionReporter().report(new RuntimeException(), null, null);
    }

    @Test
    public void standardExceptionWithMemberGroupClassName() {
        new DefaultBuildExceptionReporter().report(new RuntimeException(), null, null, null, null);
    }

    @Test
    public void standardExceptionWithMemberGroupClassNameAndOtherInformation() {
        new DefaultBuildExceptionReporter().report(new RuntimeException(), null, null, null, "This is other info.");
    }

    @Test
    public void clusterMemberGroupBuildExceptionWithMemberGroupClassNameAndOtherInformation() {
        final Properties systemPropertiesBeforeStart = new Properties();
        systemPropertiesBeforeStart.setProperty("a", "b");

        final Properties systemPropertiesToBeApplied = new Properties();
        systemPropertiesToBeApplied.setProperty("b", "c");

        final ClusterMemberGroupBuildException buildException =
                new ClusterMemberGroupBuildException(
                        new IdentifiableException("message", REUSABLE_MEMBER_GROUP_CANNOT_BE_MERGED),
                        systemPropertiesBeforeStart,
                        systemPropertiesToBeApplied,
                        1,
                        new URL[]{},
                        "a.b.c",
                        2);

        final Map<String, String> builderKeysAndValues = Collections.singletonMap("bk", "bv");

        final Properties builderKeyToSystemPropertyNameMappings = new Properties();
        builderKeyToSystemPropertyNameMappings.setProperty("bk", "spn");

        new DefaultBuildExceptionReporter().report(buildException,
                builderKeysAndValues, builderKeyToSystemPropertyNameMappings, null, "This is other info.");
    }
}
