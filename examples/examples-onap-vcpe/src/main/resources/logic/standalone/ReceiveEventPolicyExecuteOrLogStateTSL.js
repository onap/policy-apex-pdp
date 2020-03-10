/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

if( executor.inFields.get("vnfID") == null) {
   executor.logger.info("ControlLoopStatusAlbum: vnfID is null");
   var vnfName = executor.inFields.get("vnfName");
   var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(
        executor.inFields.get("vnfName"));
} else {
   var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());
}

var status = vcpeClosedLoopStatus.get("closedLoopEventStatus").toString();
var notification = vcpeClosedLoopStatus.get("notification");

var returnValue = executor.isTrue;

if (notification != null && notification == "BLACKLIST") {
    executor.subject.getTaskKey("StopAndLogTask").copyTo(executor.selectedTask);
}
else {
    if (status == "ONSET") {
        executor.subject.getTaskKey("InitiateActionsTask").copyTo(executor.selectedTask);
    } else if (status == "ABATED") {
        executor.subject.getTaskKey("StopAndLogTask").copyTo(executor.selectedTask);
    } else {
        executor.message = "closedLoopEventStatus is \"" + status + "\", it must be either \"ONSET\" or \"ABATED\"";
        returnValue = executor.isFalse;
    }
}

executor.logger.info("ReceiveEventPolicyOnsetOrAbatedStateTSL State Selected Task:" + executor.selectedTask);

returnValue == true;

