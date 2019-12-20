/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

var controllerRequest = executor.subject.getOutFieldSchemaHelper("ControllerRequest").createNewInstance();

var controllerRequestBody = executor.subject.getOutFieldSchemaHelper("ControllerRequest").createNewSubInstance(
        "Controller_Body_Type");

var controllerRequestBodyInput = executor.subject.getOutFieldSchemaHelper("ControllerRequest").createNewSubInstance(
        "Controller_Body_Type_Input");

var controllerRequestBodyInputCommonHeader = executor.subject.getOutFieldSchemaHelper("ControllerRequest")
        .createNewSubInstance("Controller_Body_Type_Common_Header");

var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());

controllerRequest.put("version", "2.0.0");
controllerRequest.put("rpc_DasH_name", "restart");
controllerRequest.put("correlation_DasH_id", executor.inFields.get("requestID").toString());
controllerRequest.put("type", "request");

controllerRequestBodyInput.put("action", "Restart");
controllerRequestBodyInput.put("action_DasH_identifiers", new java.util.HashMap());
controllerRequestBodyInput.get("action_DasH_identifiers").put("vnf-id", executor.inFields.get("vnfID").toString());

controllerRequestBodyInputCommonHeader.put("timestamp", java.lang.System.currentTimeMillis());
controllerRequestBodyInputCommonHeader.put("api_DasH_ver", "2.00");
controllerRequestBodyInputCommonHeader.put("originator_DasH_id", executor.inFields.get("requestID").toString());
controllerRequestBodyInputCommonHeader.put("request_DasH_id", executor.inFields.get("requestID").toString());
controllerRequestBodyInputCommonHeader.put("sub_DasH_request_DasH_id", "1");
controllerRequestBodyInputCommonHeader.put("flags", new java.util.HashMap());
controllerRequestBodyInputCommonHeader.get("flags").put("ttl", "10000");
controllerRequestBodyInputCommonHeader.get("flags").put("force", "TRUE");
controllerRequestBodyInputCommonHeader.get("flags").put("mode", "EXCLUSIVE");

controllerRequestBodyInput.put("common_DasH_header", controllerRequestBodyInputCommonHeader);
controllerRequestBody.put("input", controllerRequestBodyInput);
controllerRequest.put("body", controllerRequestBody);

executor.getContextAlbum("RequestIDVNFIDAlbum").put(executor.inFields.get("requestID").toString(),
        executor.inFields.get("vnfID"));

vcpeClosedLoopStatus.put("notification", "OPERATION");
vcpeClosedLoopStatus.put("notificationTime", java.lang.System.currentTimeMillis());

executor.outFields.put("ControllerRequest", controllerRequest);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
