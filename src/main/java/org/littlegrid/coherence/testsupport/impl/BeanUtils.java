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

import com.tangosol.util.ValueManipulator;
import com.tangosol.util.processor.PropertyManipulator;

import java.util.Properties;

/**
 * Bean utilities class.
 */
final class BeanUtils {
    /**
     * Private constructor to prevent creation.
     */
    private BeanUtils() {
    }

    /**
     * Invokes setter methods to set state on bean using properties as the method name and value to set.
     *
     * @param bean  Bean on which to invoke methods.
     * @param properties  Properties, keys are used for method names, whilst values are used to set state.
     * @return  number of methods invoked.
     */
    public static int processProperties(final Object bean,
                                        final Properties properties) {

        int propertiesSetCounter = 0;

        for (String key : properties.stringPropertyNames()) {
            final String value = properties.getProperty(key);
            final ValueManipulator manipulator = new PropertyManipulator(key);

            manipulator.getUpdater().update(bean, value);

            propertiesSetCounter++;
        }

        return propertiesSetCounter;
    }
}
