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

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;

/**
 * This interface defines what operations must be provided by an executing entity in Apex. It is
 * implemented by classes that execute logic in a state machine. Each executor has an incoming
 * entity {@code IN} that triggers execution, an outgoing entity {@code OUT} that is produced by
 * execution, a subject {@code SUBJECT} that is being executed, and a context {@code CONTEXT} in
 * which execution is being carried out. An executor can be part of a chain of executors and the
 * {@code setNext} method is used to set the next executor to be executed after this executor has
 * completed.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 *
 * @param <I> type of the incoming entity
 * @param <O> type of the outgoing entity
 * @param <S> type that is the subject of execution
 * @param <C> context holding the context of execution
 */

public interface Executor<I, O, S, C> {
    /**
     * Save the subject and context of the executor.
     *
     * @param parent the parent executor of this executor or null if this executor is the top
     *        executor
     * @param executorSubject the executor subject, the subject of execution
     * @param executorContext the executor context, the context in which execution takes place
     */
    void setContext(Executor<?, ?, ?, ?> parent, S executorSubject, C executorContext);

    /**
     * Prepares the processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    void prepare() throws StateMachineException;

    /**
     * Executes the executor, running through its context in its natural order.
     *
     * @param executionId the execution ID of the current APEX execution policy thread
     * @param incomingEntity the incoming entity that triggers execution
     * @return The outgoing entity that is the result of execution
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    O execute(long executionId, I incomingEntity) throws ApexException;

    /**
     * Carry out the preparatory work for execution.
     *
     * @param executionId the execution ID of the current APEX execution policy thread
     * @param incomingEntity the incoming entity that triggers execution
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    void executePre(long executionId, I incomingEntity) throws ApexException;

    /**
     * Carry out the post work for execution, the returning entity should be set by the child
     * execution object.
     *
     * @param returnValue the return value indicates whether the execution was successful and, if it
     *        failed, how it failed
     * @throws StateMachineException on an execution error
     * @throws ContextException On context errors
     */
    void executePost(boolean returnValue) throws ApexException;

    /**
     * Cleans up after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    void cleanUp() throws StateMachineException;

    /**
     * Get the key associated with the executor.
     *
     * @return The key associated with the executor
     */
    AxConcept getKey();

    /**
     * Get the parent executor of the executor.
     *
     * @return The parent executor of this executor
     */
    Executor<?, ?, ?, ?> getParent();

    /**
     * Get the subject of the executor.
     *
     * @return The subject for the executor
     */
    S getSubject();

    /**
     * Get the context of the executor.
     *
     * @return The context for the executor
     */
    C getContext();

    /**
     * Get the incoming object of the executor.
     *
     * @return The incoming object for the executor
     */
    I getIncoming();

    /**
     * Get the outgoing object of the executor.
     *
     * @return The outgoing object for the executor
     */
    O getOutgoing();

    /**
     * Save the next executor for this executor.
     *
     * @param nextExecutor the next executor
     */
    void setNext(Executor<I, O, S, C> nextExecutor);

    /**
     * Get the next executor to be run after this executor completes its execution.
     *
     * @return The next executor
     */
    Executor<I, O, S, C> getNext();

    /**
     * Set parameters for this executor, overloaded by executors that use parameters.
     *
     * @param parameters executor parameters
     */
    void setParameters(ExecutorParameters parameters);
}
