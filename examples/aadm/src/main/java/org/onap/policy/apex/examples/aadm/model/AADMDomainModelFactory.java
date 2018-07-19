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
public class AADMDomainModelFactory {
    /**
     * Gets the AADM policy model.
     *
     * @return the AADM policy model
     */
    // CHECKSTYLE:OFF: checkstyle
    public AxPolicyModel getAADMPolicyModel() {
        // CHECKSTYLE:ON: checkstyle
        // Data types for event parameters
        final AxContextSchema imsi = new AxContextSchema(new AxArtifactKey("IMSI", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema ueIPAddress =
                new AxContextSchema(new AxArtifactKey("UEIPAddress", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema nwIPAddress =
                new AxContextSchema(new AxArtifactKey("NWIPAddress", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema dosFlag =
                new AxContextSchema(new AxArtifactKey("DOSFlag", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema roundTripTime =
                new AxContextSchema(new AxArtifactKey("RoundTripTime", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema applicationName =
                new AxContextSchema(new AxArtifactKey("ApplicationName", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema protocolGroup =
                new AxContextSchema(new AxArtifactKey("ProtocolGroup", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema eNodeBID =
                new AxContextSchema(new AxArtifactKey("ENodeBID", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema httpHostClass =
                new AxContextSchema(new AxArtifactKey("HttpHostClass", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema tcpOnFlag =
                new AxContextSchema(new AxArtifactKey("TCPOnFlag", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema probeOnFlag =
                new AxContextSchema(new AxArtifactKey("ProbeOnFlag", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema blacklistOnFlag =
                new AxContextSchema(new AxArtifactKey("BlacklistOnFlag", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema averageThroughput =
                new AxContextSchema(new AxArtifactKey("AverageThroughput", "0.0.1"), "Java", "java.lang.Double");
        final AxContextSchema serviceRequestCount =
                new AxContextSchema(new AxArtifactKey("ServiceRequestCount", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema attchCount =
                new AxContextSchema(new AxArtifactKey("AttachCount", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema subscriberCount =
                new AxContextSchema(new AxArtifactKey("SubscriberCount", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema averageServiceRequest =
                new AxContextSchema(new AxArtifactKey("AverageServiceRequest", "0.0.1"), "Java", "java.lang.Double");
        final AxContextSchema averageAttach =
                new AxContextSchema(new AxArtifactKey("AverageAttach", "0.0.1"), "Java", "java.lang.Double");
        final AxContextSchema actionTask =
                new AxContextSchema(new AxArtifactKey("ActionTask", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema version =
                new AxContextSchema(new AxArtifactKey("Version", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema profile =
                new AxContextSchema(new AxArtifactKey("Profile", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema threshold =
                new AxContextSchema(new AxArtifactKey("Threshold", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema triggerSpec =
                new AxContextSchema(new AxArtifactKey("TriggerSpec", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema periodicEventCount =
                new AxContextSchema(new AxArtifactKey("PeriodicEventCount", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema periodicDelay =
                new AxContextSchema(new AxArtifactKey("PeriodicDelay", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema periodicTime =
                new AxContextSchema(new AxArtifactKey("PeriodicTime", "0.0.1"), "Java", "java.lang.Long");

        final AxContextSchemas aadmContextSchemas = new AxContextSchemas(new AxArtifactKey("AADMDatatypes", "0.0.1"));
        aadmContextSchemas.getSchemasMap().put(imsi.getKey(), imsi);
        aadmContextSchemas.getSchemasMap().put(ueIPAddress.getKey(), ueIPAddress);
        aadmContextSchemas.getSchemasMap().put(nwIPAddress.getKey(), nwIPAddress);
        aadmContextSchemas.getSchemasMap().put(dosFlag.getKey(), dosFlag);
        aadmContextSchemas.getSchemasMap().put(roundTripTime.getKey(), roundTripTime);
        aadmContextSchemas.getSchemasMap().put(applicationName.getKey(), applicationName);
        aadmContextSchemas.getSchemasMap().put(protocolGroup.getKey(), protocolGroup);
        aadmContextSchemas.getSchemasMap().put(eNodeBID.getKey(), eNodeBID);
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

        final AxEvent aadmEvent =
                new AxEvent(new AxArtifactKey("AADMEvent", "0.0.1"), "org.onap.policy.apex.examples.aadm.events");
        aadmEvent.setSource("External");
        aadmEvent.setTarget("Apex");
        aadmEvent.getParameterMap().put("IMSI",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "IMSI"), imsi.getKey()));
        aadmEvent.getParameterMap().put("ENODEB_ID",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "ENODEB_ID"), eNodeBID.getKey()));
        aadmEvent.getParameterMap().put("IMSI_IP",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "IMSI_IP"), ueIPAddress.getKey()));
        aadmEvent.getParameterMap().put("NW_IP",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "NW_IP"), nwIPAddress.getKey()));
        aadmEvent.getParameterMap().put("DoS",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "DoS"), dosFlag.getKey()));
        aadmEvent.getParameterMap().put("TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX", new AxField(
                new AxReferenceKey(aadmEvent.getKey(), "TCP_UE_SIDE_MEDIAN_RTT_TX_TO_RX"), roundTripTime.getKey()));
        aadmEvent.getParameterMap().put("TCP_UE_SIDE_AVG_THROUGHPUT", new AxField(
                new AxReferenceKey(aadmEvent.getKey(), "TCP_UE_SIDE_AVG_THROUGHPUT"), averageThroughput.getKey()));
        aadmEvent.getParameterMap().put("APPLICATION",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "APPLICATION"), applicationName.getKey()));
        aadmEvent.getParameterMap().put("protocol_group",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "protocol_group"), protocolGroup.getKey()));
        aadmEvent.getParameterMap().put("http_host_class",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "http_host_class"), httpHostClass.getKey()));
        aadmEvent.getParameterMap().put("PROBE_ON",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "PROBE_ON"), probeOnFlag.getKey()));
        aadmEvent.getParameterMap().put("TCP_ON",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "TCP_ON"), tcpOnFlag.getKey()));
        aadmEvent.getParameterMap().put("SGW_IP_ADDRESS",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "SGW_IP_ADDRESS"), nwIPAddress.getKey()));
        aadmEvent.getParameterMap().put("UE_IP_ADDRESS",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "UE_IP_ADDRESS"), ueIPAddress.getKey()));
        aadmEvent.getParameterMap().put("SERVICE_REQUEST_COUNT", new AxField(
                new AxReferenceKey(aadmEvent.getKey(), "SERVICE_REQUEST_COUNT"), serviceRequestCount.getKey()));
        aadmEvent.getParameterMap().put("ATTACH_COUNT",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "ATTACH_COUNT"), attchCount.getKey()));
        aadmEvent.getParameterMap().put("NUM_SUBSCRIBERS",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "NUM_SUBSCRIBERS"), subscriberCount.getKey()));
        aadmEvent.getParameterMap().put("AVG_SUBSCRIBER_SERVICE_REQUEST",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "AVG_SUBSCRIBER_SERVICE_REQUEST"),
                        averageServiceRequest.getKey()));
        aadmEvent.getParameterMap().put("AVG_SUBSCRIBER_ATTACH",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "AVG_SUBSCRIBER_ATTACH"), averageAttach.getKey()));
        aadmEvent.getParameterMap().put("ACTTASK",
                new AxField(new AxReferenceKey(aadmEvent.getKey(), "ACTTASK"), actionTask.getKey()));

        final AxEvent aadmXStreamActEvent = new AxEvent(new AxArtifactKey("XSTREAM_AADM_ACT_EVENT", "0.0.1"),
                "org.onap.policy.apex.examples.aadm.events");
        aadmXStreamActEvent.setSource("Apex");
        aadmXStreamActEvent.setTarget("External");
        aadmXStreamActEvent.getParameterMap().put("IMSI",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "IMSI"), imsi.getKey()));
        aadmXStreamActEvent.getParameterMap().put("IMSI_IP",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "IMSI_IP"), ueIPAddress.getKey()));
        aadmXStreamActEvent.getParameterMap().put("ENODEB_ID",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "ENODEB_ID"), eNodeBID.getKey()));
        aadmXStreamActEvent.getParameterMap().put("NW_IP",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "NW_IP"), nwIPAddress.getKey()));
        aadmXStreamActEvent.getParameterMap().put("ACTTASK",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "ACTTASK"), actionTask.getKey()));
        aadmXStreamActEvent.getParameterMap().put("PROBE_ON",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "PROBE_ON"), probeOnFlag.getKey()));
        aadmXStreamActEvent.getParameterMap().put("TCP_ON",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "TCP_ON"), tcpOnFlag.getKey()));
        aadmXStreamActEvent.getParameterMap().put("VERSION",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "VERSION"), version.getKey()));
        aadmXStreamActEvent.getParameterMap().put("TRIGGER_SPEC",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "TRIGGER_SPEC"), triggerSpec.getKey()));
        aadmXStreamActEvent.getParameterMap().put("MAJ_MIN_MAINT_VERSION", new AxField(
                new AxReferenceKey(aadmXStreamActEvent.getKey(), "MAJ_MIN_MAINT_VERSION"), version.getKey()));
        aadmXStreamActEvent.getParameterMap().put("BLACKLIST_ON", new AxField(
                new AxReferenceKey(aadmXStreamActEvent.getKey(), "BLACKLIST_ON"), blacklistOnFlag.getKey()));
        aadmXStreamActEvent.getParameterMap().put("PROFILE",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "PROFILE"), profile.getKey()));
        aadmXStreamActEvent.getParameterMap().put("THRESHOLD",
                new AxField(new AxReferenceKey(aadmXStreamActEvent.getKey(), "THRESHOLD"), threshold.getKey()));

        final AxEvent vMMEEvent =
                new AxEvent(new AxArtifactKey("VMMEEvent", "0.0.1"), "org.onap.policy.apex.examples.aadm.events");
        vMMEEvent.setSource("External");
        vMMEEvent.setTarget("Apex");
        vMMEEvent.getParameterMap().put("IMSI",
                new AxField(new AxReferenceKey(vMMEEvent.getKey(), "IMSI"), imsi.getKey()));
        vMMEEvent.getParameterMap().put("ENODEB_ID",
                new AxField(new AxReferenceKey(vMMEEvent.getKey(), "ENODEB_ID"), eNodeBID.getKey()));
        vMMEEvent.getParameterMap().put("IMSI_IP",
                new AxField(new AxReferenceKey(vMMEEvent.getKey(), "IMSI_IP"), ueIPAddress.getKey()));
        vMMEEvent.getParameterMap().put("NW_IP",
                new AxField(new AxReferenceKey(vMMEEvent.getKey(), "NW_IP"), nwIPAddress.getKey()));
        vMMEEvent.getParameterMap().put("PROFILE",
                new AxField(new AxReferenceKey(vMMEEvent.getKey(), "PROFILE"), profile.getKey()));
        vMMEEvent.getParameterMap().put("THRESHOLD",
                new AxField(new AxReferenceKey(vMMEEvent.getKey(), "THRESHOLD"), threshold.getKey()));

        final AxEvent sapcEvent =
                new AxEvent(new AxArtifactKey("SAPCEvent", "0.0.1"), "org.onap.policy.apex.examples.aadm.events");
        sapcEvent.setSource("External");
        sapcEvent.setTarget("Apex");
        sapcEvent.getParameterMap().put("IMSI",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "IMSI"), imsi.getKey()));
        sapcEvent.getParameterMap().put("ENODEB_ID",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "ENODEB_ID"), eNodeBID.getKey()));
        sapcEvent.getParameterMap().put("IMSI_IP",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "IMSI_IP"), ueIPAddress.getKey()));
        sapcEvent.getParameterMap().put("NW_IP",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "NW_IP"), nwIPAddress.getKey()));
        sapcEvent.getParameterMap().put("PROFILE",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "PROFILE"), profile.getKey()));
        sapcEvent.getParameterMap().put("THRESHOLD",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "THRESHOLD"), threshold.getKey()));
        sapcEvent.getParameterMap().put("TCP_ON",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "TCP_ON"), tcpOnFlag.getKey()));
        sapcEvent.getParameterMap().put("PROBE_ON",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "PROBE_ON"), probeOnFlag.getKey()));
        sapcEvent.getParameterMap().put("VERSION",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "VERSION"), version.getKey()));
        sapcEvent.getParameterMap().put("BLACKLIST_ON",
                new AxField(new AxReferenceKey(sapcEvent.getKey(), "BLACKLIST_ON"), blacklistOnFlag.getKey()));

        final AxEvent sapcBlacklistSubscriberEvent =
                new AxEvent(new AxArtifactKey("SAPCBlacklistSubscriberEvent", "0.0.1"),
                        "org.onap.policy.apex.examples.aadm.events");
        sapcBlacklistSubscriberEvent.setSource("Apex");
        sapcBlacklistSubscriberEvent.setTarget("External");
        sapcBlacklistSubscriberEvent.getParameterMap().put("IMSI",
                new AxField(new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "IMSI"), imsi.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put("PROFILE",
                new AxField(new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "PROFILE"), profile.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put("BLACKLIST_ON", new AxField(
                new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "BLACKLIST_ON"), blacklistOnFlag.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put("IMSI_IP", new AxField(
                new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "IMSI_IP"), ueIPAddress.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put("NW_IP",
                new AxField(new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "NW_IP"), nwIPAddress.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put("PROBE_ON", new AxField(
                new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "PROBE_ON"), probeOnFlag.getKey()));
        sapcBlacklistSubscriberEvent.getParameterMap().put("TCP_ON",
                new AxField(new AxReferenceKey(sapcBlacklistSubscriberEvent.getKey(), "TCP_ON"), tcpOnFlag.getKey()));

        final AxEvent periodicEvent =
                new AxEvent(new AxArtifactKey("PeriodicEvent", "0.0.1"), "org.onap.policy.apex.examples.aadm.events");
        periodicEvent.setSource("System");
        periodicEvent.setTarget("Apex");
        periodicEvent.getParameterMap().put("PERIODIC_EVENT_COUNT", new AxField(
                new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_EVENT_COUNT"), periodicEventCount.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_DELAY",
                new AxField(new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_DELAY"), periodicDelay.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_FIRST_TIME",
                new AxField(new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_FIRST_TIME"), periodicTime.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_CURRENT_TIME", new AxField(
                new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_CURRENT_TIME"), periodicTime.getKey()));
        periodicEvent.getParameterMap().put("PERIODIC_LAST_TIME",
                new AxField(new AxReferenceKey(periodicEvent.getKey(), "PERIODIC_LAST_TIME"), periodicTime.getKey()));

        final AxEvents aadmEvents = new AxEvents(new AxArtifactKey("AADMEvents", "0.0.1"));
        aadmEvents.getEventMap().put(aadmEvent.getKey(), aadmEvent);
        aadmEvents.getEventMap().put(aadmXStreamActEvent.getKey(), aadmXStreamActEvent);
        aadmEvents.getEventMap().put(vMMEEvent.getKey(), vMMEEvent);
        aadmEvents.getEventMap().put(sapcEvent.getKey(), sapcEvent);
        aadmEvents.getEventMap().put(sapcBlacklistSubscriberEvent.getKey(), sapcBlacklistSubscriberEvent);
        aadmEvents.getEventMap().put(periodicEvent.getKey(), periodicEvent);

        // Data types for context
        final AxContextSchema eNodeBStatus = new AxContextSchema(new AxArtifactKey("ENodeBStatus", "0.0.1"), "Java",
                "org.onap.policy.apex.examples.aadm.concepts.ENodeBStatus");
        final AxContextSchema imsiStatus = new AxContextSchema(new AxArtifactKey("IMSIStatus", "0.0.1"), "Java",
                "org.onap.policy.apex.examples.aadm.concepts.IMSIStatus");
        final AxContextSchema ipAddressStatus = new AxContextSchema(new AxArtifactKey("IPAddressStatus", "0.0.1"),
                "Java", "org.onap.policy.apex.examples.aadm.concepts.IPAddressStatus");
        aadmContextSchemas.getSchemasMap().put(eNodeBStatus.getKey(), eNodeBStatus);
        aadmContextSchemas.getSchemasMap().put(imsiStatus.getKey(), imsiStatus);
        aadmContextSchemas.getSchemasMap().put(ipAddressStatus.getKey(), ipAddressStatus);

        // Three context albums for AADM
        final AxContextAlbum eNodeBStatusAlbum = new AxContextAlbum(new AxArtifactKey("ENodeBStatusAlbum", "0.0.1"),
                "APPLICATION", true, eNodeBStatus.getKey());
        final AxContextAlbum imsiStatusAlbum = new AxContextAlbum(new AxArtifactKey("IMSIStatusAlbum", "0.0.1"),
                "APPLICATION", true, imsiStatus.getKey());
        final AxContextAlbum ipAddressStatusAlbum = new AxContextAlbum(
                new AxArtifactKey("IPAddressStatusAlbum", "0.0.1"), "APPLICATION", true, ipAddressStatus.getKey());

        final AxContextAlbums aadmAlbums = new AxContextAlbums(new AxArtifactKey("AADMContext", "0.0.1"));
        aadmAlbums.getAlbumsMap().put(eNodeBStatusAlbum.getKey(), eNodeBStatusAlbum);
        aadmAlbums.getAlbumsMap().put(imsiStatusAlbum.getKey(), imsiStatusAlbum);
        aadmAlbums.getAlbumsMap().put(ipAddressStatusAlbum.getKey(), ipAddressStatusAlbum);

        // Tasks
        final AxLogicReader logicReader =
                new PolicyLogicReader().setLogicPackage(this.getClass().getPackage().getName()).setDefaultLogic(null);

        final AxTask aadmMatchTask = new AxTask(new AxArtifactKey("AADMMatchTask", "0.0.1"));
        aadmMatchTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmMatchTask.duplicateOutputFields(aadmEvent.getParameterMap());
        aadmMatchTask.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmMatchTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmMatchTask.getContextAlbumReferences().add(ipAddressStatusAlbum.getKey());
        aadmMatchTask.setTaskLogic(new AxTaskLogic(aadmMatchTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask aadmEstablishTask = new AxTask(new AxArtifactKey("AADMEstablishTask", "0.0.1"));
        aadmEstablishTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmEstablishTask.duplicateOutputFields(aadmEvent.getParameterMap());
        logicReader.setDefaultLogic("Default_TaskLogic");
        aadmEstablishTask.setTaskLogic(new AxTaskLogic(aadmEstablishTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask aadmDecideTask = new AxTask(new AxArtifactKey("AADMDecideTask", "0.0.1"));
        aadmDecideTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmDecideTask.duplicateOutputFields(aadmEvent.getParameterMap());
        aadmDecideTask.setTaskLogic(new AxTaskLogic(aadmDecideTask.getKey(), "TaskLogic", "MVEL", logicReader));

        logicReader.setDefaultLogic(null);

        final AxTask aadmDoSSuggestionActTask = new AxTask(new AxArtifactKey("AADMDoSSuggestionActTask", "0.0.1"));
        aadmDoSSuggestionActTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmDoSSuggestionActTask.duplicateOutputFields(aadmXStreamActEvent.getParameterMap());
        aadmDoSSuggestionActTask.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmDoSSuggestionActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmDoSSuggestionActTask
                .setTaskLogic(new AxTaskLogic(aadmDoSSuggestionActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask aadmNoActTask = new AxTask(new AxArtifactKey("AADMNoActTask", "0.0.1"));
        aadmNoActTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmNoActTask.duplicateOutputFields(aadmXStreamActEvent.getParameterMap());
        aadmNoActTask.setTaskLogic(new AxTaskLogic(aadmNoActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask aadmDoSProvenActTask = new AxTask(new AxArtifactKey("AADMDoSProvenActTask", "0.0.1"));
        aadmDoSProvenActTask.duplicateInputFields(aadmEvent.getParameterMap());
        aadmDoSProvenActTask.duplicateOutputFields(aadmXStreamActEvent.getParameterMap());
        aadmDoSProvenActTask.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmDoSProvenActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmDoSProvenActTask
                .setTaskLogic(new AxTaskLogic(aadmDoSProvenActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask vMMEMatchTask = new AxTask(new AxArtifactKey("VMMEMatchTask", "0.0.1"));
        vMMEMatchTask.duplicateInputFields(vMMEEvent.getParameterMap());
        vMMEMatchTask.duplicateOutputFields(vMMEEvent.getParameterMap());
        vMMEMatchTask.setTaskLogic(new AxTaskLogic(vMMEMatchTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask vMMEEstablishTask = new AxTask(new AxArtifactKey("VMMEEstablishTask", "0.0.1"));
        vMMEEstablishTask.duplicateInputFields(vMMEEvent.getParameterMap());
        vMMEEstablishTask.duplicateOutputFields(vMMEEvent.getParameterMap());
        logicReader.setDefaultLogic("Default_TaskLogic");
        vMMEEstablishTask.setTaskLogic(new AxTaskLogic(vMMEEstablishTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask vMMEDecideTask = new AxTask(new AxArtifactKey("VMMEDecideTask", "0.0.1"));
        vMMEDecideTask.duplicateInputFields(vMMEEvent.getParameterMap());
        vMMEDecideTask.duplicateOutputFields(vMMEEvent.getParameterMap());
        vMMEDecideTask.setTaskLogic(new AxTaskLogic(vMMEDecideTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask vMMENoActTask = new AxTask(new AxArtifactKey("VMMENoActTask", "0.0.1"));
        vMMENoActTask.duplicateInputFields(vMMEEvent.getParameterMap());
        vMMENoActTask.duplicateOutputFields(vMMEEvent.getParameterMap());
        vMMENoActTask.setTaskLogic(new AxTaskLogic(vMMENoActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask vMMEActTask = new AxTask(new AxArtifactKey("VMMEActTask", "0.0.1"));
        vMMEActTask.duplicateInputFields(vMMEEvent.getParameterMap());
        vMMEActTask.duplicateOutputFields(vMMEEvent.getParameterMap());
        logicReader.setDefaultLogic(null);
        vMMEActTask.setTaskLogic(new AxTaskLogic(vMMEActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask sapcMatchTask = new AxTask(new AxArtifactKey("SAPCMatchTask", "0.0.1"));
        sapcMatchTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcMatchTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        sapcMatchTask.setTaskLogic(new AxTaskLogic(sapcMatchTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask sapcEstablishTask = new AxTask(new AxArtifactKey("SAPCEstablishTask", "0.0.1"));
        sapcEstablishTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcEstablishTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        logicReader.setDefaultLogic("Default_TaskLogic");
        sapcEstablishTask.setTaskLogic(new AxTaskLogic(sapcEstablishTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask sapcDecideTask = new AxTask(new AxArtifactKey("SAPCDecideTask", "0.0.1"));
        sapcDecideTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcDecideTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        sapcDecideTask.setTaskLogic(new AxTaskLogic(sapcDecideTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask sapcActTask = new AxTask(new AxArtifactKey("SAPCActTask", "0.0.1"));
        sapcActTask.duplicateInputFields(sapcEvent.getParameterMap());
        sapcActTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        sapcActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        logicReader.setDefaultLogic(null);
        sapcActTask.setTaskLogic(new AxTaskLogic(sapcActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        logicReader.setDefaultLogic("Default_TaskLogic");

        final AxTask periodicMatchTask = new AxTask(new AxArtifactKey("PeriodicMatchTask", "0.0.1"));
        periodicMatchTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicMatchTask.duplicateOutputFields(periodicEvent.getParameterMap());
        periodicMatchTask.setTaskLogic(new AxTaskLogic(periodicMatchTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask periodicEstablishTask = new AxTask(new AxArtifactKey("PeriodicEstablishTask", "0.0.1"));
        periodicEstablishTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicEstablishTask.duplicateOutputFields(periodicEvent.getParameterMap());
        periodicEstablishTask
                .setTaskLogic(new AxTaskLogic(periodicEstablishTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask periodicDecideTask = new AxTask(new AxArtifactKey("PeriodicDecideTask", "0.0.1"));
        periodicDecideTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicDecideTask.duplicateOutputFields(periodicEvent.getParameterMap());
        periodicDecideTask.setTaskLogic(new AxTaskLogic(periodicDecideTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTask periodicActTask = new AxTask(new AxArtifactKey("PeriodicActTask", "0.0.1"));
        periodicActTask.duplicateInputFields(periodicEvent.getParameterMap());
        periodicActTask.duplicateOutputFields(sapcBlacklistSubscriberEvent.getParameterMap());
        periodicActTask.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        logicReader.setDefaultLogic(null);
        periodicActTask.setTaskLogic(new AxTaskLogic(periodicActTask.getKey(), "TaskLogic", "MVEL", logicReader));

        final AxTasks aadmTasks = new AxTasks(new AxArtifactKey("AADMTasks", "0.0.1"));
        aadmTasks.getTaskMap().put(aadmMatchTask.getKey(), aadmMatchTask);
        aadmTasks.getTaskMap().put(aadmEstablishTask.getKey(), aadmEstablishTask);
        aadmTasks.getTaskMap().put(aadmDecideTask.getKey(), aadmDecideTask);
        aadmTasks.getTaskMap().put(aadmDoSSuggestionActTask.getKey(), aadmDoSSuggestionActTask);
        aadmTasks.getTaskMap().put(aadmNoActTask.getKey(), aadmNoActTask);
        aadmTasks.getTaskMap().put(aadmDoSProvenActTask.getKey(), aadmDoSProvenActTask);
        aadmTasks.getTaskMap().put(vMMEMatchTask.getKey(), vMMEMatchTask);
        aadmTasks.getTaskMap().put(vMMEEstablishTask.getKey(), vMMEEstablishTask);
        aadmTasks.getTaskMap().put(vMMEDecideTask.getKey(), vMMEDecideTask);
        aadmTasks.getTaskMap().put(vMMENoActTask.getKey(), vMMENoActTask);
        aadmTasks.getTaskMap().put(vMMEActTask.getKey(), vMMEActTask);
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

        final AxPolicy aadmPolicy = new AxPolicy(new AxArtifactKey("AADMPolicy", "0.0.1"));
        aadmPolicy.setTemplate("MEDA");

        final AxState aadmActState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), "Act"));
        aadmActState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmAct2Out =
                new AxStateOutput(aadmActState.getKey(), AxReferenceKey.getNullKey(), aadmXStreamActEvent.getKey());
        aadmActState.getStateOutputs().put(aadmAct2Out.getKey().getLocalName(), aadmAct2Out);
        aadmActState.getContextAlbumReferences().add(ipAddressStatusAlbum.getKey());
        aadmActState.getContextAlbumReferences().add(imsiStatusAlbum.getKey());
        aadmActState.getContextAlbumReferences().add(eNodeBStatusAlbum.getKey());
        aadmActState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(aadmActState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        aadmActState.setDefaultTask(aadmNoActTask.getKey());
        aadmActState.getTaskReferences().put(aadmNoActTask.getKey(), new AxStateTaskReference(aadmActState.getKey(),
                aadmNoActTask.getKey(), AxStateTaskOutputType.DIRECT, aadmAct2Out.getKey()));
        aadmActState.getTaskReferences().put(aadmDoSSuggestionActTask.getKey(),
                new AxStateTaskReference(aadmActState.getKey(), aadmDoSSuggestionActTask.getKey(),
                        AxStateTaskOutputType.DIRECT, aadmAct2Out.getKey()));
        aadmActState.getTaskReferences().put(aadmDoSProvenActTask.getKey(),
                new AxStateTaskReference(aadmActState.getKey(), aadmDoSProvenActTask.getKey(),
                        AxStateTaskOutputType.DIRECT, aadmAct2Out.getKey()));

        logicReader.setDefaultLogic("Default_TaskSelectionLogic");

        final AxState aadmDecideState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), "Decide"));
        aadmDecideState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmDec2Act =
                new AxStateOutput(aadmDecideState.getKey(), aadmActState.getKey(), aadmEvent.getKey());
        aadmDecideState.getStateOutputs().put(aadmDec2Act.getKey().getLocalName(), aadmDec2Act);
        aadmDecideState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(aadmDecideState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        aadmDecideState.setDefaultTask(aadmDecideTask.getKey());
        aadmDecideState.getTaskReferences().put(aadmDecideTask.getKey(), new AxStateTaskReference(
                aadmDecideState.getKey(), aadmDecideTask.getKey(), AxStateTaskOutputType.DIRECT, aadmDec2Act.getKey()));

        final AxState aadmEstablishState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), "Establish"));
        aadmEstablishState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmEst2Dec =
                new AxStateOutput(aadmEstablishState.getKey(), aadmDecideState.getKey(), aadmEvent.getKey());
        aadmEstablishState.getStateOutputs().put(aadmEst2Dec.getKey().getLocalName(), aadmEst2Dec);
        aadmEstablishState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(aadmEstablishState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        aadmEstablishState.setDefaultTask(aadmEstablishTask.getKey());
        aadmEstablishState.getTaskReferences().put(aadmEstablishTask.getKey(),
                new AxStateTaskReference(aadmEstablishState.getKey(), aadmEstablishTask.getKey(),
                        AxStateTaskOutputType.DIRECT, aadmEst2Dec.getKey()));

        final AxState aadmMatchState = new AxState(new AxReferenceKey(aadmPolicy.getKey(), "Match"));
        aadmMatchState.setTrigger(aadmEvent.getKey());
        final AxStateOutput aadmMat2Est =
                new AxStateOutput(aadmMatchState.getKey(), aadmEstablishState.getKey(), aadmEvent.getKey());
        aadmMatchState.getStateOutputs().put(aadmMat2Est.getKey().getLocalName(), aadmMat2Est);
        aadmMatchState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(aadmMatchState.getKey(), "TaskSelectionLogic", "MVEL", logicReader));
        aadmMatchState.setDefaultTask(aadmMatchTask.getKey());
        aadmMatchState.getTaskReferences().put(aadmMatchTask.getKey(), new AxStateTaskReference(aadmMatchState.getKey(),
                aadmMatchTask.getKey(), AxStateTaskOutputType.DIRECT, aadmMat2Est.getKey()));

        aadmPolicy.setFirstState(aadmMatchState.getKey().getLocalName());
        aadmPolicy.getStateMap().put(aadmMatchState.getKey().getLocalName(), aadmMatchState);
        aadmPolicy.getStateMap().put(aadmEstablishState.getKey().getLocalName(), aadmEstablishState);
        aadmPolicy.getStateMap().put(aadmDecideState.getKey().getLocalName(), aadmDecideState);
        aadmPolicy.getStateMap().put(aadmActState.getKey().getLocalName(), aadmActState);

        final AxPolicy vMMEPolicy = new AxPolicy(new AxArtifactKey("VMMEPolicy", "0.0.1"));
        vMMEPolicy.setTemplate("MEDA");

        final AxState vMMEActState = new AxState(new AxReferenceKey(vMMEPolicy.getKey(), "Act"));
        vMMEActState.setTrigger(vMMEEvent.getKey());
        final AxStateOutput vMMEAct2Out =
                new AxStateOutput(vMMEActState.getKey(), AxReferenceKey.getNullKey(), vMMEEvent.getKey());
        vMMEActState.getStateOutputs().put(vMMEAct2Out.getKey().getLocalName(), vMMEAct2Out);
        vMMEActState.setDefaultTask(vMMEActTask.getKey());
        vMMEActState.getTaskReferences().put(vMMEActTask.getKey(), new AxStateTaskReference(vMMEActState.getKey(),
                vMMEActTask.getKey(), AxStateTaskOutputType.DIRECT, vMMEAct2Out.getKey()));
        vMMEActState.getTaskReferences().put(vMMENoActTask.getKey(), new AxStateTaskReference(vMMEActState.getKey(),
                vMMENoActTask.getKey(), AxStateTaskOutputType.DIRECT, vMMEAct2Out.getKey()));

        final AxState vMMEDecideState = new AxState(new AxReferenceKey(vMMEPolicy.getKey(), "Decide"));
        vMMEDecideState.setTrigger(vMMEEvent.getKey());
        final AxStateOutput vMMEDec2Act =
                new AxStateOutput(vMMEDecideState.getKey(), vMMEActState.getKey(), vMMEEvent.getKey());
        vMMEDecideState.getStateOutputs().put(vMMEDec2Act.getKey().getLocalName(), vMMEDec2Act);
        vMMEDecideState.setDefaultTask(vMMEDecideTask.getKey());
        vMMEDecideState.getTaskReferences().put(vMMEDecideTask.getKey(), new AxStateTaskReference(
                vMMEDecideState.getKey(), vMMEDecideTask.getKey(), AxStateTaskOutputType.DIRECT, vMMEDec2Act.getKey()));

        final AxState vMMEEstablishState = new AxState(new AxReferenceKey(vMMEPolicy.getKey(), "Establish"));
        vMMEEstablishState.setTrigger(vMMEEvent.getKey());
        final AxStateOutput vMMEEst2Dec =
                new AxStateOutput(vMMEEstablishState.getKey(), vMMEDecideState.getKey(), vMMEEvent.getKey());
        vMMEEstablishState.getStateOutputs().put(vMMEEst2Dec.getKey().getLocalName(), vMMEEst2Dec);
        vMMEEstablishState.setDefaultTask(vMMEEstablishTask.getKey());
        vMMEEstablishState.getTaskReferences().put(vMMEEstablishTask.getKey(),
                new AxStateTaskReference(vMMEEstablishState.getKey(), vMMEEstablishTask.getKey(),
                        AxStateTaskOutputType.DIRECT, vMMEEst2Dec.getKey()));

        final AxState vMMEMatchState = new AxState(new AxReferenceKey(vMMEPolicy.getKey(), "Match"));
        vMMEMatchState.setTrigger(vMMEEvent.getKey());
        final AxStateOutput vMMEMat2Est =
                new AxStateOutput(vMMEMatchState.getKey(), vMMEEstablishState.getKey(), vMMEEvent.getKey());
        vMMEMatchState.getStateOutputs().put(vMMEMat2Est.getKey().getLocalName(), vMMEMat2Est);
        vMMEMatchState.setDefaultTask(vMMEMatchTask.getKey());
        vMMEMatchState.getTaskReferences().put(vMMEMatchTask.getKey(), new AxStateTaskReference(vMMEMatchState.getKey(),
                vMMEMatchTask.getKey(), AxStateTaskOutputType.DIRECT, vMMEMat2Est.getKey()));

        vMMEPolicy.setFirstState(vMMEMatchState.getKey().getLocalName());
        vMMEPolicy.getStateMap().put(vMMEMatchState.getKey().getLocalName(), vMMEMatchState);
        vMMEPolicy.getStateMap().put(vMMEEstablishState.getKey().getLocalName(), vMMEEstablishState);
        vMMEPolicy.getStateMap().put(vMMEDecideState.getKey().getLocalName(), vMMEDecideState);
        vMMEPolicy.getStateMap().put(vMMEActState.getKey().getLocalName(), vMMEActState);

        final AxPolicy sapcPolicy = new AxPolicy(new AxArtifactKey("SAPCPolicy", "0.0.1"));
        sapcPolicy.setTemplate("MEDA");

        final AxState sapcActState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), "Act"));
        sapcActState.setTrigger(sapcEvent.getKey());
        final AxStateOutput sapcAct2Out = new AxStateOutput(sapcActState.getKey(), AxReferenceKey.getNullKey(),
                sapcBlacklistSubscriberEvent.getKey());
        sapcActState.getStateOutputs().put(sapcAct2Out.getKey().getLocalName(), sapcAct2Out);
        sapcActState.setDefaultTask(sapcActTask.getKey());
        sapcActState.getTaskReferences().put(sapcActTask.getKey(), new AxStateTaskReference(sapcActState.getKey(),
                sapcActTask.getKey(), AxStateTaskOutputType.DIRECT, sapcAct2Out.getKey()));

        final AxState sapcDecideState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), "Decide"));
        sapcDecideState.setTrigger(sapcEvent.getKey());
        final AxStateOutput sapcDec2Act =
                new AxStateOutput(sapcDecideState.getKey(), sapcActState.getKey(), sapcEvent.getKey());
        sapcDecideState.getStateOutputs().put(sapcDec2Act.getKey().getLocalName(), sapcDec2Act);
        sapcDecideState.setDefaultTask(sapcDecideTask.getKey());
        sapcDecideState.getTaskReferences().put(sapcDecideTask.getKey(), new AxStateTaskReference(
                sapcDecideState.getKey(), sapcDecideTask.getKey(), AxStateTaskOutputType.DIRECT, sapcDec2Act.getKey()));

        final AxState sapcEstablishState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), "Establish"));
        sapcEstablishState.setTrigger(sapcEvent.getKey());
        final AxStateOutput sapcEst2Dec =
                new AxStateOutput(sapcEstablishState.getKey(), sapcDecideState.getKey(), sapcEvent.getKey());
        sapcEstablishState.getStateOutputs().put(sapcEst2Dec.getKey().getLocalName(), sapcEst2Dec);
        sapcEstablishState.setDefaultTask(sapcEstablishTask.getKey());
        sapcEstablishState.getTaskReferences().put(sapcEstablishTask.getKey(),
                new AxStateTaskReference(sapcEstablishState.getKey(), sapcEstablishTask.getKey(),
                        AxStateTaskOutputType.DIRECT, sapcEst2Dec.getKey()));

        final AxState sapcMatchState = new AxState(new AxReferenceKey(sapcPolicy.getKey(), "Match"));
        sapcMatchState.setTrigger(aadmXStreamActEvent.getKey());
        final AxStateOutput sapcMat2Est =
                new AxStateOutput(sapcMatchState.getKey(), sapcEstablishState.getKey(), sapcEvent.getKey());
        sapcMatchState.getStateOutputs().put(sapcMat2Est.getKey().getLocalName(), sapcMat2Est);
        sapcMatchState.setDefaultTask(sapcMatchTask.getKey());
        sapcMatchState.getTaskReferences().put(sapcMatchTask.getKey(), new AxStateTaskReference(sapcMatchState.getKey(),
                sapcMatchTask.getKey(), AxStateTaskOutputType.DIRECT, sapcMat2Est.getKey()));

        sapcPolicy.setFirstState(sapcMatchState.getKey().getLocalName());
        sapcPolicy.getStateMap().put(sapcMatchState.getKey().getLocalName(), sapcMatchState);
        sapcPolicy.getStateMap().put(sapcEstablishState.getKey().getLocalName(), sapcEstablishState);
        sapcPolicy.getStateMap().put(sapcDecideState.getKey().getLocalName(), sapcDecideState);
        sapcPolicy.getStateMap().put(sapcActState.getKey().getLocalName(), sapcActState);

        final AxPolicy periodicPolicy = new AxPolicy(new AxArtifactKey("PeriodicPolicy", "0.0.1"));
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

        final AxState periodicDecideState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), "Decide"));
        periodicDecideState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perDec2Act =
                new AxStateOutput(periodicDecideState.getKey(), periodicActState.getKey(), periodicEvent.getKey());
        periodicDecideState.getStateOutputs().put(perDec2Act.getKey().getLocalName(), perDec2Act);
        periodicDecideState.setDefaultTask(periodicDecideTask.getKey());
        periodicDecideState.getTaskReferences().put(periodicDecideTask.getKey(),
                new AxStateTaskReference(periodicDecideState.getKey(), periodicDecideTask.getKey(),
                        AxStateTaskOutputType.DIRECT, perDec2Act.getKey()));

        final AxState periodicEstablishState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), "Establish"));
        periodicEstablishState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perEst2Dec = new AxStateOutput(periodicEstablishState.getKey(),
                periodicDecideState.getKey(), periodicEvent.getKey());
        periodicEstablishState.getStateOutputs().put(perEst2Dec.getKey().getLocalName(), perEst2Dec);
        periodicEstablishState.setDefaultTask(periodicEstablishTask.getKey());
        periodicEstablishState.getTaskReferences().put(periodicEstablishTask.getKey(),
                new AxStateTaskReference(periodicEstablishState.getKey(), periodicEstablishTask.getKey(),
                        AxStateTaskOutputType.DIRECT, perEst2Dec.getKey()));

        final AxState periodicMatchState = new AxState(new AxReferenceKey(periodicPolicy.getKey(), "Match"));
        periodicMatchState.setTrigger(periodicEvent.getKey());
        final AxStateOutput perMat2Est =
                new AxStateOutput(periodicMatchState.getKey(), periodicEstablishState.getKey(), periodicEvent.getKey());
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

        final AxPolicies aadmPolicies = new AxPolicies(new AxArtifactKey("AADMPolicies", "0.0.1"));
        aadmPolicies.getPolicyMap().put(aadmPolicy.getKey(), aadmPolicy);
        aadmPolicies.getPolicyMap().put(vMMEPolicy.getKey(), vMMEPolicy);
        aadmPolicies.getPolicyMap().put(sapcPolicy.getKey(), sapcPolicy);
        aadmPolicies.getPolicyMap().put(periodicPolicy.getKey(), periodicPolicy);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("AADMKeyInformation", "0.0.1"));
        final AxPolicyModel aadmPolicyModel = new AxPolicyModel(new AxArtifactKey("AADMPolicyModel", "0.0.1"));
        aadmPolicyModel.setPolicies(aadmPolicies);
        aadmPolicyModel.setEvents(aadmEvents);
        aadmPolicyModel.setTasks(aadmTasks);
        aadmPolicyModel.setAlbums(aadmAlbums);
        aadmPolicyModel.setSchemas(aadmContextSchemas);
        aadmPolicyModel.setKeyInformation(keyInformation);
        aadmPolicyModel.getKeyInformation().generateKeyInfo(aadmPolicyModel);

        final AxValidationResult result = aadmPolicyModel.validate(new AxValidationResult());
        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.VALID)) {
            throw new ApexRuntimeException("model " + aadmPolicyModel.getID() + " is not valid" + result);
        }
        return aadmPolicyModel;
    }
}
