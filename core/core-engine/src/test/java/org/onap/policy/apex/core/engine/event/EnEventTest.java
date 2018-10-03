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

package org.onap.policy.apex.core.engine.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.ContextRuntimeException;
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
public class EnEventTest {
    /**
     * Set up the services.
     */
    @Before
    public void setupServices() {
        ModelService.registerModel(AxContextSchemas.class, new AxContextSchemas());
        ModelService.registerModel(AxEvents.class, new AxEvents());
        ParameterService.register(new SchemaParameters());
    }

    /**
     * Tear down the services.
     */
    @After
    public void teardownServices() {
        ModelService.deregisterModel(AxContextSchema.class);
        ModelService.deregisterModel(AxEvents.class);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    @Test
    public void testEnEvent() {
        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        try {
            new EnEvent(eventKey);
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("event definition is null or was not found in model service", ee.getMessage());
        }

        try {
            new EnEvent((AxEvent) null);
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("event definition is null or was not found in model service", ee.getMessage());
        }

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
            { eventKey };
        event.setUserArtifactStack(usedArtifactStackArray);
        assertEquals(usedArtifactStackArray.length, event.getUserArtifactStack().length);
        assertEquals("EnEvent [axEvent=AxEvent:(key=AxArtifactKey:(name=Event,version=0.0.1),nameSpace=a.name.space,"
                        + "source=some source,target=some target,parameter={}), "
                        + "userArtifactStack=[AxArtifactKey:(name=Event,version=0.0.1)], map={}]", event.toString());
        try {
            event.put(null, null);
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("null keys are illegal on method parameter \"key\"", ee.getMessage());
        }

        try {
            event.put("NonField", null);
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("parameter with key \"NonField\" not defined on event \"Event\"", ee.getMessage());
        }

        AxReferenceKey fieldKey = new AxReferenceKey("Parent", "0.0.1", "MyParent", "MyField");
        AxArtifactKey fieldSchemaKey = new AxArtifactKey("FieldSchema:0.0.1");
        AxField axField = new AxField(fieldKey, fieldSchemaKey);

        AxConcept[] usedArtifactStackArrayMultiple =
            { eventKey, fieldKey, new DummyAxKey() };
        event.setUserArtifactStack(usedArtifactStackArrayMultiple);

        AxContextSchema schema = new AxContextSchema(fieldSchemaKey, "Java", "java.lang.Integer");
        ModelService.getModel(AxContextSchemas.class).getSchemasMap().put(fieldSchemaKey, schema);

        Map<String, AxField> parameterMap = new LinkedHashMap<>();
        parameterMap.put("MyField", axField);
        ModelService.getModel(AxEvents.class).get(eventKey).setParameterMap(parameterMap);

        try {
            event.put("MyField", null);
        } catch (ContextRuntimeException cre) {
            fail("test should throw an exception here");
        }
        assertNull(event.get("MyField"));

        try {
            event.put("MyField", "Hello");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException cre) {
            assertEquals("Parent:0.0.1:MyParent:MyField: object \"Hello\" of class \"java.lang.String\" "
                            + "not compatible with class \"java.lang.Integer\"", cre.getMessage());
        }

        event.put("MyField", 123);
        assertEquals(123, event.get("MyField"));

        assertTrue(event.keySet().contains("MyField"));
        assertTrue(event.values().contains(123));
        assertEquals("MyField", event.entrySet().iterator().next().getKey());

        event.putAll(event);

        try {
            event.get(null);
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("null values are illegal on method parameter \"key\"", ee.getMessage());
        }

        try {
            event.get("NonField");
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("parameter with key NonField not defined on this event", ee.getMessage());
        }

        try {
            event.remove(null);
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("null keys are illegal on method parameter \"key\"", ee.getMessage());
        }

        try {
            event.remove("NonField");
            fail("test should throw an exception here");
        } catch (EnException ee) {
            assertEquals("parameter with key NonField not defined on this event", ee.getMessage());
        }

        event.remove("MyField");
        assertNull(event.get("MyField"));

        event.put("MyField", 123);
        assertEquals(123, event.get("MyField"));
        event.clear();
        assertNull(event.get("MyField"));

        assertTrue(event.hashCode() != 0);

        assertTrue(event.equals(event));
        assertFalse(event.equals(null));
        Map<String, Object> hashMap = new HashMap<>();
        assertFalse(event.equals(hashMap));

        EnEvent otherEvent = new EnEvent(eventKey);
        assertTrue(event.equals(otherEvent));
    }
}
