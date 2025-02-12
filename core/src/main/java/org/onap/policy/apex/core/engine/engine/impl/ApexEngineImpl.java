/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024-2025 Nordix Foundation.
 *  Modifications Copyright (C) 2021-2022 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.onap.policy.common.utils.validation.Assertions.argumentNotNull;

import io.prometheus.metrics.core.metrics.Gauge;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Getter;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.engine.ApexEngine;
import org.onap.policy.apex.core.engine.engine.EnEventListener;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineStats;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.utils.resources.PrometheusUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class controls the thread of execution of a single engine in an Apex system. An engine is a single thread in a
 * pool of engines that are running a set of policies. An engine is either inactive, waiting for a policy to be
 * triggered or executing a policy. The engine runs off a queue of triggers that trigger its state machine. If the queue
 * is empty, it waits for the next trigger. The Apex engine holds its state machine in a {@link StateMachineHandler}
 * instance and uses its state machine handler to execute events.
 *
 * @author Liam Fallon
 */
public class ApexEngineImpl implements ApexEngine {

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEngineImpl.class);

    // Register state changes with prometheus
    static final Gauge ENGINE_STATE = Gauge.builder()
        .name(PrometheusUtils.PdpType.PDPA.getNamespace() + "_" + "engine_state")
        .labelNames("engine_instance_id")
        .help("State of the APEX engine as integers mapped as - 0:UNDEFINED, 1:STOPPED, 2:READY,"
            + " 3:EXECUTING, 4:STOPPING").register();

    // Recurring string constants
    private static final String UPDATE_MODEL = "updateModel()<-";
    private static final String START = "start()<-";
    private static final String STOP = "stop()<-";

    // The artifact key of this engine
    @Getter
    private final AxArtifactKey key;

    // The state of this engine
    @Getter
    private AxEngineState state = AxEngineState.STOPPED;
    private final Object stateLockObj = new Object();

    // call back listeners
    private final Map<String, EnEventListener> eventListeners = new LinkedHashMap<>();

    // The context of this engine
    @Getter
    private ApexInternalContext internalContext = null;

    // The state machines
    private StateMachineHandler stateMachineHandler = null;

    // Statistics on engine execution
    private final AxEngineStats engineStats;

    /**
     * Constructor, instantiate the engine with its state machine table.
     *
     * @param key the key of the engine
     */
    protected ApexEngineImpl(final AxArtifactKey key) {
        argumentNotNull(key, "AxArtifactKey may not be null");

        LOGGER.entry("ApexEngine()->{}, {}", key.getId(), state);

        this.key = key;

        // Set up statistics collection
        engineStats = new AxEngineStats();
        engineStats.setKey(new AxReferenceKey(key, "_EngineStats"));

        LOGGER.exit("ApexEngine()<-" + key.getId() + "," + state);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateModel(final AxPolicyModel apexModel, final boolean isSubsequentInstance) throws ApexException {
        updateStatePrometheusMetric();
        if (apexModel != null) {
            LOGGER.entry("updateModel()->{}, apexPolicyModel {}", key.getId(), apexModel.getKey().getId());
        } else {
            throw new ApexException(UPDATE_MODEL + key.getId() + ", Apex model is not defined, it has a null value");
        }

        // The engine must be stopped in order to do a model update
        synchronized (stateLockObj) {
            if (!state.equals(AxEngineState.STOPPED)) {
                throw new ApexException(
                    UPDATE_MODEL + key.getId() + ", cannot update model, engine should be stopped but is in state "
                        + state);
            }
        }

        populateIoEventsToTask(apexModel);

        // Create new internal context or update the existing one
        try {
            if (internalContext == null) {
                /// New internal context
                internalContext = new ApexInternalContext(apexModel);
            } else {
                // Existing internal context which must be updated
                internalContext.update(apexModel, isSubsequentInstance);
            }
        } catch (final ContextException e) {
            throw new ApexException(
                UPDATE_MODEL + key.getId() + ", error setting the context for engine \"" + key.getId() + "\"", e);
        }

        // Set up the state machines
        try {
            // We always set up state machines as new because it's only context that must be transferred; policies are
            // always set up as new
            stateMachineHandler = new StateMachineHandler(internalContext);
        } catch (final StateMachineException e) {
            throw new ApexException(
                UPDATE_MODEL + key.getId() + ", error setting up the engine state machines \"" + key.getId() + "\"", e);
        }

        LOGGER.exit(UPDATE_MODEL + key.getId());
    }


    private void populateIoEventsToTask(AxPolicyModel apexPolicyModel) {
        Set<AxArtifactKey> updatedTasks = new TreeSet<>();
        for (var axPolicy : apexPolicyModel.getPolicies().getPolicyMap().values()) {
            for (var axState : axPolicy.getStateMap().values()) {
                AxEvent triggerEvent = apexPolicyModel.getEvents().get(axState.getTrigger());
                axState.getTaskReferences().forEach((taskKey, taskRef) -> {
                    AxTask task = apexPolicyModel.getTasks().getTaskMap().get(taskKey);
                    task.setInputEvent(triggerEvent);
                    updateTaskBasedOnStateOutput(apexPolicyModel, updatedTasks, axState, taskKey, taskRef, task);
                    updatedTasks.add(taskKey);
                });
            }
        }
    }

    private void updateTaskBasedOnStateOutput(AxPolicyModel apexPolicyModel, Set<AxArtifactKey> updatedTasks,
                                              AxState state, AxArtifactKey taskKey, AxStateTaskReference taskRef,
                                              AxTask task) {
        Map<String, AxEvent> outputEvents = new TreeMap<>();
        AxStateOutput stateOutput = null;
        if (AxStateTaskOutputType.LOGIC.equals(taskRef.getStateTaskOutputType())) {
            // in case of SFL, outgoing event will be same for all state outputs that are part of SFL.So, take any entry
            stateOutput = state.getStateOutputs().values().iterator().next();
        } else {
            stateOutput = state.getStateOutputs().get(taskRef.getOutput().getLocalName());
        }
        if (null != stateOutput) {
            if (null == stateOutput.getOutgoingEventSet() || stateOutput.getOutgoingEventSet().isEmpty()) {
                Set<AxArtifactKey> outEventSet = new TreeSet<>();
                outEventSet.add(stateOutput.getOutgoingEvent());
                stateOutput.setOutgoingEventSet(outEventSet);
            }
            if (state.getNextStateSet().isEmpty()
                || state.getNextStateSet().contains(AxReferenceKey.getNullKey().getLocalName())) {
                stateOutput.getOutgoingEventSet().forEach(outgoingEventKey -> outputEvents
                    .put(outgoingEventKey.getName(), apexPolicyModel.getEvents().get(outgoingEventKey)));
            } else {
                AxArtifactKey outgoingEventKey = stateOutput.getOutgoingEvent();
                outputEvents.put(outgoingEventKey.getName(), apexPolicyModel.getEvents().get(outgoingEventKey));
            }
            if (updatedTasks.contains(taskKey)) {
                // this happens only when same task is used by multiple policies
                // with different eventName but same fields
                task.getOutputEvents().putAll(outputEvents);
            } else {
                task.setOutputEvents(outputEvents);
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void start() throws ApexException {
        LOGGER.entry("start() {}", key);
        synchronized (stateLockObj) {
            if (state != AxEngineState.STOPPED) {
                String message =
                    START + key.getId() + "," + state + ", cannot start engine, engine not in state STOPPED";
                throw new ApexException(message);
            }
        }

        if (stateMachineHandler == null || internalContext == null) {
            throw new ApexException(START + key.getId() + "," + state
                + ",  cannot start engine, engine has not been initialized, its model is not loaded");
        }

        // Set up the state machines
        try {
            // Start the state machines
            stateMachineHandler.start();
            engineStats.engineStart();
        } catch (final StateMachineException e) {
            String message =
                UPDATE_MODEL + key.getId() + ", error starting the engine state machines \"" + key.getId() + "\"";
            throw new ApexException(message, e);
        }

        // OK, we are good to go
        state = AxEngineState.READY;
        updateStatePrometheusMetric();

        LOGGER.exit("start()" + key);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() throws ApexException {
        LOGGER.entry("stop()-> {}", key);

        // Check if the engine is already stopped
        synchronized (stateLockObj) {
            if (state == AxEngineState.STOPPED) {
                throw new ApexException(
                    STOP + key.getId() + "," + state + ", cannot stop engine, engine is already stopped");
            }
        }
        // Stop the engine if it is in state READY, if it is in state EXECUTING, wait for execution to finish
        for (int increment = ApexEngineConstants.STOP_EXECUTION_WAIT_TIMEOUT; increment > 0;
             increment -= ApexEngineConstants.APEX_ENGINE_STOP_EXECUTION_WAIT_INCREMENT) {
            ThreadUtilities.sleep(ApexEngineConstants.APEX_ENGINE_STOP_EXECUTION_WAIT_INCREMENT);

            synchronized (stateLockObj) {
                switch (state) {
                    // Engine is OK to stop or has been stopped on return of an event
                    case READY, STOPPED:
                        state = AxEngineState.STOPPED;
                        updateStatePrometheusMetric();
                        stateMachineHandler.stop();
                        engineStats.engineStop();
                        LOGGER.exit("stop()" + key);
                        return;

                    // Engine is executing a policy, wait for it to stop
                    case EXECUTING:
                        state = AxEngineState.STOPPING;
                        updateStatePrometheusMetric();
                        break;

                    // Wait for the engine to stop
                    case STOPPING:
                        break;

                    default:
                        throw new ApexException(
                            STOP + key.getId() + "," + state + ", cannot stop engine, engine is in an undefined state");
                }
            }
        }

        // Force the engine to STOPPED state
        synchronized (stateLockObj) {
            state = AxEngineState.STOPPED;
        }
        updateStatePrometheusMetric();

        throw new ApexException(STOP + key.getId() + "," + state + ", error stopping engine, engine stop timed out");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clear() throws ApexException {
        LOGGER.entry("clear()-> {}", key);
        synchronized (stateLockObj) {
            if (state != AxEngineState.STOPPED) {
                throw new ApexException(
                    "clear" + "()<-" + key.getId() + "," + state + ", cannot clear engine, engine is not stopped");
            }
        }

        // Clear everything
        stateMachineHandler = null;
        engineStats.clean();

        if (internalContext != null) {
            internalContext.clear();
            internalContext = null;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public EnEvent createEvent(final AxArtifactKey eventKey) {
        synchronized (stateLockObj) {
            if (state != AxEngineState.READY && state != AxEngineState.EXECUTING) {
                LOGGER.warn("createEvent()<-{},{}, cannot create event, engine not in state READY", key.getId(), state);
                return null;
            }
        }

        try {
            // Create an event using the internal context
            return new EnEvent(eventKey);
        } catch (final Exception e) {
            LOGGER.warn("createEvent()<-{},{}, error on event creation: ", key.getId(), state, e);
            return null;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean handleEvent(final EnEvent incomingEvent) {
        var ret = false;
        if (incomingEvent == null) {
            LOGGER.warn("handleEvent()<-{},{}, cannot run engine, incoming event is null", key.getId(), state);
            return ret;
        }

        synchronized (stateLockObj) {
            if (state != AxEngineState.READY) {
                LOGGER.warn("handleEvent()<-{},{}, cannot run engine, engine not in state READY", key.getId(), state);
                return ret;
            }

            state = AxEngineState.EXECUTING;
        }
        updateStatePrometheusMetric();

        String message = "execute(): triggered by event " + incomingEvent;
        LOGGER.debug(message);

        // By default, we return a null event on errors
        Collection<EnEvent> outgoingEvents = null;
        try {
            engineStats.executionEnter(incomingEvent.getKey());
            outgoingEvents = stateMachineHandler.execute(incomingEvent);
            engineStats.executionExit();
            ret = true;
        } catch (final StateMachineException e) {
            LOGGER.warn("handleEvent()<-{},{}, engine execution error: ", key.getId(), state, e);

            // Create an exception return event
            outgoingEvents = createExceptionEvent(incomingEvent, e);
        }

        // Publish the outgoing event
        try {
            synchronized (eventListeners) {
                if (eventListeners.isEmpty()) {
                    LOGGER.debug("handleEvent()<-{},{}, There is no listener registered to receive outgoing event: {}",
                        key.getId(), state, outgoingEvents);
                }
                for (final EnEventListener axEventListener : eventListeners.values()) {
                    for (var outgoingEvent : outgoingEvents) {
                        axEventListener.onEnEvent(outgoingEvent);
                    }
                }
            }
        } catch (final ApexException e) {
            LOGGER.warn("handleEvent()<-{},{}, outgoing event publishing error: ", key.getId(), state, e);
            ret = false;
        }
        synchronized (stateLockObj) {
            // Only go to READY if we are still in state EXECUTING, we go to state STOPPED if we were STOPPING
            if (state == AxEngineState.EXECUTING) {
                state = AxEngineState.READY;
            } else if (state == AxEngineState.STOPPING) {
                state = AxEngineState.STOPPED;
            }
        }
        updateStatePrometheusMetric();
        return ret;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void addEventListener(final String listenerName, final EnEventListener listener) {
        if (listenerName == null) {
            String message = "addEventListener()<-" + key.getId() + "," + state + ", listenerName is null";
            throw new ApexRuntimeException(message);
        }

        if (listener == null) {
            String message = "addEventListener()<-" + key.getId() + "," + state + ", listener is null";
            throw new ApexRuntimeException(message);
        }

        eventListeners.put(listenerName, listener);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void removeEventListener(final String listenerName) {
        if (listenerName == null) {
            String message = "removeEventListener()<-" + key.getId() + "," + state + ", listenerName is null";
            throw new ApexRuntimeException(message);
        }

        eventListeners.remove(listenerName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxEngineModel getEngineStatus() {
        final var engineModel = new AxEngineModel(key);
        engineModel.setTimestamp(System.currentTimeMillis());
        engineModel.setState(state);
        engineModel.setStats(engineStats);
        return engineModel;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<AxArtifactKey, Map<String, Object>> getEngineContext() {
        final Map<AxArtifactKey, Map<String, Object>> currentContext = new LinkedHashMap<>();

        if (internalContext == null) {
            return currentContext;
        }

        for (final Entry<AxArtifactKey, ContextAlbum> contextAlbumEntry : internalContext.getContextAlbums()
            .entrySet()) {
            currentContext.put(contextAlbumEntry.getKey(), contextAlbumEntry.getValue());
        }

        return currentContext;
    }

    /**
     * Create an exception event from the incoming event including the exception information on the event.
     *
     * @param incomingEvent  The incoming event that caused the exception
     * @param eventException The exception that was thrown
     * @return the exception event
     */
    private Set<EnEvent> createExceptionEvent(final EnEvent incomingEvent, final Exception eventException) {
        // The exception event is a clone of the incoming event with the exception suffix added to
        // its name and an extra
        // field "ExceptionMessage" added
        final EnEvent exceptionEvent = (EnEvent) incomingEvent.clone();

        // Create the cascaded message string
        final var exceptionMessageStringBuilder = new StringBuilder();
        exceptionMessageStringBuilder.append(eventException.getMessage());

        Throwable subException = eventException.getCause();
        while (subException != null) {
            exceptionMessageStringBuilder.append("\ncaused by: ");
            exceptionMessageStringBuilder.append(subException.getMessage());
            subException = subException.getCause();
        }

        // Set the exception message on the event
        exceptionEvent.setExceptionMessage(exceptionMessageStringBuilder.toString());

        return Set.of(exceptionEvent);
    }

    /**
     * Update the APEX engine state to prometheus for monitoring.
     */
    private void updateStatePrometheusMetric() {
        ENGINE_STATE.labelValues(getKey().getId()).set(state.getStateIdentifier());
    }
}