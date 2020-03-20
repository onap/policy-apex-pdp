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


//albumID will be used to fetch info from our album later
var albumID = uuidType.fromString("d0050623-18e5-46c9-9298-9a567990cd7c");
var pmSubscriptionInfo = executor.getContextAlbum("PMSubscriptionAlbum").getSchemaHelper().createNewInstance();
var returnValue = true;;

if(executor.inFields.get("policyName") != null) {
    var changeType = executor.inFields.get("changeType")
    var nfName = executor.inFields.get("nfName")
    var policyName = executor.inFields.get("policyName")
    var closedLoopControlName = executor.inFields.get("closedLoopControlName")
    var subscription = executor.inFields.get("subscription")

    pmSubscriptionInfo.put("nfName", executor.inFields.get("nfName"));
    pmSubscriptionInfo.put("changeType", executor.inFields.get("changeType"))
    pmSubscriptionInfo.put("policyName", executor.inFields.get("policyName"))
    pmSubscriptionInfo.put("closedLoopControlName", executor.inFields.get("closedLoopControlName"))
    pmSubscriptionInfo.put("subscription", subscription)

    executor.getContextAlbum("PMSubscriptionAlbum").put(albumID.toString(), pmSubscriptionInfo);

    executor.outFields.put("albumID", albumID)
} else {
    executor.message = "Received invalid event"
    returnValue = false;
}
returnValue;