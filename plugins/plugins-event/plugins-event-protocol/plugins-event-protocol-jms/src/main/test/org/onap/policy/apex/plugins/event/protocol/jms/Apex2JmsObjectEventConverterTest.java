package org.onap.policy.apex.plugins.event.protocol.jms;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.*;

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

    @Test(expected = NullPointerException.class)
    public void initNull() {
        converter.init(null);
    }

    @Test
    public void initWrongClass() {
        converter.init(new ApexEventProtocolParameters());
        final String actual = testOutStream.toString();
        assertTrue(actual.contains("specified Event Protocol Parameters properties of typ"));
        assertNull(converter.getEventProtocolParameters());
    }

    @Test
    public void init() {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();
        converter.init(parameters);
        final JmsObjectEventProtocolParameters actual = converter.getEventProtocolParameters();
        assertSame(parameters, actual);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void toApexEventNull() throws ApexEventException {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();
        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        converter.toApexEvent(eventName, null);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void toApexEventObject() throws ApexEventException {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();
        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        converter.toApexEvent(eventName, new Object());
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void toApexEventNoParams() throws ApexEventException {
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        ObjectMessage object = new ActiveMQObjectMessage();
        converter.toApexEvent(eventName, object);
    }

    @Test(expected = NullPointerException.class)
    public void toApexEventIncomingObjectIsNull() throws ApexEventException {
        final JmsObjectEventProtocolParameters parameters = new JmsObjectEventProtocolParameters();

        converter.init(parameters);
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        ObjectMessage object = new ActiveMQObjectMessage();
        converter.toApexEvent(eventName, object);
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

    @Test(expected = ApexEventException.class)
    public void fromApexEventNull() throws ApexEventException {
        converter.fromApexEvent(null);
    }

    @Test(expected = ApexEventException.class)
    public void fromApexEventEmptyEvent() throws ApexEventException {
        final ApexEvent apexEvent = new ApexEvent(
                "a" + RandomStringUtils.randomAlphabetic(3),
                "a" + RandomStringUtils.randomAlphabetic(3),
                "a" + RandomStringUtils.randomAlphabetic(3),
                "",
                "");
        converter.fromApexEvent(apexEvent);
    }

    @Test(expected = ApexEventException.class)
    public void fromApexEventMultipleEvents() throws ApexEventException {
        final ApexEvent apexEvent = new ApexEvent(
                "a" + RandomStringUtils.randomAlphabetic(3),
                "a" + RandomStringUtils.randomAlphabetic(4),
                "a" + RandomStringUtils.randomAlphabetic(5),
                "",
                "");
        apexEvent.put(RandomStringUtils.randomAlphabetic(2), new Object());
        apexEvent.put(RandomStringUtils.randomAlphabetic(6), new Object());
        converter.fromApexEvent(apexEvent);
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