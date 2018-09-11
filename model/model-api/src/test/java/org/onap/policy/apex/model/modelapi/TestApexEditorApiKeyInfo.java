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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Key information for API tests.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApexEditorApiKeyInfo {

    @Test
    public void testKeyInfoCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = apexModel.validateKeyInformation(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validateKeyInformation("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createKeyInformation(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.createKeyInformation("Hello", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002671",
                "A description of hello");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createKeyInformation("Hello", "0.1.2", "1fa2e430-f2b2-11e6-bc64-92361f002672",
                "A description of hola");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createKeyInformation("Hello", "0.1.4", "1fa2e430-f2b2-11e6-bc64-92361f002672",
                "A description of connichi wa");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createKeyInformation("Hello", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createKeyInformation("Hello", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));

        result = apexModel.createKeyInformation("Hello", "0.1.2", "1fa2e430-f2b2-11e6-bc64-92361f002672",
                "A description of hola");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));

        result = apexModel.validateKeyInformation(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateKeyInformation(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.updateKeyInformation("Hello", "0.0.2", null, "An updated description of hello");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateKeyInformation("Hello", "0.0.2", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateKeyInformation("Hello", "0.1.2", "1fa2e430-f2b2-11e6-bc64-92361f002673",
                "A further updated description of hola");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.updateKeyInformation("Hello2", "0.0.2", null, "An updated description of hello");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listKeyInformation(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listKeyInformation("%%%$$", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.listKeyInformation("Hello", "0.1.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 2);

        result = apexModel.listKeyInformation("Hello", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 4);

        result = apexModel.deleteKeyInformation("Hello", "0.1.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deleteKeyInformation("Hellooooo", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listKeyInformation("Hello", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 4);

        result = apexModel.listKeyInformation(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 22);

        result = apexModel.deleteKeyInformation("%%%$$", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteKeyInformation("Hello", "0.1.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listKeyInformation("Hello", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 3);

        result = apexModel.deleteKeyInformation("Hello", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listKeyInformation("Hello", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deleteKeyInformation(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(18, result.getMessages().size());

        result = apexModel.listKeyInformation(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(0, result.getMessages().size());
    }
}
