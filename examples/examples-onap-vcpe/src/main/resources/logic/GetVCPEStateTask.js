/*
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

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);


var clEventType = Java.type("org.onap.policy.controlloop.VirtualControlLoopEvent");
var longType = Java.type("java.lang.Long");
var uuidType = Java.type("java.util.UUID");

var clEvent = executor.inFields.get("VirtualControlLoopEvent");

executor.logger.info(clEvent.getClosedLoopControlName());

var requestID = clEvent.getRequestId();
var vnfID = uuidType.fromString(clEvent.getAai().get("generic-vnf.vnf-id"));

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(vnfID);

if (vcpeClosedLoopStatus == null) {
    executor.logger.info("Creating context information for new vCPE VNF \"" + vnfID.toString() + "\"");

    vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").getSchemaHelper().createNewInstance();

    vcpeClosedLoopStatus.put("closedLoopControlName", clEvent.getClosedLoopControlName());
    vcpeClosedLoopStatus.put("closedLoopAlarmStart",  clEvent.getClosedLoopAlarmStart());
    vcpeClosedLoopStatus.put("closedLoopEventClient", clEvent.getClosedLoopEventClient());
    vcpeClosedLoopStatus.put("closedLoopEventStatus", clEvent.getClosedLoopEventStatus());
    vcpeClosedLoopStatus.put("version",               clEvent.getVersion());
    vcpeClosedLoopStatus.put("requestID",             clEvent.getRequestId());
    vcpeClosedLoopStatus.put("target_type",           clEvent.getTargetType());
    vcpeClosedLoopStatus.put("target",                clEvent.getTarget());
    vcpeClosedLoopStatus.put("from",                  clEvent.getFrom());
    vcpeClosedLoopStatus.put("policyScope",           "vCPE");
    vcpeClosedLoopStatus.put("policyName",            "ONAPvCPEPolicyModel");
    vcpeClosedLoopStatus.put("policyVersion",         "0.0.1");
    vcpeClosedLoopStatus.put("notification",          org.onap.policy.controlloop.ControlLoopNotificationType.ACTIVE);
    vcpeClosedLoopStatus.put("notificationTime",      java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC));

    var aaiInfo = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").getSchemaHelper().createNewSubInstance("VCPE_AAI_Type");

    aaiInfo.put("genericVnfResourceVersion",      clEvent.getAai().get("generic-vnf.resource-version"));
    aaiInfo.put("genericVnfVnfName",              clEvent.getAai().get("generic-vnf.vnf-name"));
    aaiInfo.put("genericVnfProvStatus",           clEvent.getAai().get("generic-vnf.prov-status"));
    aaiInfo.put("genericVnfIsClosedLoopDisabled", clEvent.getAai().get("generic-vnf.is-closed-loop-disabled"));
    aaiInfo.put("genericVnfOrchestrationStatus",  clEvent.getAai().get("generic-vnf.orchestration-status"));
    aaiInfo.put("genericVnfVnfType",              clEvent.getAai().get("generic-vnf.vnf-type"));
    aaiInfo.put("genericVnfInMaint",              clEvent.getAai().get("generic-vnf.in-maint"));
    aaiInfo.put("genericVnfServiceId",            clEvent.getAai().get("generic-vnf.service-id"));
    aaiInfo.put("genericVnfVnfId",                clEvent.getAai().get("generic-vnf.vnf-id"));
    
    vcpeClosedLoopStatus.put("AAI", aaiInfo);

    if (clEvent.getClosedLoopAlarmEnd() != null) {
        vcpeClosedLoopStatus.put("closedLoopAlarmEnd", clEvent.getClosedLoopAlarmEnd());
    } else {
        vcpeClosedLoopStatus.put("closedLoopAlarmEnd", java.time.Instant.EPOCH);
    }

    executor.getContextAlbum("VCPEClosedLoopStatusAlbum").put(vnfID.toString(), vcpeClosedLoopStatus);

    executor.logger.info("Created context information for new vCPE VNF \"" + vnfID.toString() + "\"");
}

executor.outFields.put("requestID", requestID);
executor.outFields.put("vnfID", vnfID);

executor.logger.info(executor.outFields);

returnValue = executor.isTrue;
