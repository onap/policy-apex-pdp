/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventList;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;

class Apex2ApexEventConverterTest {
    private Apex2ApexEventConverter converter;

    @BeforeEach
    void setUp() {
        converter = new Apex2ApexEventConverter();
    }

    @Test
    void initWithNull() {
        assertThatThrownBy(() -> converter.init(null))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void init() {
        assertThatNoException()
            .isThrownBy(() -> converter.init(new ApexEventProtocolParameters()));
    }

    @Test
    void toApexEventWithNull() {
        final String eventName = RandomStringUtils.randomAlphanumeric(5);
        assertThatThrownBy(() -> converter.toApexEvent(eventName, null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void toApexEventWithNonApexEvent() {
        final String eventName = RandomStringUtils.randomAlphanumeric(5);
        assertThatThrownBy(() -> converter.toApexEvent(eventName, new Object()))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void toApexEmptyEvent() throws ApexEventException {
        final String eventName = RandomStringUtils.randomAlphanumeric(4);
        final String name = RandomStringUtils.randomAlphanumeric(5);
        final String version = RandomStringUtils.randomAlphanumeric(6);
        final String nameSpace = "a" + RandomStringUtils.randomAlphanumeric(7);
        final String source = RandomStringUtils.randomAlphanumeric(8);
        final String target = RandomStringUtils.randomAlphanumeric(9);
        final String toscaPolicyState = AxToscaPolicyProcessingStatus.ENTRY.name();

        final ApexEvent event = new ApexEvent(name, version, nameSpace, source, target, toscaPolicyState);
        final List<ApexEvent> result = converter.toApexEvent(eventName, event);
        assertThat(result).isEmpty();
    }

    @Test
    void toApexEventWithApexAndOtherFields() throws ApexEventException {
        final String eventName = RandomStringUtils.randomAlphanumeric(4);
        final String name1 = RandomStringUtils.randomAlphanumeric(5);
        final String version1 = RandomStringUtils.randomAlphanumeric(6);
        final String nameSpace1 = "a" + RandomStringUtils.randomAlphanumeric(7);
        final String source1 = RandomStringUtils.randomAlphanumeric(8);
        final String target1 = RandomStringUtils.randomAlphanumeric(9);
        final String toscaPolicyState1 = AxToscaPolicyProcessingStatus.ENTRY.name();

        final ApexEvent event = new ApexEvent(name1, version1, nameSpace1, source1, target1, toscaPolicyState1);

        final String key = RandomStringUtils.randomAlphabetic(3);
        event.put(key, new Object());
        final List<ApexEvent> result = converter.toApexEvent(eventName, event);
        Object[] expected = {event};
        assertArrayEquals(expected, result.toArray());
    }

    @Test
    void toApexEventWithApexAndList() throws ApexEventException {
        final String eventName = RandomStringUtils.randomAlphanumeric(4);
        final String name1 = RandomStringUtils.randomAlphanumeric(5);
        final String version1 = RandomStringUtils.randomAlphanumeric(6);
        final String nameSpace1 = "a" + RandomStringUtils.randomAlphanumeric(7);
        final String source1 = RandomStringUtils.randomAlphanumeric(8);
        final String target1 = RandomStringUtils.randomAlphanumeric(9);
        final String toscaPolicyState1 = AxToscaPolicyProcessingStatus.ENTRY.name();

        final ApexEvent event = new ApexEvent(name1, version1, nameSpace1, source1, target1, toscaPolicyState1);

        final ApexEventList eventList = new ApexEventList();
        eventList.add(event);

        final String name2 = RandomStringUtils.randomAlphanumeric(15);
        final String version2 = RandomStringUtils.randomAlphanumeric(16);
        final String nameSpace2 = "b" + RandomStringUtils.randomAlphanumeric(17);
        final String source2 = RandomStringUtils.randomAlphanumeric(18);
        final String target2 = RandomStringUtils.randomAlphanumeric(19);
        final String toscaPolicyState2 = AxToscaPolicyProcessingStatus.EXIT_SUCCESS.name();

        final ApexEvent parentEvent = new ApexEvent(name2, version2, nameSpace2, source2, target2, toscaPolicyState2);
        final String key = RandomStringUtils.randomAlphabetic(3);
        parentEvent.put(key, eventList);
        final List<ApexEvent> result = converter.toApexEvent(eventName, parentEvent);
        Object[] expected = {event};
        assertArrayEquals(expected, result.toArray());
    }

    @Test
    void toApexEventWithApexAndListAndOtherFields() throws ApexEventException {
        final String eventName = RandomStringUtils.randomAlphanumeric(4);
        final String name1 = RandomStringUtils.randomAlphanumeric(5);
        final String version1 = RandomStringUtils.randomAlphanumeric(6);
        final String nameSpace1 = "a" + RandomStringUtils.randomAlphanumeric(7);
        final String source1 = RandomStringUtils.randomAlphanumeric(8);
        final String target1 = RandomStringUtils.randomAlphanumeric(9);
        final String toscaPolicyState1 = AxToscaPolicyProcessingStatus.ENTRY.name();

        final ApexEvent event = new ApexEvent(name1, version1, nameSpace1, source1, target1, toscaPolicyState1);

        final ApexEventList eventList = new ApexEventList();
        eventList.add(event);

        final String name2 = RandomStringUtils.randomAlphanumeric(15);
        final String version2 = RandomStringUtils.randomAlphanumeric(16);
        final String nameSpace2 = "b" + RandomStringUtils.randomAlphanumeric(17);
        final String source2 = RandomStringUtils.randomAlphanumeric(18);
        final String target2 = RandomStringUtils.randomAlphanumeric(19);
        final String toscaPolicyState2 = AxToscaPolicyProcessingStatus.EXIT_FAILURE.name();

        final ApexEvent parentEvent = new ApexEvent(name2, version2, nameSpace2, source2, target2, toscaPolicyState2);
        final String key1 = RandomStringUtils.randomAlphabetic(3);
        final String key2 = RandomStringUtils.randomAlphabetic(2);
        parentEvent.put(key1, eventList);
        parentEvent.put(key2, new Object());
        assertThatThrownBy(() -> converter.toApexEvent(eventName, parentEvent))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void fromApexEventNull() {
        assertThatThrownBy(() -> converter.fromApexEvent(null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void fromApexEvent() throws ApexEventException {
        final String name1 = RandomStringUtils.randomAlphanumeric(5);
        final String version1 = RandomStringUtils.randomAlphanumeric(6);
        final String nameSpace1 = "a" + RandomStringUtils.randomAlphanumeric(7);
        final String source1 = RandomStringUtils.randomAlphanumeric(8);
        final String target1 = RandomStringUtils.randomAlphanumeric(9);
        final String toscaPolicyState1 = AxToscaPolicyProcessingStatus.ENTRY.name();

        final ApexEvent event = new ApexEvent(name1, version1, nameSpace1, source1, target1, toscaPolicyState1);

        final Object actual = converter.fromApexEvent(event);
        assertSame(event, actual);
    }

}