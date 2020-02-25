/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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
executor.logger.info(executor.outFields);
executor.logger.info(executor.parameters);

executor.logger.info("executionProperties in:" + executor.getExecutionProperties());

executor.getExecutionProperties().setProperty("tagId", "doActionForCL");
var closedLoopId = executor.parameters.get("closedLoopId")
if (null == closedLoopId) {
    closedLoopId = "INVALID - closedLoopId not available in TaskParameters"
}
executor.getExecutionProperties().setProperty("value", closedLoopId);

executor.logger.info("executionProperties out:" + executor.getExecutionProperties());

var returnValue = executor.isTrue;