/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021, 2024 Nordix Foundation
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

package org.onap.policy.apex.core.engine.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the engine event class.
 */
class EnEventTest {
    /**
     * Set up the services.
     */
    @BeforeEach
    void setupServices() {
        ModelService.registerModel(AxContextSchemas.class, new AxContextSchemas());
        ModelService.registerModel(AxEvents.class, new AxEvents());
        ParameterService.register(new SchemaParameters());
    }

    /**
     * Tear down the services.
     */
    @AfterEach
    void teardownServices() {
        ModelService.deregisterModel(AxContextSchema.class);
        ModelService.deregisterModel(AxEvents.class);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    @Test
    void testEnEvent() {
        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        assertThatThrownBy(() -> new EnEvent(eventKey))
            .hasMessage("event definition is null or was not found in model service");
        assertThatThrownBy(() -> new EnEvent((AxEvent) null))
            .hasMessage("event definition is null or was not found in model service");
        AxEvent axEvent = new AxEvent(eventKey, "a.name.space", "some source", "some target");
        ModelService.getModel(AxEvents.class).getEventMap().put(eventKey, axEvent);

        EnEvent event = new EnEvent(eventKey);
        assertEquals(eventKey, event.getKey());
        assertEquals("Event:0.0.1", event.getId());
        assertEquals("Event", event.getName());
        assertEquals(axEvent, event.getAxEvent());
        event.setExecutionId(123454321L);
        assertEquals(123454321L, event.getExecutionId());
        event.setExceptionMessage("Something happened");
        assertEquals("Something happened", event.getExceptionMessage());
        AxConcept[] usedArtifactStackArray =
            {eventKey};
        event.setUserArtifactStack(usedArtifactStackArray);
        assertEquals(usedArtifactStackArray.length, event.getUserArtifactStack().length);
        assertEquals("EnEvent [axEvent=AxEvent:(key=AxArtifactKey:(name=Event,version=0.0.1),nameSpace=a.name.space,"
            + "source=some source,target=some target,parameter={},toscaPolicyState=), "
            + "userArtifactStack=[AxArtifactKey:(name=Event,version=0.0.1)], map={}]", event.toString());
        assertThatThrownBy(() -> event.put(null, null))
            .hasMessage("null keys are illegal on method parameter \"key\"");
        assertThatThrownBy(() -> event.put("NonField", null))
            .hasMessage("parameter with key \"NonField\" not defined on event \"Event\"");
    }

    @Test
    void testAxEvent() {
        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        AxEvent axEvent = new AxEvent(eventKey, "a.name.space", "some source", "some target");
        ModelService.getModel(AxEvents.class).getEventMap().put(eventKey, axEvent);
        EnEvent event = new EnEvent(eventKey);

        AxReferenceKey fieldKey = new AxReferenceKey("Parent", "0.0.1", "MyParent", "MyField");
        AxArtifactKey fieldSchemaKey = new AxArtifactKey("FieldSchema:0.0.1");
        AxField axField = new AxField(fieldKey, fieldSchemaKey);

        AxConcept[] usedArtifactStackArrayMultiple =
            {eventKey, fieldKey, new DummyAxKey()};
        event.setUserArtifactStack(usedArtifactStackArrayMultiple);

        AxContextSchema schema = new AxContextSchema(fieldSchemaKey, "Java", "java.lang.Integer");
        ModelService.getModel(AxContextSchemas.class).getSchemasMap().put(fieldSchemaKey, schema);

        Map<String, AxField> parameterMap = new LinkedHashMap<>();
        parameterMap.put("MyField", axField);
        ModelService.getModel(AxEvents.class).get(eventKey).setParameterMap(parameterMap);

        event.put("MyField", null);
        assertNull(event.get("MyField"));

        assertThatThrownBy(() -> event.put("MyField", "Hello"))
            .hasMessage("Parent:0.0.1:MyParent:MyField: object \"Hello\" of class \"java.lang.String\" "
                + "not compatible with class \"java.lang.Integer\"");
        event.put("MyField", 123);
        assertEquals(123, event.get("MyField"));

        assertTrue(event.containsKey("MyField"));
        assertTrue(event.containsValue(123));
        assertEquals("MyField", event.entrySet().iterator().next().getKey());

        event.putAll(event);

        assertThatThrownBy(() -> event.get(null))
            .hasMessage("null values are illegal on method parameter \"key\"");
        assertThatThrownBy(() -> event.get("NonField"))
            .hasMessage("parameter with key NonField not defined on this event");
        assertThatThrownBy(() -> event.remove(null))
            .hasMessage("null keys are illegal on method parameter \"key\"");
        assertThatThrownBy(() -> event.remove("NonField"))
            .hasMessage("parameter with key NonField not defined on this event");
        event.remove("MyField");
        assertNull(event.get("MyField"));

        event.put("MyField", 123);
        assertEquals(123, event.get("MyField"));
        event.clear();
        assertNull(event.get("MyField"));

        assertNotEquals(0, event.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(event, event); // NOSONAR
        assertNotNull(event);
        Map<String, Object> hashMap = new HashMap<>();
        assertNotEquals(event, hashMap);

        EnEvent otherEvent = new EnEvent(eventKey);
        assertEquals(event, otherEvent);
    }
}