/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020, 2024 Nordix Foundation. All rights reserved.
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

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

class JavascriptExecutorTest {

    @Test
    void testReturnOK() throws StateMachineException {
        JavascriptExecutor executor = new JavascriptExecutor(
            new AxArtifactKey("TestTask:0.0.1"), "true;");
        assertThatCode(() -> executor.execute(new Object())).doesNotThrowAnyException();
    }

    @Test
    void testReturnNonBoolean() throws StateMachineException {
        JavascriptExecutor executor = new JavascriptExecutor(
            new AxArtifactKey("TestTask:0.0.1"), "var a = 1; a;");
        assertThatThrownBy(() -> executor.execute(new Object()))
            .hasMessageContaining("logic for TestTask:0.0.1 returned a non-boolean value");
    }

    @Test
    void testBlankLogic() {
        assertThatThrownBy(() -> new JavascriptExecutor(
            new AxArtifactKey("TestTask:0.0.1"), " "))
            .hasMessageContaining("no logic specified for TestTask:0.0.1");
    }

    @Test
    void testCompileFailed() {
        assertThatThrownBy(() -> new JavascriptExecutor(
            new AxArtifactKey("TestTask:0.0.1"), "return boolean;"))
            .hasMessageContaining("logic failed to compile for TestTask:0.0.1");
    }
}
