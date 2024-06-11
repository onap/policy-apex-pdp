/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.modelapi.impl.ApexModelImpl;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Test the apex model API.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ApexModelApiTest {
    private Connection connection;

    @BeforeEach
    void setup() throws Exception {
        // Hold the h2 database up for entire tests
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
    }

    @AfterEach
    void teardown() throws Exception {
        // Close the h2 database after tests
        connection.close();
    }

    @Test
    void testApexModelLoadFromFile() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        ApexApiResult result = apexModel.loadFromFile("src/main/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.junk");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        assertEquals("Unable to unmarshal Apex concept", result.getMessages().get(0).trim());
    }

    @Test
    void testApexModelSaveToFile() throws IOException {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        ApexApiResult result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        final File tempJsonModelFile = File.createTempFile("ApexModelTest", ".json");
        result = apexModel.saveToFile(tempJsonModelFile.getPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        final ApexModel jsonApexModel = new ApexModelFactory().createApexModel(null);
        result = jsonApexModel.loadFromFile(tempJsonModelFile.getPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        tempJsonModelFile.delete();

        final File tempModelFile = File.createTempFile("ApexModelTest", ".json");
        result = apexModel.saveToFile(tempModelFile.getPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        final ApexModel testApexModel = new ApexModelFactory().createApexModel(null);
        result = testApexModel.loadFromFile(tempModelFile.getPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        tempModelFile.delete();
    }

    @Test
    void testApexModelUrl() throws IOException {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        assertThatThrownBy(() -> apexModel.readFromUrl(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> apexModel.writeToUrl(null)).isInstanceOf(IllegalArgumentException.class);
        ApexApiResult result = null;
        result = apexModel.readFromUrl("zooby/looby");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.writeToUrl("zooby/looby");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.readFromUrl("zooby://zooby/looby");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.writeToUrl("zooby://zooby/looby");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        final File tempJsonModelFile = File.createTempFile("ApexModelTest", "json");

        result = apexModel.saveToFile(tempJsonModelFile.getPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        final String tempFileUrlString = tempJsonModelFile.toURI().toString();
        result = apexModel.readFromUrl(tempFileUrlString);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.writeToUrl(tempFileUrlString);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        assertEquals("protocol doesn't support output", result.getMessages().get(0));

        tempJsonModelFile.delete();
    }

    @Test
    void testApexModelMisc() throws IOException {
        final ApexModelImpl apexModelImpl = (ApexModelImpl) new ApexModelFactory().createApexModel(null);

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

        result = apexModelImpl.loadFromFile(tempFile.getPath());
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.analyse();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.validate();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.compare(tempFile.getPath(), true, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.compareWithString(modelString, true, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.split("policy");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.split(tempFile.getPath(), "policy");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.merge(tempFile.getPath(), true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModelImpl.mergeWithString(modelString, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        assertNotEquals(0, apexModelImpl.hashCode());
        assertNotNull(apexModelImpl.getCopy());
        assertNotNull(apexModelImpl.build());
    }
}
