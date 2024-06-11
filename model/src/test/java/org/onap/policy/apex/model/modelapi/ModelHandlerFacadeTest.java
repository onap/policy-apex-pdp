/*-
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.modelapi.impl.ModelHandlerFacade;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Test the model handler facade.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ModelHandlerFacadeTest {

    @Test
    void testModelHandlerFacade() throws IOException {
        assertThatThrownBy(() -> new ModelHandlerFacade(null, null))
            .hasMessage("apexModel may not be null");
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        assertThatThrownBy(() -> new ModelHandlerFacade(apexModel, null))
            .hasMessage("apexProperties may not be null");
        final Properties modelProperties = new Properties();
        final ModelHandlerFacade mhf = new ModelHandlerFacade(apexModel, modelProperties);
        assertNotNull(mhf);

        ApexApiResult result = mhf.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mhf.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mhf.loadFromString(modelString);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mhf.loadFromString(modelString);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mhf.readFromUrl("blah://somewhere/over/the/rainbow");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.loadFromString(modelString);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mhf.readFromUrl("http://somewhere/over/the/rainbow");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        final File tempFile = File.createTempFile("ApexModel", "json");
        tempFile.deleteOnExit();

        result = mhf.writeToUrl("File:///" + tempFile.getCanonicalPath());
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.validate();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = mhf.validate();
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.compare("src/test/resources/models/NonExistant.json", true, true);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.compareWithString("zooby", true, true);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.split("FailSplit", "NonExistantPolicy");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.split("NonExistantPolicy");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.merge("src/test/resources/models/NonExistant.json", false);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = mhf.merge("src/test/resources/models/PolicyModel.json", false);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mhf.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mhf.mergeWithString("@£@$@£", true);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = mhf.mergeWithString(modelString, false);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
    }
}
