/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
public class CommandLineEditorScriptingTest {

    private File tempModelFile;
    private File tempLogFile;

    private String[] samplePolicyArgs;

    private String[] samplePolicyMapArgs;

    /**
     * Initialise args.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void initialiseArgs() throws IOException {
        tempModelFile = File.createTempFile("SampleLBPolicyMap", ".json");
        tempLogFile = File.createTempFile("SampleLBPolicyMap", ".log");

        samplePolicyArgs = new String[] {"-c", "src/test/resources/scripts/SampleLBPolicy.apex", "-o",
                tempModelFile.getAbsolutePath(), "-l", tempLogFile.getAbsolutePath()};

        samplePolicyMapArgs = new String[] {"-c", "src/test/resources/scripts/SampleLBPolicy_WithMap.apex", "-o",
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
     * Test sample FLB policy script.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testSamplePolicyScript() throws IOException, ApexModelException {
        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(samplePolicyArgs);
        assertEquals(0, cliEditor.getErrorCount());

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);

        final URL writtenModelUrl = ResourceUtils.getLocalFile(tempModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelUrl.openStream());

        final URL compareModelUrl =
                ResourceUtils.getLocalFile("src/test/resources/compare/FLBPolicyModel_Compare.json");
        final AxPolicyModel compareModel = modelReader.read(compareModelUrl.openStream());

        final URL compareModelNoAlbumsUrl =
            ResourceUtils.getLocalFile("src/test/resources/compare/FLBPolicyModel_noAlbums_Compare.json");
        final AxPolicyModel compareNoAlbumsModel = modelReader.read(compareModelNoAlbumsUrl.openStream());

        // Ignore key info UUIDs
        writtenModel.getKeyInformation().getKeyInfoMap().clear();
        compareModel.getKeyInformation().getKeyInfoMap().clear();
        compareNoAlbumsModel.getKeyInformation().getKeyInfoMap().clear();

        assertTrue(writtenModel.equals(compareModel));
        assertTrue(writtenModel.equals(compareNoAlbumsModel));
        assertTrue(compareModel.equals(compareNoAlbumsModel));
    }

    /**
     * Test sample FLB map policy script.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testSampleMapPolicyScript() throws IOException, ApexModelException {
        tempModelFile.delete();

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(samplePolicyMapArgs);
        assertEquals(0, cliEditor.getErrorCount());

        assertTrue(tempModelFile.isFile());

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);

        final URL writtenModelUrl = ResourceUtils.getLocalFile(tempModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelUrl.openStream());

        final AxValidationResult validationResult = new AxValidationResult();
        writtenModel.validate(validationResult);
        assertEquals(AxValidationResult.ValidationResult.OBSERVATION, validationResult.getValidationResult());
    }
}
