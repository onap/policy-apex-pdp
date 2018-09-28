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

package org.onap.policy.apex.core.deployment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        try {
            final String[] eventArgs =
                { "-h" };

            BatchDeployer.main(eventArgs);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("invalid arguments: [-h]", exc.getMessage().substring(0, 23));
        }
    }

    @Test
    public void testBatchDeployerBadPort() {
        try {
            final String[] eventArgs =
                { "localhost", "aport", "afile" };

            BatchDeployer.main(eventArgs);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("argument port is invalid", exc.getMessage().substring(0, 24));
        }
    }

    @Test
    public void testBatchDeployerOk() {
        try {
            final String[] eventArgs =
                { "Host", "43443", "src/test/resources/models/SamplePolicyModelJAVASCRIPT.json" };

            BatchDeployer.main(eventArgs);
        } catch (Exception exc) {
            assertEquals("model deployment failed on parameters Host 43443", exc.getMessage());
        }
    }
    
    @Test
    public void testBatchDeployerDeployString() {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        try {
            deployer.init();
        } catch (ApexDeploymentException ade) {
            assertEquals("model deployment failed on parameters localhost 12345 true", ade.getMessage());
        }

        try {
            deployer.init();
        } catch (ApexDeploymentException ade) {
            ade.printStackTrace();
            fail("test should not throw an exception");
        }

        try {
            deployer.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
        } catch (ApexException ade) {
            assertEquals("could not deploy apex model from src/test/resources/models/SamplePolicyModelJAVASCRIPT.json",
                            ade.getMessage());
        }

        try {
            deployer.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
        } catch (ApexException ade) {
            fail("test should not throw an exception");
        }

        deployer.close();
    }

    @Test
    public void testBatchDeployerStream() throws ApexModelException, FileNotFoundException {

        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        try {
            deployer.init();
        } catch (ApexDeploymentException ade) {
            assertEquals("model deployment failed on parameters localhost 12345 true", ade.getMessage());
        }

        try {
            deployer.init();
        } catch (ApexDeploymentException ade) {
            ade.printStackTrace();
            fail("test should not throw an exception");
        }

        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);
        final AxPolicyModel apexPolicyModel = modelReader.read(
                        new FileInputStream(new File("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json")));

        try {
            deployer.deployModel(apexPolicyModel, false, false);
        } catch (ApexException ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:12345", ade.getMessage());
        }

        try {
            deployer.deployModel(apexPolicyModel, false, false);
        } catch (ApexException ade) {
            fail("test should not throw an exception");
        }

        deployer.close();
    }

    @Test
    public void testBatchDeployerUninitialized() {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        BatchDeployer deployer = new BatchDeployer("localhost", 12345, new PrintStream(baosOut, true));
        deployer.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));

        try {
            deployer.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
            fail("test should throw an exception");
        } catch (ApexException ade) {
            assertEquals("cound not deploy apex model, deployer is not initialized", ade.getMessage());
        }

        try {
            deployer.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
            fail("test should throw an exception");
        } catch (ApexException ade) {
            assertEquals("cound not deploy apex model, deployer is not initialized", ade.getMessage());
        }

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
                        new FileInputStream(new File("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json")));

        try {
            deployer.deployModel(apexPolicyModel, false, false);
            fail("test should throw an exception");
        } catch (ApexException ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:12345", ade.getMessage());
        }

        deployer.close();
    }
}
