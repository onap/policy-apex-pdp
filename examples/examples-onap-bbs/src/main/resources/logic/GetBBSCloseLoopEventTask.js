/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
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
load("nashorn:mozilla_compat.js");

executor.logger.info("Begin Execution GetBBSCloseLoopEventTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);
var returnValue = executor.isTrue;

var clEventType = Java.type(
    "org.onap.policy.controlloop.VirtualControlLoopEvent");
var clEvent = executor.inFields.get("VirtualControlLoopEvent");
executor.logger.info(clEvent.toString());
executor.logger.info(clEvent.getClosedLoopControlName());

var requestID = clEvent.getRequestId();
executor.logger.info("requestID = " + requestID);
var attachmentPoint = null;
var NomadicONTContext = null;
var serviceInstanceId = null;

if (clEvent.getAai().get("attachmentPoint") != null) {
    attachmentPoint = clEvent.getAai().get("attachmentPoint");
    executor.logger.info("attachmentPoint = " + attachmentPoint);
    NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(
        attachmentPoint);
    serviceInstanceId = clEvent.getAai().get(
        "service-information.hsia-cfs-service-instance-id");
    executor.logger.info("serviceInstanceId = " + serviceInstanceId);

    if (NomadicONTContext == null) {
        executor.logger.info(
            "Creating context information for new ONT Device \"" +
            attachmentPoint.toString() + "\"");

        NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").getSchemaHelper()
            .createNewInstance();

        NomadicONTContext.put("closedLoopControlName", clEvent.getClosedLoopControlName());
        NomadicONTContext.put("closedLoopAlarmStart", clEvent.getClosedLoopAlarmStart()
            .toEpochMilli());
        NomadicONTContext.put("closedLoopEventClient", clEvent.getClosedLoopEventClient());
        NomadicONTContext.put("closedLoopEventStatus", clEvent.getClosedLoopEventStatus()
            .toString());
        NomadicONTContext.put("version", clEvent.getVersion());
        NomadicONTContext.put("requestID", clEvent.getRequestId().toString());
        NomadicONTContext.put("target_type", clEvent.getTargetType().toString());
        NomadicONTContext.put("target", clEvent.getTarget());
        NomadicONTContext.put("from", clEvent.getFrom());
        NomadicONTContext.put("policyScope", "Nomadic ONT");
        NomadicONTContext.put("policyName", clEvent.getPolicyName());
        NomadicONTContext.put("policyVersion", "1.0.0");
        NomadicONTContext.put("notificationTime", java.lang.System.currentTimeMillis());
        NomadicONTContext.put("message", "");
        NomadicONTContext.put("result", "SUCCESS");
        var aaiInfo = executor.getContextAlbum("NomadicONTContextAlbum").getSchemaHelper()
            .createNewSubInstance("VCPE_AAI_Type");

        aaiInfo.put("attachmentPoint", clEvent.getAai().get("attachmentPoint"));
        aaiInfo.put("cvlan", clEvent.getAai().get("cvlan"));
        aaiInfo.put("service_information_hsia_cfs_service_instance_id", clEvent
            .getAai().get(
                "service-information.hsia-cfs-service-instance-id"));
        aaiInfo.put("svlan", clEvent.getAai().get("svlan"));
        aaiInfo.put("remoteId", clEvent.getAai().get("remoteId"));


        NomadicONTContext.put("AAI", aaiInfo);

        if (clEvent.getClosedLoopAlarmEnd() != null) {
            NomadicONTContext.put("closedLoopAlarmEnd", clEvent.getClosedLoopAlarmEnd()
                .toEpochMilli());
        } else {
            NomadicONTContext.put("closedLoopAlarmEnd", java.lang.Long.valueOf(
                0));
        }

        executor.getContextAlbum("NomadicONTContextAlbum").put(attachmentPoint.toString(),
            NomadicONTContext);
        executor.logger.info("Created context information for new vCPE VNF \"" +
            attachmentPoint.toString() + "\"");
    }

    executor.outFields.put("requestID", requestID);
    executor.outFields.put("attachmentPoint", attachmentPoint);
    executor.outFields.put("serviceInstanceId", serviceInstanceId);
    executor.logger.info(executor.outFields);
    executor.logger.info("Event Successfully Received and stored in album");
}
else
{
    executor.message = "Received NULL attachment-point";
    returnValue = executor.isFalse;
}

