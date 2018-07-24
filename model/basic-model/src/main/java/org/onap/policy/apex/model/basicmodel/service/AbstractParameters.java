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

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class defines an abstract parameter class that acts as a base class for all parameters in Apex. The abstract parameter class holds the name of a
 * subclass of this abstract parameter class {@link AbstractParameters}. The class of the parameter class is checked at construction and on calls to the
 * {@link #getParameterClass()} method.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class AbstractParameters {
    // The name of the parameter subclass
    private final String parameterClassName;

    /**
     * Constructor, creates a parameter class that must be a subclass of {@link AbstractParameters}.
     *
     * @param parameterClassName the full canonical class name of the parameter class
     */
    public AbstractParameters(final String parameterClassName) {
        try {
            Assertions.assignableFrom(Class.forName(parameterClassName), AbstractParameters.class);
        }
        catch (IllegalArgumentException | ClassNotFoundException e) {
            throw new ApexRuntimeException(
                    "class \"" + parameterClassName + "\" not found or not an instance of \"" + this.getClass().getCanonicalName() + "\"", e);
        }
        
        this.parameterClassName = parameterClassName;
    }

    /**
     * Gets the parameter class.
     *
     * @return the parameter class
     */
    @SuppressWarnings("unchecked")
    public final Class<? extends AbstractParameters> getParameterClass() {
        try {
            return (Class<? extends AbstractParameters>) Class.forName(parameterClassName);
        }
        catch (final ClassNotFoundException e) {
            throw new ApexRuntimeException("class not found for parameter class name \"" + parameterClassName + "\"", e);
        }
    }

    /**
     * Gets the parameter class name.
     *
     * @return the parameter class name
     */
    public final String getParameterClassName() {
        return parameterClassName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractParameters [parameterClassName=" + parameterClassName + "]";
    }
}
