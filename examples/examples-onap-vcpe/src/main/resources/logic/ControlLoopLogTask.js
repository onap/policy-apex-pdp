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

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());

executor.logger.info("Logging context information for VNF \"" + executor.inFields.get("vnfID") + "\"");

var clNotification = new org.onap.policy.controlloop.VirtualControlLoopNotification();

clNotification.setClosedLoopControlName(vcpeClosedLoopStatus.get("closedLoopControlName"));
clNotification.setClosedLoopAlarmStart(java.time.Instant.ofEpochMilli(vcpeClosedLoopStatus.get("closedLoopAlarmStart")));
clNotification.setClosedLoopAlarmEnd(java.time.Instant.ofEpochMilli(vcpeClosedLoopStatus.get("closedLoopAlarmEnd")));
clNotification.setClosedLoopEventClient(vcpeClosedLoopStatus.get("closedLoopEventClient"));
clNotification.setVersion(vcpeClosedLoopStatus.get("version"));
clNotification.setRequestId(java.util.UUID.fromString(vcpeClosedLoopStatus.get("requestID")));
clNotification.setTargetType(org.onap.policy.controlloop.ControlLoopTargetType.toType(vcpeClosedLoopStatus.get("target_type")));
clNotification.setTarget(org.onap.policy.controlloop.ControlLoopEventStatus.toStatus(vcpeClosedLoopStatus.get("target")));
clNotification.setFrom(vcpeClosedLoopStatus.get("from"));
clNotification.setPolicyScope(vcpeClosedLoopStatus.get("policyScope"));
clNotification.setPolicyName(vcpeClosedLoopStatus.get("policyName"));
clNotification.setPolicyVersion(vcpeClosedLoopStatus.get("policyVersion"));
clNotification.setNotification(org.onap.policy.controlloop.ControlLoopNotificationType.toType(vcpeClosedLoopStatus.get("notification")));
clNotification.setMessage(vcpeClosedLoopStatus.get("message"));

var notificationInstant = java.time.Instant.ofEpochSecond(vcpeClosedLoopStatus.get("notificationTime"));
var notificationTime = java.time.ZonedDateTime.ofInstant(notificationInstant, java.time.ZoneOffset.UTC);
clNotification.setNotificationTime(notificationTime);

var aaiInfo = vcpeClosedLoopStatus.get("AAI");

clNotification.getAai().put("generic-vnf.resource-version",        aaiInfo.get("genericVnfResourceVersion"));      
clNotification.getAai().put("generic-vnf.vnf-name",                aaiInfo.get("genericVnfVnfName"));              
clNotification.getAai().put("generic-vnf.prov-status",             aaiInfo.get("genericVnfProvStatus"));           
clNotification.getAai().put("generic-vnf.is-closed-loop-disabled", aaiInfo.get("genericVnfIsClosedLoopDisabled")); 
clNotification.getAai().put("generic-vnf.orchestration-status",    aaiInfo.get("genericVnfOrchestrationStatus"));  
clNotification.getAai().put("generic-vnf.vnf-type",                aaiInfo.get("genericVnfVnfType"));              
clNotification.getAai().put("generic-vnf.in-maint",                aaiInfo.get("genericVnfInMaint"));              
clNotification.getAai().put("generic-vnf.service-id",              aaiInfo.get("genericVnfServiceId"));            
clNotification.getAai().put("generic-vnf.vnf-id",                  aaiInfo.get("genericVnfVnfId"));                

executor.outFields.put("VirtualControlLoopNotification", clNotification);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
