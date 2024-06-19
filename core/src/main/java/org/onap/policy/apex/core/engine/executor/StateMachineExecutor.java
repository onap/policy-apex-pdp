/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.core.engine.executor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import lombok.Getter;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;

/**
 * This class is the executor for a state machine built from a policy.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class StateMachineExecutor implements Executor<EnEvent, Collection<EnEvent>, AxPolicy, ApexInternalContext> {
    // The Apex Policy and context for this state machine
    private AxPolicy axPolicy = null;
    @Getter
    private Executor<?, ?, ?, ?> parent = null;
    private ApexInternalContext internalContext = null;

    // The list of state executors for this state machine
    private final Map<AxReferenceKey, StateExecutor> stateExecutorMap = new TreeMap<>();

    // The first executor
    private StateExecutor firstExecutor = null;

    // The next state machine executor
    private Executor<EnEvent, Collection<EnEvent>, AxPolicy, ApexInternalContext> nextExecutor = null;

    // The executor factory
    private ExecutorFactory executorFactory = null;

    /**
     * Constructor, save the executor factory that will give us executors for task selection logic and task logic.
     *
     * @param executorFactory the executor factory
     * @param owner the artifact key of the owner of this state machine
     */
    public StateMachineExecutor(final ExecutorFactory executorFactory, final AxArtifactKey owner) {
        this.executorFactory = executorFactory;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setContext(final Executor<?, ?, ?, ?> newParent, final AxPolicy newAxPolicy,
            final ApexInternalContext newInternalContext) {
        // Save the policy and context for this state machine
        this.parent = newParent;
        this.axPolicy = newAxPolicy;
        this.internalContext = newInternalContext;

        // Clear the first executor, setContext can be called multiple times
        firstExecutor = null;

        // Create the state executors for this state machine
        StateExecutor lastExecutor = null;
        for (final AxState state : axPolicy.getStateMap().values()) {
            // Create a state executor for this state and add its context (the state)
            final var stateExecutor = new StateExecutor(executorFactory);
            stateExecutor.setContext(this, state, internalContext);

            // Update the next executor on the last executor
            if (lastExecutor != null) {
                lastExecutor.setNext(stateExecutor);
            }
            lastExecutor = stateExecutor;

            // Add the state executor to the executor list
            stateExecutorMap.put(state.getKey(), stateExecutor);

            // Set the first executor if it is not set
            if (state.getKey().getLocalName().equals(axPolicy.getFirstState())) {
                firstExecutor = stateExecutor;
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void prepare() throws StateMachineException {
        for (final StateExecutor stateExecutor : stateExecutorMap.values()) {
            stateExecutor.prepare();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<EnEvent> execute(final long executionId, final Properties executionProperties,
        final EnEvent incomingEvent) throws StateMachineException, ContextException {
        // Check if there are any states on the state machine
        if (stateExecutorMap.isEmpty()) {
            throw new StateMachineException("no states defined on state machine");
        }

        // Check if the first state of the machine is defined
        if (firstExecutor == null) {
            throw new StateMachineException("first state not defined on state machine");
        }

        // Get the first state of the state machine and define a state output that starts state
        // execution
        var stateExecutor = firstExecutor;
        var stateOutput = new StateOutput(new AxStateOutput(firstExecutor.getSubject().getKey(),
                incomingEvent.getKey(), firstExecutor.getSubject().getKey()), incomingEvent);

        while (true) {
            // OutputEventSet in a stateoutput can contain multiple events only when it is of the final state
            // otherwise, there can be only 1 item in outputEventSet
            stateOutput = stateExecutor.execute(executionId, executionProperties,
                stateOutput.getOutputEvents().values().iterator().next());

            // Use the next state of the state output to find if all the states have executed
            if (stateOutput.getNextState().equals(AxReferenceKey.getNullKey())) {
                break;
            }

            // Use the next state of the state output to find the next state
            stateExecutor = stateExecutorMap.get(stateOutput.getNextState());
            if (stateExecutor == null) {
                throw new StateMachineException(
                        "state execution failed, next state \"" + stateOutput.getNextState().getId() + "\" not found");
            }
        }

        return stateOutput.getOutputEvents().values();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePre(final long executionId, final Properties executionProperties,
            final EnEvent incomingEntity) throws StateMachineException {
        throw new StateMachineException("execution pre work not implemented on class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePost(final boolean returnValue) throws StateMachineException {
        throw new StateMachineException("execution post work not implemented on class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cleanUp() throws StateMachineException {
        for (final StateExecutor stateExecutor : stateExecutorMap.values()) {
            stateExecutor.cleanUp();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getKey() {
        return axPolicy.getKey();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final AxPolicy getSubject() {
        return axPolicy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final ApexInternalContext getContext() {
        return internalContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final EnEvent getIncoming() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final Collection<EnEvent> getOutgoing() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void setNext(
        final Executor<EnEvent, Collection<EnEvent>, AxPolicy, ApexInternalContext> newNextExecutor) {
        this.nextExecutor = newNextExecutor;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final Executor<EnEvent, Collection<EnEvent>, AxPolicy, ApexInternalContext> getNext() {
        return nextExecutor;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setParameters(final ExecutorParameters parameters) {
        // Not implemented in this class
    }
}
