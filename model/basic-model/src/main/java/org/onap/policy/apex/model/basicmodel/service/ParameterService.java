/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.model.basicmodel.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;

/**
 * The parameter service makes Apex parameters available to all classes in a JVM.
 *
 * <p>The reason for having a parameter service is to avoid having to pass parameters down long call
 * chains in modules such as the Apex engine and editor. The parameter service makes parameters
 * available statically.
 *
 * <p>The parameter service must be used with care because changing a parameter set anywhere in a
 *  JVM will affect all users of those parameters anywhere in the JVM.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class ParameterService {
    // The map holding the parameters
    private static Map<Class<?>, AbstractParameters> parameterMap = new ConcurrentHashMap<>();

    /**
     * This class is an abstract static class that cannot be extended.
     */
    private ParameterService() {}

    /**
     * Register parameters with the parameter service.
     *
     * @param <P> the generic type
     * @param parametersClass the class of the parameter, used to index the parameter
     * @param parameters the parameters
     */
    public static <P extends AbstractParameters> void registerParameters(final Class<P> parametersClass,
            final P parameters) {
        parameterMap.put(parametersClass, parameters);
    }

    /**
     * Remove parameters from the parameter service.
     *
     * @param <P> the generic type
     * @param parametersClass the class of the parameter, used to index the parameter
     */
    public static <P extends AbstractParameters> void deregisterParameters(final Class<P> parametersClass) {
        parameterMap.remove(parametersClass);
    }

    /**
     * Get parameters from the parameter service.
     *
     * @param <P> the generic type
     * @param parametersClass the class of the parameter, used to index the parameter
     * @return The parameter
     */
    @SuppressWarnings("unchecked")
    public static <P extends AbstractParameters> P getParameters(final Class<P> parametersClass) {
        final P parameter = (P) parameterMap.get(parametersClass);

        if (parameter == null) {
            throw new ApexRuntimeException(
                    "Parameters for " + parametersClass.getCanonicalName() + " not found in parameter service");
        }

        return parameter;
    }

    /**
     * Check if parameters is defined on the parameter service.
     *
     * @param <P> the generic type
     * @param parametersClass the class of the parameter, used to index the parameter
     * @return true if the parameter is defined
     */
    public static <P extends AbstractParameters> boolean existsParameters(final Class<P> parametersClass) {
        return parameterMap.get(parametersClass) != null;
    }

    /**
     * Get all the entries in the parameters map.
     *
     * @return The entries
     */
    public static Set<Entry<Class<?>, AbstractParameters>> getAll() {
        return parameterMap.entrySet();
    }

    /**
     * Clear all parameters in the parameter service.
     */
    public static void clear() {
        parameterMap.clear();
    }
}
