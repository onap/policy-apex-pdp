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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class JavascriptExecutorTest {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavascriptExecutorTest.class);

    private AtomicBoolean concurrentResult = new AtomicBoolean();

    @Before
    public void beforeSetTimeouts() {
        JavascriptExecutor.setTimeunit4Latches(TimeUnit.SECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(60);
        JavascriptExecutor.setCleanupLatchTimeout(10);
    }

    @Test
    public void testJavescriptExecutorConcurrencyNormal() throws StateMachineException, IOException {
        JavascriptExecutor.setTimeunit4Latches(TimeUnit.SECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(60);
        JavascriptExecutor.setCleanupLatchTimeout(10);

        JavascriptExecutor executor = new JavascriptExecutor(new AxArtifactKey("executor:0.0.1"));

        assertThatThrownBy(() -> {
            executor.init(null);
        }).hasMessageMatching("^javascriptCode is marked .*on.*ull but is null$");

        assertThatThrownBy(() -> {
            executor.init("   ");
        }).hasMessage("initiation failed, no logic specified for executor executor:0.0.1");

        assertThatCode(() -> {
            executor.init("var x = 1;");
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            executor.init("var x = 1;");
        }).hasMessage("initiation failed, executor executor:0.0.1 already initialized, run cleanUp to clear executor");

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            executor.cleanUp();
        }).hasMessage("cleanup failed, executor executor:0.0.1 is not initialized");

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).hasMessage("execution failed, executor executor:0.0.1 is not initialized");

        assertThatCode(() -> {
            executor.init("var x = 1;");
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).hasMessage(
            "execute: logic for executor:0.0.1 returned a non-boolean value org.mozilla.javascript.Undefined@0");

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).hasMessage(
            "execute: logic for executor:0.0.1 returned a non-boolean value org.mozilla.javascript.Undefined@0");

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).hasMessage(
            "execute: logic for executor:0.0.1 returned a non-boolean value org.mozilla.javascript.Undefined@0");

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).hasMessage(
            "execute: logic for executor:0.0.1 returned a non-boolean value org.mozilla.javascript.Undefined@0");

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            executor.cleanUp();
        }).hasMessage("cleanup failed, executor executor:0.0.1 is not initialized");

        assertThatThrownBy(() -> {
            executor.execute("hello");
        }).hasMessage("execution failed, executor executor:0.0.1 is not initialized");
    }

    @Test
    public void testJavescriptExecutorConcurrencyLatchTimeout() throws StateMachineException, IOException {
        JavascriptExecutor.setTimeunit4Latches(TimeUnit.MICROSECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(1);
        JavascriptExecutor.setCleanupLatchTimeout(10000000);

        JavascriptExecutor executor = new JavascriptExecutor(new AxArtifactKey("executor:0.0.1"));

        assertThatThrownBy(() -> {
            executor.init("var x = 1;");
        }).hasMessage("JavascriptExecutor executor:0.0.1 initiation timed out after 1 MICROSECONDS");

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        JavascriptExecutor.setTimeunit4Latches(TimeUnit.SECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(60);

        assertThatCode(() -> {
            executor.init("var x = 1;");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        JavascriptExecutor.setTimeunit4Latches(TimeUnit.MICROSECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(60000000);
        JavascriptExecutor.setCleanupLatchTimeout(1);

        assertThatCode(() -> {
            executor.init("var x = 1;");
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            executor.cleanUp();
        }).hasMessage("JavascriptExecutor executor:0.0.1 cleanup timed out after 1 MICROSECONDS");

        JavascriptExecutor.setCleanupLatchTimeout(10000000);
        assertThatThrownBy(() -> {
            executor.cleanUp();
        }).hasMessage("cleanup failed, executor executor:0.0.1 is not initialized");

        assertThatCode(() -> {
            executor.init("var x = 1;");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();
    }

    @Test
    public void testJavescriptExecutorBadStates() throws StateMachineException, IOException {
        JavascriptExecutor executor = new JavascriptExecutor(new AxArtifactKey("executor:0.0.1"));

        assertThatThrownBy(() -> {
            executor.execute("hello");
        }).hasMessage("execution failed, executor executor:0.0.1 is not initialized");

        assertThatThrownBy(() -> {
            executor.cleanUp();
        }).hasMessage("cleanup failed, executor executor:0.0.1 is not initialized");

        assertThatCode(() -> {
            executor.init("var x = 1;");
        }).doesNotThrowAnyException();

        executor.getExecutorThread().interrupt();
        await().atMost(10, TimeUnit.SECONDS).until(() -> !executor.getExecutorThread().isAlive());

        assertThatThrownBy(() -> {
            executor.execute("hello");
        }).hasMessage("execution failed, executor executor:0.0.1 is not running, "
            + "run cleanUp to clear executor and init to restart executor");

        assertThatThrownBy(() -> {
            executor.execute("hello");
        }).hasMessage("execution failed, executor executor:0.0.1 is not running, "
            + "run cleanUp to clear executor and init to restart executor");

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();
    }

    @Test
    public void testJavescriptExecutorExecution() throws StateMachineException, IOException {
        JavascriptExecutor executor = new JavascriptExecutor(new AxArtifactKey("executor:0.0.1"));

        assertThatCode(() -> {
            executor.init("true;");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            assertTrue(executor.execute("hello"));
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.init("false;");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            assertFalse(executor.execute("hello"));
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            executor.init("aaaaa = \"sss");
        }).hasMessage(
            "logic failed to compile for executor:0.0.1 with message: unterminated string literal (executor:0.0.1#1)");

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.init("true;");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            assertTrue(executor.execute("hello"));
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.init("throw \"this is an error\";");
        }).doesNotThrowAnyException();

        assertThatThrownBy(() -> {
            assertTrue(executor.execute("hello"));
        }).hasMessage("logic failed to run for executor:0.0.1 with message: this is an error (executor:0.0.1#1)");

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.init("var x = 0; while (x < 100) { x++; }; true;");
        }).doesNotThrowAnyException();

        concurrentResult.set(true);

        // Execute an infinite loop in Javascript
        (new Thread() {
            public void run() {
                try {
                    while (executor.execute("hello")) {
                        LOGGER.debug("test thread running . . .");
                        // Loop until interrupted
                    }
                } catch (StateMachineException e) {
                    LOGGER.debug("test thread caught exception", e);
                }
                concurrentResult.set(false);
                LOGGER.debug("test thread exited");
            }
        }).start();

        await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> executor.getExecutorThread().isAlive());

        executor.getExecutorThread().interrupt();

        await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> !concurrentResult.get());

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.init("true;");
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            assertTrue(executor.execute("hello"));
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            executor.init("x = 1; true;");
        }).doesNotThrowAnyException();

        concurrentResult.set(true);

        // Execute an infinite loop in Javascript
        Thread executionThread = new Thread() {
            public void run() {
                try {
                    while (executor.execute("hello")) {
                        ;
                    }
                } catch (StateMachineException e) {
                    ;
                }
            }
        };
        executionThread.start();

        executionThread.interrupt();

        await().atMost(300, TimeUnit.MILLISECONDS).until(() -> !executionThread.isAlive());
        await().atMost(300, TimeUnit.MILLISECONDS).until(() -> !executor.getExecutorThread().isAlive());

        assertThatCode(() -> {
            executor.cleanUp();
        }).doesNotThrowAnyException();
    }
}
