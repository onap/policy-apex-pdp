/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

package org.onap.policy.apex.core.deployment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Test the periodic event manager utility.
 */
public class BatchDeployerTest {
    @Test
    public void testBatchDeployerBad() {
        assertThatThrownBy(() -> {
            final String[] eventArgs =
                { "-h" };

            BatchDeployer.main(eventArgs);
        }).hasMessageContaining("invalid arguments: [-h]");
    }

    @Test
    public void testBatchDeployerBadPort() {
        assertThatThrownBy(() -> {
            final String[] eventArgs =
                { "localhost", "aport", "afile" };

            BatchDeployer.main(eventArgs);
        }).hasMessageContaining("argument port is invalid");
    }

    @Test
    public void testBatchDeployerOk() {
        assertThatThrownBy(() -> {
            final String[] eventArgs =
                { "Host", "43443", "src/test/resources/models/SamplePolicyModelJAVASCRIPT.json" };

            BatchDeployer.main(eventArgs);
        }).hasMessageContaining("model deployment failed on parameters Host 43443");
    }

    @Test
    public void testBatchDeployerDeployString() throws ApexException {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        deployer.init();

        assertThatThrownBy(() -> deployer.deployModel("src/test/resources/models/SmallModel.json", false, false))
            .hasMessageContaining("cound not deploy apex model");

        deployer.close();
    }

    @Test
    public void testBatchDeployerStream() throws FileNotFoundException, ApexException {

        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        deployer.init();

        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);
        final AxPolicyModel apexPolicyModel = modelReader.read(
                        new FileInputStream(new File("src/test/resources/models/SmallModel.json")));

        assertThatThrownBy(() -> deployer.deployModel(apexPolicyModel, false, false))
            .hasMessageContaining("failed response Operation failed received from serverlocalhost:12345");
        deployer.deployModel(apexPolicyModel, false, false);

        deployer.close();
    }

    @Test
    public void testBatchDeployerUninitialized() {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        assertThatThrownBy(() -> deployer.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json",
                false, false))
            .hasMessageContaining("cound not deploy apex model, deployer is not initialized");
        assertThatThrownBy(() -> deployer.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json",
                false, false))
            .hasMessageContaining("cound not deploy apex model, deployer is not initialized");
        deployer.close();
    }

    @Test
    public void testBatchDeployerStreamUninitialized() throws ApexModelException, FileNotFoundException {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);
        final AxPolicyModel apexPolicyModel = modelReader.read(
                        new FileInputStream(new File("src/test/resources/models/SmallModel.json")));

        assertThatThrownBy(() -> deployer.deployModel(apexPolicyModel, false, false))
            .hasMessageContaining("failed response Operation failed received from serverlocalhost:12345");
        deployer.close();
    }
}
