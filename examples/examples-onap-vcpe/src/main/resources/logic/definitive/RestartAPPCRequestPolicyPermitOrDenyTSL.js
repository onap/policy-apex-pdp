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

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());

var guardResult = vcpeClosedLoopStatus.get("notification");

if (guardResult == "OPERATION: GUARD_PERMIT") {
    executor.subject.getTaskKey("APPCRestartVNFRequestTask").copyTo(executor.selectedTask);
} else {
    executor.subject.getTaskKey("DeniedTask").copyTo(executor.selectedTask);
}

executor.logger.info("RestartAPPCRequestPolicyPermitOrDenyTSL State Selected Task:" + executor.selectedTask);

true;
