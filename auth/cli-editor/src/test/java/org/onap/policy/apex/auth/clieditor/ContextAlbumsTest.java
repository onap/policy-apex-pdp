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
import static org.junit.Assert.assertNotNull;
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

/**
 * The Class TestContextAlbums.
 */
public class ContextAlbumsTest {
    private String[] logicBlockArgs;

    private File tempModelFile;

    /**
     * Creates the temp files.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void createTempFiles() throws IOException {
        tempModelFile = File.createTempFile("TestPolicyModel", ".json");

        logicBlockArgs = new String[] {"-c", "src/test/resources/scripts/ContextAlbums.apex", "-o",
                tempModelFile.getAbsolutePath(), "-nl"};
    }

    /**
     * Removes the generated models.
     */
    @After
    public void removeGeneratedModels() {
        tempModelFile.delete();
    }

    /**
     * Test logic block.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException if there is an Apex error
     */
    @Test
    public void testLogicBlock() throws IOException, ApexModelException {
        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(logicBlockArgs);
        assertEquals(1, cliEditor.getErrorCount());

        // Read the file from disk
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);

        final URL writtenModelUrl = ResourceUtils.getLocalFile(tempModelFile.getCanonicalPath());
        final AxPolicyModel writtenModel = modelReader.read(writtenModelUrl.openStream());
        assertNotNull(writtenModel);

        final URL compareModelUrl =
                ResourceUtils.getLocalFile("src/test/resources/compare/ContextAlbumsModel_Compare.json");
        final AxPolicyModel compareModel = modelReader.read(compareModelUrl.openStream());

        // Ignore key info UUIDs
        writtenModel.getKeyInformation().getKeyInfoMap().clear();
        compareModel.getKeyInformation().getKeyInfoMap().clear();

        assertTrue(writtenModel.equals(compareModel));
    }
}
