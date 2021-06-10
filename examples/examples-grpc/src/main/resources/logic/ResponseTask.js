/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix. All rights reserved.
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


var uuidType = java.util.UUID;

var albumID = uuidType.fromString("d0050623-18e5-46c9-9298-9a567990cd7c");

var pmSubscriptionInfo = executor.getContextAlbum("PMSubscriptionAlbum").get(albumID.toString());

var responseStatus = executor.subject.getOutFieldSchemaHelper("CDSResponseStatusEvent", "status").createNewInstance();

responseStatus.put("subscriptionName", pmSubscriptionInfo.get("subscription").get("subscriptionName"))
responseStatus.put("nfName", pmSubscriptionInfo.get("nfName"))
responseStatus.put("changeType", pmSubscriptionInfo.get("changeType"))

var response = executor.inFields.get("payload")

if ("failure".equals(response.get("create_DasH_subscription_DasH_response").get("odl_DasH_response").get("status"))) {
    responseStatus.put("message", "failed")
} else {
    responseStatus.put("message", "success")
}

executor.outFields.put("status", responseStatus)
//var test = executor.subject.getOutFieldSchemaHelper("CDSResponseStatusEvent2", "test").createNewInstance();
var map = java.util.HashMap();
map.put("test", "val1");
executor.logger.info("+++++++++++++++++"+executor.outFieldsList)
// executor.logger.info("+++++++++++++++++"+typeof executor.outFieldsList)
// executor.logger.info("+++++++++++++++++"+map)
executor.addFieldsToOutputList(map);
executor.logger.info("+++++++++++++++++"+executor.outFieldsList)
true;
