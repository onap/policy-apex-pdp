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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.modelapi.impl.ApexModelImpl;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * Test the apex model API.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexModelApiTest {
    private Connection connection;

    @Before
    public void setup() throws Exception {
        // Hold the h2 database up for entire tests
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
    }

    @After
    public void teardown() throws Exception {
        // Close the h2 database after tests
        connection.close();
    }

    @Test
    public void testApexModelLoadFromFile() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = apexModel.loadFromFile("src/main/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deleteModel();
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.xml");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deleteModel();
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.junk");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        assertTrue(result.getMessages().get(0).equals("format of input for Apex concept is neither JSON nor XML"));
    }

    @Test
    public void testApexModelSaveToFile() throws IOException {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        final File tempJsonModelFile = File.createTempFile("ApexModelTest", ".json");
        result = apexModel.saveToFile(tempJsonModelFile.getCanonicalPath(), false);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        final ApexModel jsonApexModel = new ApexModelFactory().createApexModel(null, false);
        result = jsonApexModel.loadFromFile(tempJsonModelFile.getCanonicalPath());
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        tempJsonModelFile.delete();

        final File tempXmlModelFile = File.createTempFile("ApexModelTest", ".xml");
        result = apexModel.saveToFile(tempXmlModelFile.getCanonicalPath(), true);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        final ApexModel xmlApexModel = new ApexModelFactory().createApexModel(null, false);
        result = xmlApexModel.loadFromFile(tempXmlModelFile.getCanonicalPath());
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        tempXmlModelFile.delete();
    }

    @Test
    public void testApexModelDatabase() throws IOException {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        final DaoParameters DaoParameters = new DaoParameters();
        DaoParameters.setPluginClass("org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao");
        DaoParameters.setPersistenceUnit("DAOTest");

        result = apexModel.saveToDatabase(DaoParameters);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deleteModel();
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.loadFromDatabase("PolicyModel", "0.0.1", DaoParameters);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deleteModel();
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.loadFromDatabase("PolicyModel", null, DaoParameters);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deleteModel();
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.loadFromDatabase("VPNPolicyModel", "0.0.1", DaoParameters);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
    }

    @Test
    public void testApexModelUrl() throws IOException {
        ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = null;

        try {
            result = apexModel.readFromUrl(null);
            fail("expecting an IllegalArgumentException");
        } catch (final Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            result = apexModel.writeToUrl(null, true);
            fail("expecting an IllegalArgumentException");
        } catch (final Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        result = apexModel.readFromUrl("zooby/looby");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));

        result = apexModel.writeToUrl("zooby/looby", true);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));

        result = apexModel.readFromUrl("zooby://zooby/looby");
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));

        result = apexModel.writeToUrl("zooby://zooby/looby", false);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));

        apexModel = new ApexModelFactory().createApexModel(null, false);

        final File tempJsonModelFile = File.createTempFile("ApexModelTest", ".json");
        result = apexModel.saveToFile(tempJsonModelFile.getCanonicalPath(), false);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        final String tempFileUrlString = tempJsonModelFile.toURI().toString();
        result = apexModel.readFromUrl(tempFileUrlString);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.writeToUrl(tempFileUrlString, false);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        assertTrue(result.getMessages().get(0).equals("protocol doesn't support output"));

        tempJsonModelFile.delete();
    }

    @Test
    public void testApexModelMisc() throws IOException {
        final ApexModelImpl apexModelImpl = (ApexModelImpl) new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = null;

        result = apexModelImpl.getModelKey();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.listModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.createModel("ModelName", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.updateModel("ModelName", "0.0.1", UUID.randomUUID().toString(), "Model Description");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexModelImpl.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");
        result = apexModelImpl.loadFromString(modelString);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        final File tempFile = File.createTempFile("ApexModel", "json");
        tempFile.deleteOnExit();
        TextFileUtils.putStringAsFile(modelString, tempFile);

        apexModelImpl.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.loadFromFile(tempFile.getCanonicalPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.saveToFile(null, false);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.analyse();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.validate();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.compare(tempFile.getCanonicalPath(), true, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.compareWithString(modelString, true, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.split("policy");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.split(tempFile.getCanonicalPath(), "policy");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.merge(tempFile.getCanonicalPath(), true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.mergeWithString(modelString, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        assertNotEquals(0, apexModelImpl.hashCode());
        assertNotNull(apexModelImpl.clone());
        assertNotNull(apexModelImpl.build());
    }
}
