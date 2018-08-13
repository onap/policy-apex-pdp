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

package org.onap.policy.apex.client.editor.rest;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Exceptions from the Apex editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEditorException extends ApexException {
    private static final long serialVersionUID = 4867385591967018254L;

    /**
     * Instantiates a new apex editor exception.
     *
     * @param message the message on the exception
     */
    public ApexEditorException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new apex editor exception.
     *
     * @param message the message on the exception
     * @param object the object that the exception was thrown on
     */
    public ApexEditorException(final String message, final Object object) {
        super(message, object);
    }

    /**
     * Instantiates a new apex editor exception.
     *
     * @param message the message on the exception
     * @param ex the exception that caused this Apex exception
     */
    public ApexEditorException(final String message, final Exception ex) {
        super(message, ex);
    }

    /**
     * Instantiates a new apex editor exception.
     *
     * @param message the message on the exception
     * @param ex the exception that caused this Apex exception
     * @param object the object that the exception was thrown on
     */
    public ApexEditorException(final String message, final Exception ex, final Object object) {
        super(message, ex, object);
    }

}
