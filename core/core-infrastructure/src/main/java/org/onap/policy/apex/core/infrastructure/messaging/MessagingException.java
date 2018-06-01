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

package org.onap.policy.apex.core.infrastructure.messaging;

/**
 * This class will be called if an error occurs in Java handling.
 *
 * @author Liam Fallon
 */
public class MessagingException extends Exception {
    private static final long serialVersionUID = -6375859029774312663L;

    /**
     * Instantiates a new messaging exception.
     *
     * @param message the message
     */
    public MessagingException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new messaging exception.
     *
     * @param message the message
     * @param e the e
     */
    public MessagingException(final String message, final Exception e) {
        super(message, e);
    }
}
