/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation
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

package org.onap.policy.apex.core.engine.executor.context;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the state facade.
 */
@ExtendWith(MockitoExtension.class)
class AxTaskFacadeTest {
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
    @BeforeEach
    void startMocking() {
        AxContextSchemas schemas = new AxContextSchemas();

        AxArtifactKey stringTypeKey = new AxArtifactKey("StringType:0.0.1");
        AxContextSchema stringType = new AxContextSchema(stringTypeKey, "Java", "java.lang.String");
        schemas.getSchemasMap().put(stringTypeKey, stringType);

        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxArtifactKey task0Key = new AxArtifactKey("Task0:0.0.1");
        Mockito.doReturn(task0Key).when(axTaskMock).getKey();
        Mockito.doReturn(task0Key.getId()).when(axTaskMock).getId();

        Map<String, AxField> inFieldMap = Map.of("InField0", axInputFieldMock, "InFieldBad", axInputFieldBadMock);
        Map<String, AxField> outFieldMap = Map.of("OutField0", axOutputFieldMock, "OutFieldBad", axOutputFieldBadMock);

        AxEvent inEvent = new AxEvent();
        inEvent.setParameterMap(inFieldMap);
        AxEvent outEvent = new AxEvent(new AxArtifactKey("outputEvent:1.0.0"));
        outEvent.setParameterMap(outFieldMap);
        Map<String, AxEvent> outEvents = new TreeMap<>();
        outEvents.put(outEvent.getKey().getName(), outEvent);

        Mockito.doReturn(inEvent).when(axTaskMock).getInputEvent();
        Mockito.doReturn(outEvents).when(axTaskMock).getOutputEvents();

        Mockito.doReturn(new AxReferenceKey(task0Key, "InField0")).when(axInputFieldMock).getKey();
        Mockito.doReturn(stringTypeKey).when(axInputFieldMock).getSchema();

        Mockito.doReturn(new AxReferenceKey(task0Key, "OutField0")).when(axOutputFieldMock).getKey();
        Mockito.doReturn(stringTypeKey).when(axOutputFieldMock).getSchema();

        ParameterService.register(new SchemaParameters());
    }

    @AfterEach
    void teardown() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ModelService.clear();
    }

    @Test
    void testAxStateFacade() {
        AxTaskFacade taskFacade = new AxTaskFacade(axTaskMock);

        assertEquals("Task0", taskFacade.getTaskName());
        assertEquals("Task0:0.0.1", taskFacade.getId());

        assertThatThrownBy(() -> taskFacade.getInFieldSchemaHelper("InFieldDoesntExist"))
            .hasMessage("no incoming field with name \"InFieldDoesntExist\" " + "defined on task "
                + "\"Task0:0.0.1\"");
        assertThatThrownBy(() -> taskFacade.getOutFieldSchemaHelper("OutFieldDoesntExist"))
            .hasMessage("no outgoing field with name \"OutFieldDoesntExist\" " + "defined on task "
                + "\"Task0:0.0.1\"");
        assertNotNull(taskFacade.getInFieldSchemaHelper("InField0"));
        assertNotNull(taskFacade.getOutFieldSchemaHelper("OutField0"));

        assertThatThrownBy(() -> taskFacade.getInFieldSchemaHelper("InFieldBad"))
            .hasMessage("schema helper cannot be created for task field \"InFieldBad\" "
                + "with key \"null\" with schema \"null\"");
        assertThatThrownBy(() -> taskFacade.getOutFieldSchemaHelper("OutFieldBad"))
            .hasMessage("schema helper cannot be created for task field \"OutFieldBad\" "
                + "with key \"null\" with schema \"null\"");
    }
}
