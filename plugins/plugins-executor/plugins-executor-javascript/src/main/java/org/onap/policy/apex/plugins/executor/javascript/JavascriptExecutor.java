/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.javascript;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JavascriptExecutor is the executor for task logic written in Javascript.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavascriptExecutor implements Runnable {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavascriptExecutor.class);

    public static final int DEFAULT_OPTIMIZATION_LEVEL = 9;

    // Token passed to executor thread to stop execution
    private static final Object STOP_EXECUTION_TOKEN = "*** STOP EXECUTION ***";

    // Recurring string constants
    private static final String WITH_MESSAGE = " with message: ";
    private static final String JAVASCRIPT_EXECUTOR = "JavascriptExecutor ";
    private static final String EXECUTION_FAILED_EXECUTOR = "execution failed, executor ";

    @Setter(AccessLevel.PROTECTED)
    private static TimeUnit timeunit4Latches = TimeUnit.SECONDS;
    @Setter(AccessLevel.PROTECTED)
    private static int intializationLatchTimeout = 60;
    @Setter(AccessLevel.PROTECTED)
    private static int cleanupLatchTimeout = 60;

    // The key of the subject that wants to execute Javascript code
    final AxKey subjectKey;

    private String javascriptCode;
    private Context javascriptContext;
    private Script script;

    private final BlockingQueue<Object> executionQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Boolean> resultQueue = new LinkedBlockingQueue<>();

    @Getter(AccessLevel.PROTECTED)
    private Thread executorThread;
    private CountDownLatch intializationLatch;
    private CountDownLatch cleanupLatch;
    private AtomicReference<StateMachineException> executorException = new AtomicReference<>(null);

    /**
     * Initializes the Javascript executor.
     *
     * @param subjectKey the key of the subject that is requesting Javascript execution
     */
    public JavascriptExecutor(final AxKey subjectKey) {
        this.subjectKey = subjectKey;
    }

    /**
     * Prepares the executor for processing and compiles the Javascript code.
     *
     * @param javascriptCode the Javascript code to execute
     * @throws StateMachineException thrown when instantiation of the executor fails
     */
    public synchronized void init(@NonNull final String javascriptCode) throws StateMachineException {
        LOGGER.debug("JavascriptExecutor {} starting ... ", subjectKey.getId());

        if (executorThread != null) {
            throw new StateMachineException("initiation failed, executor " + subjectKey.getId()
                + " already initialized, run cleanUp to clear executor");
        }

        if (StringUtils.isBlank(javascriptCode)) {
            throw new StateMachineException("initiation failed, no logic specified for executor " + subjectKey.getId());
        }

        this.javascriptCode = javascriptCode;

        executorThread = new Thread(this);
        executorThread.setName(this.getClass().getSimpleName() + ":" + subjectKey.getId());
        intializationLatch = new CountDownLatch(1);
        cleanupLatch = new CountDownLatch(1);

        try {
            executorThread.start();
        } catch (IllegalThreadStateException e) {
            throw new StateMachineException("initiation failed, executor " + subjectKey.getId() + " failed to start",
                e);
        }

        try {
            if (!intializationLatch.await(intializationLatchTimeout, timeunit4Latches)) {
                executorThread.interrupt();
                throw new StateMachineException(JAVASCRIPT_EXECUTOR + subjectKey.getId()
                    + " initiation timed out after " + intializationLatchTimeout + " " + timeunit4Latches);
            }
        } catch (InterruptedException e) {
            LOGGER.debug("JavascriptExecutor {} interrupted on execution thread startup", subjectKey.getId(), e);
            Thread.currentThread().interrupt();
        }

        if (executorException.get() != null) {
            executorThread.interrupt();
            checkAndThrowExecutorException();
        }

        checkAndThrowExecutorException();

        LOGGER.debug("JavascriptExecutor {} started ... ", subjectKey.getId());
    }

    /**
     * Execute a Javascript script.
     *
     * @param executionContext the execution context to use for script execution
     * @return true if execution was successful, false otherwise
     * @throws StateMachineException on execution errors
     */
    public synchronized boolean execute(final Object executionContext) throws StateMachineException {
        if (executorThread == null) {
            throw new StateMachineException(EXECUTION_FAILED_EXECUTOR + subjectKey.getId() + " is not initialized");
        }

        if (!executorThread.isAlive() || executorThread.isInterrupted()) {
            throw new StateMachineException(EXECUTION_FAILED_EXECUTOR + subjectKey.getId()
                + " is not running, run cleanUp to clear executor and init to restart executor");
        }

        executionQueue.add(executionContext);

        boolean result = false;

        try {
            result = resultQueue.take();
        } catch (final InterruptedException e) {
            executorThread.interrupt();
            Thread.currentThread().interrupt();
            throw new StateMachineException(
                JAVASCRIPT_EXECUTOR + subjectKey.getId() + "interrupted on execution result wait", e);
        }

        checkAndThrowExecutorException();

        return result;
    }

    /**
     * Cleans up the executor after processing.
     *
     * @throws StateMachineException thrown when cleanup of the executor fails
     */
    public synchronized void cleanUp() throws StateMachineException {
        if (executorThread == null) {
            throw new StateMachineException("cleanup failed, executor " + subjectKey.getId() + " is not initialized");
        }

        if (executorThread.isAlive()) {
            executionQueue.add(STOP_EXECUTION_TOKEN);

            try {
                if (!cleanupLatch.await(cleanupLatchTimeout, timeunit4Latches)) {
                    executorException.set(new StateMachineException(JAVASCRIPT_EXECUTOR + subjectKey.getId()
                        + " cleanup timed out after " + cleanupLatchTimeout + " " + timeunit4Latches));
                }
            } catch (InterruptedException e) {
                LOGGER.debug("JavascriptExecutor {} interrupted on execution cleanup wait", subjectKey.getId(), e);
                Thread.currentThread().interrupt();
            }
        }

        executorThread = null;
        executionQueue.clear();
        resultQueue.clear();

        checkAndThrowExecutorException();
    }

    @Override
    public void run() {
        LOGGER.debug("JavascriptExecutor {} initializing ... ", subjectKey.getId());

        try {
            initExecutor();
        } catch (StateMachineException sme) {
            LOGGER.warn("JavascriptExecutor {} initialization failed", subjectKey.getId(), sme);
            executorException.set(sme);
            intializationLatch.countDown();
            cleanupLatch.countDown();
            return;
        }

        intializationLatch.countDown();

        LOGGER.debug("JavascriptExecutor {} executing ... ", subjectKey.getId());

        // Take jobs from the execution queue of the worker and execute them
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Object contextObject = executionQueue.take();
                if (STOP_EXECUTION_TOKEN.equals(contextObject)) {
                    LOGGER.debug("execution close was ordered for  " + subjectKey.getId());
                    break;
                }
                resultQueue.add(executeScript(contextObject));
            } catch (final InterruptedException e) {
                LOGGER.debug("execution was interruped for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
                executionQueue.add(STOP_EXECUTION_TOKEN);
                Thread.currentThread().interrupt();
            } catch (StateMachineException sme) {
                executorException.set(sme);
                resultQueue.add(false);
            }
        }

        resultQueue.add(false);

        try {
            Context.exit();
        } catch (final Exception e) {
            executorException.set(new StateMachineException(
                "executor close failed to close for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e));
        }

        cleanupLatch.countDown();

        LOGGER.debug("JavascriptExecutor {} completed processing", subjectKey.getId());
    }

    private void initExecutor() throws StateMachineException {
        try {
            // Create a Javascript context for this thread
            javascriptContext = Context.enter();

            // Set up the default values of the context
            javascriptContext.setOptimizationLevel(DEFAULT_OPTIMIZATION_LEVEL);
            javascriptContext.setLanguageVersion(Context.VERSION_1_8);

            script = javascriptContext.compileString(javascriptCode, subjectKey.getId(), 1, null);
        } catch (Exception e) {
            Context.exit();
            throw new StateMachineException(
                "logic failed to compile for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
        }
    }

    private boolean executeScript(final Object executionContext) throws StateMachineException {
        Object returnObject = null;

        try {
            // Pass the subject context to the Javascript engine
            Scriptable javascriptScope = javascriptContext.initStandardObjects();
            javascriptScope.put("executor", javascriptScope, executionContext);

            // Run the script
            returnObject = script.exec(javascriptContext, javascriptScope);
        } catch (final Exception e) {
            throw new StateMachineException(
                "logic failed to run for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
        }

        if (!(returnObject instanceof Boolean)) {
            throw new StateMachineException(
                "execute: logic for " + subjectKey.getId() + " returned a non-boolean value " + returnObject);
        }

        return (boolean) returnObject;
    }

    private void checkAndThrowExecutorException() throws StateMachineException {
        StateMachineException exceptionToThrow = executorException.getAndSet(null);
        if (exceptionToThrow != null) {
            LOGGER.debug("JavascriptExecutor {} throwing exception ", exceptionToThrow);
            throw exceptionToThrow;
        }
    }
}
