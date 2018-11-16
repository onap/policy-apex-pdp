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

package org.onap.policy.apex.auth.clieditor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class TestLogicBlock.
 */
public class LogicBlockTest {
    private String[] logicBlockArgs;
    private String[] avroSchemaArgs;

    private File tempLogicModelFile;
    private File tempAvroModelFile;

    /**
     * Creates the temp files.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void createTempFiles() throws IOException {
        tempLogicModelFile = File.createTempFile("TestLogicPolicyModel", ".json");
        tempAvroModelFile = File.createTempFile("TestAvroPolicyModel", ".json");

        logicBlockArgs = new String[] {"-c", "src/test/resources/scripts/LogicBlock.apex", "-o",
                tempLogicModelFile.getCanonicalPath(), "-if", "true", "-nl"};

        avroSchemaArgs = new String[] {"-c", "src/test/resources/scripts/AvroSchema.apex", "-o",
                tempAvroModelFile.getCanonicalPath(), "-nl"};
    }

    /**
     * Removes the temp files.
     */
    @After
    public void removeTempFiles() {
        tempLogicModelFile.delete();
        tempAvroModelFile.delete();
    }

    /**
     * Test logic block.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testLogicBlock() throws IOException, ApexModelException {
        new ApexCommandLineEditorMain(logicBlockArgs);

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);

        final URL writtenModelUrl = ResourceUtils.getLocalFile(tempLogicModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelUrl.openStream());

        final URL compareModelUrl =
                ResourceUtils.getLocalFile("src/test/resources/compare/LogicBlockModel_Compare.json");
        final AxPolicyModel compareModel = modelReader.read(compareModelUrl.openStream());

        // Ignore key info UUIDs
        writtenModel.getKeyInformation().getKeyInfoMap().clear();
        compareModel.getKeyInformation().getKeyInfoMap().clear();

        assertTrue(writtenModel.equals(compareModel));
    }

    /**
     * Test avro schema.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testAvroSchema() throws IOException, ApexModelException {
        new ApexCommandLineEditorMain(avroSchemaArgs);

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);

        final URL writtenModelUrl = ResourceUtils.getLocalFile(tempAvroModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelUrl.openStream());

        final URL compareModelUrl =
                ResourceUtils.getLocalFile("src/test/resources/compare/AvroSchemaModel_Compare.json");
        final AxPolicyModel compareModel = modelReader.read(compareModelUrl.openStream());

        // Ignore key info UUIDs
        writtenModel.getKeyInformation().getKeyInfoMap().clear();
        compareModel.getKeyInformation().getKeyInfoMap().clear();

        assertTrue(writtenModel.equals(compareModel));
    }
}
