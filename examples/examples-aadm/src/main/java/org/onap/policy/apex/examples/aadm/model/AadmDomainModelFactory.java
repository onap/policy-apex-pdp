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

package org.onap.policy.apex.examples.aadm.model;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxLogicReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.apex.model.policymodel.handling.PolicyLogicReader;

/**
 * The Class AADMDomainModelFactory.
 */
public class AadmDomainModelFactory {
    // Recurring string constants
    private static final String MATCH = "Match";
    private static final String ESTABLISH = "Establish";
    private static final String DECIDE = "Decide";
    private static final String TASK_SELECTION_LOGIC = "TaskSelectionLogic";
    private static final String DEFAULT_TASK_LOGIC = "DefaultTaskLogic";
    private static final String TASK_LOGIC = "TaskLogic";
    private static final String VERSION = "VERSION";
    private static final String THRESHOLD = "THRESHOLD";
    private static final String PROFILE2 = "PROFILE";
    private static final String BLACKLIST_ON = "BLACKLIST_ON";
    private static final String ACTTASK = "ACTTASK";
    private static final String TCP_ON = "TCP_ON";
    private static final String PROBE_ON = "PROBE_ON";
    private static final String APPLICATION = "APPLICATION";
    private static final String NW_IP = "NW_IP";
    private static final String IMSI_IP = "IMSI_IP";
    private static final String ENODEB_ID = "ENODEB_ID";
    private static final String DEFAULT_SOURCE = "External";
    private static final String DEFAULT_NAMESPACE = "org.onap.policy.apex.examples.aadm.events";
    private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
    private static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String JAVA_LANG_LONG = "java.lang.Long";
    private static final String DEFAULT_VERSION = "0.0.1";

    /**
     * Gets the AADM policy model.
     *
     * @return the AADM policy model
     */
    // CHECKSTYLE:OFF: checkstyle
    public AxPolicyModel getAadmPolicyModel() {
        // CHECKSTYLE:ON: checkstyle
        // Data types for event parameters
        final AxContextSchema imsi = new AxContextSchema(new AxArtifactKey("IMSI", DEFAULT_VERSION), "Java",
                        JAVA_LANG_LONG);
        final AxContextSchema ueIpAddress = new AxContextSchema(new AxArtifactKey("UEIPAddress", DEFAULT_VERSION),
                        "Java", JAVA_LANG_STRING);
        final AxContextSchema nwIpAddress = new AxContextSchema(new AxArtifactKey("NWIPAddress", DEFAULT_VERSION),
                        "Java", JAVA_LANG_STRING);
        final AxContextSchema dosFlag = new AxContextSchema(new AxArtifactKey("DOSFlag", DEFAULT_VERSION), "Java",
                        JAVA_LANG_BOOLEAN);
        final AxContextSchema roundTripTime = new AxContextSchema(new AxArtifactKey("RoundTripTime", DEFAULT_VERSION),
                        "Java", JAVA_LANG_LONG);
        final AxContextSchema applicationName = new AxContextSchema(
                        new AxArtifactKey("ApplicationName", DEFAULT_VERSION), "Java", JAVA_LANG_STRING);
        final AxContextSchema protocolGroup = new AxContextSchema(new AxArtifactKey("ProtocolGroup", DEFAULT_VERSION),
                        "Java", JAVA_LANG_STRING);
        final AxContextSchema eNodeBId = new AxContextSchema(new AxArtifactKey("ENodeBID", DEFAULT_VERSION), "Java",
                        JAVA_LANG_LONG);
        final AxContextSchema httpHostClass = new AxContextSchema(new AxArtifactKey("HttpHostClass", DEFAULT_VERSION),
                        "Java", JAVA_LANG_STRING);
        final AxContextSchema tcpOnFlag = new AxContextSchema(new AxArtifactKey("TCPOnFlag", DEFAULT_VERSION), "Java",
                        JAVA_LANG_BOOLEAN);
        final AxContextSchema probeOnFlag = new AxContextSchema(new AxArtifactKey("ProbeOnFlag", DEFAULT_VERSION),
                        "Java", JAVA_LANG_BOOLEAN);
        final AxContextSchema blacklistOnFlag = new AxContextSchema(
                        new AxArtifactKey("BlacklistOnFlag", DEFAULT_VERSION), "Java", JAVA_LANG_BOOLEAN);
        final AxContextSchema averageThroughput = new AxContextSchema(
                        new AxArtifactKey("AverageThroughput", DEFAULT_VERSION), "Java", JAVA_LANG_DOUBLE);
        final AxContextSchema serviceRequestCount = new AxContextSchema(
                        new AxArtifactKey("ServiceRequestCount", DEFAULT_VERSION), "Java", JAVA_LANG_INTEGER);
        final AxContextSchema attchCount = new AxContextSchema(new AxArtifactKey("AttachCount", DEFAULT_VERSION),
                        "Java", JAVA_LANG_INTEGER);
        final AxContextSchema subscriberCount = new AxContextSchema(
                        new AxArtifactKey("SubscriberCount", DEFAULT_VERSION), "Java", JAVA_LANG_INTEGER);
        final AxContextSchema averageServiceRequest = new AxContextSchema(
                        new AxArtifactKey("AverageServiceRequest", DEFAULT_VERSION), "Java", JAVA_LANG_DOUBLE);
        final AxContextSchema averageAttach = new AxContextSchema(new AxArtifactKey("AverageAttach", DEFAULT_VERSION),
                        "Java", JAVA_LANG_DOUBLE);
        final AxContextSchema actionTask = new AxContextSchema(new AxArtifactKey("ActionTask", DEFAULT_VERSION), "Java",
                        JAVA_LANG_STRING);
        final AxContextSchema version = new AxContextSchema(new AxArtifactKey("Version", DEFAULT_VERSION), "Java",
                        JAVA_LANG_STRING);
        final AxContextSchema profile = new AxContextSchema(new AxArtifactKey("Profile", DEFAULT_VERSION), "Java",
                        JAVA_LANG_STRING);
        final AxContextSchema threshold = new AxContextSchema(new AxArtifactKey("Threshold", DEFAULT_VERSION), "Java",
                        JAVA_LANG_LONG);
        final AxContextSchema triggerSpec = new AxContextSchema(new AxArtifactKey("TriggerSpec", DEFAULT_VERSION),
                        "Java", JAVA_LANG_STRING);
        final AxContextSchema periodicEventCount = new AxContextSchema(
                        new AxArtifactKey("PeriodicEventCount", DEFAULT_VERSION), "Java", JAVA_LANG_LONG);
        final AxContextSchema periodicDelay = new AxContextSchema(new AxArtifactKey("PeriodicDelay", DEFAULT_VERSION),
                        "Java", JAVA_LANG_LONG);
        final AxContextSchema periodicTime = new AxContextSchema(new AxArtifactKey("PeriodicTime", DEFAULT_VERSION),
                        "Java", JAVA_LANG_LONG);

        final AxContextSchemas aadmContextSchemas = new AxContextSchemas(
                        new AxArtifactKey("AADMDatatypes", DEFAULT_VERSION));
        aadmContextSchemas.getSchemasMap().put(imsi.getKey(), imsi);
        aadmContextSchemas.getSchemasMap().put(ueIpAddress.getKey(), ueIpAddress);
        aadmContextSchemas.getSchemasMap().put(nwIpAddress.getKey(), nwIpAddress);
        aadmContextSchemas.getSchemasMap().put(dosFlag.getKey(), dosFlag);
        aadmContextSchemas.getSchemasMap().put(roundTripTime.getKey(), roundTripTime);
        aadmContextSchemas.getSchemasMap().put(applicationName.getKey(), applicationName);
        aadmContextSchemas.getSchemasMap().put(protocolGroup.getKey(), protocolGroup);
        aadmContextSchemas.getSchemasMap().put(eNodeBId.getKey(), eNodeBId);
        aadmContextSchemas.getSchemasMap().put(httpHostClass.getKey(), httpHostClass);
        aadmContextSchemas.getSchemasMap().put(tcpOnFlag.getKey(), tcpOnFlag);
        aadmContextSchemas.getSchemasMap().put(probeOnFlag.getKey(), probeOnFlag);
        aadmContextSchemas.getSchemasMap().put(blacklistOnFlag.getKey(), blacklistOnFlag);
        aadmContextSchemas.getSchemasMap().put(averageThroughput.getKey(), averageThroughput);
        aadmContextSchemas.getSchemasMap().put(serviceRequestCount.getKey(), serviceRequestCount);
        aadmContextSchemas.getSchemasMap().put(attchCount.getKey(), attchCount);
        aadmContextSchemas.getSchemasMap().put(subscriberCount.getKey(), subscriberCount);
        aadmContextSchemas.getSchemasMap().put(averageServiceRequest.getKey(), averageServiceRequest);
        aadmContextSchemas.getSchemasMap().put(averageAttach.getKey(), averageAttach);
        aadmContextSchemas.getSchemasMap().put(actionTask.getKey(), actionTask);
        aadmContextSchemas.getSchemasMap().put(version.getKey(), version);
        aadmContextSchemas.getSchemasMap().put(profile.getKey(), profile);
        aadmContextSchemas.getSchemasMap().put(threshold.getKey(), threshold);
        aadmContextSchemas.getSchemasMap().put(triggerSpec.getKey(), triggerSpec);
        aadmContextSchemas.getSchemasMap().put(periodicEventCount.getKey(), periodicEventCount);
        aadmContextSchemas.getSchemasMap().put(periodicDelay.getKey(), periodicDelay);
        aadmContextSchemas.getSchemasMap().put(periodicTime.getKey(), periodicTime);

        final AxEvent aadmEvent = new AxEvent(new AxArtifactKey("AADMEvent", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        aadmEvent.setSource(DEFAULT_SOURCE);
        aadmEvent.setTarget("Apex");
        aadmEvent.getParameterMap().put("IMSI",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "IMSI"), imsi.getKey()));
        aadmEvent.getParameterMap().put(ENODEB_ID,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), ENODEB_ID), eNodeBId.getKey()));
        aadmEvent.getParameterMap().put(IMSI_IP,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), IMSI_IP), ueIpAddress.getKey()));
        aadmEvent.getParameterMap().put(NW_IP,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), NW_IP), nwIpAddress.getKey()));
        aadmEvent.getParameterMap().put("DoS",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "DoS"), dosFlag.getKey()));
        aadmEvent.getParameterMap().put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX"),
                                        roundTripTime.getKey()));
        aadmEvent.getParameterMap().put("TCP_UE_SIDE_AVG_THROUGHPUT",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "TCP_UE_SIDE_AVG_THROUGHPUT"),
                                        averageThroughput.getKey()));
        aadmEvent.getParameterMap().put(APPLICATION,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), APPLICATION), applicationName.getKey()));
        aadmEvent.getParameterMap().put("protocol_group",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "protocol_group"), protocolGroup.getKey()));
        aadmEvent.getParameterMap().put("http_host_class",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "http_host_class"), httpHostClass.getKey()));
        aadmEvent.getParameterMap().put(PROBE_ON,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), PROBE_ON), probeOnFlag.getKey()));
        aadmEvent.getParameterMap().put(TCP_ON,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), TCP_ON), tcpOnFlag.getKey()));
        aadmEvent.getParameterMap().put("SGW_IP_ADDRESS",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "SGW_IP_ADDRESS"), nwIpAddress.getKey()));
        aadmEvent.getParameterMap().put("UE_IP_ADDRESS",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "UE_IP_ADDRESS"), ueIpAddress.getKey()));
        aadmEvent.getParameterMap().put("SERVICE_REQUEST_COUNT", new AxField(
                        new AxReferenceKey(aadmEvent.getKey(), "SERVICE_REQUEST_COUNT"), serviceRequestCount.getKey()));
        aadmEvent.getParameterMap().put("ATTACH_COUNT",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "ATTACH_COUNT"), attchCount.getKey()));
        aadmEvent.getParameterMap().put("NUM_SUBSCRIBERS", new AxField(
                        new AxReferenceKey(aadmEvent.getKey(), "NUM_SUBSCRIBERS"), subscriberCount.getKey()));
        aadmEvent.getParameterMap().put("AVG_SUBSCRIBER_SERVICE_REQUEST",
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), "AVG_SUBSCRIBER_SERVICE_REQUEST"),
                                        averageServiceRequest.getKey()));
        aadmEvent.getParameterMap().put("AVG_SUBSCRIBER_ATTACH", new AxField(
                        new AxReferenceKey(aadmEvent.getKey(), "AVG_SUBSCRIBER_ATTACH"), averageAttach.getKey()));
        aadmEvent.getParameterMap().put(ACTTASK,
                        new AxField(new AxReferenceKey(aadmEvent.getKey(), ACTTASK), actionTask.getKey()));

        final AxEvent aadmXStreamActEvent = new AxEvent(new AxArtifactKey("XSTREAM_AADM_ACT_EVENT", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        aadmXStreamActEvent.setSource("Apex");
        aadmXStreamActEvent.setTarget(DEFAULT_SOURCE);
        aadmXStreamActEvent.getParameterMap().put("IMSI",
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "IMSI"), imsi.getKey()));
        aadmXStreamActEvent.getParameterMap().put(IMSI_IP,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), IMSI_IP), ueIpAddress.getKey()));
        aadmXStreamActEvent.getParameterMap().put(ENODEB_ID,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), ENODEB_ID), eNodeBId.getKey()));
        aadmXStreamActEvent.getParameterMap().put(NW_IP,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), NW_IP), nwIpAddress.getKey()));
        aadmXStreamActEvent.getParameterMap().put(ACTTASK,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), ACTTASK), actionTask.getKey()));
        aadmXStreamActEvent.getParameterMap().put(PROBE_ON, new AxField(
                        new AxReferenceKey(aadmXStreamActEvent.getKey(), PROBE_ON), probeOnFlag.getKey()));
        aadmXStreamActEvent.getParameterMap().put(TCP_ON,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), TCP_ON), tcpOnFlag.getKey()));
        aadmXStreamActEvent.getParameterMap().put(VERSION,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), VERSION), version.getKey()));
        aadmXStreamActEvent.getParameterMap().put("TRIGGER_SPEC", new AxField(
                        new AxReferenceKey(aadmXStreamActEvent.getKey(), "TRIGGER_SPEC"), triggerSpec.getKey()));
        aadmXStreamActEvent.getParameterMap().put("MAJ_MIN_MAINT_VERSION", new AxField(
                        new AxReferenceKey(aadmXStreamActEvent.getKey(), "MAJ_MIN_MAINT_VERSION"), version.getKey()));
        aadmXStreamActEvent.getParameterMap().put(BLACKLIST_ON, new AxField(
                        new AxReferenceKey(aadmXStreamActEvent.getKey(), BLACKLIST_ON), blacklistOnFlag.getKey()));
        aadmXStreamActEvent.getParameterMap().put(PROFILE2,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), PROFILE2), profile.getKey()));
        aadmXStreamActEvent.getParameterMap().put(THRESHOLD,
                        new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), THRESHOLD), threshold.getKey()));

        final AxEvent vMmeEvent = new AxEvent(new AxArtifactKey("VMMEEvent", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        vMmeEvent.setSource(DEFAULT_SOURCE);
        vMmeEvent.setTarget("Apex");
        vMmeEvent.getParameterMap().put("IMSI",
                        new AxField(new AxReferenceKey(vMmeEvent.getKey(), "IMSI"), imsi.getKey()));
        vMmeEvent.getParameterMap().put(ENODEB_ID,
                        new AxField(new AxReferenceKey(vMmeEvent.getKey(), ENODEB_ID), eNodeBId.getKey()));
        vMmeEvent.getParameterMap().put(IMSI_IP,
                        new AxField(new AxReferenceKey(vMmeEvent.getKey(), IMSI_IP), ueIpAddress.getKey()));
        vMmeEvent.getParameterMap().put(NW_IP,
                        new AxField(new AxReferenceKey(vMmeEvent.getKey(), NW_IP), nwIpAddress.getKey()));
        vMmeEvent.getParameterMap().put(PROFILE2,
                        new AxField(new AxReferenceKey(vMmeEvent.getKey(), PROFILE2), profile.getKey()));
        vMmeEvent.getParameterMap().put(THRESHOLD,
                        new AxField(new AxReferenceKey(vMmeEvent.getKey(), THRESHOLD), threshold.getKey()));

        final AxEvent sapcEvent = new AxEvent(new AxArtifactKey("SAPCEvent", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        sapcEvent.setSource(DEFAULT_SOURCE);
        sapcEvent.setTarget("Apex");
        sapcEvent.getParameterMap().put("IMSI",
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), "IMSI"), imsi.getKey()));
        sapcEvent.getParameterMap().put(ENODEB_ID,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), ENODEB_ID), eNodeBId.getKey()));
        sapcEvent.getParameterMap().put(IMSI_IP,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), IMSI_IP), ueIpAddress.getKey()));
        sapcEvent.getParameterMap().put(NW_IP,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), NW_IP), nwIpAddress.getKey()));
        sapcEvent.getParameterMap().put(PROFILE2,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), PROFILE2), profile.getKey()));
        sapcEvent.getParameterMap().put(THRESHOLD,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), THRESHOLD), threshold.getKey()));
        sapcEvent.getParameterMap().put(TCP_ON,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), TCP_ON), tcpOnFlag.getKey()));
        sapcEvent.getParameterMap().put(PROBE_ON,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), PROBE_ON), probeOnFlag.getKey()));
        sapcEvent.getParameterMap().put(VERSION,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), VERSION), version.getKey()));
        sapcEvent.getParameterMap().put(BLACKLIST_ON,
                        new AxField(new AxReferenceKey(sapcEvent.getKey(), BLACKLIST_ON), blacklistOnFlag.getKey()));

        final AxEvent sapcBlacklistSubscriberEvent = new AxEvent(
                        new AxArtifactKey("SAPCBlacklistSubscriberEvent", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        sapcBlacklistSubscriberEvent.setSource("Apex");
        sapcBlacklistSubscriberEvent.setTarget(DEFAULT_SOURCE);
        sapcBlacklistSubscriberEvent.getParameterMap().put("IMSI",
                        new AxField(new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "IMSI"), imsi.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put(PROFILE2, new AxField(
                        new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), PROFILE2), profile.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put(BLACKLIST_ON,
                        new AxField(new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), BLACKLIST_ON),
                                        blacklistOnFlag.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put(IMSI_IP, new AxField(
                        new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), IMSI_IP), ueIpAddress.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put(NW_IP, new AxField(
                        new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), NW_IP), nwIpAddress.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put(PROBE_ON, new AxField(
                        new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), PROBE_ON), probeOnFlag.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put(TCP_ON, new AxField(
                        new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), TCP_ON), tcpOnFlag.getKey()));

        final AxEvent periodicEvent = new AxEvent(new AxArtifactKey("PeriodicEvent", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        periodicEvent.setSource("System");
        periodicEvent.setTarget("Apex");
        periodicEvent.getParameterMap().put("PERIODIC_EVENT_COUNT",
                        new AxField(new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_EVENT_COUNT"),
                                        periodicEventCount.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_DELAY", new AxField(
                        new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_DELAY"), periodicDelay.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_FIRST_TIME", new AxField(
                        new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_FIRST_TIME"), periodicTime.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_CURRENT_TIME", new AxField(
                        new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_CURRENT_TIME"), periodicTime.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_LAST_TIME", new AxField(
                        new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_LAST_TIME"), periodicTime.getKey()));

        final AxEvents aadmEvents = new AxEvents(new AxArtifactKey("AADMEvents", DEFAULT_VERSION));
        aadmEvents.getEventMap().put(aadmEvent.getKey(), aadmEvent);
        aadmEvents.getEventMap().put(aadmXStreamActEvent.getKey(), aadmXStreamActEvent);
        aadmEvents.getEventMap().put(vMmeEvent.getKey(), vMmeEvent);
        aadmEvents.getEventMap().put(sapcEvent.getKey(), sapcEvent);
        aadmEvents.getEventMap().put(sapcBlacklistSubscriberEvent.getKey(), sapcBlacklistSubscriberEvent);
        aadmEvents.getEventMap().put(periodicEvent.getKey(), periodicEvent);

        // Data types for context
        final AxContextSchema eNodeBStatus = new AxContextSchema(new AxArtifactKey("ENodeBStatus", DEFAULT_VERSION),
                        "Java", "org.onap.policy.apex.examples.aadm.concepts.ENodeBStatus");
        final AxContextSchema imsiStatus = new AxContextSchema(new AxArtifactKey("IMSIStatus", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.examples.aadm.concepts.ImsiStatus");
        final AxContextSchema ipAddressStatus = new AxContextSchema(
                        new AxArtifactKey("IPAddressStatus", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.examples.aadm.concepts.IpAddressStatus");
        aadmContextSchemas.getSchemasMap().put(eNodeBStatus.getKey(), eNodeBStatus);
        aadmContextSchemas.getSchemasMap().put(imsiStatus.getKey(), imsiStatus);
        aadmContextSchemas.getSchemasMap().put(ipAddressStatus.getKey(), ipAddressStatus);

        // Three context albums for AADM
        final AxContextAlbum eNodeBStatusAlbum = new AxContextAlbum(
                        new AxArtifactKey("ENodeBStatusAlbum", DEFAULT_VERSION), APPLICATION, true,
                        eNodeBStatus.getKey());
        final AxContextAlbum imsiStatusAlbum = new AxContextAlbum(new AxArtifactKey("IMSIStatusAlbum", DEFAULT_VERSION),
                        APPLICATION, true, imsiStatus.getKey());
        final AxContextAlbum ipAddressStatusAlbum = new AxContextAlbum(
                        new AxArtifactKey("IPAddressStatusAlbum", DEFAULT_VERSION), APPLICATION, true,
                        ipAddressStatus.getKey());

        final AxContextAlbums aadmAlbums = new AxContextAlbums(new AxArtifactKey("AADMContext", DEFAULT_VERSION));
        aadmAlbums.getAlbumsMap().put(eNodeBStatusAlbum.getKey(), eNodeBStatusAlbum);
        aadmAlbums.getAlbumsMap().put(imsiStatusAlbum.getKey(), imsiStatusAlbum);
        aadmAlbums.getAlbumsMap().put(ipAddressStatusAlbum.getKey(), ipAddressStatusAlbum);

        // Tasks
        final AxLogicReader logicReader = new PolicyLogicReader()
                        .setLogicPackage(this.getClass().getPackage().getName()).setDefaultLogic(null);

        final AxTask aadmMatchTask = new AxTask(new AxArtifactKey("AADMMatchTask", DEFAULT_VERSION));
        aadmMatchTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmMatchTask.duplicateOutputFields(aadmEvent.getParameterMap());
        aadmMatchTask.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmMatchTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmMatchTask.getContextAlbumReferences().add(ipAddressStatusAlbum.getKey());
        aadmMatchTask.setTaskLogic(new AxTaskLogic(aadmMatchTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask aadmEstablishTask = new AxTask(new AxArtifactKey("AADMEstablishTask", DEFAULT_VERSION));
        aadmEstablishTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmEstablishTask.duplicateOutputFields(aadmEvent.getParameterMap());
        logicReader.setDefaultLogic(DEFAULT_TASK_LOGIC);
        aadmEstablishTask.setTaskLogic(new AxTaskLogic(aadmEstablishTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask aadmDecideTask = new AxTask(new AxArtifactKey("AADMDecideTask", DEFAULT_VERSION));
        aadmDecideTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmDecideTask.duplicateOutputFields(aadmEvent.getParameterMap());
        aadmDecideTask.setTaskLogic(new AxTaskLogic(aadmDecideTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        logicReader.setDefaultLogic(null);

        final AxTask aadmDoSSuggestionActTask = new AxTask(
                        new AxArtifactKey("AADMDoSSuggestionActTask", DEFAULT_VERSION));
        aadmDoSSuggestionActTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmDoSSuggestionActTask.duplicateOutputFields(aadmXStreamActEvent.getParameterMap());
        aadmDoSSuggestionActTask.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmDoSSuggestionActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmDoSSuggestionActTask.setTaskLogic(
                        new AxTaskLogic(aadmDoSSuggestionActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask aadmNoActTask = new AxTask(new AxArtifactKey("AADMNoActTask", DEFAULT_VERSION));
        aadmNoActTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmNoActTask.duplicateOutputFields(aadmXStreamActEvent.getParameterMap());
        aadmNoActTask.setTaskLogic(new AxTaskLogic(aadmNoActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask aadmDoSProvenActTask = new AxTask(new AxArtifactKey("AADMDoSProvenActTask", DEFAULT_VERSION));
        aadmDoSProvenActTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmDoSProvenActTask.duplicateOutputFields(aadmXStreamActEvent.getParameterMap());
        aadmDoSProvenActTask.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmDoSProvenActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmDoSProvenActTask
                        .setTaskLogic(new AxTaskLogic(aadmDoSProvenActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask vMmeMatchTask = new AxTask(new AxArtifactKey("VMMEMatchTask", DEFAULT_VERSION));
        vMmeMatchTask.duplicateInputFields(vMmeEvent.getParameterMap());
        vMmeMatchTask.duplicateOutputFields(vMmeEvent.getParameterMap());
        vMmeMatchTask.setTaskLogic(new AxTaskLogic(vMmeMatchTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask vMmeEstablishTask = new AxTask(new AxArtifactKey("VMMEEstablishTask", DEFAULT_VERSION));
        vMmeEstablishTask.duplicateInputFields(vMmeEvent.getParameterMap());
        vMmeEstablishTask.duplicateOutputFields(vMmeEvent.getParameterMap());
        logicReader.setDefaultLogic(DEFAULT_TASK_LOGIC);
        vMmeEstablishTask.setTaskLogic(new AxTaskLogic(vMmeEstablishTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask vMmeDecideTask = new AxTask(new AxArtifactKey("VMMEDecideTask", DEFAULT_VERSION));
        vMmeDecideTask.duplicateInputFields(vMmeEvent.getParameterMap());
        vMmeDecideTask.duplicateOutputFields(vMmeEvent.getParameterMap());
        vMmeDecideTask.setTaskLogic(new AxTaskLogic(vMmeDecideTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask vMmeNoActTask = new AxTask(new AxArtifactKey("VMMENoActTask", DEFAULT_VERSION));
        vMmeNoActTask.duplicateInputFields(vMmeEvent.getParameterMap());
        vMmeNoActTask.duplicateOutputFields(vMmeEvent.getParameterMap());
        vMmeNoActTask.setTaskLogic(new AxTaskLogic(vMmeNoActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask vMmeActTask = new AxTask(new AxArtifactKey("VMMEActTask", DEFAULT_VERSION));
        vMmeActTask.duplicateInputFields(vMmeEvent.getParameterMap());
        vMmeActTask.duplicateOutputFields(vMmeEvent.getParameterMap());
        logicReader.setDefaultLogic(null);
        vMmeActTask.setTaskLogic(new AxTaskLogic(vMmeActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask sapcMatchTask = new AxTask(new AxArtifactKey("SAPCMatchTask", DEFAULT_VERSION));
        sapcMatchTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcMatchTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        sapcMatchTask.setTaskLogic(new AxTaskLogic(sapcMatchTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask sapcEstablishTask = new AxTask(new AxArtifactKey("SAPCEstablishTask", DEFAULT_VERSION));
        sapcEstablishTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcEstablishTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        logicReader.setDefaultLogic(DEFAULT_TASK_LOGIC);
        sapcEstablishTask.setTaskLogic(new AxTaskLogic(sapcEstablishTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask sapcDecideTask = new AxTask(new AxArtifactKey("SAPCDecideTask", DEFAULT_VERSION));
        sapcDecideTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcDecideTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        sapcDecideTask.setTaskLogic(new AxTaskLogic(sapcDecideTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask sapcActTask = new AxTask(new AxArtifactKey("SAPCActTask", DEFAULT_VERSION));
        sapcActTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcActTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        sapcActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        logicReader.setDefaultLogic(null);
        sapcActTask.setTaskLogic(new AxTaskLogic(sapcActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        logicReader.setDefaultLogic(DEFAULT_TASK_LOGIC);

        final AxTask periodicMatchTask = new AxTask(new AxArtifactKey("PeriodicMatchTask", DEFAULT_VERSION));
        periodicMatchTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicMatchTask.duplicateOutputFields(periodicEvent.getParameterMap());
        periodicMatchTask.setTaskLogic(new AxTaskLogic(periodicMatchTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask periodicEstablishTask = new AxTask(new AxArtifactKey("PeriodicEstablishTask", DEFAULT_VERSION));
        periodicEstablishTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicEstablishTask.duplicateOutputFields(periodicEvent.getParameterMap());
        periodicEstablishTask.setTaskLogic(
                        new AxTaskLogic(periodicEstablishTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask periodicDecideTask = new AxTask(new AxArtifactKey("PeriodicDecideTask", DEFAULT_VERSION));
        periodicDecideTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicDecideTask.duplicateOutputFields(periodicEvent.getParameterMap());
        periodicDecideTask.setTaskLogic(new AxTaskLogic(periodicDecideTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTask periodicActTask = new AxTask(new AxArtifactKey("PeriodicActTask", DEFAULT_VERSION));
        periodicActTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicActTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        periodicActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        logicReader.setDefaultLogic(null);
        periodicActTask.setTaskLogic(new AxTaskLogic(periodicActTask.getKey(), TASK_LOGIC, "MVEL", logicReader));

        final AxTasks aadmTasks = new AxTasks(new AxArtifactKey("AADMTasks", DEFAULT_VERSION));
        aadmTasks.getTaskMap().put(aadmMatchTask.getKey(), aadmMatchTask);
        aadmTasks.getTaskMap().put(aadmEstablishTask.getKey(), aadmEstablishTask);
        aadmTasks.getTaskMap().put(aadmDecideTask.getKey(), aadmDecideTask);
        aadmTasks.getTaskMap().put(aadmDoSSuggestionActTask.getKey(), aadmDoSSuggestionActTask);
        aadmTasks.getTaskMap().put(aadmNoActTask.getKey(), aadmNoActTask);
        aadmTasks.getTaskMap().put(aadmDoSProvenActTask.getKey(), aadmDoSProvenActTask);
        aadmTasks.getTaskMap().put(vMmeMatchTask.getKey(), vMmeMatchTask);
        aadmTasks.getTaskMap().put(vMmeEstablishTask.getKey(), vMmeEstablishTask);
        aadmTasks.getTaskMap().put(vMmeDecideTask.getKey(), vMmeDecideTask);
        aadmTasks.getTaskMap().put(vMmeNoActTask.getKey(), vMmeNoActTask);
        aadmTasks.getTaskMap().put(vMmeActTask.getKey(), vMmeActTask);
        aadmTasks.getTaskMap().put(sapcMatchTask.getKey(), sapcMatchTask);
        aadmTasks.getTaskMap().put(sapcEstablishTask.getKey(), sapcEstablishTask);
        aadmTasks.getTaskMap().put(sapcDecideTask.getKey(), sapcDecideTask);
        aadmTasks.getTaskMap().put(sapcActTask.getKey(), sapcActTask);
        aadmTasks.getTaskMap().put(periodicMatchTask.getKey(), periodicMatchTask);
        aadmTasks.getTaskMap().put(periodicEstablishTask.getKey(), periodicEstablishTask);
        aadmTasks.getTaskMap().put(periodicDecideTask.getKey(), periodicDecideTask);
        aadmTasks.getTaskMap().put(periodicActTask.getKey(), periodicActTask);

        // Policies
        logicReader.setDefaultLogic(null);

        final AxPolicy aadmPolicy = new AxPolicy(new AxArtifactKey("AADMPolicy", DEFAULT_VERSION));
        aadmPolicy.setTemplate("MEDA");

        final AxState aadmActState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), "Act"));
        aadmActState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmAct2Out = new AxStateOutput(aadmActState.getKey(), AxReferenceKey.getNullKey(),
                        aadmXStreamActEvent.getKey());
        aadmActState.getStateOutputs().put(aadmAct2Out.getKey().getLocalName(), aadmAct2Out);
        aadmActState.getContextAlbumReferences().add(ipAddressStatusAlbum.getKey());
        aadmActState.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmActState.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmActState.setTaskSelectionLogic(
                        new AxTaskSelectionLogic(aadmActState.getKey(), TASK_SELECTION_LOGIC, "MVEL", logicReader));
        aadmActState.setDefaultTask(aadmNoActTask.getKey());
        aadmActState.getTaskReferences().put(aadmNoActTask.getKey(), new AxStateTaskReference(aadmActState.getKey(),
                        aadmNoActTask.getKey(), AxStateTaskOutputType.DIRECT, aadmAct2Out.getKey()));
        aadmActState.getTaskReferences().put(aadmDoSSuggestionActTask.getKey(),
                        new AxStateTaskReference(aadmActState.getKey(), aadmDoSSuggestionActTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, aadmAct2Out.getKey()));
        aadmActState.getTaskReferences().put(aadmDoSProvenActTask.getKey(),
                        new AxStateTaskReference(aadmActState.getKey(), aadmDoSProvenActTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, aadmAct2Out.getKey()));

        logicReader.setDefaultLogic("DefaultTaskSelectionLogic");

        final AxState aadmDecideState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), DECIDE));
        aadmDecideState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmDec2Act = new AxStateOutput(aadmDecideState.getKey(), aadmActState.getKey(),
                        aadmEvent.getKey());
        aadmDecideState.getStateOutputs().put(aadmDec2Act.getKey().getLocalName(), aadmDec2Act);
        aadmDecideState.setTaskSelectionLogic(
                        new AxTaskSelectionLogic(aadmDecideState.getKey(), TASK_SELECTION_LOGIC, "MVEL", logicReader));
        aadmDecideState.setDefaultTask(aadmDecideTask.getKey());
        aadmDecideState.getTaskReferences().put(aadmDecideTask.getKey(),
                        new AxStateTaskReference(aadmDecideState.getKey(), aadmDecideTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, aadmDec2Act.getKey()));

        final AxState aadmEstablishState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), ESTABLISH));
        aadmEstablishState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmEst2Dec = new AxStateOutput(aadmEstablishState.getKey(), aadmDecideState.getKey(),
                        aadmEvent.getKey());
        aadmEstablishState.getStateOutputs().put(aadmEst2Dec.getKey().getLocalName(), aadmEst2Dec);
        aadmEstablishState.setTaskSelectionLogic(new AxTaskSelectionLogic(aadmEstablishState.getKey(),
                        TASK_SELECTION_LOGIC, "MVEL", logicReader));
        aadmEstablishState.setDefaultTask(aadmEstablishTask.getKey());
        aadmEstablishState.getTaskReferences().put(aadmEstablishTask.getKey(),
                        new AxStateTaskReference(aadmEstablishState.getKey(), aadmEstablishTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, aadmEst2Dec.getKey()));

        final AxState aadmMatchState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), MATCH));
        aadmMatchState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmMat2Est = new AxStateOutput(aadmMatchState.getKey(), aadmEstablishState.getKey(),
                        aadmEvent.getKey());
        aadmMatchState.getStateOutputs().put(aadmMat2Est.getKey().getLocalName(), aadmMat2Est);
        aadmMatchState.setTaskSelectionLogic(
                        new AxTaskSelectionLogic(aadmMatchState.getKey(), TASK_SELECTION_LOGIC, "MVEL", logicReader));
        aadmMatchState.setDefaultTask(aadmMatchTask.getKey());
        aadmMatchState.getTaskReferences().put(aadmMatchTask.getKey(), new AxStateTaskReference(aadmMatchState.getKey(),
                        aadmMatchTask.getKey(), AxStateTaskOutputType.DIRECT, aadmMat2Est.getKey()));

        aadmPolicy.setFirstState(aadmMatchState.getKey().getLocalName());
        aadmPolicy.getStateMap().put(aadmMatchState.getKey().getLocalName(), aadmMatchState);
        aadmPolicy.getStateMap().put(aadmEstablishState.getKey().getLocalName(), aadmEstablishState);
        aadmPolicy.getStateMap().put(aadmDecideState.getKey().getLocalName(), aadmDecideState);
        aadmPolicy.getStateMap().put(aadmActState.getKey().getLocalName(), aadmActState);

        final AxPolicy vMmePolicy = new AxPolicy(new AxArtifactKey("VMMEPolicy", DEFAULT_VERSION));
        vMmePolicy.setTemplate("MEDA");

        final AxState vMmeActState = new AxState(new AxReferenceKey(vMmePolicy.getKey(), "Act"));
        vMmeActState.setTrigger(vMmeEvent.getKey());
        final AxStateOutput vMmeAct2Out = new AxStateOutput(vMmeActState.getKey(), AxReferenceKey.getNullKey(),
                        vMmeEvent.getKey());
        vMmeActState.getStateOutputs().put(vMmeAct2Out.getKey().getLocalName(), vMmeAct2Out);
        vMmeActState.setDefaultTask(vMmeActTask.getKey());
        vMmeActState.getTaskReferences().put(vMmeActTask.getKey(), new AxStateTaskReference(vMmeActState.getKey(),
                        vMmeActTask.getKey(), AxStateTaskOutputType.DIRECT, vMmeAct2Out.getKey()));
        vMmeActState.getTaskReferences().put(vMmeNoActTask.getKey(), new AxStateTaskReference(vMmeActState.getKey(),
                        vMmeNoActTask.getKey(), AxStateTaskOutputType.DIRECT, vMmeAct2Out.getKey()));

        final AxState vMmeDecideState = new AxState(new AxReferenceKey(vMmePolicy.getKey(), DECIDE));
        vMmeDecideState.setTrigger(vMmeEvent.getKey());
        final AxStateOutput vMmeDec2Act = new AxStateOutput(vMmeDecideState.getKey(), vMmeActState.getKey(),
                        vMmeEvent.getKey());
        vMmeDecideState.getStateOutputs().put(vMmeDec2Act.getKey().getLocalName(), vMmeDec2Act);
        vMmeDecideState.setDefaultTask(vMmeDecideTask.getKey());
        vMmeDecideState.getTaskReferences().put(vMmeDecideTask.getKey(),
                        new AxStateTaskReference(vMmeDecideState.getKey(), vMmeDecideTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, vMmeDec2Act.getKey()));

        final AxState vMmeEstablishState = new AxState(new AxReferenceKey(vMmePolicy.getKey(), ESTABLISH));
        vMmeEstablishState.setTrigger(vMmeEvent.getKey());
        final AxStateOutput vMmeEst2Dec = new AxStateOutput(vMmeEstablishState.getKey(), vMmeDecideState.getKey(),
                        vMmeEvent.getKey());
        vMmeEstablishState.getStateOutputs().put(vMmeEst2Dec.getKey().getLocalName(), vMmeEst2Dec);
        vMmeEstablishState.setDefaultTask(vMmeEstablishTask.getKey());
        vMmeEstablishState.getTaskReferences().put(vMmeEstablishTask.getKey(),
                        new AxStateTaskReference(vMmeEstablishState.getKey(), vMmeEstablishTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, vMmeEst2Dec.getKey()));

        final AxState vMmeMatchState = new AxState(new AxReferenceKey(vMmePolicy.getKey(), MATCH));
        vMmeMatchState.setTrigger(vMmeEvent.getKey());
        final AxStateOutput vMmeMat2Est = new AxStateOutput(vMmeMatchState.getKey(), vMmeEstablishState.getKey(),
                        vMmeEvent.getKey());
        vMmeMatchState.getStateOutputs().put(vMmeMat2Est.getKey().getLocalName(), vMmeMat2Est);
        vMmeMatchState.setDefaultTask(vMmeMatchTask.getKey());
        vMmeMatchState.getTaskReferences().put(vMmeMatchTask.getKey(), new AxStateTaskReference(vMmeMatchState.getKey(),
                        vMmeMatchTask.getKey(), AxStateTaskOutputType.DIRECT, vMmeMat2Est.getKey()));

        vMmePolicy.setFirstState(vMmeMatchState.getKey().getLocalName());
        vMmePolicy.getStateMap().put(vMmeMatchState.getKey().getLocalName(), vMmeMatchState);
        vMmePolicy.getStateMap().put(vMmeEstablishState.getKey().getLocalName(), vMmeEstablishState);
        vMmePolicy.getStateMap().put(vMmeDecideState.getKey().getLocalName(), vMmeDecideState);
        vMmePolicy.getStateMap().put(vMmeActState.getKey().getLocalName(), vMmeActState);

        final AxPolicy sapcPolicy = new AxPolicy(new AxArtifactKey("SAPCPolicy", DEFAULT_VERSION));
        sapcPolicy.setTemplate("MEDA");

        final AxState sapcActState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), "Act"));
        sapcActState.setTrigger(sapcEvent.getKey());
        final AxStateOutput sapcAct2Out = new AxStateOutput(sapcActState.getKey(), AxReferenceKey.getNullKey(),
                        sapcBlacklistSubscriberEvent.getKey());
        sapcActState.getStateOutputs().put(sapcAct2Out.getKey().getLocalName(), sapcAct2Out);
        sapcActState.setDefaultTask(sapcActTask.getKey());
        sapcActState.getTaskReferences().put(sapcActTask.getKey(), new AxStateTaskReference(sapcActState.getKey(),
                        sapcActTask.getKey(), AxStateTaskOutputType.DIRECT, sapcAct2Out.getKey()));

        final AxState sapcDecideState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), DECIDE));
        sapcDecideState.setTrigger(sapcEvent.getKey());
        final AxStateOutput sapcDec2Act = new AxStateOutput(sapcDecideState.getKey(), sapcActState.getKey(),
                        sapcEvent.getKey());
        sapcDecideState.getStateOutputs().put(sapcDec2Act.getKey().getLocalName(), sapcDec2Act);
        sapcDecideState.setDefaultTask(sapcDecideTask.getKey());
        sapcDecideState.getTaskReferences().put(sapcDecideTask.getKey(),
                        new AxStateTaskReference(sapcDecideState.getKey(), sapcDecideTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, sapcDec2Act.getKey()));

        final AxState sapcEstablishState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), ESTABLISH));
        sapcEstablishState.setTrigger(sapcEvent.getKey());
        final AxStateOutput sapcEst2Dec = new AxStateOutput(sapcEstablishState.getKey(), sapcDecideState.getKey(),
                        sapcEvent.getKey());
        sapcEstablishState.getStateOutputs().put(sapcEst2Dec.getKey().getLocalName(), sapcEst2Dec);
        sapcEstablishState.setDefaultTask(sapcEstablishTask.getKey());
        sapcEstablishState.getTaskReferences().put(sapcEstablishTask.getKey(),
                        new AxStateTaskReference(sapcEstablishState.getKey(), sapcEstablishTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, sapcEst2Dec.getKey()));

        final AxState sapcMatchState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), MATCH));
        sapcMatchState.setTrigger(aadmXStreamActEvent.getKey());
        final AxStateOutput sapcMat2Est = new AxStateOutput(sapcMatchState.getKey(), sapcEstablishState.getKey(),
                        sapcEvent.getKey());
        sapcMatchState.getStateOutputs().put(sapcMat2Est.getKey().getLocalName(), sapcMat2Est);
        sapcMatchState.setDefaultTask(sapcMatchTask.getKey());
        sapcMatchState.getTaskReferences().put(sapcMatchTask.getKey(), new AxStateTaskReference(sapcMatchState.getKey(),
                        sapcMatchTask.getKey(), AxStateTaskOutputType.DIRECT, sapcMat2Est.getKey()));

        sapcPolicy.setFirstState(sapcMatchState.getKey().getLocalName());
        sapcPolicy.getStateMap().put(sapcMatchState.getKey().getLocalName(), sapcMatchState);
        sapcPolicy.getStateMap().put(sapcEstablishState.getKey().getLocalName(), sapcEstablishState);
        sapcPolicy.getStateMap().put(sapcDecideState.getKey().getLocalName(), sapcDecideState);
        sapcPolicy.getStateMap().put(sapcActState.getKey().getLocalName(), sapcActState);

        final AxPolicy periodicPolicy = new AxPolicy(new AxArtifactKey("PeriodicPolicy", DEFAULT_VERSION));
        periodicPolicy.setTemplate("MEDA");

        final AxState periodicActState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), "Act"));
        periodicActState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perAct2Out = new AxStateOutput(periodicActState.getKey(), AxReferenceKey.getNullKey(),
                        sapcBlacklistSubscriberEvent.getKey());
        periodicActState.getStateOutputs().put(perAct2Out.getKey().getLocalName(), perAct2Out);
        periodicActState.setDefaultTask(periodicActTask.getKey());
        periodicActState.getTaskReferences().put(periodicActTask.getKey(),
                        new AxStateTaskReference(periodicActState.getKey(), periodicActTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, perAct2Out.getKey()));

        final AxState periodicDecideState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), DECIDE));
        periodicDecideState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perDec2Act = new AxStateOutput(periodicDecideState.getKey(), periodicActState.getKey(),
                        periodicEvent.getKey());
        periodicDecideState.getStateOutputs().put(perDec2Act.getKey().getLocalName(), perDec2Act);
        periodicDecideState.setDefaultTask(periodicDecideTask.getKey());
        periodicDecideState.getTaskReferences().put(periodicDecideTask.getKey(),
                        new AxStateTaskReference(periodicDecideState.getKey(), periodicDecideTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, perDec2Act.getKey()));

        final AxState periodicEstablishState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), ESTABLISH));
        periodicEstablishState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perEst2Dec = new AxStateOutput(periodicEstablishState.getKey(),
                        periodicDecideState.getKey(), periodicEvent.getKey());
        periodicEstablishState.getStateOutputs().put(perEst2Dec.getKey().getLocalName(), perEst2Dec);
        periodicEstablishState.setDefaultTask(periodicEstablishTask.getKey());
        periodicEstablishState.getTaskReferences().put(periodicEstablishTask.getKey(),
                        new AxStateTaskReference(periodicEstablishState.getKey(), periodicEstablishTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, perEst2Dec.getKey()));

        final AxState periodicMatchState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), MATCH));
        periodicMatchState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perMat2Est = new AxStateOutput(periodicMatchState.getKey(), periodicEstablishState.getKey(),
                        periodicEvent.getKey());
        periodicMatchState.getStateOutputs().put(perMat2Est.getKey().getLocalName(), perMat2Est);
        periodicMatchState.setDefaultTask(periodicMatchTask.getKey());
        periodicMatchState.getTaskReferences().put(periodicMatchTask.getKey(),
                        new AxStateTaskReference(periodicMatchState.getKey(), periodicMatchTask.getKey(),
                                        AxStateTaskOutputType.DIRECT, perMat2Est.getKey()));

        periodicPolicy.setFirstState(periodicMatchState.getKey().getLocalName());
        periodicPolicy.getStateMap().put(periodicMatchState.getKey().getLocalName(), periodicMatchState);
        periodicPolicy.getStateMap().put(periodicEstablishState.getKey().getLocalName(), periodicEstablishState);
        periodicPolicy.getStateMap().put(periodicDecideState.getKey().getLocalName(), periodicDecideState);
        periodicPolicy.getStateMap().put(periodicActState.getKey().getLocalName(), periodicActState);

        final AxPolicies aadmPolicies = new AxPolicies(new AxArtifactKey("AADMPolicies", DEFAULT_VERSION));
        aadmPolicies.getPolicyMap().put(aadmPolicy.getKey(), aadmPolicy);
        aadmPolicies.getPolicyMap().put(vMmePolicy.getKey(), vMmePolicy);
        aadmPolicies.getPolicyMap().put(sapcPolicy.getKey(), sapcPolicy);
        aadmPolicies.getPolicyMap().put(periodicPolicy.getKey(), periodicPolicy);

        final AxKeyInformation keyInformation = new AxKeyInformation(
                        new AxArtifactKey("AADMKeyInformation", DEFAULT_VERSION));
        final AxPolicyModel aadmPolicyModel = new AxPolicyModel(new AxArtifactKey("AADMPolicyModel", DEFAULT_VERSION));
        aadmPolicyModel.setPolicies(aadmPolicies);
        aadmPolicyModel.setEvents(aadmEvents);
        aadmPolicyModel.setTasks(aadmTasks);
        aadmPolicyModel.setAlbums(aadmAlbums);
        aadmPolicyModel.setSchemas(aadmContextSchemas);
        aadmPolicyModel.setKeyInformation(keyInformation);
        aadmPolicyModel.getKeyInformation().generateKeyInfo(aadmPolicyModel);

        final AxValidationResult result = aadmPolicyModel.validate(new AxValidationResult());
        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.VALID)) {
            throw new ApexRuntimeException("model " + aadmPolicyModel.getId() + " is not valid" + result);
        }
        return aadmPolicyModel;
    }
}
