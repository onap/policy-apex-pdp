/*-
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

package org.onap.policy.apex.model.basicmodel.concepts;

/**
 * This class is a base run time exception from which all Apex run time exceptions are sub classes.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -8507246953751956974L;

    // The object on which the exception was thrown
    private final transient Object object;

    /**
     * Instantiates a new apex runtime exception.
     *
     * @param message the message on the exception
     */
    public ApexRuntimeException(final String message) {
        this(message, null);
    }

    /**
     * Instantiates a new apex runtime exception.
     *
     * @param message the message on the exception
     * @param object the object that the exception was thrown on
     */
    public ApexRuntimeException(final String message, final Object object) {
        super(message);
        this.object = object;
    }

    /**
     * Instantiates a new apex runtime exception.
     *
     * @param message the message on the exception
     * @param ex the exception that caused this Apex exception
     */
    public ApexRuntimeException(final String message, final Exception ex) {
        this(message, ex, null);
    }

    /**
     * Instantiates a new apex runtime exception.
     *
     * @param message the message on the exception
     * @param ex the exception that caused this Apex exception
     * @param object the object that the exception was thrown on
     */
    public ApexRuntimeException(final String message, final Exception ex, final Object object) {
        super(message, ex);
        this.object = object;
    }

    /**
     * Get the message from this exception and its causes.
     *
     * @return the message of this exception and all the exceptions that caused this exception
     */
    public String getCascadedMessage() {
        return ApexException.buildCascadedMessage(this);
    }

    /**
     * Get the object on which the exception was thrown.
     *
     * @return The object
     */
    public Object getObject() {
        return object;
    }
}
