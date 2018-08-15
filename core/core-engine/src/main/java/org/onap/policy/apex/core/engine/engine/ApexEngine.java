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

package org.onap.policy.apex.core.engine.engine;

import java.util.Map;

import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * The Interface ApexEngine is used to control the execution of a single Apex engine thread. This
 * engine instance executes the policies in an {@link AxPolicyModel}, which defines the policies
 * that are executed by the engine and the context in which they execute. Many instances of an Apex
 * engine may run on the same Apex model, in which case they operate the same policy set in parallel
 * over the same context. When the {@code handleEvent} method is passed to the Apex engine, the
 * engine executes the policy triggered by that event. A single Apex engine instance does not
 * executed multiple policies in parallel, it receives a trigger event and executes the policy for
 * that event to completion before it is available to execute another policy.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ApexEngine {
    /**
     * The amount of milliseconds to wait for the current Apex engine to timeout on engine stop
     * requests. If the timeout is exceeded, the stop aborts.
     */
    int APEX_ENGINE_STOP_EXECUTION_WAIT_TIMEOUT = 3000;

    /** The wait increment (or pause time) when waiting for the Apex engine to stop. */
    int APEX_ENGINE_STOP_EXECUTION_WAIT_INCREMENT = 100;

    /**
     * Update the Apex model to be used by the Apex engine. The engine must be in state "STOPPED"
     * when the model is updated. The engine will replace the current model with the incoming model
     * if the model of the engine was previously updated and the value of common context is
     * transferred if there is common context in the old and new models.
     *
     * @param apexModel the apex model
     * @throws ApexException on model update errors
     */
    void updateModel(AxPolicyModel apexModel) throws ApexException;

    /**
     * Starts an Apex engine so that it can receive events.
     *
     * @throws ApexException on start errors
     */
    void start() throws ApexException;

    /**
     * Stops an Apex engine in an orderly way. This method must be called prior to model updates.
     *
     * @throws ApexException on stop errors
     */
    void stop() throws ApexException;

    /**
     * Clears all models and data from an Apex engine. The engine must be stopped.
     *
     * @throws ApexException on clear errors
     */
    void clear() throws ApexException;

    /**
     * This method constructs an event with the correct event context so that it can later be sent
     * to the Apex engine.
     *
     * @param eventKey The key of the event in the Apex model
     * @return the created event
     */
    EnEvent createEvent(AxArtifactKey eventKey);

    /**
     * This method passes an event to the Apex model to invoke a policy. If the event matches a
     * policy, then that policy is executed.
     *
     * @param incomingEvent the incoming event
     * @return return true if a policy was invoked without error, otherwise false.
     */
    boolean handleEvent(EnEvent incomingEvent);

    /**
     * A method to add a call back listener class that listens for action events from the engine.
     *
     * @param listenerName the unique name of the listener
     * @param listener is an instance of type {@link EnEventListener}
     */
    void addEventListener(String listenerName, EnEventListener listener);

    /**
     * A method to remove a call back listener class.
     *
     * @param listenerName the name of the listener to remove
     */
    void removeEventListener(String listenerName);

    /**
     * Get the artifact key of the engine.
     *
     * @return the artifact key
     */
    AxArtifactKey getKey();

    /**
     * Get the state of the engine.
     *
     * @return the engine state
     */
    AxEngineState getState();

    /**
     * Get the engine status information, this is just the engine state.
     *
     * @return the Apex status information
     */
    AxEngineModel getEngineStatus();

    /**
     * Get the engine run time information, the status and context.
     *
     * @return the Apex runtime information
     */
    Map<AxArtifactKey, Map<String, Object>> getEngineContext();
}
