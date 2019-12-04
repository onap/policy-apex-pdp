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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.jms;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * A factory for creating TestInitialContext objects.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestInitialContextFactory implements InitialContextFactory {

    private final Context context = new TestContext();

    /**
     * Instantiates a new test initial context factory.
     *
     * @throws NamingException the naming exception
     */
    public TestInitialContextFactory() throws NamingException {
        // Default constructor
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Context getInitialContext(final Hashtable<?, ?> environment) throws NamingException {
        return context;
    }
}
