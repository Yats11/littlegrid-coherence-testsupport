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

import java.util.Properties;

/**
 * System utilities class providing useful system related methods.
 */
final class SystemUtils {
    /**
     * Private constructor to prevent creation.
     */
    private SystemUtils() {
    }

    /**
     * Captures current system properties.
     *
     * @return current properties.
     */
    public static Properties snapshotSystemProperties() {
        Properties properties = new Properties();

        for (String key : System.getProperties().stringPropertyNames()) {
            String value = System.getProperty(key);

            properties.setProperty(key, value);
        }

        return properties;
    }

    /**
     * Apply the properties to the system properties.
     *
     * @param properties New and updated system properties.
     */
    public static void applyToSystemProperties(final Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);

            if (!value.trim().isEmpty()) {
                System.setProperty(key, value);
            }
        }
    }

    /**
     * Get current system properties which start with the specified prefix.
     *
     * @param prefix Prefix.
     * @return properties.
     */
    public static Properties getSystemPropertiesWithPrefix(final String prefix) {
        Properties prefixedProperties = new Properties();

        for (String key : System.getProperties().stringPropertyNames()) {
            if (key.contains(prefix)) {
                String value = System.getProperty(key);

                prefixedProperties.setProperty(key, value);
            }
        }

        return prefixedProperties;
    }
}
