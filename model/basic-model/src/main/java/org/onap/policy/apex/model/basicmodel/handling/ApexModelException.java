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

package org.onap.policy.apex.model.basicmodel.handling;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * This exception is invoked if an exception occurs in model handling.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexModelException extends ApexException {
    private static final long serialVersionUID = -4245694568321686450L;

    /**
     * Instantiates a new apex model handling exception.
     *
     * @param message the message
     */
    public ApexModelException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new apex model handling exception.
     *
     * @param message the message
     * @param exception the exception
     */
    public ApexModelException(final String message, final Exception exception) {
        super(message, exception);
    }
}
