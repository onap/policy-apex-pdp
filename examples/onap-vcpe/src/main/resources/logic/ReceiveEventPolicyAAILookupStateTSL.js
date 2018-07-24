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

var returnValue = executor.TRUE;

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());
var aaiInfo = vcpeClosedLoopStatus.get("AAI");

executor.logger.info(aaiInfo);

if (aaiInfo.get("generic_DasH_vnf.resource_DasH_version") != null
        && aaiInfo.get("generic_DasH_vnf.vnf_DasH_name") != null
        && aaiInfo.get("generic_DasH_vnf.prov_DasH_status") != null
        && aaiInfo.get("generic_DasH_vnf.is_DasH_closed_DasH_loop_DasH_disabled") != null
        && aaiInfo.get("generic_DasH_vnf.orchestration_DasH_status") != null
        && aaiInfo.get("generic_DasH_vnf.vnf_DasH_type") != null
        && aaiInfo.get("generic_DasH_vnf.in_DasH_maint") != null
        && aaiInfo.get("generic_DasH_vnf.service_DasH_id") != null
        && aaiInfo.get("generic_DasH_vnf.vnf_DasH_id") != null) {
    executor.subject.getTaskKey("AAILookupRequestTask").copyTo(executor.selectedTask);
} else {
    executor.subject.getTaskKey("NoAAILookupTask").copyTo(executor.selectedTask);
}

executor.logger.info("ReceiveEventPolicyOnsetOrAbatedStateTSL State Selected Task:" + executor.selectedTask);
