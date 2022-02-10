/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.apex.model.enginemodel.concepts;

/**
 * This enumeration indicates the execution state of an Apex engine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

public enum AxEngineState {
    /** The state of the engine is not known. */
    UNDEFINED(0),
    /** The engine is stopped. */
    STOPPED(1),
    /** The engine is running and is waiting to execute a policy. */
    READY(2),
    /** The engine is running and is executing a policy. */
    EXECUTING(3),
    /** The engine has been ordered to stop and is stopping. */
    STOPPING(4);

    private final int stateIdentifier;

    AxEngineState(int stateIdentifier) {
        this.stateIdentifier = stateIdentifier;
    }

    public int getStateIdentifier() {
        return stateIdentifier;
    }
}
