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

package org.onap.policy.apex.service.engine.event;

import static org.junit.Assert.assertNotNull;

import java.util.Map.Entry;

import org.junit.Test;
import org.onap.policy.apex.service.engine.event.impl.EventConsumerFactory;
import org.onap.policy.apex.service.engine.event.impl.EventProducerFactory;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;

import org.onap.policy.apex.service.parameters.ApexParameterException;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;

/**
 * Test Plugin Factories.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class TestPluginFactories {

    @Test
    public void testEventConsumerFactory() throws ApexEventException, ApexParameterException {
        final String[] args = {"-c", "src/test/resources/parameters/factoryGoodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        for (final Entry<String, EventHandlerParameters> ce : parameters.getEventInputParameters().entrySet()) {
            final ApexEventConsumer consumer = new EventConsumerFactory().createConsumer(
                    parameters.getEngineServiceParameters().getName() + "_consumer_" + ce.getKey(), ce.getValue());
            assertNotNull(consumer);
        }

        for (final Entry<String, EventHandlerParameters> pe : parameters.getEventOutputParameters().entrySet()) {
            final ApexEventProducer producer = new EventProducerFactory().createProducer(
                    parameters.getEngineServiceParameters().getName() + "_producer_" + pe.getKey(), pe.getValue());
            assertNotNull(producer);
        }
    }
}
