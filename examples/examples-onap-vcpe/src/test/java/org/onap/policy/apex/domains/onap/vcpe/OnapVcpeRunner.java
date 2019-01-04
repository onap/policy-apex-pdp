/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * Test the ONAP vCPE use case.
 */
public class OnapVcpeRunner {

    private OnapVcpeRunner() throws ApexException {

        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/main/resources/policy/ONAPvCPEPolicyModel.apex",
            "-l",
            "target/ONAPvCPEPolicyModel.log",
            "-o",
            "target/classes/ONAPvCPEPolicyModel.json"
        };
        // @formatter:on

        new ApexCommandLineEditorMain(cliArgs);

        // @formatter:off
        final String[] apexArgs = {
            "-rfr",
            "target/classes",
            "-c",
            "src/main/resources/examples/config/ONAPvCPE/ApexConfig_Sim.json",
            "-m",
            "target/classes/ONAPvCPEPolicyModel.json"
        };
        // @formatter:on

        final ApexMain apexMain = new ApexMain(apexArgs);

        ThreadUtilities.sleep(1000000);
        apexMain.shutdown();
    }
    
    public static void main(String[] args) throws ApexException {
        new OnapVcpeRunner();
    }
}
