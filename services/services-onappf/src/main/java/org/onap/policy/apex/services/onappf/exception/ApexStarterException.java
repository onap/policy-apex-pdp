/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.exception;

/**
 * This exception will be called if an error occurs in apex starter.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterException extends Exception {
    private static final long serialVersionUID = -510646141043975917L;

    /**
     * Instantiates a new apex starter exception with a message.
     *
     * @param message the message
     */
    public ApexStarterException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new apex starter exception caused by an exception.
     *
     * @param exception the exception that caused this exception to be thrown
     */
    public ApexStarterException(final Exception exception) {
        super(exception);
    }

    /**
     * Instantiates a new apex starter exception with a message and a caused by an exception.
     *
     * @param message the message
     * @param exception the exception that caused this exception to be thrown
     */
    public ApexStarterException(final String message, final Exception exception) {
        super(message, exception);
    }

}
