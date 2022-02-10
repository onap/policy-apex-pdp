/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020,2022 Nordix Foundation.
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

import org.junit.Test;

/**
 * Context schema for API tests.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEditorApiContextSchemaTest {
    @Test
    public void testContextSchemaCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        ApexApiResult result = apexModel.validateContextSchemas(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validateContextSchemas("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listContextSchemas(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createContextSchema("Hello", "0.0.2", "Java", "java.lang.String",
                "1fa2e430-f2b2-11e6-bc64-92361f002671", "A description of hello");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createContextSchema("Hello", "0.1.2", "Java", "java.lang.String",
                "1fa2e430-f2b2-11e6-bc64-92361f002672", "A description of hola");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createContextSchema("Hello", "0.1.4", "Java", "java.lang.String",
                "1fa2e430-f2b2-11e6-bc64-92361f002672", "A description of connichi wa");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createContextSchema("Hello", null, "Java", "java.lang.String", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createContextSchema("Hello", null, "Java", "java.lang.String", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.deleteContextSchema("Hello", "0.1.4");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createContextSchema("Hello", "0.1.4", "Java", "java.lang.String",
                "1fa2e430-f2b2-11e6-bc64-92361f002672", "A description of connichi wa");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createContextSchema("Hello2", null, null, "java.lang.String", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createContextSchema("Hello2", null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createContextSchema("Hello2", null, "Java", "java.lang.String", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createContextSchema("Hello", "0.1.2", "Java", "java.lang.Float",
                "1fa2e430-f2b2-11e6-bc64-92361f002672", "A description of hola");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        result = apexModel.deleteContextSchema("Hello", "0.1.4");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createContextSchema("Hello", "0.1.4", "Java", "java.lang.String",
                "1fa2e430-f2b2-11e6-bc64-92361f002672", "A description of connichi wa");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.validateContextSchemas(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateContextSchema(null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.updateContextSchema("Hello", "0.0.2", null, null, null, "An updated description of hello");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateContextSchema("Hello", "0.0.2", null, null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateContextSchema("Hello", "0.1.2", "Java", "java.lang.Integer",
                "1fa2e430-f2b2-11e6-bc64-92361f002673", "A further updated description of hola");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateContextSchema("Hello2", "0.0.2", null, null, null, "An updated description of hello");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listContextSchemas("@£%%$", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.listContextSchemas("Hello", "0.1.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.listContextSchemas("Hello", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());

        result = apexModel.listContextSchemas(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(9, result.getMessages().size());

        result = apexModel.deleteContextSchema("@£%%$", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteContextSchema("Hello", "0.1.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteContextSchema("Hellooooo", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listContextSchemas("Hello", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());

        result = apexModel.deleteContextSchema("Hello", "0.1.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listContextSchemas("Hello", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(3, result.getMessages().size());

        result = apexModel.deleteContextSchema("Hello", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listContextSchemas("Hello", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listContextSchemas(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.deleteContextSchema(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(5, result.getMessages().size());

        result = apexModel.listContextSchemas(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(0, result.getMessages().size());
    }
}
