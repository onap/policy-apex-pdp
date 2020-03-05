/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
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

executor.logger.debug(executor.getSubject().getId());
executor.logger.debug(executor.getInFields().toString());
executor.logger.debug("executionProperties:" + executor.getExecutionProperties());

executor.logger.debug("testToRun:" +  executor.getInFields().get("testToRun"));

var returnValue = true;

// Convert Java string to a Javascript variable
var testToRun = String(executor.getInFields().get("testToRun")).valueOf();

switch (testToRun) {
    case "ReadOnly":
        executor.subject.getTaskKey("ReadOnlyTask").copyTo(executor.selectedTask);
        break;

    case "EmptyToEmpty":
        executor.subject.getTaskKey("EmptyToEmptyTask").copyTo(executor.selectedTask);
        break;

    case "EmptyToDefined":
        executor.subject.getTaskKey("EmptyToDefinedTask").copyTo(executor.selectedTask);
        break;

    case "DefinedToEmpty":
        executor.subject.getTaskKey("DefinedToEmptyTask").copyTo(executor.selectedTask);
        break;

    case "RemoveProperty":
        executor.subject.getTaskKey("RemovePropertyTask").copyTo(executor.selectedTask);
        break;

    case "AddProperty":
        executor.subject.getTaskKey("AddPropertyTask").copyTo(executor.selectedTask);
        break;

    default:
        executor.logger.debug("invalid value for testToRun:" +  executor.getInFields().get("testToRun"));
        returnValue = false;
}

executor.logger.debug("Selected Task:" + executor.selectedTask);

returnValue;

