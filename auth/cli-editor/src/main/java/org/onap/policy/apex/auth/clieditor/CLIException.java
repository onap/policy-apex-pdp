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

package org.onap.policy.apex.auth.clieditor;

/**
 * A run time exception used to report parsing and command input errors.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CLIException extends IllegalArgumentException {
    private static final long serialVersionUID = 6520231162404452427L;

    /**
     * Create a CLIException with a message.
     *
     * @param message the message
     */
    public CLIException(final String message) {
        super(message);
    }

    /**
     * Create a CLIException with a message and an exception.
     *
     * @param message the message
     * @param th the throwable
     */
    public CLIException(final String message, final Throwable th) {
        super(message, th);
    }
}
