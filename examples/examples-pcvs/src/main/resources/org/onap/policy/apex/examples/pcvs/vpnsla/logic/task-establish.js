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
importClass(org.slf4j.LoggerFactory);

importClass(java.util.ArrayList);

importClass(org.apache.avro.generic.GenericData.Array);
importClass(org.apache.avro.generic.GenericRecord);
importClass(org.apache.avro.Schema);

var logger = executor.logger;
logger.trace("start: " + executor.subject.id);
logger.trace("-- infields: " + executor.inFields);

var rootLogger = LoggerFactory.getLogger(logger.ROOT_LOGGER_NAME);

var ifEdgeName = executor.inFields["edgeName"];
var ifEdgeStatus = executor.inFields["status"].toString();
var ifhasChanged = executor.inFields["hasChanged"];
var ifMatchStart = executor.inFields["matchStart"];

var albumCustomerMap = executor.getContextAlbum("albumCustomerMap");
var albumProblemMap = executor.getContextAlbum("albumProblemMap");

var linkProblem = albumProblemMap.get(ifEdgeName);

// create outfiled for situation
var situation = executor.subject.getOutFieldSchemaHelper("situation").createNewInstance();
situation.put("violatedSLAs", new ArrayList());

// create a string as states+hasChanged+linkProblem and switch over it
var switchTest = ifEdgeStatus + ":" + ifhasChanged + ":" + (linkProblem == null ? "no" : "yes");
switch (switchTest) {
case "UP:false:no":
    logger.trace("-- edge <" + ifEdgeName + "> UP:false:no => everything ok");
    logger.info("vpnsla: everything ok");
    situation.put("problemID", "NONE");
    break;
case "UP:false:yes":
    logger.trace("-- edge <" + ifEdgeName + "> UP:false:yes ==> did we miss earlier up?, removing problem");
    albumProblemMap.remove(ifEdgeName);
    linkProblem = null;
    situation.put("problemID", "NONE");
    break;
case "UP:true:no":
    logger.trace("-- edge <" + ifEdgeName + "> UP:true:no ==> did we miss the earlier down?, creating new problem");
    situation.put("problemID", ifEdgeName);
    break;
case "UP:true:yes":
    logger.trace("-- edge <" + ifEdgeName + "> UP:true:yes ==> detected solution, link up again");
    logger.info("vpnsla: problem solved");
    linkProblem.put("endTime", ifMatchStart);
    linkProblem.put("status", "SOLVED");
    situation.put("problemID", "NONE");
    break;
case "DOWN:false:no":
    logger.trace("-- edge <" + ifEdgeName + "> DOWN:false:no ==> did we miss an earlier down?, creating new problem");
    situation.put("problemID", ifEdgeName);
    break;
case "DOWN:false:yes":
    logger.trace("-- edge <" + ifEdgeName + "> DOWN:false:yes ==> problem STILL exists");
    logger.info("vpnsla: problem still exists");
    linkProblem.put("status", "STILL");
    situation.put("problemID", ifEdgeName);
    break;
case "DOWN:true:no":
    logger.trace("-- edge <" + ifEdgeName + "> DOWN:true:no ==> found NEW problem");
    logger.info("vpnsla: this is a new problem");
    situation.put("problemID", ifEdgeName);
    break;
case "DOWN:true:yes":
    logger.trace("-- edge <" + ifEdgeName
            + "> DOWN:true:yes ==> did we miss to remove an earlier problem?, remove and create new problem");
    linkProblem = null;
    situation.put("problemID", ifEdgeName);
    break;

default:
    logger.error("-- input wrong for edge" + ifEdgeName + ": edge status <" + ifEdgeStatus
            + "> unknown or null on hasChanged <" + ifhasChanged + ">");
    rootLogger.error("-- input wrong for edge" + ifEdgeName + ": edge status <" + ifEdgeStatus
            + "> unknown or null on hasChanged <" + ifhasChanged + ">");
}

// create new problem if situation requires it
if (situation.get("problemID").equals(ifEdgeName) && linkProblem == null) {
    logger.trace("-- edge <" + ifEdgeName + "> creating new problem");
    linkProblem = albumProblemMap.getSchemaHelper().createNewInstance();
    linkProblem.put("edge", ifEdgeName);
    linkProblem.put("startTime", ifMatchStart);
    linkProblem.put("lastUpdate", ifMatchStart);
    linkProblem.put("endTime", 0);
    linkProblem.put("status", "NEW");
    linkProblem.put("edgeUsedBy", new ArrayList());
    linkProblem.put("impededLast", new ArrayList());

    for (var i = 0; i < albumCustomerMap.values().size(); i++) {
        var customer = albumCustomerMap.values().get(i);
        var customerLinks = albumCustomerMap.values().get(i).get("links");
        for (var k = 0; k < customerLinks.size(); k++) {
            if (customerLinks.get(k) == ifEdgeName) {
                linkProblem.get("edgeUsedBy").add(customer.get("customerName"));
            }
        }
    }
    albumProblemMap.put(ifEdgeName, linkProblem);
    logger.trace("-- edge <" + ifEdgeName + "> problem created as <" + linkProblem + ">");
}

// set dtYTD if situation requires it
if (linkProblem != null && (linkProblem.get("status") == "STILL" || linkProblem.get("status") == "SOLVED")) {
    var linkDownTimeinSecs = (ifMatchStart - linkProblem.get("lastUpdate")) / 1000;
    logger.trace("-- edge <" + ifEdgeName + "> down time: " + linkDownTimeinSecs + " s");
    for (var k = 0; k < linkProblem.get("impededLast").size(); k++) {
        for (var i = 0; i < albumCustomerMap.values().size(); i++) {
            var customer = albumCustomerMap.values().get(i);
            if (customer.get("customerName").equals(linkProblem.get("impededLast").get(k))) {
                logger.info("-- vpnsla: customer " + customer.get("customerName") + " YDT downtime increased from "
                        + customer.get("dtYTD") + " to " + (customer.get("dtYTD") + linkDownTimeinSecs));
                customer.put("dtYTD", (customer.get("dtYTD") + linkDownTimeinSecs))
            }
        }
    }
    // set lastUpdate to this policy execution for next execution calculation
    linkProblem.put("lastUpdate", ifMatchStart);
}

// check SLA violations if situation requires it
if (linkProblem != null && linkProblem.get("status") != "SOLVED") {
    logger.info(">e> customer\tDT SLA\tDT YTD\tviolation");
    for (var i = 0; i < albumCustomerMap.values().size(); i++) {
        var customer = albumCustomerMap.values().get(i);
        if (customer.get("dtYTD") > customer.get("dtSLA")) {
            situation.get("violatedSLAs").add(customer.get("customerName"));
            logger.info(">e> " + customer.get("customerName") + "\t\t" + customer.get("dtSLA") + "s\t"
                    + customer.get("dtYTD") + "s\t" + "!!");
        } else {
            logger.info(">e> " + customer.get("customerName") + "\t\t" + customer.get("dtSLA") + "s\t"
                    + customer.get("dtYTD") + "s");
        }
    }
}

executor.outFields["situation"] = situation;

logger.trace("-- out fields <" + executor.outFields + ">");

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);
logger.trace("finished: " + executor.subject.id);
logger.debug(".e");
