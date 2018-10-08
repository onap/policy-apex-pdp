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

var appcResponse = executor.inFields.get("APPCLCMResponseEvent");

var requestIDString = appcResponse.getCorrelationId();
var vnfID = executor.getContextAlbum("RequestIDVNFIDAlbum").remove(requestIDString);

var returnValue = executor.isTrue;

if (vnfID != null) {
    var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(vnfID.toString());
    var requestId = java.util.UUID.fromString(vcpeClosedLoopStatus.get("requestID"));

    vcpeClosedLoopStatus.put("notificationTime", java.lang.System.currentTimeMillis());

    if (org.onap.policy.appclcm.LcmResponseCode.toResponseValue(appcResponse.getBody().getStatus().getCode()) == org.onap.policy.appclcm.LcmResponseCode.SUCCESS) {
        vcpeClosedLoopStatus.put("notification", "OPERATION_SUCCESS");
        vcpeClosedLoopStatus.put("message", "vCPE restarted");
    }
    else {
        vcpeClosedLoopStatus.put("notification", "OPERATION_FAILURE");
        vcpeClosedLoopStatus.put("message", "vCPE restart failed");
    }

    executor.outFields.put("requestID", requestId);
    executor.outFields.put("vnfID", vnfID);
} else {
    executor.message = "VNF ID not found in context album for request ID " + requestID;
    returnValue = executor.isFalse
}

executor.logger.info(executor.outFields);
