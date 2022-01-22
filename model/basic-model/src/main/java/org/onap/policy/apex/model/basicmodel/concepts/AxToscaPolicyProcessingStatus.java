/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Bell Canada. All rights reserved.
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
 * This enumeration indicates the status of TOSCA policy processing on an APEX event.
 */
public enum AxToscaPolicyProcessingStatus {

    /** Indicates the entrypoint for the processing of a TOSCA Policy. */
    ENTRY(0),

    /** Indicates a successful exit point for a TOSCA Policy. */
    EXIT_SUCCESS(1),

    /** Indicates a failure exit point for a TOSCA Policy. */
    EXIT_FAILURE(2);

    private final int statusCode;

    AxToscaPolicyProcessingStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}