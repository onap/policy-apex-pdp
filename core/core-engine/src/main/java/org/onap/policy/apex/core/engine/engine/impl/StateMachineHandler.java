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

package org.onap.policy.apex.core.engine.engine.impl;

import java.util.HashMap;

import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.ExecutorFactory;
import org.onap.policy.apex.core.engine.executor.StateMachineExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.core.engine.executor.impl.ExecutorFactoryImpl;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This handler holds and manages state machines for each policy in an Apex engine. When the class
 * is instantiated, an executor {@link StateMachineExecutor} is created for each policy in the
 * policy model the state machine handler will execute. The executors for each policy are held in a
 * map indexed by event.
 *
 * <p>When an event is received on the policy, the state machine executor to execute that event is
 * looked up on the executor map and the event is passed to the executor for execution.
 *
 * @author Liam Fallon
 *
 */
public class StateMachineHandler {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(StateMachineHandler.class);

    // The key of the Apex model we are executing
    private final AxArtifactKey key;

    // The state machines in this engine
    private final HashMap<AxEvent, StateMachineExecutor> stateMachineExecutorMap = new HashMap<>();

    // The executor factory is used to get logic executors for the particular type of executor we
    // need for task
    // selection logic or task logic
    private final ExecutorFactory executorFactory;

    /**
     * This constructor builds the state machines for the policies in the apex model.
     *
     * @param internalContext The internal context we are using
     * @throws StateMachineException On state machine initiation errors
     */
    protected StateMachineHandler(final ApexInternalContext internalContext) throws StateMachineException {
        LOGGER.entry("StateMachineHandler()->" + internalContext.getKey().getId());

        key = internalContext.getKey();

        // Create the executor factory to generate executors as the engine runs policies
        executorFactory = new ExecutorFactoryImpl();

        // Iterate over the policies in the policy model and create a state machine for each one
        for (final AxPolicy policy : ModelService.getModel(AxPolicies.class).getPolicyMap().values()) {
            // Create a state machine for this policy
            final StateMachineExecutor thisStateMachineExecutor =
                    new StateMachineExecutor(executorFactory, policy.getKey());

            // This executor is the top executor so has no parent
            thisStateMachineExecutor.setContext(null, policy, internalContext);

            // Get the incoming trigger event
            final AxEvent triggerEvent = ModelService.getModel(AxEvents.class)
                    .get(policy.getStateMap().get(policy.getFirstState()).getTrigger());

            // Put the state machine executor on the map for this trigger
            final StateMachineExecutor lastStateMachineExecutor =
                    stateMachineExecutorMap.put(triggerEvent, thisStateMachineExecutor);
            if (lastStateMachineExecutor != null
                    && lastStateMachineExecutor.getSubject() != thisStateMachineExecutor.getSubject()) {
                LOGGER.error("No more than one policy in a model can have the same trigger event. In model "
                        + internalContext.getKey().getId() + " Policy ("
                        + lastStateMachineExecutor.getSubject().getKey().getId() + ") and Policy ("
                        + thisStateMachineExecutor.getSubject().getKey().getId() + ") have the same Trigger event ("
                        + triggerEvent.getKey().getId() + ") ");
                LOGGER.error(" Policy (" + lastStateMachineExecutor.getSubject().getKey() + ") has overwritten Policy ("
                        + thisStateMachineExecutor.getSubject().getKey().getId()
                        + " so this overwritten policy will never be triggered in this engine.");
            }
        }

        LOGGER.exit("StateMachineHandler()<-" + internalContext.getKey().getId());
    }

    /**
     * This constructor starts the state machines for each policy, carrying out whatever
     * initialization executors need.
     *
     * @throws StateMachineException On state machine initiation errors
     */
    protected void start() throws StateMachineException {
        LOGGER.entry("start()->" + key.getId());

        // Iterate over the state machines
        for (final StateMachineExecutor smExecutor : stateMachineExecutorMap.values()) {
            try {
                smExecutor.prepare();
            } catch (final StateMachineException e) {
                final String stateMachineID = smExecutor.getContext().getKey().getId();
                LOGGER.warn("start()<-" + key.getId() + ", start failed, state machine \"" + stateMachineID + "\"", e);
                throw new StateMachineException(
                        "start()<-" + key.getId() + ", start failed, state machine \"" + stateMachineID + "\"", e);
            }
        }

        LOGGER.exit("start()<-" + key.getId());
    }

    /**
     * This method is called to execute an event on the state machines in an engine.
     *
     * @param event The trigger event for the state machine
     * @return The result of the state machine execution run
     * @throws StateMachineException On execution errors in a state machine
     */
    protected EnEvent execute(final EnEvent event) throws StateMachineException {
        LOGGER.entry("execute()->" + event.getName());

        // Try to execute the state machine for the trigger
        final StateMachineExecutor stateMachineExecutor = stateMachineExecutorMap.get(event.getAxEvent());
        if (stateMachineExecutor == null) {
            final String exceptionMessage =
                    "state machine execution not possible, policy not found for trigger event " + event.getName();
            LOGGER.warn(exceptionMessage);

            event.setExceptionMessage(exceptionMessage);
            return event;
        }

        // Run the state machine
        try {
            LOGGER.debug("execute(): state machine \"{}\" execution starting  . . .", stateMachineExecutor);
            final EnEvent outputObject = stateMachineExecutor.execute(event.getExecutionID(), event);

            LOGGER.debug("execute()<-: state machine \"{}\" execution completed", stateMachineExecutor);
            return outputObject;
        } catch (final Exception e) {
            LOGGER.warn("execute()<-: state machine \"" + stateMachineExecutor + "\" execution failed", e);
            throw new StateMachineException("execute()<-: execution failed on state machine " + stateMachineExecutor,
                    e);
        }
    }

    /**
     * Closes down the state machines of an engine.
     */
    protected void stop() {
        LOGGER.entry("stop()->");

        // Iterate through all state machines and clean them
        for (final StateMachineExecutor smExecutor : stateMachineExecutorMap.values()) {
            try {
                smExecutor.cleanUp();
            } catch (final StateMachineException e) {
                final String smID = smExecutor.getContext().getKey().getId();
                LOGGER.warn("stop()<-clean up failed, state machine \"" + smID + "\" cleanup failed", e);
            }
        }
        LOGGER.exit("stop()<-");
    }
}
