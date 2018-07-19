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

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);

executor.logger.info("Task Selection Execution: '" + executor.subject.id + "'. Input Event: '" + executor.inFields
        + "'");

branchid = executor.inFields.get("branch_ID");
taskorig = executor.subject.getTaskKey("MorningBoozeCheck");
taskalt = executor.subject.getTaskKey("MorningBoozeCheckAlt1");
taskdef = executor.subject.getDefaultTaskKey();

if (branchid >= 0 && branchid < 1000) {
    taskorig.copyTo(executor.selectedTask);
} else if (branchid >= 1000 && branchid < 2000) {
    taskalt.copyTo(executor.selectedTask);
} else {
    taskdef.copyTo(executor.selectedTask);
}

/*
 * This task selection logic selects task "MorningBoozeCheck" for branches with
 * 0<=branch_ID<1000 and selects task "MorningBoozeCheckAlt1" for branches
 * with 1000<=branch_ID<2000. Otherwise the default task is selected. In this
 * case the default task is also "MorningBoozeCheck"
 */
