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

var uuidType = Java.type("java.util.UUID");
var integerType = Java.type("java.lang.Integer");

var requestID = uuidType.fromString(executor.inFields.get("correlation-id"));
var vnfID = executor.getContextAlbum("RequestIDVNFIDAlbum").remove(requestID.toString());

var returnValue = executor.TRUE;

if (vnfID != null) {
    var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(vnfID.toString());

    var notification = "OPERATION: VNF RESTART WITH RETURN CODE "
            + executor.inFields.get("body").get("output").get("status").get("code") + ", "
            + executor.inFields.get("body").get("output").get("status").get("message");

    vcpeClosedLoopStatus.put("notification", notification);
    vcpeClosedLoopStatus.put("notificationTime", executor.inFields.get("body").get("output").get("common_DasH_header")
            .get("timestamp"));

    executor.outFields.put("requestID", requestID);
    executor.outFields.put("vnfID", vnfID);
} else {
    executor.message = "VNF ID not found in context album for request ID " + requestID;
    returnValue = executor.FALSE
}

executor.logger.info(executor.outFields);
