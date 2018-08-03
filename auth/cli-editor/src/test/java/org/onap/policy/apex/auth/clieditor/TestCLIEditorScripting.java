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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * The Class TestCLIEditorScripting.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestCLIEditorScripting {

    private File tempModelFile;
    private File tempLogFile;

    private String[] sampleLBPolicyArgs;

    private String[] sampleLBPolicyMapArgs;

    /**
     * Initialise args.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void initialiseArgs() throws IOException {
        tempModelFile = File.createTempFile("SampleLBPolicyMap", ".json");
        tempLogFile = File.createTempFile("SampleLBPolicyMap", ".log");

        sampleLBPolicyArgs = new String[] {"-c", "src/test/resources/scripts/SampleLBPolicy.apex", "-o",
                tempModelFile.getAbsolutePath(), "-l", tempLogFile.getAbsolutePath()};

        sampleLBPolicyMapArgs = new String[] {"-c", "src/test/resources/scripts/SampleLBPolicy_WithMap.apex", "-o",
                tempModelFile.getAbsolutePath(), "-l", tempLogFile.getAbsolutePath()};
    }

    /**
     * Removes the generated files.
     */
    @After
    public void removeGeneratedFiles() {
        tempModelFile.delete();
        tempLogFile.delete();
    }

    /**
     * Test sample Fuzzy LB policy script.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testSampleLBPolicyScript() throws IOException, ApexModelException {
        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(sampleLBPolicyArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);

        final URL writtenModelURL = ResourceUtils.getLocalFile(tempModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelURL.openStream());

        final URL compareModelURL =
                ResourceUtils.getLocalFile("src/test/resources/compare/FuzzyPolicyModel_Compare.json");
        final AxPolicyModel compareModel = modelReader.read(compareModelURL.openStream());

        // Ignore key info UUIDs
        writtenModel.getKeyInformation().getKeyInfoMap().clear();
        compareModel.getKeyInformation().getKeyInfoMap().clear();

        assertTrue(writtenModel.equals(compareModel));
    }

    /**
     * Test sample Fuzzy LB map policy script.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testSampleLBMapPolicyScript() throws IOException, ApexModelException {
        tempModelFile.delete();

        final ApexCLIEditorMain cliEditor = new ApexCLIEditorMain(sampleLBPolicyMapArgs);
        assertEquals(0, cliEditor.getErrorCount());

        assertTrue(tempModelFile.isFile());

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);

        final URL writtenModelURL = ResourceUtils.getLocalFile(tempModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelURL.openStream());

        final AxValidationResult validationResult = new AxValidationResult();
        writtenModel.validate(validationResult);
        assertEquals(AxValidationResult.ValidationResult.OBSERVATION, validationResult.getValidationResult());
    }
}
