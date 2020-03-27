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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

public class JavescriptExecutorTest {

    @Test
    public void testJavescriptExecutorConcurrency() throws StateMachineException, IOException {
        JavascriptExecutor executor = new JavascriptExecutor(new AxArtifactKey("executor:0.0.1"));

        // Try to interrupt initiation
        executor.init("var x = 1;");
        // executor.getExecutorThread().interrupt();

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).isInstanceOf(StateMachineException.class);

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).isInstanceOf(StateMachineException.class);

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).isInstanceOf(StateMachineException.class);

        assertThatThrownBy(() -> {
            executor.execute("Hello");
        }).isInstanceOf(StateMachineException.class);

        executor.cleanUp();
    }

}
