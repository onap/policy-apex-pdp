/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

var returnValue = executor.isTrue;
var status = null;

if( executor.inFields.get("vnfID") == null) {
   executor.logger.info("OnsetOrAbatedStateTSL: vnfID is null");
   var vnfName = executor.inFields.get("vnfName");
   var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfName"));
   status = vcpeClosedLoopStatus.get("closedLoopEventStatus").toString();
} else {
   var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());
   status = vcpeClosedLoopStatus.get("closedLoopEventStatus").toString();
}

var returnValue = executor.isTrue;

if (status == "ONSET") {
    executor.subject.getTaskKey("GuardRequestTask").copyTo(executor.selectedTask);
} else if (status == "ABATED") {
    executor.subject.getTaskKey("AbatedTask").copyTo(executor.selectedTask);
    onsetFlag = executor.isFalse;
} else {
    executor.message = "closedLoopEventStatus is \"" + status + "\", it must be either \"ONSET\" or \"ABATED\"";
    returnValue = executor.isFalse;
}

executor.logger.info("ReceiveEventPolicyOnsetOrAbatedStateTSL State Selected Task:" + executor.selectedTask);

returnValue == true;
