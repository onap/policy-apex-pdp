/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2022-2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.acm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;


/**
 * Test class to run an example policy for ACM interaction. Event received on
 * message topic (dummy REST Endpoint here) and triggers a new message.
 */
class TestApexAcmExample {

    @Test
    void testExample() {
        try {

            // @formatter:off
            final String[] cliArgs = new String[] {
                "-c",
                "src/main/resources/policy/APEXacElementPolicy.apex",
                "-l",
                "target/APEXacElementPolicyModel.log",
                "-ac",
                "src/main/resources/examples/config/apexACM/ApexConfig.json",
                "-t",
                "src/main/resources/tosca/ToscaTemplate.json",
                "-ot",
                "target/classes/APEXacElementPolicy.json"
            };
            // @formatter:on

            assertDoesNotThrow(() -> new ApexCliToscaEditorMain(cliArgs));
            assertTrue(Files.exists(Path.of("target/classes/APEXacElementPolicy.json")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
