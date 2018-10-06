/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.core.engine.executor.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the state facade.
 */
public class AxTaskFacadeTest {
    @Mock
    private AxTask axTaskMock;

    @Mock
    private AxInputField axInputFieldMock;

    @Mock
    private AxInputField axInputFieldBadMock;

    @Mock
    private AxOutputField axOutputFieldMock;

    @Mock
    private AxOutputField axOutputFieldBadMock;

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        AxContextSchemas schemas = new AxContextSchemas();

        AxArtifactKey stringTypeKey = new AxArtifactKey("StringType:0.0.1");
        AxContextSchema stringType = new AxContextSchema(stringTypeKey, "Java", "java.lang.String");
        schemas.getSchemasMap().put(stringTypeKey, stringType);

        ModelService.registerModel(AxContextSchemas.class, schemas);

        MockitoAnnotations.initMocks(this);

        AxArtifactKey task0Key = new AxArtifactKey("Task0:0.0.1");
        Mockito.doReturn(task0Key).when(axTaskMock).getKey();
        Mockito.doReturn(task0Key.getId()).when(axTaskMock).getId();

        Map<String, AxInputField> inFieldMap = new LinkedHashMap<>();
        Map<String, AxOutputField> outFieldMap = new LinkedHashMap<>();

        inFieldMap.put("InField0", axInputFieldMock);
        inFieldMap.put("InFieldBad", axInputFieldBadMock);
        outFieldMap.put("OutField0", axOutputFieldMock);
        outFieldMap.put("OutFieldBad", axOutputFieldBadMock);

        Mockito.doReturn(inFieldMap).when(axTaskMock).getInputFields();
        Mockito.doReturn(outFieldMap).when(axTaskMock).getOutputFields();

        Mockito.doReturn(new AxReferenceKey(task0Key, "InField0")).when(axInputFieldMock).getKey();
        Mockito.doReturn(stringTypeKey).when(axInputFieldMock).getSchema();

        Mockito.doReturn(new AxReferenceKey(task0Key, "OutField0")).when(axOutputFieldMock).getKey();
        Mockito.doReturn(stringTypeKey).when(axOutputFieldMock).getSchema();

        ParameterService.register(new SchemaParameters());
    }

    @After
    public void teardown() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ModelService.clear();
    }

    @Test
    public void testAxStateFacade() {
        AxTaskFacade taskFacade = new AxTaskFacade(axTaskMock);

        assertEquals("Task0", taskFacade.getTaskName());
        assertEquals("Task0:0.0.1", taskFacade.getId());

        try {
            taskFacade.getInFieldSchemaHelper("InFieldDoesntExist");
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("no incoming field with name \"InFieldDoesntExist\" " + "defined on task \"Task0:0.0.1\"",
                            exc.getMessage());
        }

        try {
            taskFacade.getOutFieldSchemaHelper("OutFieldDoesntExist");
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("no outgoing field with name \"OutFieldDoesntExist\" " + "defined on task \"Task0:0.0.1\"",
                            exc.getMessage());
        }

        assertNotNull(taskFacade.getInFieldSchemaHelper("InField0"));
        assertNotNull(taskFacade.getOutFieldSchemaHelper("OutField0"));

        try {
            taskFacade.getInFieldSchemaHelper("InFieldBad");
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("schema helper cannot be created for task field \"InFieldBad\" "
                            + "with key \"null\" with schema \"null\"", exc.getMessage());
        }

        try {
            taskFacade.getOutFieldSchemaHelper("OutFieldBad");
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("schema helper cannot be created for task field \"OutFieldBad\" "
                            + "with key \"null\" with schema \"null\"", exc.getMessage());
        }

    }
}
