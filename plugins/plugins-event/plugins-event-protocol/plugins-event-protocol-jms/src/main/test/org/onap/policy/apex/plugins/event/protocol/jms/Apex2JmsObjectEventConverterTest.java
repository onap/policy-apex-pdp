/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2023 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.protocol.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;

public class Apex2JmsObjectEventConverterTest {
    private Apex2JmsObjectEventConverter converter;
    private final PrintStream orgOutBuffer = System.out;
    private ByteArrayOutputStream testOutStream;

    @Before
    public void setUp() throws Exception {
        converter = new Apex2JmsObjectEventConverter();
        testOutStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutStream));
    }

    @After
    public void tearDown() {
        System.setOut(orgOutBuffer);
    }

    @Test
    public void initNull() {
        assertThatThrownBy(() -> converter.init(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void initWrongClass() {
        converter.init(new ApexEventProtocolParameters());
        final String actual = testOutStream.toString();
        assertThat(actual).contains("specified Event Protocol Parameters properties of typ");
        assertNull(converter.getEventProtocolParameters());
    }

    @Test
    public void init() {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();
        converter.init(parameters);
        final JmsObjectEventProtocolParameters actual = converter.getEventProtocolParameters();
        assertSame(parameters, actual);
    }

    @Test
    public void toApexEventNull() {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();
        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        assertThatThrownBy(() -> converter.toApexEvent(eventName, null))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void toApexEventObject() {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();
        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        assertThatThrownBy(() -> converter.toApexEvent(eventName, new Object()))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void toApexEventNoParams() {
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        ObjectMessage object = new ActiveMQObjectMessage();
        assertThatThrownBy(() -> converter.toApexEvent(eventName, object))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void toApexEventIncomingObjectIsNull() {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();

        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        ObjectMessage object = new ActiveMQObjectMessage();
        assertThatThrownBy(() -> converter.toApexEvent(eventName, object))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void toApexEvent() throws ApexEventException, JMSException {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();

        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        final ObjectMessage object = new ActiveMQObjectMessage();
        final String value = RandomStringUtils.randomAlphabetic(3);
        object.setObject(value);

        // Prepare expected object
        final ApexEvent expectedEvent = new ApexEvent("String" + parameters.getIncomingEventSuffix(),
            parameters.getIncomingEventVersion(),
            "java.lang",
            parameters.getIncomingEventSource(),
            parameters.getIncomingEventTarget());
        // Overwrite executionId to match executionId of actual
        expectedEvent.setExecutionId(1);
        final Object[] expected = {expectedEvent};

        // Run tested method
        final List<ApexEvent> actual = converter.toApexEvent(eventName, object);
        // Overwrite executionId to match executionId of expected
        actual.get(0).setExecutionId(1);
        assertArrayEquals(expected, actual.toArray());
    }

    @Test
    public void fromApexEventNull() {
        assertThatThrownBy(() -> converter.fromApexEvent(null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void fromApexEventEmptyEvent() throws ApexEventException {
        final ApexEvent apexEvent = new ApexEvent(
            "a" + RandomStringUtils.randomAlphabetic(3),
            "a" + RandomStringUtils.randomAlphabetic(3),
            "a" + RandomStringUtils.randomAlphabetic(3),
            "",
            "");
        assertThatThrownBy(() -> converter.fromApexEvent(apexEvent))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void fromApexEventMultipleEvents() throws ApexEventException {
        final ApexEvent apexEvent = new ApexEvent(
            "a" + RandomStringUtils.randomAlphabetic(3),
            "a" + RandomStringUtils.randomAlphabetic(4),
            "a" + RandomStringUtils.randomAlphabetic(5),
            "",
            "");
        apexEvent.put(RandomStringUtils.randomAlphabetic(2), new Object());
        apexEvent.put(RandomStringUtils.randomAlphabetic(6), new Object());
        assertThatThrownBy(() -> converter.fromApexEvent(apexEvent)).isInstanceOf(ApexEventException.class);
    }

    @Test
    public void fromApexEventSingleEvent() throws ApexEventException {
        final ApexEvent apexEvent = new ApexEvent(
            "a" + RandomStringUtils.randomAlphabetic(3),
            "a" + RandomStringUtils.randomAlphabetic(3),
            "a" + RandomStringUtils.randomAlphabetic(3),
            "",
            "");

        final Object expected = new Object();
        apexEvent.put(RandomStringUtils.randomAlphabetic(2), expected);

        final Object actual = converter.fromApexEvent(apexEvent);

        assertSame(expected, actual);
    }
}
