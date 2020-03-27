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

    // Recurring string constants
    private static final String WITH_MESSAGE = " with message: ";

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
    public void init(@NonNull final String javascriptCode) throws StateMachineException {
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
                throw new StateMachineException("JavascriptExecutor " + subjectKey.getId()
                    + " initiation timed out after " + intializationLatchTimeout + " " + timeunit4Latches);
            }
        } catch (InterruptedException e) {
            LOGGER.debug("JavascriptExecutor {} interrupted on execution thread startup", subjectKey.getId(), e);
            Thread.currentThread().interrupt();
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
    public boolean execute(final Object executionContext) throws StateMachineException {
        if (executorThread == null) {
            throw new StateMachineException("execution failed, executor " + subjectKey.getId() + " is not initialized");
        }

        if (!executorThread.isAlive()) {
            throw new StateMachineException("execution failed, executor " + subjectKey.getId()
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
                "JavascriptExecutor " + subjectKey.getId() + "interrupted on execution result wait", e);
        }

        checkAndThrowExecutorException();

        return result;
    }

    /**
     * Cleans up the executor after processing.
     *
     * @throws StateMachineException thrown when cleanup of the executor fails
     */
    public void cleanUp() throws StateMachineException {
        if (executorThread == null) {
            throw new StateMachineException("cleanup failed, executor " + subjectKey.getId() + " is not initialized");
        }

        if (executorThread.isAlive()) {
            executorThread.interrupt();

            try {
                if (!cleanupLatch.await(cleanupLatchTimeout, timeunit4Latches)) {
                    executorException.set(new StateMachineException("JavascriptExecutor " + subjectKey.getId()
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

                boolean result = executeScript(contextObject);
                resultQueue.add(result);
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
            throw exceptionToThrow;
        }
    }
}
