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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.plugins.event.carrier.cds.ApexCdsConsumer;
import org.onap.policy.apex.plugins.event.carrier.cds.ApexCdsProducer;
import org.onap.policy.apex.plugins.event.carrier.cds.CdsCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexCdsConsumerTest {
    ApexCdsConsumer apexKafkaConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventProducer apexKafkaProducer = null;

    /**
     * Set up testing.
     *
     * @throws ApexEventException on test set up errors.
     */
    @Before
    public void setUp() throws ApexEventException {
        apexKafkaConsumer = new ApexCdsConsumer();
        consumerParameters = new EventHandlerParameters();
        apexKafkaProducer = new ApexCdsProducer();
        consumerParameters
                .setCarrierTechnologyParameters(new CdsCarrierTechnologyParameters() {});

        assertThatThrownBy(() -> {
            apexKafkaConsumer.init("TestApexKafkaConsumer", consumerParameters, incomingEventReceiver);
        }).hasMessage("a CDS consumer may not be specified, CDS may only accept events");
    }

    @Test
    public void testStart() {
        assertThatThrownBy(() -> {
            apexKafkaConsumer.start();
        }).hasMessage("a CDS consumer may not be specified, CDS may only accept events");
    }

    @Test
    public void testGetName() {
        assertEquals(null, new ApexCdsConsumer().getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertThatThrownBy(() -> {
            apexKafkaConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR);
        }).hasMessage("a CDS consumer may not be specified, CDS may only accept events");
    }

    @Test
    public void testSetPeeredReference() {
        assertThatThrownBy(() -> {
            apexKafkaConsumer.setPeeredReference(null, null);
        }).hasMessage("a CDS consumer may not be specified, CDS may only accept events");
    }

    @Test()
    public void testStop() {
        assertThatThrownBy(() -> {
            new ApexCdsConsumer().stop();
        }).hasMessage("a CDS consumer may not be specified, CDS may only accept events");
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonKafkaCarrierTechnologyParameters() throws ApexEventException {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexKafkaConsumer.init("TestApexKafkaConsumer", consumerParameters, incomingEventReceiver);
    }

}
