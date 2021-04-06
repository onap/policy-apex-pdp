package org.onap.policy.apex.plugins.event.protocol.jms;

import org.junit.Test;

import static org.junit.Assert.*;

public class JmsObjectEventProtocolParametersTest {

    @Test
    public void getIncomingEventVersion() {
        final JmsObjectEventProtocolParameters jmsObjectEventProtocolParameters = new JmsObjectEventProtocolParameters();
        final String actual = jmsObjectEventProtocolParameters.getIncomingEventVersion();
        assertEquals("1.0.0", actual);
    }

    @Test
    public void getIncomingEventSource() {
        final JmsObjectEventProtocolParameters jmsObjectEventProtocolParameters = new JmsObjectEventProtocolParameters();
        final String actual = jmsObjectEventProtocolParameters.getIncomingEventSource();
        assertEquals("JMS", actual);
    }

    @Test
    public void getIncomingEventTarget() {
        final JmsObjectEventProtocolParameters jmsObjectEventProtocolParameters = new JmsObjectEventProtocolParameters();
        final String actual = jmsObjectEventProtocolParameters.getIncomingEventTarget();
        assertEquals("Apex", actual);
    }
}