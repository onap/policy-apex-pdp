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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

public class JavescriptExecutorTest {

    @Test
    public void testJavescriptExecutorConcurrencyNormal() throws StateMachineException, IOException {
        JavascriptExecutor.setTimeunit4Latches(TimeUnit.SECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(60);
        JavascriptExecutor.setCleanupLatchTimeout(10);

        JavascriptExecutor executor = new JavascriptExecutor(new AxArtifactKey("executor:0.0.1"));

        assertThatThrownBy(() -> {
            executor.init(null);
        }).hasMessage("javascriptCode is marked non-null but is null");

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
        }).hasMessage("JavascriptExecutor executor:0.0.1 initiation timed out");

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
        }).hasMessage("JavascriptExecutor executor:0.0.1 cleanup timed out");

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

        JavascriptExecutor.setTimeunit4Latches(TimeUnit.SECONDS);
        JavascriptExecutor.setIntializationLatchTimeout(60);
        JavascriptExecutor.setCleanupLatchTimeout(10);
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
        await().atMost(10, TimeUnit.MILLISECONDS);

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
}
