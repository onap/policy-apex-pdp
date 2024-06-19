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

import static org.awaitility.Awaitility.await;

import jakarta.ws.rs.client.ClientBuilder;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * Test class to run an example policy for ACM interaction. Event received on
 * message topic (dummy REST Endpoint here) and triggers a new message.
 */
class TestApexAcmExample {

    @Test
    void testExample() {
        try (var dmmap = new AcmTestServerDmaap()) {
            dmmap.validate();

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

            new ApexCliToscaEditorMain(cliArgs);

            // @formatter:off
            final String[] apexArgs = {
                "-rfr",
                "target/classes",
                "-p",
                "target/classes/APEXacElementPolicy.json"
            };
            // @formatter:on

            final var client = ClientBuilder.newClient();
            final var apexMain = new ApexMain(apexArgs);

            await().atMost(5000, TimeUnit.MILLISECONDS).until(apexMain::isAlive);

            String getLoggedEventUrl = "http://localhost:3904/events/getLoggedEvent";
            await().atMost(20000, TimeUnit.MILLISECONDS).until(() -> {
                var response = client.target(getLoggedEventUrl).request("application/json").get();
                var responseEntity = response.readEntity(String.class);
                return responseEntity != null && !responseEntity.isEmpty();
            });
            apexMain.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
