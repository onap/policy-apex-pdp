/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2023-2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.jms;

import static org.onap.policy.apex.testsuites.integration.uservice.adapt.jms.TestJms2Jms.HOST;
import static org.onap.policy.apex.testsuites.integration.uservice.adapt.jms.TestJms2Jms.JMS_TOPIC_APEX_IN;
import static org.onap.policy.apex.testsuites.integration.uservice.adapt.jms.TestJms2Jms.JMS_TOPIC_APEX_OUT;
import static org.onap.policy.apex.testsuites.integration.uservice.adapt.jms.TestJms2Jms.PORT;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;

/**
 * The Class TestContext.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContext implements Context {

    private final Properties testProperties;

    /**
     * Instantiates a new test context.
     */
    public TestContext() {
        try {
            testProperties = new Properties();

            final Map<String, Object> params = new HashMap<>();
            params.put("host", HOST);
            params.put("port", PORT);
            testProperties.put("ConnectionFactory", new ActiveMQConnectionFactory(TestJms2Jms.SERVER_URI));
            testProperties.put(JMS_TOPIC_APEX_IN, new ActiveMQTopic(JMS_TOPIC_APEX_IN));
            testProperties.put(JMS_TOPIC_APEX_OUT, new ActiveMQTopic(JMS_TOPIC_APEX_OUT));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexRuntimeException("Context initiation failed", e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object lookup(final Name name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object lookup(final String name) {
        return testProperties.get(name);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void bind(final Name name, final Object obj) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void bind(final String name, final Object obj) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rebind(final Name name, final Object obj) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rebind(final String name, final Object obj) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unbind(final Name name) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unbind(final String name) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rename(final Name oldName, final Name newName) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void rename(final String oldName, final String newName) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NamingEnumeration<NameClassPair> list(final String name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NamingEnumeration<Binding> listBindings(final String name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void destroySubcontext(final Name name) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void destroySubcontext(final String name) {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Context createSubcontext(final Name name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Context createSubcontext(final String name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object lookupLink(final Name name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object lookupLink(final String name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NameParser getNameParser(final Name name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public NameParser getNameParser(final String name) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Name composeName(final Name name, final Name prefix) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String composeName(final String name, final String prefix) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object addToEnvironment(final String propName, final Object propVal) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object removeFromEnvironment(final String propName) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Hashtable<?, ?> getEnvironment() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void close() {
        // Not used here
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getNameInNamespace() {
        return null;
    }
}
