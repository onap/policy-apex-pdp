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

package org.onap.policy.apex.core.protocols.engdep;

import org.onap.policy.apex.core.protocols.Action;

/**
 * Action types the EngDep messaging protocol supports.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public enum EngDepAction implements Action {
    /** Action to get information on the running engine service. */
    GET_ENGINE_SERVICE_INFO {
        @Override
        public String getActionString() {
            return "Apex engine service information";
        }
    },
    /** Action to update the policy model in an engine service. */
    UPDATE_MODEL {
        @Override
        public String getActionString() {
            return "update model on Apex engine service";
        }
    },
    /** Action to start an engine service. */
    START_ENGINE {
        @Override
        public String getActionString() {
            return "starts an Apex engine";
        }
    },
    /** Action to stop an engine service. */
    STOP_ENGINE {
        @Override
        public String getActionString() {
            return "stops an Apex engine service";
        }
    },
    /** Action to start sending periodic events to an engine service. */
    START_PERIODIC_EVENTS {
        @Override
        public String getActionString() {
            return "starts periodic events on an Apex engine service";
        }
    },
    /** Action to stop sending periodic events to an engine service. */
    STOP_PERIODIC_EVENTS {
        @Override
        public String getActionString() {
            return "stops periodic events on an Apex engine service";
        }
    },
    /** Action to get the status of an engine in the engine service. */
    GET_ENGINE_STATUS {
        @Override
        public String getActionString() {
            return "gets the status of an Apex engine service";
        }
    },
    /** Action to get information on an engine in the engine service. */
    GET_ENGINE_INFO {
        @Override
        public String getActionString() {
            return "gets runtime information an Apex engine service";
        }
    },
    /** The response message to all actions. */
    RESPONSE {
        @Override
        public String getActionString() {
            return "response from Apex engine service";
        }
    };
}
