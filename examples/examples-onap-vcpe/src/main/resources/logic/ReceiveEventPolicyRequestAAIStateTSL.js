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

var returnValue = executor.isTrue;

if( executor.inFields.get("vnfID") == null) {
    executor.logger.info("ReceiveEventPolicyRequestAAIStateTSL: vnfID is null");
    var vnfName = executor.inFields.get("vnfName");
    var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfName"));
    executor.logger.info("CL event status: " + vcpeClosedLoopStatus.get("closedLoopEventStatus"));
    executor.subject.getTaskKey("NoAAILookupTask").copyTo(executor.selectedTask);
 } else {
    var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());
    var aaiInfo = vcpeClosedLoopStatus.get("AAI");

    executor.logger.info(aaiInfo);

    if (aaiInfo.get("genericVnfResourceVersion") != null
        && aaiInfo.get("genericVnfVnfName") != null
        && aaiInfo.get("genericVnfProvStatus") != null
        && aaiInfo.get("genericVnfIsClosedLoopDisabled") != null
        && aaiInfo.get("genericVnfOrchestrationStatus") != null
        && aaiInfo.get("genericVnfVnfType") != null
        && aaiInfo.get("genericVnfInMaint") != null
        && aaiInfo.get("genericVnfServiceId") != null
        && aaiInfo.get("genericVnfVnfId") != null) {
        executor.subject.getTaskKey("NoAAILookupTask").copyTo(executor.selectedTask);
    } else {
        executor.subject.getTaskKey("AAILookupRequestTask").copyTo(executor.selectedTask);
    }
}
executor.logger.info("ReceiveEventPolicyOnsetOrAbatedStateTSL State Selected Task:" + executor.selectedTask);
