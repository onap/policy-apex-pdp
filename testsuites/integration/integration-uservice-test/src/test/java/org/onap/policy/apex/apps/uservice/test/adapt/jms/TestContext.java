/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.apps.uservice.test.adapt.jms;

import static org.onap.policy.apex.apps.uservice.test.adapt.jms.TestJMS2JMS.HOST;
import static org.onap.policy.apex.apps.uservice.test.adapt.jms.TestJMS2JMS.JMS_TOPIC_APEX_IN;
import static org.onap.policy.apex.apps.uservice.test.adapt.jms.TestJMS2JMS.JMS_TOPIC_APEX_OUT;
import static org.onap.policy.apex.apps.uservice.test.adapt.jms.TestJMS2JMS.PORT;
import static org.onap.policy.apex.apps.uservice.test.adapt.jms.TestJMS2JMS.connectionFactory;

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
import javax.naming.NamingException;

import org.apache.activemq.command.ActiveMQTopic;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContext implements Context {

    private Properties testProperties;

    public TestContext() {
        try {
            testProperties = new Properties();

            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("host", HOST);
            params.put("port", PORT);
            testProperties.put("ConnectionFactory", connectionFactory);
            testProperties.put(JMS_TOPIC_APEX_IN, new ActiveMQTopic(JMS_TOPIC_APEX_IN));
            testProperties.put(JMS_TOPIC_APEX_OUT, new ActiveMQTopic(JMS_TOPIC_APEX_OUT));
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ApexRuntimeException("Context initiation failed", e);
        }
    }

    @Override
    public Object lookup(final Name name) throws NamingException {
        return null;
    }

    @Override
    public Object lookup(final String name) throws NamingException {
        return testProperties.get(name);
    }

    @Override
    public void bind(final Name name, final Object obj) throws NamingException {}

    @Override
    public void bind(final String name, final Object obj) throws NamingException {}

    @Override
    public void rebind(final Name name, final Object obj) throws NamingException {}

    @Override
    public void rebind(final String name, final Object obj) throws NamingException {}

    @Override
    public void unbind(final Name name) throws NamingException {}

    @Override
    public void unbind(final String name) throws NamingException {}

    @Override
    public void rename(final Name oldName, final Name newName) throws NamingException {}

    @Override
    public void rename(final String oldName, final String newName) throws NamingException {}

    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<NameClassPair> list(final String name) throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(final String name) throws NamingException {
        return null;
    }

    @Override
    public void destroySubcontext(final Name name) throws NamingException {}

    @Override
    public void destroySubcontext(final String name) throws NamingException {}

    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        return null;
    }

    @Override
    public Context createSubcontext(final String name) throws NamingException {
        return null;
    }

    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return null;
    }

    @Override
    public Object lookupLink(final String name) throws NamingException {
        return null;
    }

    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return null;
    }

    @Override
    public NameParser getNameParser(final String name) throws NamingException {
        return null;
    }

    @Override
    public Name composeName(final Name name, final Name prefix) throws NamingException {
        return null;
    }

    @Override
    public String composeName(final String name, final String prefix) throws NamingException {
        return null;
    }

    @Override
    public Object addToEnvironment(final String propName, final Object propVal) throws NamingException {
        return null;
    }

    @Override
    public Object removeFromEnvironment(final String propName) throws NamingException {
        return null;
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return null;
    }

    @Override
    public void close() throws NamingException {}

    @Override
    public String getNameInNamespace() throws NamingException {
        return null;
    }

}
