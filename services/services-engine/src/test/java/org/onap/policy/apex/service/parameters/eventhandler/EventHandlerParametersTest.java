/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.parameters.eventhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

class EventHandlerParametersTest {

    private EventHandlerParameters handlerParams;

    @BeforeEach
    void setUp() {
        handlerParams = new EventHandlerParameters();
    }

    @Test
    void testConstructor() {
        assertFalse(handlerParams.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS));
        assertFalse(handlerParams.isPeeredMode(EventHandlerPeeredMode.REQUESTOR));
        assertNull(handlerParams.getSynchronousPeer());
        assertNull(handlerParams.getRequestorPeer());
        assertEquals(0, handlerParams.getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
        assertEquals(0, handlerParams.getPeerTimeout(EventHandlerPeeredMode.REQUESTOR));
        assertNull(handlerParams.getEventName());
        assertNull(handlerParams.getEventNameFilter());
    }

    @Test
    void testSetAndGetCarrierTechnologyParameters() {
        CarrierTechnologyParameters carrierParams = mock(CarrierTechnologyParameters.class);
        handlerParams.setCarrierTechnologyParameters(carrierParams);
        assertEquals(carrierParams, handlerParams.getCarrierTechnologyParameters());
    }

    @Test
    void testSetAndGetEventProtocolParameters() {
        EventProtocolParameters protocolParams = mock(EventProtocolParameters.class);
        handlerParams.setEventProtocolParameters(protocolParams);
        assertEquals(protocolParams, handlerParams.getEventProtocolParameters());
    }

    @Test
    void testCheckSetName() {
        assertTrue(handlerParams.checkSetName());
        handlerParams.setName(null);
        assertFalse(handlerParams.checkSetName());
        handlerParams.setName(" ");
        assertFalse(handlerParams.checkSetName());
        handlerParams.setName("ValidName");
        assertTrue(handlerParams.checkSetName());
    }

    @Test
    void testSetPeeredMode() {
        handlerParams.setPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS, true);
        assertTrue(handlerParams.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS));
        handlerParams.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
        assertTrue(handlerParams.isPeeredMode(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testSetAndGetPeer() {
        handlerParams.setPeer(EventHandlerPeeredMode.SYNCHRONOUS, "SyncPeer");
        assertEquals("SyncPeer", handlerParams.getPeer(EventHandlerPeeredMode.SYNCHRONOUS));
        handlerParams.setPeer(EventHandlerPeeredMode.REQUESTOR, "RequestorPeer");
        assertEquals("RequestorPeer", handlerParams.getPeer(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testSetAndGetPeerTimeout() {
        handlerParams.setPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS, 5000);
        assertEquals(5000, handlerParams.getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
        handlerParams.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 7000);
        assertEquals(7000, handlerParams.getPeerTimeout(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testIsSetEventName() {
        assertFalse(handlerParams.isSetEventName());
        handlerParams.setEventName("TestEvent");
        assertTrue(handlerParams.isSetEventName());
    }

    @Test
    void testIsSetEventNameFilter() {
        assertFalse(handlerParams.isSetEventNameFilter());
        handlerParams.setEventNameFilter(".*");
        assertTrue(handlerParams.isSetEventNameFilter());
    }

    @Test
    void testValidateValidFilter() {
        handlerParams.setEventNameFilter(".*");
        BeanValidationResult result = handlerParams.validate();
        assertEquals(ValidationStatus.INVALID, result.getStatus());
    }

    @Test
    void testValidateInvalidFilter() {
        handlerParams.setEventNameFilter("[invalid_regex");
        BeanValidationResult result = handlerParams.validate();
        assertEquals(ValidationStatus.INVALID, result.getStatus());
        assertFalse(result.getMessage().contains("not a valid regular expression"));
    }

    @Test
    void testValidateNullFilter() {
        handlerParams.setEventNameFilter(null);
        BeanValidationResult result = handlerParams.validate();
        assertEquals(ValidationStatus.INVALID, result.getStatus());
    }

    @Test
    void testPeeredModeDefaultCases() {
        assertNull(handlerParams.getPeer(EventHandlerPeeredMode.REQUESTOR));
        handlerParams.setPeer(EventHandlerPeeredMode.REQUESTOR, "test");
        assertEquals(0, handlerParams.getPeerTimeout(EventHandlerPeeredMode.REQUESTOR));
        handlerParams.setPeerTimeout(EventHandlerPeeredMode.REQUESTOR, 1000);
        handlerParams.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
    }
}
