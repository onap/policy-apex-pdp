/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada.
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

package org.onap.policy.apex.model.eventmodel.concepts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test events.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class EventsTest {

    @Test
    void testEvents() {
        final TreeMap<String, AxField> parameterMap = new TreeMap<>();
        final TreeMap<String, AxField> parameterMapEmpty = new TreeMap<>();

        final AxEvent event = new AxEvent();

        final AxArtifactKey eventKey = new AxArtifactKey("EventName", "0.0.1");
        event.setKey(eventKey);
        assertEquals("EventName:0.0.1", event.getKey().getId());
        assertEquals("EventName:0.0.1", event.getKeys().get(0).getId());

        event.setNameSpace("namespace");
        assertEquals("namespace", event.getNameSpace());

        event.setSource("source");
        assertEquals("source", event.getSource());

        event.setTarget("target");
        assertEquals("target", event.getTarget());

        event.setParameterMap(parameterMap);
        assertEquals(0, event.getParameterMap().size());

        event.setToscaPolicyState(AxToscaPolicyProcessingStatus.ENTRY.name());
        assertEquals(AxToscaPolicyProcessingStatus.ENTRY.name(), event.getToscaPolicyState());

        final AxField eventField =
            new AxField(new AxReferenceKey(eventKey, "Field0"), new AxArtifactKey("Field0Schema", "0.0.1"));
        event.getParameterMap().put(eventField.getKey().getLocalName(), eventField);
        assertEquals(1, event.getParameterMap().size());

        final AxField eventFieldBadParent =
            new AxField(new AxReferenceKey(new AxArtifactKey("OtherEvent", "0.0.01"), "Field0"),
                new AxArtifactKey("Field0Schema", "0.0.1"));

        final AxArtifactKey newEventKey = new AxArtifactKey("NewEventName", "0.0.1");
        event.setKey(newEventKey);
        assertEquals("NewEventName:0.0.1", event.getKey().getId());
        assertEquals("NewEventName:0.0.1", event.getKeys().get(0).getId());
        assertEquals("NewEventName:0.0.1",
            event.getParameterMap().get("Field0").getKey().getParentArtifactKey().getId());
        event.setKey(eventKey);
        assertEquals("EventName:0.0.1", event.getKey().getId());
        assertEquals("EventName:0.0.1", event.getKeys().get(0).getId());

        assertTrue(event.getFields().contains(eventField));
        assertTrue(event.hasFields(new TreeSet<>(parameterMap.values())));

        assertValidationResults_SingleEvent(event, eventKey, eventField, eventFieldBadParent);

        event.clean();
        event.buildReferences();
        assertNotEquals(AxKey.NULL_KEY_NAME,
            event.getParameterMap().values().iterator().next().getKey().getParentKeyName());

        assertCompareTo(event, parameterMap, eventKey, parameterMapEmpty);

        assertNotNull(event.getKeys());

        final AxEvents events = new AxEvents();
        assertValidationResult_GroupEvents(events, eventKey, event);

        events.clean();
        event.buildReferences();
        assertNotEquals(AxKey.NULL_KEY_NAME,
            event.getParameterMap().values().iterator().next().getKey().getParentKeyName());

        assertCompareToSpecificEvents(events, event, eventKey);
    }

    private static void assertValidationResults_SingleEvent(AxEvent event, AxArtifactKey eventKey, AxField eventField,
                                  AxField eventFieldBadParent) {
        AxValidationResult result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        event.setKey(eventKey);
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.setNameSpace("");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.WARNING, result.getValidationResult());

        event.setNameSpace("namespace");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.setSource("");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        event.setSource("source");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.setTarget("");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        event.setTarget("target");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.getParameterMap().put(AxKey.NULL_KEY_NAME, null);
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        event.getParameterMap().remove(AxKey.NULL_KEY_NAME);
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.getParameterMap().put("NullField", null);
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        event.getParameterMap().remove("NullField");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.getParameterMap().put("NullField", eventField);
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        event.getParameterMap().remove("NullField");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.getParameterMap().put("BadParent", eventFieldBadParent);
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        event.getParameterMap().remove("BadParent");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        event.setToscaPolicyState("invalid_enum");
        result = new AxValidationResult();
        result = event.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        event.setToscaPolicyState(AxToscaPolicyProcessingStatus.ENTRY.name());
    }

    private static void assertCompareTo(AxEvent event, TreeMap<String, AxField> parameterMap,
                                        AxArtifactKey eventKey, TreeMap<String, AxField> parameterMapEmpty) {
        final AxEvent clonedEvent = new AxEvent(event);
        assertEquals("AxEvent:(key=AxArtifactKey:(name=EventName,version=0.0.1),nameSpace=namespace",
            clonedEvent.toString().substring(0, 77));

        assertNotEquals(0, event.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(event, event); // NOSONAR
        assertEquals(event, clonedEvent);
        assertNotNull(event);

        Object helloObj = "Hello";
        assertNotEquals(event, helloObj);
        assertNotEquals(event, new AxEvent(AxArtifactKey.getNullKey(), "namespace", "source", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name()));
        assertNotEquals(event, new AxEvent(eventKey, "namespace1", "source", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name()));
        assertNotEquals(event, new AxEvent(eventKey, "namespace", "source2", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name()));
        assertNotEquals(event, new AxEvent(eventKey, "namespace", "source", "target3", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name()));
        assertNotEquals(event, new AxEvent(eventKey, "namespace", "source", "target", parameterMapEmpty,
            AxToscaPolicyProcessingStatus.ENTRY.name()));
        assertEquals(event, new AxEvent(eventKey, "namespace", "source", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name()));

        assertEquals(0, event.compareTo(event));
        assertEquals(0, event.compareTo(clonedEvent));
        assertNotEquals(0, event.compareTo(new AxArtifactKey()));
        assertNotEquals(0, event.compareTo(null));
        assertNotEquals(0, event.compareTo(new AxEvent(AxArtifactKey.getNullKey(), "namespace", "source", "target",
            parameterMap, AxToscaPolicyProcessingStatus.ENTRY.name())));
        assertNotEquals(0, event.compareTo(new AxEvent(eventKey, "namespace1", "source", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name())));
        assertNotEquals(0, event.compareTo(new AxEvent(eventKey, "namespace", "source2", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name())));
        assertNotEquals(0, event.compareTo(new AxEvent(eventKey, "namespace", "source", "target3", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name())));
        assertNotEquals(0, event.compareTo(new AxEvent(eventKey, "namespace", "source", "target", parameterMapEmpty,
            AxToscaPolicyProcessingStatus.ENTRY.name())));
        assertEquals(0, event.compareTo(new AxEvent(eventKey, "namespace", "source", "target", parameterMap,
            AxToscaPolicyProcessingStatus.ENTRY.name())));
    }

    private static void assertValidationResult_GroupEvents(AxEvents events, AxArtifactKey eventKey, AxEvent event) {
        AxValidationResult result;
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        // Invalid, no events in event map
        events.setKey(new AxArtifactKey("EventsKey", "0.0.1"));
        assertEquals("EventsKey:0.0.1", events.getKey().getId());

        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        events.getEventMap().put(eventKey, event);
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        events.getEventMap().put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        events.getEventMap().remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        events.getEventMap().put(new AxArtifactKey("NullValueKey", "0.0.1"), null);
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        events.getEventMap().remove(new AxArtifactKey("NullValueKey", "0.0.1"));
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        events.getEventMap().put(new AxArtifactKey("BadEventKey", "0.0.1"), event);
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        events.getEventMap().remove(new AxArtifactKey("BadEventKey", "0.0.1"));
        result = new AxValidationResult();
        result = events.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }

    private static void assertCompareToSpecificEvents(AxEvents events, AxEvent event, AxArtifactKey eventKey) {
        final AxEvents clonedEvents = new AxEvents(events);
        assertEquals("AxEvents:(key=AxArtifactKey:(name=EventsKey,version=0.0.1),e",
            clonedEvents.toString().substring(0, 60));

        assertNotEquals(0, events.hashCode());

        Object helloObj = "Hello";
        assertEquals(events, clonedEvents);
        assertNotNull(events);
        assertNotEquals(event, helloObj);
        assertNotEquals(events, new AxEvents(new AxArtifactKey()));

        assertEquals(0, events.compareTo(events));
        assertEquals(0, events.compareTo(clonedEvents));
        assertNotEquals(0, events.compareTo(null));
        assertNotEquals(0, events.compareTo(new AxArtifactKey()));
        assertNotEquals(0, events.compareTo(new AxEvents(new AxArtifactKey())));

        clonedEvents.get(eventKey).setSource("AnotherSource");
        assertNotEquals(0, events.compareTo(clonedEvents));

        assertEquals(events.getKey(), events.getKeys().get(0));

        assertEquals("EventName", events.get("EventName").getKey().getName());
        assertEquals("EventName", events.get("EventName", "0.0.1").getKey().getName());
        assertEquals(1, events.getAll("EventName", "0.0.1").size());
        assertEquals(0, events.getAll("NonExistentEventsName").size());
    }
}
