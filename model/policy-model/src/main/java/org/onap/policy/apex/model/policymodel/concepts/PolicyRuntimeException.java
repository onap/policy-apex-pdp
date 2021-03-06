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

package org.onap.policy.apex.model.policymodel.concepts;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;

/**
 * This exception is raised if a runtime error occurs in an Apex policy or in Apex policy handling.
 *
 * @author Liam Fallon
 */
public class PolicyRuntimeException extends ApexRuntimeException {
    private static final long serialVersionUID = -8507246953751956974L;

    /**
     * Instantiates a new apex policy runtime exception with a message.
     *
     * @param message the message
     */
    public PolicyRuntimeException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new apex policy runtime exception with a message and a caused by exception.
     *
     * @param message the message
     * @param exception the exception that caused this exception to be thrown
     */
    public PolicyRuntimeException(final String message, final Exception exception) {
        super(message, exception);
    }
}
