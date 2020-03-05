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

executor.logger.debug("executionProperties: " + executor.getExecutionProperties());

if (executor.getExecutionProperties().get("HTTP_CODE_STATUS") == "500")
    executor.outFields.put("testToRun", "CodeFilterSet");
else
    executor.outFields.put("testToRun", "CodeFilterDefault");

executor.logger.debug("testToRun: " + executor.outFields.get("testToRun"));

true;
