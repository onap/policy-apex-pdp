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

load("nashorn:mozilla_compat.js");

var logger = executor.logger;
logger.trace("start: " + executor.subject.id);
logger.trace("-- infields: " + executor.inFields);

var ifDecision = executor.inFields["decision"];
var ifMatchStart = executor.inFields["matchStart"];

var albumCustomerMap = executor.getContextAlbum("albumCustomerMap");
var albumProblemMap = executor.getContextAlbum("albumProblemMap");

switch (ifDecision.get("decision").toString()) {
case "NONE":
    executor.outFields["edgeName"] = "";
    executor.outFields["action"] = "";
    break;
case "IMPEDE":
    for (var i = 0; i < ifDecision.get("customers").size(); i++) {
        customer = albumCustomerMap.get(ifDecision.get("customers").get(i).toString());
        executor.outFields["edgeName"] = customer.get("links").get(0);
        executor.outFields["action"] = "firewall";
    }
    break;
case "REBUILD":
    // finally solved, remove problem
    albumProblemMap.remove(ifDecision.get("problemID"));
    executor.outFields["edgeName"] = "L10"; // this is ###static###
    executor.outFields["action"] = "rebuild"; // this is ###static###
    break;
default:

}

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);

if (executor.outFields["action"] != "") {
    logger.info("vpnsla: action is to " + executor.outFields["action"] + " " + executor.outFields["edgeName"]);
} else {
    logger.info("vpnsla: no action required");
}

logger.trace("-- outfields: " + executor.outFields);
logger.trace("finished: " + executor.subject.id);
logger.debug(".a");

var now = new Date().getTime();
logger.info("VPN SLA finished in " + (now - ifMatchStart) + " ms");
