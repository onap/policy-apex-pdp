/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.examples.myfirstpolicy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * Test MyFirstPolicyModel CLI.
 */
public class MfpModelCliTest {
    private static AxPolicyModel testApexModel1;
    private static AxPolicyModel testApexModel2;

    /**
     * Setup the test.
     *
     * @throws Exception if there is an error
     */
    @BeforeClass
    public static void setup() throws Exception {
        testApexModel1 = new TestMfpModelCreator.TestMfp1ModelCreator().getModel();
        testApexModel2 = new TestMfpModelCreator.TestMfp2ModelCreator().getModel();
    }

    /**
     * Test CLI policy.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException ifd there is an Apex Error
     */
    @Test
    public void testCliPolicy() throws IOException, ApexModelException {

        final File tempLogFile1 = File.createTempFile("TestMyFirstPolicy1CLI", ".log");
        final File tempModelFile1 = File.createTempFile("TestMyFirstPolicy1CLI", ".json");
        final File tempLogFile2 = File.createTempFile("TestMyFirstPolicy2CLI", ".log");
        final File tempModelFile2 = File.createTempFile("TestMyFirstPolicy2CLI", ".json");
        final String[] testApexModel1CliArgs =
            { "-c", "src/main/resources/examples/models/MyFirstPolicy/1/MyFirstPolicyModel_0.0.1.apex", "-l",
                        tempLogFile1.getAbsolutePath(), "-o", tempModelFile1.getAbsolutePath() };
        final String[] testApexModel2CliArgs =
            { "-c", "src/main/resources/examples/models/MyFirstPolicy/2/MyFirstPolicyModel_0.0.1.apex", "-l",
                        tempLogFile2.getAbsolutePath(), "-o", tempModelFile2.getAbsolutePath() };

        new ApexCommandLineEditorMain(testApexModel1CliArgs);
        new ApexCommandLineEditorMain(testApexModel2CliArgs);

        final ApexModelReader<AxPolicyModel> reader = new ApexModelReader<>(AxPolicyModel.class);
        AxPolicyModel generatedmodel = reader.read(TextFileUtils.getTextFileAsString(tempModelFile1.getAbsolutePath()));

        assertEquals("Model generated from the CLI (" + testApexModel1CliArgs[1] + ") into file "
                + tempModelFile1.getAbsolutePath() + " is not the same as the test Model for "
                + testApexModel1.getKey(), testApexModel1, generatedmodel);

        generatedmodel = reader.read(TextFileUtils.getTextFileAsString(tempModelFile2.getAbsolutePath()));
        assertEquals("Model generated from the CLI (" + testApexModel2CliArgs[1] + ") into file "
                + tempModelFile2.getAbsolutePath() + " is not the same as the test Model for "
                + testApexModel2.getKey(), testApexModel2, generatedmodel);

        tempLogFile1.delete();
        tempModelFile1.delete();

        tempLogFile2.delete();
        tempModelFile2.delete();

    }
}
