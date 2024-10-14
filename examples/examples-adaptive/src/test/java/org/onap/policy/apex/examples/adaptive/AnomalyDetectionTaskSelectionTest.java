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
import org.onap.policy.apex.examples.adaptive.model.java.AnomalyDetectionPolicyDecideTaskSelectionLogic;
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

class AnomalyDetectionTaskSelectionTest {

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
        event0.getParameterMap().put("Iteration", axField);
        var events = new AxEvents();
        events.getEventMap().put(event0Key, event0);

        ModelService.registerModel(AxEvents.class, events);
        var simpleDoubleSchema = new AxContextSchema(new AxArtifactKey(event0Key),
                "JAVA", "java.lang.Double");
        schemas.getSchemasMap().put(simpleDoubleSchema.getKey(), simpleDoubleSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        var event = new EnEvent(event0Key);
        event.put("MonitoredValue", 100.0D);

        var anomalyDetectionLogic = spy(new AnomalyDetectionPolicyDecideTaskSelectionLogic());
        var internalContext = mock(ApexInternalContext.class);


        var executionContext =
                spy(new TaskSelectionExecutionContext(mock(TaskSelectExecutor.class), 15326, new AxState(),
                        event, new AxArtifactKey(), internalContext));

        assertThatThrownBy(() -> anomalyDetectionLogic.getTask(executionContext))
                .isInstanceOf(ContextRuntimeException.class)
                .hasMessageContaining("cannot find definition of context album \"AnomalyDetectionAlbum\"");

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

        doReturn(mock(ContextAlbumImpl.class)).when(executionContext2).getContextAlbum("AnomalyDetectionAlbum");

        assertDoesNotThrow(() -> anomalyDetectionLogic.getTask(executionContext2));
    }

    @Test
    void testGetStats() {
        var getStats = ReflectionUtils.findMethod(AnomalyDetectionPolicyDecideTaskSelectionLogic.class,
                "getStatsTest", List.class, double.class);
        assertNotNull(getStats);
        ReflectionUtils.makeAccessible(getStats);
        var anomalyDetectionLogic = spy(new AnomalyDetectionPolicyDecideTaskSelectionLogic());
        assertDoesNotThrow(() -> ReflectionUtils.invokeMethod(getStats, anomalyDetectionLogic,
                List.of(35.5, 56.7, 89.7), 22.3));
    }

    @Test
    void testRemoveValue() {
        var removeValue = ReflectionUtils.findMethod(AnomalyDetectionPolicyDecideTaskSelectionLogic.class,
                "removevalue",
                Double[].class, double.class);
        assertNotNull(removeValue);
        ReflectionUtils.makeAccessible(removeValue);
        var anomalyDetectionLogic = spy(new AnomalyDetectionPolicyDecideTaskSelectionLogic());
        Double[] array = {22.1, 23.2};
        assertDoesNotThrow(() -> ReflectionUtils.invokeMethod(removeValue, anomalyDetectionLogic, array, 23.2));
    }

    @Test
    void testisAllEqual() {
        var isAllEqual = ReflectionUtils.findMethod(AnomalyDetectionPolicyDecideTaskSelectionLogic.class, "isAllEqual",
                Double[].class);
        assertNotNull(isAllEqual);
        ReflectionUtils.makeAccessible(isAllEqual);
        var anomalyDetectionLogic = spy(new AnomalyDetectionPolicyDecideTaskSelectionLogic());
        Double[] array = {22.1, 23.2, 22.1};
        assertDoesNotThrow(() -> ReflectionUtils.invokeMethod(isAllEqual, anomalyDetectionLogic, (Object) array));

    }

}
