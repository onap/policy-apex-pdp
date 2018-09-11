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
 * Context album for API tests.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApexEditorApiContextAlbum {
    @Test
    public void testContextAlbumCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null, false);

        ApexApiResult result = apexModel.validateContextAlbum(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validateContextAlbum("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.createContextAlbum("MyMap002", "0.0.2", "APPLICATION", "true", "MapType", "0.0.1",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createContextAlbum("MyMap012", "0.1.2", "ZOOBY", "false", "MapType", "0.0.1",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 012");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createContextAlbum("MyMap012", "0.1.4", "UNDEFINED", null, "MapType", "0.0.1",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 014");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createContextAlbum("MyMap012", null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "+++", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "MapZooby", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "MapType", "--++", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.FAILED));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "MapType", "0.0.2", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "MapType", "0.0.1", null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createContextAlbum("MyMap012", null, "EPHEMERAL", "false", "MapType", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createContextAlbum("MyMap002", "0.0.2", "APPLICATION", "true", "MapType", null,
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_EXISTS));
        result = apexModel.createContextAlbum("MyMap011", "0.1.2", "APPLICATION", "true", "MapType", "0.0.1",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.deleteContextAlbum("MyMap012", "0.1.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.createContextAlbum("MyMap012", "0.1.2", "ZOOBY", "false", "MapType", "0.0.1",
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 012");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.validateContextAlbum(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateContextAlbum(null, null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updateContextAlbum("MyMap002", "0.0.2", null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap002", "0.0.2", "ZOOBY", "true", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap002", "0.0.2", null, null, null, null,
                "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap012", null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap012", null, null, "true", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap015", null, null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updateContextAlbum("MyMap014", "0.1.5", null, null, null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", "false", null, null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", "false", "StringType", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", "false", "String", null, null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", "false", "StringType", "0.0.2", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", "false", "StringType", "0.0.1", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.updateContextAlbum("MyMap012", null, "APPLICATION", "Hello", "StringType", "0.0.1", null,
                null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listContextAlbum("@£%%$", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.listContextAlbum(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.listContextAlbum("MyMap012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.listContextAlbum("MyMap012", "0.0.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        result = apexModel.listContextAlbum("MyMap012", "0.2.5");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));
        result = apexModel.listContextAlbum("MyMap014", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deleteContextAlbum("@£%%$", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteContextAlbum("MyMap012", "0.1.1");
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deleteContextAlbum("MyMap012oooo", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.listContextAlbum("MyMap012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 3);

        result = apexModel.deleteContextAlbum("MyMap012", "0.1.2");
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listContextAlbum("MyMap012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertTrue(result.getMessages().size() == 2);

        result = apexModel.deleteContextAlbum("MyMap012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));

        result = apexModel.listContextAlbum("MyMap012", null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST));

        result = apexModel.deleteContextAlbum(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(4, result.getMessages().size());

        result = apexModel.listContextAlbum(null, null);
        assertTrue(result.getResult().equals(ApexApiResult.Result.SUCCESS));
        assertEquals(0, result.getMessages().size());
    }
}
