/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.adaptive;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.impl.ContextAlbumImpl;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.core.engine.executor.context.TaskSelectionExecutionContext;
import org.onap.policy.apex.examples.adaptive.concepts.AutoLearn;
import org.onap.policy.apex.examples.adaptive.model.java.AutoLearnPolicyDecideTaskSelectionLogic;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.common.parameters.ParameterService;
import org.springframework.util.ReflectionUtils;


class AutoLearnTaskSelectionTest {

    private SchemaParameters schemaParameters;


    @BeforeEach
    void beforeTest() {
        schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());
        ParameterService.register(schemaParameters);
    }

    /**
     * After test.
     */
    @AfterEach
    void afterTest() {
        ParameterService.deregister(schemaParameters);
    }

    @Test
    void testGetTask() {
        var key = new AxArtifactKey();
        var axEvent = new AxEvent(key);
        ModelService.registerModel(AxEvent.class, axEvent);
        var model = new AxPolicyModel(key);
        model.setAlbums(new AxContextAlbums(key));
        ModelService.registerModel(AxModel.class, model);

        var event0Key = new AxArtifactKey("Event0:0.0.1");
        var event0 = new AxEvent(event0Key, "a.name.space", "source", "target");
        var schemas = new AxContextSchemas(event0Key);
        var axField = new AxField();
        axField.setSchema(schemas.getKey());
        event0.getParameterMap().put("MonitoredValue", axField);
        var events = new AxEvents();
        events.getEventMap().put(event0Key, event0);

        ModelService.registerModel(AxEvents.class, events);
        var simpleDoubleSchema = new AxContextSchema(new AxArtifactKey(event0Key),
                "JAVA", "java.lang.Double");
        schemas.getSchemasMap().put(simpleDoubleSchema.getKey(), simpleDoubleSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        var event = new EnEvent(event0Key);
        event.put("MonitoredValue", 100.0D);

        var autoLearnLogic = new AutoLearnPolicyDecideTaskSelectionLogic();
        var internalContext = mock(ApexInternalContext.class);


        var executionContext =
                spy(new TaskSelectionExecutionContext(mock(TaskSelectExecutor.class), 15326, new AxState(),
                event, new AxArtifactKey(), internalContext));

        assertThatThrownBy(() -> autoLearnLogic.getTask(executionContext)).isInstanceOf(ContextRuntimeException.class)
                .hasMessageContaining("cannot find definition of context album \"AutoLearnAlbum\"");


        var taskKey = new AxArtifactKey("task1", "0.0.1");
        var task = new AxTask(taskKey);
        task.setInputEvent(event0);

        var tasks = new AxTasks(new AxArtifactKey("task1", "0.0.1"),
                Map.of(new AxArtifactKey(taskKey), task));
        ModelService.registerModel(AxTasks.class, tasks);

        var axState = new AxState(new AxReferenceKey("axstate:0.0.1:newtask:test"));
        axState.setTaskReferences(Map.of(taskKey, new AxStateTaskReference()));

        var executionContext2 =
                spy(new TaskSelectionExecutionContext(mock(TaskSelectExecutor.class), 15326, axState,
                        event, new AxArtifactKey(), internalContext));

        doReturn(mock(ContextAlbumImpl.class)).when(executionContext2).getContextAlbum("AutoLearnAlbum");
        assertDoesNotThrow(() -> autoLearnLogic.getTask(executionContext2));

    }

    @Test
    void testGetOption() {
        var autoLearn = new AutoLearn();
        autoLearn.setCounts(List.of(2L));
        autoLearn.setAvDiffs(List.of(2.0, 3.0));
        var getOption = ReflectionUtils.findMethod(AutoLearnPolicyDecideTaskSelectionLogic.class,
                "getOption", double.class, AutoLearn.class);
        assertNotNull(getOption);

        var size = ReflectionUtils.findField(AutoLearnPolicyDecideTaskSelectionLogic.class, "size");

        ReflectionUtils.makeAccessible(getOption);
        ReflectionUtils.makeAccessible(size);
        var autoLearnTaskSelectionLogic = spy(new AutoLearnPolicyDecideTaskSelectionLogic());
        ReflectionUtils.setField(size, autoLearnTaskSelectionLogic, 2);
        assertDoesNotThrow(() -> ReflectionUtils.invokeMethod(getOption, autoLearnTaskSelectionLogic, 2.0,
                autoLearn));
    }

    @Test
    void testLearn() {
        var autoLearn = new AutoLearn();
        autoLearn.setCounts(List.of(2L, 3L));
        autoLearn.setAvDiffs(List.of(2.0, 3.0));
        var learn = ReflectionUtils.findMethod(AutoLearnPolicyDecideTaskSelectionLogic.class, "learn", int.class,
                double.class, AutoLearn.class);
        assertNotNull(learn);
        ReflectionUtils.makeAccessible(learn);
        var autoLearnTaskSelectionLogic = spy(new AutoLearnPolicyDecideTaskSelectionLogic());
        assertDoesNotThrow(() -> ReflectionUtils.invokeMethod(learn, autoLearnTaskSelectionLogic, 1, 2.0,
                autoLearn));
    }
}
