/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.parameters;

/**
 * This class holds constants used when managing context parameter groups in apex.
 */
public abstract class ContextParameterConstants {
    public static final String MAIN_GROUP_NAME = "CONTEXT_PARAMETERS";
    public static final String SCHEMA_GROUP_NAME = "CONTEXT_SCHEMA_PARAMETERS";
    public static final String SCHEMA_HELPER_GROUP_NAME = "CONTEXT_SCHEMA_HELPER_PARAMETERS";
    public static final String DISTRIBUTOR_GROUP_NAME = "CONTEXT_DISTRIBUTOR_PARAMETERS";
    public static final String LOCKING_GROUP_NAME = "CONTEXT_LOCKING_PARAMETERS";
    public static final String PERSISTENCE_GROUP_NAME = "CONTEXT_PERSISTENCE_PARAMETERS";

    /**
     * Private default constructor to prevent subclassing.
     */
    private ContextParameterConstants() {
        // Prevents subclassing
    }

}
