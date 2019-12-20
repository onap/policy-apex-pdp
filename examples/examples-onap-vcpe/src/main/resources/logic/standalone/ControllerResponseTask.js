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
 * APPC LCM Response code: 100 ACCEPTED
 *                         200 ERROR UNEXPECTED ERROR means failure
 *                         312 REJECTED DUPLICATE REQUEST
 *                         400 SUCCESS
 *
 * Note: Sometimes the corelationId has a -1 at the tail, need to get rid of it when present.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var controllerResponse = executor.inFields.get("ControllerResponse");

var requestIDString = new java.lang.String(controllerResponse.get("correlation_DasH_id"));
executor.logger.info("requestIDString =\"" + requestIDString + "\"");
var vnfID = executor.getContextAlbum("RequestIDVNFIDAlbum").get(requestIDString);
executor.logger.info("vnfID = " + vnfID);

var returnValue = executor.isTrue;

if (vnfID != null) {
    var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(vnfID.toString());
    var requestId = java.util.UUID.fromString(vcpeClosedLoopStatus.get("requestID"));

    vcpeClosedLoopStatus.put("notificationTime", java.lang.System.currentTimeMillis());

    var returnedCode = controllerResponse.get("body").get("output").get("status").get("code");
    var responseStatus = null;

    if (returnedCode == 100) {
        responseStatus = "ACCEPTED";
    } else if (returnedCode == 200) {
        responseStatus = "ERROR";
    } else if (returnedCode >= 300 && returnedCode <= 316) {
        responseStatus = "REJECT";
    } else if (returnedCode == 400) {
        responseStatus = "SUCCESS";
    } else if (returnedCode == 450 || (returnedCode >= 401 && returnedCode <= 406)) {
        responseStatus = "FAILURE";
    } else if (returnedCode == 500) {
        responseStatus = "PARTIAL_SUCCESS";
    } else if (returnedCode >= 501 && returnedCode <= 599) {
        responseStatus = "PARTIAL_FAILURE";
    } else {
        responseStatus = "SYSTEM_ERROR";
    }

    executor.logger.info("Got from APPC code: " + responseStatus);

    if (responseStatus === "SUCCESS") {
        vcpeClosedLoopStatus.put("notification", "OPERATION_SUCCESS");
        vcpeClosedLoopStatus.put("message", "vCPE restarted");
        executor.getContextAlbum("RequestIDVNFIDAlbum").remove(requestIDString);
    } else if (responseStatus === "ACCEPTED" || responseStatus === "REJECT") {
        executor.logger.info("Got ACCEPTED 100 or REJECT 312, keep the context, wait for next response. Code is: "
                + responseStatus);
    } else {
        executor.getContextAlbum("RequestIDVNFIDAlbum").remove(requestIDString);
        vcpeClosedLoopStatus.put("notification", "OPERATION_FAILURE");
        vcpeClosedLoopStatus.put("message", "vCPE restart failed, code is " + responseStatus + ", message is "
                + controllerResponse.get("body").get("output").get("status").get("message"));
    }

    executor.outFields.put("requestID", requestId);
    executor.outFields.put("vnfID", vnfID);
} else {
    executor.message = "VNF ID not found in context album for request ID " + requestIDString;
    returnValue = executor.isFalse;
}

executor.logger.info(executor.outFields);
