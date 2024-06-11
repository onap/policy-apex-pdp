/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test events for API tests.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ApexEditorApiEventTest {

    @Test
    void testEventCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        ApexApiResult result = apexModel.validateEvent(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validateEvent("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createEvent("MyEvent002", "0.0.2", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEvent("MyEvent012", "0.1.2", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEvent("MyEvent012", "0.1.4", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 014", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEvent("MyEvent012", null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEvent("MyEvent012", null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createEvent("MyEvent002", "0.0.2", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createEvent("@£$%^", "0.2.5", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteEvent("MyEvent012", "0.1.4");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEvent("MyEvent012", "0.1.4", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 014", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.validateEvent(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateEvent(null, null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result =
            apexModel.updateEvent("MyEvent012", "0.1.2", "Another Namespace", null, "Another target", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateEvent("MyEvent002", "0.0.2", "My Namespace", "My Source", "my target",
            "1fa2e430-f2b2-11e6-bc64-92361f002700", "A description of 002", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateEvent("MyEvent012", null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateEvent("MyEvent015", null, null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateEvent("MyEvent014", "0.1.5", null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateEvent("@£$%^^", "0.6.9", null, null, null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.listEvent("@£$%", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listEvent(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEvent("MyEvent012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEvent("MyEvent012", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEvent("MyEvent012", "0.2.5");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listEvent("MyEvent123", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteEvent("@£$%^", "0.1.1");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deleteEvent("MyEvent012", "0.1.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteEvent("MyEvent012oooo", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listEvent("MyEvent012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(3, result.getMessages().size());

        result = apexModel.deleteEvent("MyEvent012", "0.1.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listEvent("MyEvent012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.deleteEvent("MyEvent012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listEvent("MyEvent012", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createEventPar("MyEvent123", null, "NewPar00", null, null, false);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createEventPar("MyEvent002", "4.5.6", "NewPar00", null, null, true);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createEventPar("MyEvent002", "0.1.4", "NewPar00", null, null, false);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createEventPar("MyEvent002", "0.0.2", "NewPar00", null, null, true);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexModel.createContextSchema("eventContextItem0", "0.0.1", "Java", "java.lang.Integer",
            "1fa2e430-f2b2-11e6-bc64-92361f002673", "A further updated description of hola");
        result = apexModel.createEventPar("MyEvent002", "0.0.2", "NewPar00", "eventContextItem0", null, false);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEventPar("MyEvent002", "0.0.2", "NewPar00", "eventContextItem0", null, true);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createEventPar("MyEvent002", "0.0.2", "NewPar01", "eventContextItem0", "0.0.1", false);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEventPar("MyEvent002", "0.0.2", "NewPar02", "eventContextItem0", "0.0.2", true);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createEventPar("MyEvent002", null, "NewPar02", "eventContextItem0", null, false);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createEventPar("MyEvent002", null, "NewPar03", "eventContextItem0", null, true);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listEventPar("@£%%$", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listEventPar("MyEvent002", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEventPar("MyEvent002", "0.0.1", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listEventPar("MyEvent002", "0.0.2", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEventPar("MyEvent002", "0.0.2", "NewPar01");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEventPar("MyEvent002", "0.0.2", "NewPar02");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEventPar("MyEvent002", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteEventPar("@££%%%", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deleteEventPar("NonExistentEvent", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(4, apexModel.listEventPar("MyEvent002", null, null).getMessages().size());
        result = apexModel.deleteEventPar("MyEvent002", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(4, apexModel.listEventPar("MyEvent002", null, null).getMessages().size());
        result = apexModel.deleteEventPar("MyEvent002", null, "NewPar02");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(3, apexModel.listEventPar("MyEvent002", null, null).getMessages().size());
        result = apexModel.deleteEventPar("MyEvent002", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listEventPar("MyEvent002", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deleteEventPar("MyEvent002", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listEventPar("MyEvent002", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteEvent(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(4, result.getMessages().size());

        result = apexModel.listEvent(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(0, result.getMessages().size());
    }
}
