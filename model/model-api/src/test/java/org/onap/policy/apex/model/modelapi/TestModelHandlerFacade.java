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

package org.onap.policy.apex.model.modelapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.dao.DAOParameters;
import org.onap.policy.apex.model.modelapi.impl.ModelHandlerFacade;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestModelHandlerFacade {

    @Test
    public void testModelHandlerFacade() throws IOException {
        try {
            new ModelHandlerFacade(null, null, false);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("apexModel may not be null", e.getMessage());
        }

        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        try {
            new ModelHandlerFacade(apexModel, null, false);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("apexProperties may not be null", e.getMessage());
        }

        final Properties modelProperties = new Properties();
        final ModelHandlerFacade mhf = new ModelHandlerFacade(apexModel, modelProperties, false);
        assertNotNull(mhf);

        ApexAPIResult result = mhf.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = mhf.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexAPIResult.RESULT.CONCEPT_EXISTS, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        result = apexModel.deleteModel();
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = mhf.loadFromString(modelString);
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = mhf.loadFromString(modelString);
        assertEquals(ApexAPIResult.RESULT.CONCEPT_EXISTS, result.getResult());

        final DAOParameters daoParameters = new DAOParameters();
        result = mhf.loadFromDatabase("SomeModel", null, daoParameters);
        assertEquals(ApexAPIResult.RESULT.CONCEPT_EXISTS, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = mhf.loadFromDatabase("SomeModel", null, daoParameters);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.saveToDatabase(daoParameters);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.readFromURL("blah://somewhere/over/the/rainbow");
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.loadFromString(modelString);
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = mhf.readFromURL("http://somewhere/over/the/rainbow");
        assertEquals(ApexAPIResult.RESULT.CONCEPT_EXISTS, result.getResult());

        final File tempFile = File.createTempFile("ApexModel", "json");
        tempFile.deleteOnExit();

        result = mhf.writeToURL("File:///" + tempFile.getCanonicalPath(), false);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.validate();
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());
        result = mhf.validate();
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.compare("src/test/resources/models/NonExistant.json", true, true);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.compareWithString("zooby", true, true);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.split("FailSplit", "NonExistantPolicy");
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.split("NonExistantPolicy");
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.merge("src/test/resources/models/NonExistant.json", false);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());
        result = mhf.merge("src/test/resources/models/PolicyModel.json", false);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = mhf.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());

        result = mhf.mergeWithString("@£@$@£", true);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexAPIResult.RESULT.SUCCESS, result.getResult());
        result = mhf.mergeWithString(modelString, false);
        assertEquals(ApexAPIResult.RESULT.FAILED, result.getResult());
    }
}
