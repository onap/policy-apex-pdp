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

var appcRequest = new org.onap.policy.appclcm.LcmRequestWrapper;
appcRequest.setBody(new org.onap.policy.appclcm.LcmRequest);
appcRequest.getBody().setCommonHeader(new org.onap.policy.appclcm.LcmCommonHeader);

appcRequest.setVersion("2.0.0");
appcRequest.setRpcName("restart");
appcRequest.setCorrelationId(executor.inFields.get("requestID"));
appcRequest.setType("request");

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(executor.inFields.get("vnfID").toString());

appcRequest.getBody().getCommonHeader().setTimeStamp(java.time.Instant.now());
appcRequest.getBody().getCommonHeader().setApiVer("5.00");
appcRequest.getBody().getCommonHeader().setOriginatorId(executor.inFields.get("requestID").toString());
appcRequest.getBody().getCommonHeader().setRequestId(executor.inFields.get("requestID"));
appcRequest.getBody().getCommonHeader().setSubRequestId("1");
appcRequest.getBody().getCommonHeader().getFlags().put("ttl", "10000");
appcRequest.getBody().getCommonHeader().getFlags().put("force", "TRUE");
appcRequest.getBody().getCommonHeader().getFlags().put("mode", "EXCLUSIVE");

appcRequest.getBody().setActionIdentifiers(new java.util.HashMap());
appcRequest.getBody().getActionIdentifiers().put("vnf-id", executor.inFields.get("vnfID").toString());

executor.getContextAlbum("RequestIDVNFIDAlbum").put(executor.inFields.get("requestID").toString(), executor.inFields.get("vnfID"));

vcpeClosedLoopStatus.put("notification",     org.onap.policy.controlloop.ControlLoopNotificationType.OPERATION);
vcpeClosedLoopStatus.put("notificationTime", java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC));

executor.outFields.put("APPCLCMRequestEvent", appcRequest);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
