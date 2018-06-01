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

import static org.onap.policy.apex.model.utilities.Assertions.argumentNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.engine.ApexEngine;
import org.onap.policy.apex.core.engine.engine.EnEventListener;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineStats;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
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

    // The artifact key of this engine
    private final AxArtifactKey key;

    // The state of this engine
    private AxEngineState state = AxEngineState.STOPPED;

    // call back listeners
    private final Map<String, EnEventListener> eventListeners = new LinkedHashMap<String, EnEventListener>();

    // The context of this engine
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

        LOGGER.entry("ApexEngine()->" + key.getID() + "," + state);

        this.key = key;

        // Set up statistics collection
        engineStats = new AxEngineStats();
        engineStats.setKey(new AxReferenceKey(key, "_EngineStats"));

        LOGGER.exit("ApexEngine()<-" + key.getID() + "," + state);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.engine.ApexEngine#updateModel(org.onap.policy.apex.model.policymodel.concepts.
     * AxPolicyModel)
     */
    @Override
    public void updateModel(final AxPolicyModel apexModel) throws ApexException {
        if (apexModel != null) {
            LOGGER.entry("updateModel()->" + key.getID() + ", apexPolicyModel=" + apexModel.getKey().getID());
        } else {
            LOGGER.warn("updateModel()<-" + key.getID() + ", Apex model not set");
            throw new ApexException(
                    "updateModel()<-" + key.getID() + ", Apex model is not defined, it has a null value");
        }

        // The engine must be stopped in order to do a model update
        if (!state.equals(AxEngineState.STOPPED)) {
            throw new ApexException("updateModel()<-" + key.getID()
                    + ", cannot update model, engine should be stopped but is in state " + state);
        }

        // Create new internal context or update the existing one
        try {
            if (internalContext == null) {
                /// New internal context
                internalContext = new ApexInternalContext(apexModel);
            } else {
                // Exiting internal context which must be updated
                internalContext.update(apexModel);
            }
        } catch (final ContextException e) {
            LOGGER.warn(
                    "updateModel()<-" + key.getID() + ", error setting the context for engine \"" + key.getID() + "\"",
                    e);
            throw new ApexException(
                    "updateModel()<-" + key.getID() + ", error setting the context for engine \"" + key.getID() + "\"",
                    e);
        }

        // Set up the state machines
        try {
            // We always set up state machines as new because it's only context that must be transferred; policies are
            // always set up as new
            stateMachineHandler = new StateMachineHandler(internalContext);
        } catch (final StateMachineException e) {
            LOGGER.warn("updateModel()<-" + key.getID() + ", error setting up the engine state machines \""
                    + key.getID() + "\"", e);
            throw new ApexException("updateModel()<-" + key.getID() + ", error setting up the engine state machines \""
                    + key.getID() + "\"", e);
        }

        LOGGER.exit("updateModel()<-" + key.getID());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#start()
     */
    @Override
    public void start() throws ApexException {
        LOGGER.entry("start()" + key);

        if (state != AxEngineState.STOPPED) {
            LOGGER.warn("start()<-" + key.getID() + "," + state + ", cannot start engine, engine not in state STOPPED");
            throw new ApexException(
                    "start()<-" + key.getID() + "," + state + ", cannot start engine, engine not in state STOPPED");
        }

        if (stateMachineHandler == null || internalContext == null) {
            LOGGER.warn("start()<-" + key.getID() + "," + state
                    + ", cannot start engine, engine has not been initialized, its model is not loaded");
            throw new ApexException("start()<-" + key.getID() + "," + state
                    + ",  cannot start engine, engine has not been initialized, its model is not loaded");
        }

        // Set up the state machines
        try {
            // Start the state machines
            stateMachineHandler.start();
            engineStats.engineStart();
        } catch (final StateMachineException e) {
            LOGGER.warn("updateModel()<-" + key.getID() + ", error starting the engine state machines \"" + key.getID()
                    + "\"", e);
            throw new ApexException("updateModel()<-" + key.getID() + ", error starting the engine state machines \""
                    + key.getID() + "\"", e);
        }

        // OK, we are good to go
        state = AxEngineState.READY;

        LOGGER.exit("start()" + key);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#stop()
     */
    @Override
    public void stop() throws ApexException {
        LOGGER.entry("stop()->" + key);

        // Stop the engine if it is in state READY, if it is in state EXECUTING, wait for execution to finish
        for (int increment = APEX_ENGINE_STOP_EXECUTION_WAIT_TIMEOUT; increment > 0; increment =
                APEX_ENGINE_STOP_EXECUTION_WAIT_INCREMENT) {
            synchronized (state) {
                switch (state) {
                    // Already stopped
                    case STOPPED:

                        throw new ApexException("stop()<-" + key.getID() + "," + state
                                + ", cannot stop engine, engine is already stopped");
                        // The normal case, the engine wasn't doing anything or it was executing
                    case READY:
                    case STOPPING:

                        state = AxEngineState.STOPPED;
                        stateMachineHandler.stop();
                        engineStats.engineStop();
                        LOGGER.exit("stop()" + key);
                        return;
                    // Engine is executing a policy, wait for it to stop
                    case EXECUTING:
                        state = AxEngineState.STOPPING;
                        break;
                    default:
                        throw new ApexException("stop()<-" + key.getID() + "," + state
                                + ", cannot stop engine, engine is in an undefined state");
                }
            }
        }

        throw new ApexException("stop()<-" + key.getID() + "," + state + ", cannot stop engine, engine stop timed out");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#clear()
     */
    @Override
    public void clear() throws ApexException {
        LOGGER.entry("clear()->" + key);
        if (state != AxEngineState.STOPPED) {
            throw new ApexException(
                    "clear" + "()<-" + key.getID() + "," + state + ", cannot clear engine, engine is not stopped");
        }

        // Clear everything
        stateMachineHandler = null;
        engineStats.clean();
        internalContext.clear();
        internalContext = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#createEvent(org.onap.policy.apex.core.model.concepts.
     * AxArtifactKey)
     */
    @Override
    public EnEvent createEvent(final AxArtifactKey eventKey) {
        if (state != AxEngineState.READY && state != AxEngineState.EXECUTING) {
            LOGGER.warn(
                    "createEvent()<-" + key.getID() + "," + state + ", cannot create event, engine not in state READY");
            return null;
        }

        try {
            // Create an event using the internal context
            return new EnEvent(eventKey);
        } catch (final Exception e) {
            LOGGER.warn("createEvent()<-" + key.getID() + "," + state + ", error on event creation", e);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.engine.ApexEngine#handleEvent(org.onap.policy.apex.core.engine.event.EnEvent)
     */
    @Override
    public boolean handleEvent(final EnEvent incomingEvent) {
        boolean ret = false;
        if (incomingEvent == null) {
            LOGGER.warn("handleEvent()<-" + key.getID() + "," + state + ", cannot run engine, incoming event is null");
            return ret;
        }

        synchronized (state) {
            if (state != AxEngineState.READY) {
                LOGGER.warn("handleEvent()<-" + key.getID() + "," + state
                        + ", cannot run engine, engine not in state READY");
                return ret;
            }

            state = AxEngineState.EXECUTING;
        }

        LOGGER.debug("execute(): triggered by event " + incomingEvent.toString());

        // By default we return a null event on errors
        EnEvent outgoingEvent = null;
        try {
            engineStats.executionEnter(incomingEvent.getKey());
            outgoingEvent = stateMachineHandler.execute(incomingEvent);
            engineStats.executionExit();
            ret = true;
        } catch (final StateMachineException e) {
            LOGGER.warn("handleEvent()<-" + key.getID() + "," + state + ", engine execution error: ", e);

            // Create an exception return event
            outgoingEvent = createExceptionEvent(incomingEvent, e);
        }

        // Publish the outgoing event
        try {
            synchronized (eventListeners) {
                if (eventListeners.isEmpty()) {
                    LOGGER.debug("handleEvent()<-" + key.getID() + "," + state
                            + ", There is no listener registered to recieve outgoing event: " + outgoingEvent);
                }
                for (final EnEventListener axEventListener : eventListeners.values()) {
                    axEventListener.onEnEvent(outgoingEvent);
                }
            }
        } catch (final ApexException e) {
            LOGGER.warn("handleEvent()<-" + key.getID() + "," + state + ", outgoing event publishing error: ", e);
            ret = false;
        }
        synchronized (state) {
            // Only go to READY if we are still in state EXECUTING, we could be in state STOPPING
            if (state == AxEngineState.EXECUTING) {
                state = AxEngineState.READY;
            }
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#addEventListener(java.lang.String,
     * org.onap.policy.apex.core.engine.engine.EnEventListener)
     */
    @Override
    public void addEventListener(final String listenerName, final EnEventListener listener) {
        eventListeners.put(listenerName, listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#removeEventListener(java.lang.String)
     */
    @Override
    public void removeEventListener(final String listenerName) {
        eventListeners.remove(listenerName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#getState()
     */
    @Override
    public final AxEngineState getState() {
        return state;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#getEngineStatus()
     */
    @Override
    public AxEngineModel getEngineStatus() {
        final AxEngineModel engineModel = new AxEngineModel(key);
        engineModel.setTimestamp(System.currentTimeMillis());
        engineModel.setState(state);
        engineModel.setStats(engineStats);
        return engineModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.engine.ApexEngine#getEngineRuntime()
     */
    @Override
    public Map<AxArtifactKey, Map<String, Object>> getEngineContext() {
        final Map<AxArtifactKey, Map<String, Object>> currentContext =
                new LinkedHashMap<AxArtifactKey, Map<String, Object>>();

        for (final Entry<AxArtifactKey, ContextAlbum> contextAlbumEntry : internalContext.getContextAlbums()
                .entrySet()) {
            currentContext.put(contextAlbumEntry.getKey(), contextAlbumEntry.getValue());
        }

        return currentContext;
    }

    /**
     * Get the internal context for the Apex engine.
     *
     * @return The Apex Internal Context
     */
    public ApexInternalContext getInternalContext() {
        return internalContext;
    }

    /**
     * Create an exception event from the incoming event including the exception information on the event.
     *
     * @param incomingEvent The incoming event that caused the exception
     * @param eventException The exception that was thrown
     * @return the exception event
     */
    private EnEvent createExceptionEvent(final EnEvent incomingEvent, final Exception eventException) {
        // The exception event is a clone of the incoming event with the exception suffix added to its name and an extra
        // field "ExceptionMessage" added
        final EnEvent exceptionEvent = (EnEvent) incomingEvent.clone();

        // Create the cascaded message string
        final StringBuilder exceptionMessageStringBuilder = new StringBuilder();
        exceptionMessageStringBuilder.append(eventException.getMessage());

        Throwable subException = eventException.getCause();
        while (subException != null) {
            exceptionMessageStringBuilder.append("\ncaused by: ");
            exceptionMessageStringBuilder.append(subException.getMessage());
            subException = subException.getCause();
        }

        // Set the exception message on the event
        exceptionEvent.setExceptionMessage(exceptionMessageStringBuilder.toString());

        return exceptionEvent;
    }
}
