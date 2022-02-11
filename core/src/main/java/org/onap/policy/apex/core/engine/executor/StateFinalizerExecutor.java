/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

import static org.onap.policy.common.utils.validation.Assertions.argumentOfClassNotNull;

import java.util.Map;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.context.StateFinalizerExecutionContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This abstract class executes state finalizer logic in a state of an Apex policy and is specialized by classes that
 * implement execution of state finalizer logic.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class StateFinalizerExecutor
        implements Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(StateFinalizerExecutor.class);

    // Repeated string constants
    private static final String EXECUTE_POST_SFL = "execute-post: state finalizer logic \"";

    // Hold the state and context definitions
    @Getter
    private Executor<?, ?, ?, ?> parent = null;
    private AxState axState = null;
    private AxStateFinalizerLogic finalizerLogic = null;
    private ApexInternalContext internalContext = null;

    // Holds the incoming and outgoing fields
    private Map<String, Object> incomingFields = null;

    // The next state finalizer executor
    private Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> nextExecutor = null;

    // The execution context; contains the facades for events and context to be used by tasks
    // executed by this task
    // executor
    @Getter(AccessLevel.PROTECTED)
    private StateFinalizerExecutionContext executionContext = null;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setContext(final Executor<?, ?, ?, ?> incomingParent,
            final AxStateFinalizerLogic incomingFinalizerLogic, final ApexInternalContext incomingInternalContext) {
        this.parent = incomingParent;
        axState = (AxState) parent.getSubject();
        this.finalizerLogic = incomingFinalizerLogic;
        this.internalContext = incomingInternalContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void prepare() throws StateMachineException {
        LOGGER.debug("prepare:" + finalizerLogic.getId() + "," + finalizerLogic.getLogicFlavour() + ","
                + finalizerLogic.getLogic());
        argumentOfClassNotNull(finalizerLogic.getLogic(), StateMachineException.class,
                "state finalizer logic cannot be null.");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String execute(final long executionId, final Properties executionProperties,
            final Map<String, Object> newIncomingFields) throws StateMachineException, ContextException {
        throw new StateMachineException("execute() not implemented on abstract StateFinalizerExecutionContext class, "
                + "only on its subclasses");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePre(final long executionId, @NonNull final Properties executionProperties,
            final Map<String, Object> newIncomingFields) throws StateMachineException, ContextException {
        LOGGER.debug("execute-pre:" + finalizerLogic.getLogicFlavour() + "," + getSubject().getId() + ","
                + finalizerLogic.getLogic());

        // Record the incoming fields
        this.incomingFields = newIncomingFields;

        // Get state finalizer context object
        executionContext = new StateFinalizerExecutionContext(this, executionId, executionProperties, axState,
                getIncoming(), axState.getStateOutputs().keySet(), getContext());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePost(final boolean returnValue) throws StateMachineException, ContextException {
        if (!returnValue) {
            String errorMessage = "execute-post: state finalizer logic execution failure on state \"" + axState.getId()
                    + "\" on finalizer logic " + finalizerLogic.getId();
            if (executionContext.getMessage() != null) {
                errorMessage += ", user message: " + executionContext.getMessage();
            }
            LOGGER.warn(errorMessage);
            throw new StateMachineException(errorMessage);
        }

        // Check a state output has been selected
        if (getOutgoing() == null) {
            String message = EXECUTE_POST_SFL + finalizerLogic.getId() + "\" did not select an output state";
            LOGGER.warn(message);
            throw new StateMachineException(message);
        }

        if (!axState.getStateOutputs().keySet().contains(getOutgoing())) {
            LOGGER.warn(EXECUTE_POST_SFL + finalizerLogic.getId() + "\" selected output state \"" + getOutgoing()
                    + "\" that does not exsist on state \"" + axState.getId() + "\"");
            throw new StateMachineException(EXECUTE_POST_SFL + finalizerLogic.getId() + "\" selected output state \""
                    + getOutgoing() + "\" that does not exsist on state \"" + axState.getId() + "\"");
        }

        LOGGER.debug("execute-post:{}, returning  state output \"{}\" and fields {}", finalizerLogic.getId(),
                getOutgoing(), incomingFields);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cleanUp() throws StateMachineException {
        throw new StateMachineException("cleanUp() not implemented on class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxReferenceKey getKey() {
        return finalizerLogic.getKey();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxStateFinalizerLogic getSubject() {
        return finalizerLogic;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexInternalContext getContext() {
        return internalContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Object> getIncoming() {
        return incomingFields;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getOutgoing() {
        if (executionContext != null) {
            return executionContext.getSelectedStateOutputName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setNext(
            final Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> inNextEx) {
        this.nextExecutor = inNextEx;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> getNext() {
        return nextExecutor;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setParameters(final ExecutorParameters parameters) {
        // Not used
    }
}
