/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

var vnfID = executor.inFields.get("vnfID");
if (vnfID == null) {
    vnfID = executor.inFields.get("vnfName");
}
executor.logger.info("vnfID=" + vnfID);

var controlLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(vnfID.toString());

executor.logger.info("Logging context information for VNF \"" + vnfID + "\"");

var clNotification = executor.subject.getOutFieldSchemaHelper("VirtualControlLoopNotification").createNewInstance();

clNotification.put("requestID",             executor.inFields.get("requestID").toString());
clNotification.put("closedLoopControlName", controlLoopStatus.get("closedLoopControlName"));
clNotification.put("closedLoopAlarmStart",  controlLoopStatus.get("closedLoopAlarmStart"));
clNotification.put("closedLoopAlarmEnd",    controlLoopStatus.get("closedLoopAlarmEnd"));
clNotification.put("closedLoopEventClient", controlLoopStatus.get("closedLoopEventClient"));
clNotification.put("version",               controlLoopStatus.get("version"));
clNotification.put("targetType",            controlLoopStatus.get("target_type"));
clNotification.put("target",                controlLoopStatus.get("target"));
clNotification.put("from",                  controlLoopStatus.get("from"));
clNotification.put("policyScope",           controlLoopStatus.get("policyScope"));
clNotification.put("policyName",            controlLoopStatus.get("policyName"));
clNotification.put("policyVersion",         controlLoopStatus.get("policyVersion"));
clNotification.put("notification",          controlLoopStatus.get("notification"));
clNotification.put("message",               controlLoopStatus.get("message"));
clNotification.put("notificationTime",      controlLoopStatus.get("notificationTime"));
clNotification.put("opsClTimer",            0);

var aaiInfo = controlLoopStatus.get("AAI");

var aaiInfoOut = new java.util.HashMap();

aaiInfoOut.put("generic-vnf.resource-version", aaiInfo.get("genericVnfResourceVersion"));
aaiInfoOut.put("generic-vnf.vnf-name", aaiInfo.get("genericVnfVnfName"));
aaiInfoOut.put("generic-vnf.prov-status", aaiInfo.get("genericVnfProvStatus"));
aaiInfoOut.put("generic-vnf.is-closed-loop-disabled", aaiInfo.get("genericVnfIsClosedLoopDisabled"));
aaiInfoOut.put("generic-vnf.orchestration-status", aaiInfo.get("genericVnfOrchestrationStatus"));
aaiInfoOut.put("generic-vnf.vnf-type", aaiInfo.get("genericVnfVnfType"));
aaiInfoOut.put("generic-vnf.in-maint", aaiInfo.get("genericVnfInMaint"));
aaiInfoOut.put("generic-vnf.service-id", aaiInfo.get("genericVnfServiceId"));
if (vnfID != null) {
    aaiInfoOut.put("generic-vnf.vnf-id", aaiInfo.get("genericVnfVnfId"));
}

clNotification.put("AAI", aaiInfoOut);

executor.outFields.put("VirtualControlLoopNotification", clNotification);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;

returnValue == true;
