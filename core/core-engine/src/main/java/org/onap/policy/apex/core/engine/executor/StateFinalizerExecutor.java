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

package org.onap.policy.apex.core.engine.executor;

import static org.onap.policy.apex.model.utilities.Assertions.argumentOfClassNotNull;

import java.util.Map;

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
    private StateFinalizerExecutionContext executionContext = null;

    /**
     * Gets the execution internalContext.
     *
     * @return the execution context
     */
    protected StateFinalizerExecutionContext getExecutionContext() {
        return executionContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#setContext(org.onap.policy.apex.core.
     * engine.executor.Executor, java.lang.Object, java.lang.Object)
     */
    @Override
    public void setContext(final Executor<?, ?, ?, ?> incomingParent,
                    final AxStateFinalizerLogic incomingFinalizerLogic,
                    final ApexInternalContext incomingInternalContext) {
        this.parent = incomingParent;
        axState = (AxState) parent.getSubject();
        this.finalizerLogic = incomingFinalizerLogic;
        this.internalContext = incomingInternalContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#prepare()
     */
    @Override
    public void prepare() throws StateMachineException {
        LOGGER.debug("prepare:" + finalizerLogic.getId() + "," + finalizerLogic.getLogicFlavour() + ","
                        + finalizerLogic.getLogic());
        argumentOfClassNotNull(finalizerLogic.getLogic(), StateMachineException.class,
                        "state finalizer logic cannot be null.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#execute(java.lang.long, java.lang.Object)
     */
    @Override
    public String execute(final long executionId, final Map<String, Object> newIncomingFields)
                    throws StateMachineException, ContextException {
        throw new StateMachineException("execute() not implemented on abstract StateFinalizerExecutionContext class, "
                        + "only on its subclasses");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#executePre(java.lang.long, java.lang.Object)
     */
    @Override
    public final void executePre(final long executionId, final Map<String, Object> newIncomingFields)
                    throws StateMachineException, ContextException {
        LOGGER.debug("execute-pre:" + finalizerLogic.getLogicFlavour() + "," + getSubject().getId() + ","
                        + finalizerLogic.getLogic());

        // Record the incoming fields
        this.incomingFields = newIncomingFields;

        // Get state finalizer context object
        executionContext = new StateFinalizerExecutionContext(this, executionId, axState, getIncoming(),
                        axState.getStateOutputs().keySet(), getContext());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#executePost(boolean)
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

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#cleanUp()
     */
    @Override
    public void cleanUp() throws StateMachineException {
        throw new StateMachineException("cleanUp() not implemented on class");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getKey()
     */
    @Override
    public AxReferenceKey getKey() {
        return finalizerLogic.getKey();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getParent()
     */
    @Override
    public Executor<?, ?, ?, ?> getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getSubject()
     */
    @Override
    public AxStateFinalizerLogic getSubject() {
        return finalizerLogic;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getContext()
     */
    @Override
    public ApexInternalContext getContext() {
        return internalContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getIncoming()
     */
    @Override
    public Map<String, Object> getIncoming() {
        return incomingFields;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getOutgoing()
     */
    @Override
    public String getOutgoing() {
        if (executionContext != null) {
            return executionContext.getSelectedStateOutputName();
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#setNext(org.onap.policy.apex.core.engine.
     * executor.Executor)
     */
    @Override
    public void setNext(
                    final Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> inNextEx) {
        this.nextExecutor = inNextEx;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getNext()
     */
    @Override
    public Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> getNext() {
        return nextExecutor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#setParameters(org.onap.policy.apex.core. engine.
     * ExecutorParameters)
     */
    @Override
    public void setParameters(final ExecutorParameters parameters) {
    }
}
