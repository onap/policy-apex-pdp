/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021  Nordix Foundation
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

package org.onap.policy.apex.service.engine.event.impl.enevent;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;

public class ApexEvent2EnEventConverterTest {
    private ApexEvent2EnEventConverter converter;
    private final Random random = new Random();

    @Before
    public void setUp() throws Exception {
        converter = new ApexEvent2EnEventConverter(null);
    }

    @Test(expected = ApexEventException.class)
    public void toApexEventNull() throws ApexException {
        final String eventName = RandomStringUtils.randomAlphabetic(3);
        converter.toApexEvent(eventName, null);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void toApexEventWrongClass() throws ApexException {
        final String eventName = RandomStringUtils.randomAlphabetic(3);
        final String name = RandomStringUtils.randomAlphanumeric(5);
        final String version = RandomStringUtils.randomAlphanumeric(6);
        final String nameSpace = "a" + RandomStringUtils.randomAlphanumeric(7);
        final String source = RandomStringUtils.randomAlphanumeric(8);
        final String target = RandomStringUtils.randomAlphanumeric(9);

        final ApexEvent event = new ApexEvent(name, version, nameSpace, source, target);

        converter.toApexEvent(eventName, event);
    }

    @Test
    public void toApex() throws ApexException {
        // prepare String values for events
        final String name = RandomStringUtils.randomAlphabetic(5);
        final String version = RandomStringUtils.randomAlphabetic(6);
        final String nameSpace = "b" + RandomStringUtils.randomAlphabetic(7);
        final String source = RandomStringUtils.randomAlphabetic(8);
        final String target = RandomStringUtils.randomAlphabetic(9);
        final int executionId = random.nextInt(1000);
        final String exceptionMessage = RandomStringUtils.randomAlphabetic(11);

        // prepare events
        final AxEvent axEvent = new AxEvent();
        axEvent.getKey().setName(name);
        axEvent.getKey().setVersion(version);
        axEvent.setNameSpace(nameSpace);
        axEvent.setSource(source);
        axEvent.setTarget(target);
        final EnEvent enEvent = new EnEvent(axEvent);
        enEvent.setExecutionId(executionId);
        enEvent.setExceptionMessage(exceptionMessage);

        // prepare expected event
        final ApexEvent apexEvent = new ApexEvent(name, version, nameSpace, source, target);
        apexEvent.setExecutionId(executionId);
        apexEvent.setExceptionMessage(exceptionMessage);
        final Object[] expected = {apexEvent};

        // Test
        final List<ApexEvent> actual = converter.toApexEvent(RandomStringUtils.randomAlphabetic(3), enEvent);
        assertArrayEquals(expected, actual.toArray());
    }
}