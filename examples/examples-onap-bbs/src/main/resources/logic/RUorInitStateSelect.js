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
executor.logger.info("Begin Execution RUorInitStateSelect.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var returnValue = executor.isTrue;
var result = null;

var attachmentPoint = executor.inFields.get("attachmentPoint");
var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);

executor.logger.info("==========>" + executor.outFields);
executor.logger.info("==========>" + executor.inFields);

result = vcpeClosedLoopStatus.get("result");

if (result === "SUCCESS") {
    executor.subject.getTaskKey("SdncResourceUpdateTask").copyTo(executor.selectedTask);
} else {
    executor.subject.getTaskKey("ErrorAAIServiceAssignedLogTask").copyTo(executor.selectedTask);
    onsetFlag = executor.isFalse;
} 

executor.logger.info("State Selected Task:" + executor.selectedTask);
executor.logger.info("End Execution RUorInitStateSelect.js");
