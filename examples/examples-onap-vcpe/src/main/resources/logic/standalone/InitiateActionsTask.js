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

var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());

var eventList = executor.subject.getOutFieldSchemaHelper("ActionEventList").createNewInstance();

var eventType = Java.type("org.onap.policy.apex.service.engine.event.ApexEvent");

var controllerRequestActionEvent = new eventType("ActionEvent", "0.0.1", "org.onap.policy.apex.onap.vcpe", "APEX",
        "APEX");

controllerRequestActionEvent.put("action", "ControllerRequestAction");
controllerRequestActionEvent.put("requestID", executor.inFields.get("requestID"));
controllerRequestActionEvent.put("vnfID", executor.inFields.get("vnfID"));
controllerRequestActionEvent.put("vnfName", executor.inFields.get("vnfName"));

eventList.add(controllerRequestActionEvent);

var logActionEvent = new eventType("ActionEvent", "0.0.1", "org.onap.policy.apex.onap.vcpe", "APEX", "APEX");

logActionEvent.put("action", "LogAction");
logActionEvent.put("requestID", executor.inFields.get("requestID"));
logActionEvent.put("vnfID", executor.inFields.get("vnfID"));
logActionEvent.put("vnfName", executor.inFields.get("vnfName"));

eventList.add(logActionEvent);

executor.outFields.put("ActionEventList", eventList);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
