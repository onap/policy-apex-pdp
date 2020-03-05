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

executor.logger.info(executor.getSubject().getId());
executor.logger.info(executor.getInFields().toString());
executor.logger.info(executor.getOutFields().toString());
executor.logger.info(executor.getParameters().toString());

executor.logger.info("executionProperties in: {}", executor.getExecutionProperties().toString());

executor.getExecutionProperties().setProperty("tagId", "doActionForCL");
var closedLoopId = executor.getParameters().get("closedLoopId")
if (null == closedLoopId) {
    closedLoopId = "INVALID - closedLoopId not available in TaskParameters"
}
executor.getExecutionProperties().setProperty("value", closedLoopId);

executor.logger.info("executionProperties out: {}", executor.getExecutionProperties().toString());

true;