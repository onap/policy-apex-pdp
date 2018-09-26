/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.impl.schema.java;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

//@formatter:off
/**
 * Event protocol parameters for JSON as an event protocol.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>adaptedClass: The name of the class being adapted.
 * <li>adapterClass: the JSON adapter class to use for the adapted class.
 * </ol>
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
//@formatter:on
public class JavaSchemaHelperJsonAdapterParameters implements ParameterGroup {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavaSchemaHelperJsonAdapterParameters.class);

    // Recurring string constants
    private static final String ADAPTED_CLASS = "adaptedClass";
    private static final String ADAPTOR_CLASS = "adaptorClass";

    private String adaptedClass;
    private String adaptorClass;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getAdaptedClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String adaptedClass) {
        setAdaptedClass(adaptedClass);
    }

    /**
     * Gets the adapted class.
     *
     * @return the adapted class
     */
    public String getAdaptedClass() {
        return adaptedClass;
    }

    /**
     * Gets the adapted class.
     *
     * @return the adapted class
     */
    public Class<?> getAdaptedClazz() {
        if (adaptedClass == null) {
            return null;
        }
        
        try {
            return Class.forName(adaptedClass);
        } catch (final ClassNotFoundException e) {
            LOGGER.warn("class \"" + adaptedClass + "\" not found: ", e);
            return null;
        }
    }

    /**
     * Sets the adapted class.
     *
     * @param adaptedClass the new adapted class
     */
    public void setAdaptedClass(String adaptedClass) {
        this.adaptedClass = adaptedClass;
    }

    /**
     * Gets the adaptor class.
     *
     * @return the adaptor class
     */
    public String getAdaptorClass() {
        return adaptorClass;
    }

    /**
     * Gets the adaptor class.
     *
     * @return the adaptor class
     */
    public Class<?> getAdaptorClazz() {
        if (adaptorClass == null) {
            return null;
        }
        
        try {
            return Class.forName(adaptorClass);
        } catch (final ClassNotFoundException e) {
            LOGGER.warn("class \"" + adaptorClass + "\" not found: ", e);
            return null;
        }
    }

    /**
     * Sets the adaptor class.
     *
     * @param adaptorClass the new adaptor class
     */
    public void setAdaptorClass(String adaptorClass) {
        this.adaptorClass = adaptorClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);

        getClass(ADAPTED_CLASS, adaptedClass, result);
        
        Class<?> adaptorClazz = getClass(ADAPTOR_CLASS, adaptorClass, result);
        if (adaptorClazz != null) {
            String errorMessage = null;
            
            if (!JsonSerializer.class.isAssignableFrom(adaptorClazz)) {
                errorMessage = "class is not a JsonSerializer";
            }
            
            if (!JsonDeserializer.class.isAssignableFrom(adaptorClazz)) {
                if (errorMessage == null) {
                    errorMessage = "class is not a JsonDeserializer";
                }
                else {
                    errorMessage = "class is not a JsonSerializer or JsonDeserializer";
                }
            }

            if (errorMessage != null) {
                result.setResult(ADAPTOR_CLASS, ValidationStatus.INVALID, errorMessage);
            }
        }

        return result;
    }

    /**
     * Check a class exists.
     * 
     * @param parameterName the parameter name of the class to check for existence 
     * @param classToCheck the class to check for existence 
     * @param result the result of the check
     */
    private Class<?> getClass(String parameterName, String classToCheck, final GroupValidationResult result) {
        if (classToCheck == null || classToCheck.trim().length() == 0) {
            result.setResult(parameterName, ValidationStatus.INVALID, "parameter is null or blank");
            return null;
        }
        
        // Get the class for the event protocol
        try {
            return Class.forName(classToCheck);
        } catch (final ClassNotFoundException e) {
            result.setResult(parameterName, ValidationStatus.INVALID, "class not found: " + e.getMessage());
            LOGGER.warn("class not found: ", e);
            return null;
        }
    }
}
