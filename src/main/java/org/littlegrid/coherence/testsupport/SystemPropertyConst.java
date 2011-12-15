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

/**
 * Coherence system property constants.
 */
public final class SystemPropertyConst {
    /**
     * Private constructor to prevent creation.
     */
    private SystemPropertyConst() {
    }

    /**
     * Constant for defining standard: tangosol.coherence.
     */
    public static final String TANGOSOL_COHERENCE_DOT = "tangosol.coherence.";

    /**
     * Constant for standard log level system property.
     */
    public static final String LOG_LEVEL_KEY = TANGOSOL_COHERENCE_DOT + "log.level";

    /**
     * Constant for standard role property.
     */
    public static final String ROLE_NAME_KEY = TANGOSOL_COHERENCE_DOT + "role";

    /**
     * Constant for standard distributed local storage property.
     */
    public static final String DISTRIBUTED_LOCAL_STORAGE_KEY = TANGOSOL_COHERENCE_DOT + "distributed.localstorage";

    /**
     * Constant for standard Extend enabled property.
     */
    public static final String EXTEND_ENABLED_KEY = TANGOSOL_COHERENCE_DOT + "extend.enabled";

    /**
     * Constant for standard Extend port property.
     */
    public static final String EXTEND_PORT_KEY = TANGOSOL_COHERENCE_DOT + "extend.port";

    /**
     * Constant for standard TCMP enabled property.
     */
    public static final String TCMP_ENABLED_KEY = TANGOSOL_COHERENCE_DOT + "tcmp.enabled";

    /**
     * Constant for standard WKA address property.
     */
    public static final String WKA_ADDRESS_KEY = TANGOSOL_COHERENCE_DOT + "wka";

    /**
     * Constant for standard WKA port property.
     */
    public static final String WKA_PORT_KEY = TANGOSOL_COHERENCE_DOT + "wka.port";

    /**
     * Constant for standard local address property.
     */
    public static final String LOCAL_ADDRESS_KEY = TANGOSOL_COHERENCE_DOT + "localhost";

    /**
     * Constant for standard local port property.
     */
    public static final String LOCAL_PORT_KEY = TANGOSOL_COHERENCE_DOT + "localport";

    /**
     * Constant for standard TTL property.
     */
    public static final String TTL_KEY = TANGOSOL_COHERENCE_DOT + "ttl";

    /**
     * Constant for standard log property.
     */
    public static final String LOG_KEY = TANGOSOL_COHERENCE_DOT + "log";

    /**
     * Constant for standard cache configuration property.
     */
    public static final String CACHE_CONFIGURATION_KEY = TANGOSOL_COHERENCE_DOT + "cacheconfig";

    /**
     * Constant for standard override configuration property.
     */
    public static final String OVERRIDE_KEY = TANGOSOL_COHERENCE_DOT + "override";

    /**
     * Constant for standard cluster configuration property.
     */
    public static final String CLUSTER_NAME_KEY = TANGOSOL_COHERENCE_DOT + "cluster";

    /**
     * Constant for standard management property.
     */
    public static final String MANAGEMENT_KEY = TANGOSOL_COHERENCE_DOT + "management";

    /**
     * Constant for standard remote management property.
     */
    public static final String MANAGEMENT_REMOTE_KEY = "management.remote";

    /**
     * Constant for standard JMX remote management property.
     */
    public static final String JMXREMOTE_KEY = "com.sun.management.jmxremote";
}
