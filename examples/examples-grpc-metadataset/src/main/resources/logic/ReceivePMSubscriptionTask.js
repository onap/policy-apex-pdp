/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix. All rights reserved.
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
var HashMapType = java.util.HashMap;


//albumID will be used to fetch info from our album later
var albumID = uuidType.fromString("d0050623-18e5-46c9-9298-9a567990cd7c");
var pmSubscriptionInfo = executor.getContextAlbum("PMSubscriptionAlbum").getSchemaHelper().createNewInstance();
var returnValue = true;;

if (executor.inFields.get("policyName") != null) {
    var changeType = executor.inFields.get("changeType")
    var nfName = executor.inFields.get("nfName")
    var policyName = executor.inFields.get("policyName")
    var closedLoopControlName = executor.inFields.get("closedLoopControlName")
    var subscription = executor.inFields.get("subscription")

    var obj = {};
    obj["nfName"] = executor.inFields.get("nfName")
    executor.logger.info("nfName" + executor.stringify2Json(obj))

    var ticketInfo = new HashMapType();
    populate_creator_info(ticketInfo);
    executor.logger.info("ticketInfo" + executor.stringify2Json(ticketInfo))

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

function populate_creator_info(ticketInfo){
    populate_field(ticketInfo, "appId", "NSO");
    populate_field(ticketInfo, "creatorId", "fidLab");
    populate_field(ticketInfo, "creatorFirstName", "PSO");
    populate_field(ticketInfo, "creatorLastName", "team7");
    populate_field(ticketInfo, "creatorGroup", "PSO-team7");
    populate_field(ticketInfo, "creatorPEIN", "0000000");
    populate_field(ticketInfo, "creatorPhoneNumber", "800-450-7771");
    populate_field(ticketInfo, "fid", "fidLab");
    populate_field(ticketInfo, "organizationCode", "PSO");
    populate_field(ticketInfo, "source", create_caEn_value("SURV/ALARM FROM/PSO"));
    populate_field(ticketInfo, "customerName", "XYZ");
    populate_field(ticketInfo, "authorization", "Basic dGVzdHVzZXI=");
}

function populate_field(mapname, name, value){
    if (value == null){
        mapname.put(name, "none");
    } else{
        mapname.put(name, value);
    }

}

function create_caEn_value(value){
    var attr = {};
    attr["caEn"] = String(value);
    return attr;
}

returnValue;
