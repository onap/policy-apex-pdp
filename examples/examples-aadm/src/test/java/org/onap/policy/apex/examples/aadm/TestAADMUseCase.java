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

package org.onap.policy.apex.examples.aadm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import org.onap.policy.apex.examples.aadm.model.AADMDomainModelFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MVELExecutorParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * User: Sergey Sachkov Date: 13/10/15
 */
public class TestAADMUseCase {
    private static final XLogger logger = XLoggerFactory.getXLogger(TestAADMUseCase.class);

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;
    private EngineParameters engineParameters;

    @Before
    public void beforeTest() {
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
        engineParameters.getExecutorParameterMap().put("MVEL", new MVELExecutorParameters());
        ParameterService.register(engineParameters);
    }

    @After
    public void afterTest() {
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
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testAADMCase() throws ApexException, InterruptedException, IOException {
        final AxPolicyModel apexPolicyModel = new AADMDomainModelFactory().getAADMPolicyModel();
        assertNotNull(apexPolicyModel);
        final AxArtifactKey key = new AxArtifactKey("AADMApexEngine", "0.0.1");

        final ApexEngineImpl apexEngine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(key);
        final TestApexActionListener listener = new TestApexActionListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexPolicyModel);
        apexEngine.start();

        final AxEvent axEvent = getTriggerEvent(apexPolicyModel);
        assertNotNull(axEvent);

        // getting number of connections send it to policy, expecting probe action
        logger.info("Sending too many connections trigger ");
        EnEvent event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", new Long(123));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        // no DOS_IN_eNodeB set so return probe action
        assertTrue(result.get("ACTTASK").equals("probe"));
        assertTrue((boolean) result.get("TCP_ON"));
        assertTrue((boolean) result.get("PROBE_ON"));
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));

        final ContextAlbum eNodeBStatusAlbum = apexEngine.getInternalContext().get("ENodeBStatusAlbum");
        final ENodeBStatus eNodeBStatus = (ENodeBStatus) eNodeBStatusAlbum.get("123");
        eNodeBStatus.setDOSCount(101);
        eNodeBStatusAlbum.put(eNodeBStatus.getENodeB(), eNodeBStatus);

        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", new Long(123));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        // DOS_IN_eNodeB set to be more than throughput so return act action
        assertTrue(result.get("ACTTASK").equals("act"));
        // only one imsi was sent to process, so stop probe and tcp
        assertTrue(!(boolean) result.get("TCP_ON"));
        assertTrue(!(boolean) result.get("PROBE_ON"));
        assertEquals(100, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDOSCount(99);

        // getting number of connections send it to policy, expecting probe action
        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", new Long(123));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertTrue(result.get("ACTTASK").equals("probe"));
        assertTrue((boolean) result.get("TCP_ON"));
        assertTrue((boolean) result.get("PROBE_ON"));
        assertEquals(99, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDOSCount(99);

        // tcp correlation return positive dos
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", new Long(123));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertTrue(result.get("ACTTASK").equals("act"));
        assertTrue(!(boolean) result.get("TCP_ON"));
        assertTrue(!(boolean) result.get("PROBE_ON"));
        assertEquals(98, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDOSCount(101);

        // user moving enodeB
        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", new Long(123));
        event.put("SERVICE_REQUEST_COUNT", 99);
        event.put("AVG_SUBSCRIBER_SERVICE_REQUEST", 101.0);
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertTrue(result.get("ACTTASK").equals("act"));
        assertTrue(!(boolean) result.get("TCP_ON"));
        assertTrue(!(boolean) result.get("PROBE_ON"));
        assertEquals(100, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));

        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("ENODEB_ID", new Long(124));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertTrue(result.get("ACTTASK").equals("probe"));
        assertTrue((boolean) result.get("TCP_ON"));
        assertTrue((boolean) result.get("PROBE_ON"));
        assertEquals(99, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());
        assertEquals(1, ((ENodeBStatus) eNodeBStatusAlbum.get("124")).getDOSCount());
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));
        // End of user moving enodeB

        ((ENodeBStatus) eNodeBStatusAlbum.get("123")).setDOSCount(101);

        // user becomes non anomalous
        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("IMSI_IP", "101.111.121.131");
        event.put("ENODEB_ID", new Long(123));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertTrue(result.get("ACTTASK").equals("probe"));
        assertTrue((boolean) result.get("TCP_ON"));
        assertTrue((boolean) result.get("PROBE_ON"));
        assertEquals(102, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));

        logger.info("Sending too many connections trigger ");
        event = apexEngine.createEvent(axEvent.getKey());
        event.put("IMSI", new Long(123456));
        event.put("ENODEB_ID", new Long(123));
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
        assertTrue(result.getName().startsWith("XSTREAM_AADM_ACT_EVENT"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertTrue(result.get("ACTTASK").equals("probe"));
        assertTrue((boolean) result.get("TCP_ON"));
        assertTrue((boolean) result.get("PROBE_ON"));
        assertEquals(102, ((ENodeBStatus) eNodeBStatusAlbum.get("123")).getDOSCount());
        logger.info("Receiving action event with {} action", result.get("ACTTASK"));
        // End of user becomes non anomalous
        apexEngine.handleEvent(result);
        result = listener.getResult();
        assertTrue(result.getName().startsWith("SAPCBlacklistSubscriberEvent"));
        assertTrue(result.get("PROFILE").equals("ServiceA"));
        assertTrue(result.get("BLACKLIST_ON").equals(true));

        event = apexEngine.createEvent(new AxArtifactKey("PeriodicEvent", "0.0.1"));
        event.put("PERIODIC_EVENT_COUNT", (long) 100);
        event.put("PERIODIC_DELAY", (long) 1000);
        event.put("PERIODIC_FIRST_TIME", System.currentTimeMillis());
        event.put("PERIODIC_CURRENT_TIME", System.currentTimeMillis());
        event.put("PERIODIC_LAST_TIME", System.currentTimeMillis());
        apexEngine.handleEvent(event);
        result = listener.getResult();
        assertTrue(result.getName().startsWith("SAPCBlacklistSubscriberEvent"));
        assertEquals("ExecutionIDs are different", event.getExecutionID(), result.getExecutionID());
        assertEquals(0L, result.get("IMSI"));
        assertTrue(result.get("PROFILE").equals("ServiceA"));
        assertTrue(result.get("BLACKLIST_ON").equals(false));

        apexEngine.stop();
    }

    /**
     * Test vpn cleardown.
     */
    @After
    public void testAADMCleardown() {}

    /**
     * Gets the trigger event.
     *
     * @param apexModel the apex model
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
