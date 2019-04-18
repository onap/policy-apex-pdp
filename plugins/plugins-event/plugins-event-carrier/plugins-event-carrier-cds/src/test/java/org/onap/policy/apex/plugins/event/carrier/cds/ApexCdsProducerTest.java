/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.cds;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.plugins.event.carrier.cds.ApexCdsConsumer;
import org.onap.policy.apex.plugins.event.carrier.cds.ApexCdsProducer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexCdsProducerTest {
    ApexCdsProducer apexKafkaProducer = null;
    ApexCdsConsumer apexKafkaConsumer = null;
    EventHandlerParameters producerParameters = null;
    PeeredReference peeredReference = null;
    SynchronousEventCache synchronousEventCache = null;

    /**
     * Set up testing.
     */
    @Before
    public void setUp() throws Exception {
        apexKafkaProducer = new ApexCdsProducer();
        apexKafkaConsumer = new ApexCdsConsumer();
        producerParameters = new EventHandlerParameters();

    }

    @Test(expected = ApexEventException.class)
    public void testInit() throws ApexEventException {
        apexKafkaProducer.init("TestApexKafkaProducer", producerParameters);
    }

    @Test
    public void testGetName() {
        assertNull(apexKafkaProducer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexKafkaProducer.getPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS));
    }
}
