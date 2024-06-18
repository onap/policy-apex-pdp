/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.domains.onap.vcpe;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * Test the ONAP vCPE use case.
 */
public class OnapVcpeStandaloneRunner {

    private OnapVcpeStandaloneRunner() throws ApexException {

        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/main/resources/policy/ONAPvCPEPolicyModelStandalone.apex",
            "-ac",
            "src/main/resources/examples/config/ONAPvCPEStandalone/ApexConfig_Sim.json",
            "-t",
            "src/main/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        // @formatter:off
        final String[] apexArgs = {
            "-p",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on
        final ApexMain apexMain = new ApexMain(apexArgs);

        await().atMost(5000, TimeUnit.MILLISECONDS).until(apexMain::isAlive);

        // This test should be amended to start and shutdown the simulator as part of the test and not separately as
        // is done in the gRPC test.
        ThreadUtilities.sleep(1000000);
        apexMain.shutdown();
    }

    public static void main(String[] args) throws ApexException {
        new OnapVcpeStandaloneRunner();
    }
}
