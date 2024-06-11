/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
import org.onap.policy.apex.model.modelapi.impl.ApexModelImpl;

/**
 * Test tasks for API tests.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ApexEditorApiTaskTest {
    @Test
    void testTaskCrud() {
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        ApexApiResult result = apexModel.validateTask(null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.validateTask("%%%$£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.loadFromFile("src/test/resources/models/PolicyModel.json");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.createTask("@^^$^^$", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createTask("MyTask002", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("MyTask002", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createTask("MyTask012", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("MyTask012", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.listTask(null, null);
        result = apexModel.createTask("MyTask002", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createTask("MyTask012", "0.1.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.deleteTask("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTask("MyTask002", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.validateTask(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateTask("@$$$£", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updateTask("MyTask002", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTask("MyTask002", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateTask("MyTask002", "0.0.2", "1fa2e430-f2b2-11e6-bc64-92361f002700",
            "A description of 002");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTask("MyTask012", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTask("MyTask012", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTask("MyTask012", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTask("MyTask015", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateTask("MyTask014", "0.1.5", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listTask("£@£@@£@£", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listTask(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTask("MyTask012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTask("MyTask012", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTask("MyTask012", "0.2.5");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listTask("MyTask014", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteTask("@£££@", "0.1.1");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deleteTask("MyTask012", "0.1.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deleteTask("MyTask012oooo", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listTask("MyTask012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.deleteTask("MyTask012", "0.1.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listTask("MyTask012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());

        result = apexModel.deleteTask("MyTask012", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.listTask("MyTask012", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.updateTaskLogic("MyTask002", null, "NewLogic00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskLogic("MyTask123", null, "NewLogic00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "4.5.6", "NewLogic00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "0.1.4", "NewLogic00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "0.0.2", "NewLogic00", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "0.0.2", "UNDEFINED", "Some Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "0.0.2", "MVEL", "Some Task Logic");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "0.0.2", "JAVA", "Some Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", "0.0.2", "JYTHON", "Some Task Logic");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", null, "JAVASCRIPT", "Some Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskLogic("MyTask002", null, "JRUBY", "Some Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexModel.updateTaskLogic("MyTask002", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTaskLogic("MyTask002", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateTaskLogic("MyTask002", "0.0.2", "", "Some Other Task Logic");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.updateTaskLogic("MyTask002", "0.0.2", "MVEL", "Some Other Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTaskLogic("MyTask012", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateTaskLogic("MyTask002", null, null, "Some Other Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTaskLogic("MyTask002", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.updateTaskLogic("MyTask015", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.updateTaskLogic("MyTask014", "0.1.5", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.listTaskLogic("MyTask002", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskLogic("MyTask002", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskLogic(null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.deleteTaskLogic("@£@£@£", "0.0.2");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deleteTaskLogic("NonExistantTask", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        assertEquals(1, apexModel.listTaskLogic("MyTask002", null).getMessages().size());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskLogic("MyTask002", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(1, apexModel.listTaskLogic("MyTask002", null).getMessages().size());
        result = apexModel.createTaskLogic("MyTask002", null, "JRUBY", "Some Task Logic");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskLogic("MyTask002", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.deleteTaskLogic("MyTask002", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskField("MyTask002", "0.0.2", "NewField00", "eventContextItem0", null, false);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(ApexModelImpl.FIELDS_DEPRECATED_WARN_MSG, result.getMessage().trim());
        result = apexModel.handleTaskField("MyTask002", "0.0.2", "NewField01");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(ApexModelImpl.FIELDS_DEPRECATED_WARN_MSG, result.getMessage().trim());

        result = apexModel.createTaskParameter("MyTask123", null, "NewPar00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskParameter("MyTask002", "4.5.6", "NewPar00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskParameter("MyTask002", "0.1.4", "NewPar00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar00", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar00", "eventContextItem0");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar00", "eventContextItem0");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar01", "eventContextItem0");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar02", "eventContextItem0");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar02", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", "0.0.2", "NewPar03", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createTaskParameter("MyTask002", null, null, "Default value");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexModel.listTaskParameter("@£$%", null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", "0.0.3", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", "0.0.2", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", "0.0.2", "NewPar01");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", "0.0.2", "NewPar02");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteTaskParameter("@£$%", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deleteTaskParameter("NonExistantTask", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(3, apexModel.listTaskParameter("MyTask002", null, null).getMessages().size());
        result = apexModel.deleteTaskParameter("MyTask002", "0.0.2", "NewPar04");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(3, apexModel.listTaskParameter("MyTask002", null, null).getMessages().size());
        result = apexModel.deleteTaskParameter("MyTask002", null, "NewPar02");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, apexModel.listTaskParameter("MyTask002", null, null).getMessages().size());
        result = apexModel.deleteTaskParameter("MyTask002", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.listTaskParameter("MyTask002", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deleteTaskParameter("MyTask002", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskContextRef("@£$$", null, "AContextMap00", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.createTaskContextRef("MyTask123", null, "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask123", null, "AContextMap00", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask123", null, "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "4.5.6", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.1.4", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "AContextMap00", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "contextAlbum2", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "contextAlbum1", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = apexModel.createTaskContextRef("MyTask002", null, "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        result = apexModel.listTaskContextRef("@£$%", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.listTaskContextRef("MyTask002", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "AContextMap04", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "contextAlbum0", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "contextAlbum1", "0.0.1");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", "0.0.2", "contextAlbum1", "0.0.2");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteTaskContextRef("@£$%", "0.0.2", "AContextMap04", null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexModel.deleteTaskContextRef("NonExistantTask", "0.0.2", "AContextMap04", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        assertEquals(2, apexModel.listTaskContextRef("MyTask002", null, null, null).getMessages().size());
        result = apexModel.deleteTaskContextRef("MyTask002", "0.0.2", "AContextMap04", null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deleteTaskContextRef("MyTask002", null, "contextAlbum0", "0.0.3");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deleteTaskContextRef("MyTask002", null, "contextAlbum0", "0.1.5");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexModel.deleteTaskContextRef("MyTask002", null, "contextAlbum0", null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.deleteTaskContextRef("MyTask002", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(1, result.getMessages().size());
        result = apexModel.listTaskContextRef("MyTask002", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexModel.deleteTask(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(2, result.getMessages().size());

        result = apexModel.listTask(null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertEquals(0, result.getMessages().size());
    }
}
