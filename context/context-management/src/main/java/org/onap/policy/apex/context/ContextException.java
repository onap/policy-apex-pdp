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

package org.onap.policy.apex.context;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * This exception will be called if an error occurs in an Apex context item.
 *
 * @author Liam Fallon
 */
public class ContextException extends ApexException {
    private static final long serialVersionUID = -8507246953751956974L;

    /**
     * Instantiates a new apex context exception with a message.
     *
     * @param message the message
     */
    public ContextException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new apex context exception with a message and a caused by exception.
     *
     * @param message the message
     * @param ex the exception that caused this exception to be thrown
     */
    public ContextException(final String message, final Exception ex) {
        super(message, ex);
    }
}
