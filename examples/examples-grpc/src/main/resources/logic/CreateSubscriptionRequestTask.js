/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix. All rights reserved.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

var pmSubscriptionInfo = executor.getContextAlbum("PMSubscriptionAlbum").get(executor.inFields.get("albumID").toString())
var payload = executor.inFields.get("payload")
var actionName = "create-subscription"

var commonHeader = new java.util.HashMap();
commonHeader.put("originatorId", "sdnc");
commonHeader.put("requestId", "123456-1000");
commonHeader.put("subRequestId", "sub-123456-1000");

var actionIdentifiers = new java.util.HashMap();
actionIdentifiers.put("actionName", actionName);
actionIdentifiers.put("blueprintName", "pm_control");
actionIdentifiers.put("blueprintVersion", "1.0.0");
actionIdentifiers.put("mode", "sync");

executor.outFields.put("commonHeader", commonHeader);
executor.outFields.put("actionIdentifiers", actionIdentifiers);
executor.outFields.put("payload", payload);

executor.logger.info("Sending Activate Subscription Event to CDS")

true;