/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.aadm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineImpl;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.examples.aadm.concepts.ENodeBStatus;
import org.onap.policy.apex.examples.aadm.model.AadmDomainModelFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class tests AADM use case.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
class AadmUseCaseTest {
    private static final XLogger logger = XLoggerFactory.getXLogger(AadmUseCaseTest.class);

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;
    private EngineParameters engineParameters;

    /**
     * Test AADM use case setup.
     */
    @BeforeEach
    void beforeTest() {
        schemaParameters = new SchemaParameters();

        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);

        contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());

        engineParameters = new EngineParameters();
        engineParameters.getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        ParameterService.register(engineParameters);
    }

    /**
     * After test.
     */
    @AfterEach
    void afterTest() {
        ParameterService.deregister(engineParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);

        ParameterService.deregister(schemaParameters);
    }

    /**
     * Test aadm case.
     *
     * @throws ApexException the apex exception
     */
    @Test
    void testAadmCase() throws ApexException {
        final AxPolicyModel apexPolicyModel = new AadmDomainModelFactory().getAadmPolicyModel();
        assertNotNull(apexPolicyModel);
        final AxArtifactKey key = new AxArtifactKey("AADMApexEngine", "0.0.1");

        final ApexEngineImpl apexEngine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(key);
        final TestApexActionListener listener = new TestApexActionListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexPolicyModel, false);
        apexEngine.start();

        final AxEvent axEvent = getTriggerEvent(apexPolicyModel);
        assertNotNull(axEvent);

        // getting number of connections send it to policy, expecting probe action
        logger.info("Sending too many connections trigger ");
        EnEvent event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", 123L);
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 101.0);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("NUM_SUBSCRIBERS", 101);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("DoS", false);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 0D);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");
        apexEngine.handleEvent(event);
        EnEvent result = listener.getResult();
        assertProbe(result, event);
        loginfoacttaskevent(result);

        final ContextAlbum eNodeBStatusAlbum = apexEngine.getInternalContext().get("ENodeBStatusAlbum");
        final ENodeBStatus eNodeBStatus = (ENodeBStatus) eNodeBStatusAlbum.get("123");
        eNodeBStatus.setDosCount(101);
        eNodeBStatusAlbum.put(eNodeBStatus.getENodeB(), eNodeBStatus);

        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", 123L);
        event.put("SERVICE_REQUEST_COUNT", 101);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 99.0);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("NUM_SUBSCRIBERS", 101);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 101.0);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("DoS", false);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbeDone(result, event, 100, eNodeBStatusAlbum);

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDosCount(99);

        // getting number of connections send it to policy, expecting probe action
        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", 123L);
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 101.0);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("NUM_SUBSCRIBERS", 99);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("DoS", false);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 0D);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbe(result, event);
        assertEquals(99, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDosCount());

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDosCount(99);

        // tcp correlation return positive dos
        loginfoacttaskevent(result);
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", 123L);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 101.0);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 0.0);
        event.put("DoS", false);
        event.put("NUM_SUBSCRIBERS", 0);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SERVICE_REQUEST_COUNT", 0);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("UE_IP_ADDRESS", "");
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbeDone(result, event, 98, eNodeBStatusAlbum);

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDosCount(101);

        // user moving enodeB
        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", 123L);
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("NUM_SUBSCRIBERS", 101);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 0.0);
        event.put("DoS", false);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 0D);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbeDone(result, event, 100, eNodeBStatusAlbum);
        loginfoacttaskevent(result);

        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("ENODEB_ID", 124L);
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 101.0);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("NUM_SUBSCRIBERS", 101);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("DoS", false);
        event.put("IMSI_IP", "");
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 0D);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbe(result, event);
        assertEquals(99, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDosCount());
        assertEquals(1, ((ENodeBStatus) eNodeBStatusAlbum.get("124")).getDosCount());
        loginfoacttaskevent(result);
        // End of user moving enodeB

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDosCount(101);

        // user becomes non anomalous
        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", 123L);
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 101.0);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("NUM_SUBSCRIBERS", 101);
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("DoS", false);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 0D);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbe(result, event);
        assertEquals(102, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDosCount());
        loginfoacttaskevent(result);

        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", 123456L);
        event.put("ENODEB_ID", 123L);
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("UE_IP_ADDRESS", "101.111.121.131");
        event.put("ACTTASK", "");
        event.put("APPLICATION", "");
        event.put("ATTACH_COUNT", 0);
        event.put("AVG_SUBSCRIBER_ATTACH", 0D);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 0.0);
        event.put("DoS", false);
        event.put("IMSI_IP", "");
        event.put("NUM_SUBSCRIBERS", 0);
        event.put("NW_IP", "");
        event.put("PROBE_ON", false);
        event.put("SGW_IP_ADDRESS", "");
        event.put("TCP_ON", false);
        event.put("TCP_UE_SIDE_AVG_THROUGHPUT", 0D);
        event.put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", 0L);
        event.put("http_host_class", "");
        event.put("protocol_group", "");

        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertProbe(result, event);
        assertEquals(102, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDosCount());
        loginfoacttaskevent(result);
        // End of user becomes non-anomalous
        apexEngine.handleEvent(result);
        result = listener.getResult();
        assertTrue(result.getName().startsWith("SAPCBlacklistSubscriberEvent"));
        assertEquals("ServiceA", result.get("PROFILE"));
        assertTrue((boolean) result.get("BLACKLIST_ON"));

        event = apexEngine.createEvent(new AxArtifactKey("PeriodicEvent", "0.0.1"));
        event.put("PERIODIC_EVENT_COUNT", (long) 100);
        event.put("PERIODIC_DELAY", (long) 1000);
        event.put("PERIODIC_FIRST_TIME", System.currentTimeMillis());
        event.put("PERIODIC_CURRENT_TIME", System.currentTimeMillis());
        event.put("PERIODIC_LAST_TIME", System.currentTimeMillis());
        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertTrue(result.getName().startsWith("SAPCBlacklistSubscriberEvent"));
        assertEquals(event.getExecutionId(), result.getExecutionId(), "ExecutionIDs are different");
        assertEquals(0L, result.get("IMSI"));
        assertEquals("ServiceA", result.get("PROFILE"));
        assertFalse((boolean) result.get("BLACKLIST_ON"));

        apexEngine.stop();
    }

    private static void assertProbe(EnEvent result, EnEvent event) {
        logger.info("Result name: {}", result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals(event.getExecutionId(), result.getExecutionId(), "ExecutionIDs are different");
        assertEquals("probe", result.get("ACTTASK"));
        assertTrue((boolean) result.get("TCP_ON"));
        assertTrue((boolean) result.get("PROBE_ON"));
    }

    private static void assertProbeDone(EnEvent result, EnEvent event, int expected, ContextAlbum contextAlbum) {
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals(event.getExecutionId(), result.getExecutionId(), "ExecutionIDs are different");
        // DOS_IN_eNodeB set to be more than throughput so return act action
        assertEquals("act", result.get("ACTTASK"));
        // only one imsi was sent to process, so stop probe and tcp
        assertFalse((boolean) result.get("TCP_ON"));
        assertFalse((boolean) result.get("PROBE_ON"));
        assertEquals(expected, ((ENodeBStatus) contextAlbum.get("123")).getDosCount());
        loginfoacttaskevent(result);
    }

    /**
     * Logs action from Result variable.
     * @param result the result event
     */
    private static void loginfoacttaskevent(EnEvent result) {
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));
    }

    /**
     * Gets the trigger event.
     *
     * @param apexPolicyModel the apex policy model
     * @return the trigger event
     */
    private AxEvent getTriggerEvent(final AxPolicyModel apexPolicyModel) {
        for (final AxEvent axEvent : apexPolicyModel.getEvents().getEventMap().values()) {
            if (axEvent.getKey().getId().equals("AADMEvent:0.0.1")) {
                return axEvent;
            }
        }
        return null;
    }
}
