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

package org.onap.policy.apex.core.engine.engine.impl;

/**
 * Constants for the Apex engine.
 *
 */
public abstract class ApexEngineConstants {
    /**
     * The amount of milliseconds to wait for the current Apex engine to timeout on engine stop
     * requests. If the timeout is exceeded, the stop aborts.
     */
    public static final int STOP_EXECUTION_WAIT_TIMEOUT = 3000;

    /** The wait increment (or pause time) when waiting for the Apex engine to stop. */
    public static final int APEX_ENGINE_STOP_EXECUTION_WAIT_INCREMENT = 100;
    
    /**
     * Private constructor to prevent subclassing.
     */
    private ApexEngineConstants() {
        // Constructor to avoid subclassing
    }
}
