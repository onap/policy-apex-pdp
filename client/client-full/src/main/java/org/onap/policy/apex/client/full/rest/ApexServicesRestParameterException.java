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

package org.onap.policy.apex.client.full.rest;

/**
 * A run time exception used to report parsing and parameter input errors.
 *
 * <p>User: ewatkmi Date: 31 Jul 2017
 */
public class ApexServicesRestParameterException extends IllegalArgumentException {
    private static final long serialVersionUID = 6520231162404452427L;

    /**
     * Create an ApexServicesRestParameterException with a message.
     *
     * @param message the message
     */
    public ApexServicesRestParameterException(final String message) {
        super(message);
    }

    /**
     * Create an ApexServicesRestParameterException with a message and an exception.
     *
     * @param message the message
     * @param throwable The exception that caused the exception
     */
    public ApexServicesRestParameterException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
