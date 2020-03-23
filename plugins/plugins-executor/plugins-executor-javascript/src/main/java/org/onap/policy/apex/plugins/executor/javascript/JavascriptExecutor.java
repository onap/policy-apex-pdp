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
import java.util.concurrent.atomic.AtomicReference;

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

    // Recurring string constants
    private static final String WITH_MESSAGE = " with message: ";

    // The key of the subject that wants to execute Javascript code
    final AxKey subjectKey;

    private String javascriptCode;
    private Context javascriptContext;
    private Script script;

    private final BlockingQueue<Object> executionQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Boolean> resultQueue = new LinkedBlockingQueue<>();

    private final Thread executorThread;
    private CountDownLatch intializationLatch = new CountDownLatch(1);
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private AtomicReference<StateMachineException> executorException = new AtomicReference<>(null);

    /**
     * Initializes the Javascript executor.
     *
     * @param subjectKey the key of the subject that is requesting Javascript execution
     */
    public JavascriptExecutor(final AxKey subjectKey) {
        this.subjectKey = subjectKey;

        executorThread = new Thread(this);
        executorThread.setName(this.getClass().getSimpleName() + ":" + subjectKey.getId());
    }

    /**
     * Prepares the executor for processing and compiles the Javascript code.
     *
     * @param javascriptCode the Javascript code to execute
     * @throws StateMachineException thrown when instantiation of the executor fails
     */
    public void init(final String javascriptCode) throws StateMachineException {
        LOGGER.debug("JavascriptExecutor {} starting ... ", subjectKey.getId());

        if (executorThread.isAlive()) {
            throw new StateMachineException(
                "initiation failed, executor " + subjectKey.getId() + " is already running");
        }

        if (StringUtils.isEmpty(javascriptCode)) {
            throw new StateMachineException("no logic specified for " + subjectKey.getId());
        }

        this.javascriptCode = javascriptCode;

        try {
            executorThread.start();
        } catch (Exception e) {
            throw new StateMachineException("initiation failed, executor " + subjectKey.getId() + " failed to start",
                e);
        }

        try {
            intializationLatch.await();
        } catch (InterruptedException e) {
            LOGGER.debug("JavascriptExecutor {} interrupted on execution thread startup", subjectKey.getId(), e);
            Thread.currentThread().interrupt();
        }

        if (executorException.get() != null) {
            clearAndThrowExecutorException();
        }

        LOGGER.debug("JavascriptExecutor {} started ... ", subjectKey.getId());
    }

    /**
     * Execute a Javascript script.
     *
     * @param executionContext the execution context to use for script execution
     * @return true if execution was successful, false otherwise
     * @throws StateMachineException on execution errors
     */
    public boolean execute(final Object executionContext) throws StateMachineException {
        if (!executorThread.isAlive()) {
            throw new StateMachineException("execution failed, executor " + subjectKey.getId() + " is not running");
        }

        executionQueue.add(executionContext);

        boolean result = false;

        try {
            result = resultQueue.take();
        } catch (final InterruptedException e) {
            LOGGER.debug("JavascriptExecutor {} interrupted on execution result wait", subjectKey.getId(), e);
            Thread.currentThread().interrupt();
        }

        if (executorException.get() != null) {
            clearAndThrowExecutorException();
        }

        return result;
    }

    /**
     * Cleans up the executor after processing.
     *
     * @throws StateMachineException thrown when cleanup of the executor fails
     */
    public void cleanUp() throws StateMachineException {
        if (!executorThread.isAlive()) {
            throw new StateMachineException("cleanup failed, executor " + subjectKey.getId() + " is not running");
        }

        executorThread.interrupt();

        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.debug("JavascriptExecutor {} interrupted on execution clkeanup wait", subjectKey.getId(), e);
            Thread.currentThread().interrupt();
        }

        if (executorException.get() != null) {
            clearAndThrowExecutorException();
        }
    }

    @Override
    public void run() {
        LOGGER.debug("JavascriptExecutor {} initializing ... ", subjectKey.getId());

        try {
            initExecutor();
        } catch (StateMachineException sme) {
            LOGGER.warn("JavascriptExecutor {} initialization failed", sme);
            executorException.set(sme);
            intializationLatch.countDown();
            return;
        }

        intializationLatch.countDown();

        LOGGER.debug("JavascriptExecutor {} executing ... ", subjectKey.getId());

        // Take jobs from the execution queue of the worker and execute them
        while (!executorThread.isInterrupted()) {
            try {
                Object contextObject = executionQueue.take();
                if (contextObject == null) {
                    break;
                }

                resultQueue.add(executeScript(contextObject));
            } catch (final InterruptedException e) {
                LOGGER.debug("execution was interruped for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
                resultQueue.add(false);
                Thread.currentThread().interrupt();
            } catch (StateMachineException sme) {
                executorException.set(sme);
                resultQueue.add(false);
            }
        }

        try {
            Context.exit();
        } catch (final Exception e) {
            executorException.set(new StateMachineException(
                "executor close failed to close for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e));
        }

        shutdownLatch.countDown();

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

    private void clearAndThrowExecutorException() throws StateMachineException {
        StateMachineException exceptionToThrow = executorException.getAndSet(null);
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
    }

    protected Thread getExecutorThread() {
        return executorThread;
    }
}
