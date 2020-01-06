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

var appcRequest = new org.onap.policy.appclcm.AppcLcmDmaapWrapper;
appcRequest.setBody(new org.onap.policy.appclcm.AppcLcmBody);
appcRequest.getBody().setInput(new org.onap.policy.appclcm.AppcLcmInput);
appcRequest.getBody().getInput().setCommonHeader(
        new org.onap.policy.appclcm.AppcLcmCommonHeader);

appcRequest.setVersion("2.0.0");
appcRequest.setRpcName("restart");
appcRequest.setCorrelationId(executor.inFields.get("requestID"));
appcRequest.setType("request");

var vcpeClosedLoopStatus = executor
        .getContextAlbum("VCPEClosedLoopStatusAlbum").get(
                executor.inFields.get("vnfID").toString());

appcRequest.getBody().getInput().getCommonHeader().setTimeStamp(java.time.Instant.now());
appcRequest.getBody().getInput().getCommonHeader().setApiVer("2.00");
appcRequest.getBody().getInput().getCommonHeader().setOriginatorId(
        executor.inFields.get("requestID").toString());
appcRequest.getBody().getInput().getCommonHeader().setRequestId(
        executor.inFields.get("requestID"));
appcRequest.getBody().getInput().getCommonHeader().setSubRequestId("1");
appcRequest.getBody().getInput().getCommonHeader().getFlags().put("ttl", "10000");
appcRequest.getBody().getInput().getCommonHeader().getFlags().put("force", "TRUE");
appcRequest.getBody().getInput().getCommonHeader().getFlags().put("mode", "EXCLUSIVE");

appcRequest.getBody().getInput().setAction("Restart");
appcRequest.getBody().getInput().setActionIdentifiers(new java.util.HashMap());
appcRequest.getBody().getInput().getActionIdentifiers().put("vnf-id",
        executor.inFields.get("vnfID").toString());

executor.getContextAlbum("RequestIDVNFIDAlbum").put(
        executor.inFields.get("requestID").toString(),
        executor.inFields.get("vnfID"));

vcpeClosedLoopStatus.put("notification", "OPERATION");
vcpeClosedLoopStatus.put("notificationTime", java.lang.System
        .currentTimeMillis());

executor.outFields.put("APPCLCMRequestEvent", appcRequest);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
