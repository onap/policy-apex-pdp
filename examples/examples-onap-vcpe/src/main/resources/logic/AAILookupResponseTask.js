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

var vnfID = executor.getContextAlbum("ControlLoopExecutionIDAlbum").remove(executor.executionID.toString());

executor.logger.info("Continuing execution with VNF ID: " + vnfID);

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(vnfID.toString());
executor.logger.info(vcpeClosedLoopStatus);

var guardResult = executor.inFields.get("decision");

if (guardResult === "PERMIT") {
    vcpeClosedLoopStatus.put("notification", "OPERATION: GUARD_PERMIT");
} else if (guardResult === "DENY") {
    vcpeClosedLoopStatus.put("notification", "OPERATION: GUARD_DENY");
} else {
    executor.message = "guard result must be either \"PERMIT\" or \"DENY\"";
    returnValue = executor.FALSE;
}

var uuidType = Java.type("java.util.UUID");
var requestID = uuidType.fromString(vcpeClosedLoopStatus.get("requestID"));

executor.outFields.put("requestID", requestID);
executor.outFields.put("vnfID", vnfID);

executor.logger.info(executor.outFields);

var returnValue = executor.TRUE;
