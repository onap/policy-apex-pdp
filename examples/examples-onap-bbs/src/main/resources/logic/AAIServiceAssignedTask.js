/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
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
importClass(org.apache.avro.Schema);
importClass(java.io.BufferedReader);
importClass(java.io.IOException);
importClass(java.nio.file.Files);
importClass(java.nio.file.Paths);

executor.logger.info("Begin Execution AAIServiceAssignedTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var requestID = executor.inFields.get("requestID");
var serviceInstanceId = executor.inFields.get("serviceInstanceId");

var NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(
    attachmentPoint);
executor.logger.info(NomadicONTContext);

var jsonObj;
var aaiUpdateResult = true;

var wbClient = Java.type("org.onap.policy.apex.examples.bbs.WebClient");
var client = new wbClient();


/* Get AAI URL from Configuration file. */
var AAI_URL = "localhost:8080";
var CUSTOMER_ID = requestID;
var SERVICE_INSTANCE_ID = serviceInstanceId;
var resource_version;
var relationship_list;
var HTTP_PROTOCOL = "https://";
var results;
var putUrl;
var service_instance;
try {
    var br = Files.newBufferedReader(Paths.get(
        "/home/apexuser/examples/config/ONAPBBS/config.txt"));
    // read line by line
    var line;
    while ((line = br.readLine()) != null) {
        if (line.startsWith("AAI_URL")) {
            var str = line.split("=");
            AAI_URL = str[str.length - 1];
            break;
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}

executor.logger.info("AAI_URL " + AAI_URL);

/* Get service instance Id from AAI */
try {
    var urlGet = HTTP_PROTOCOL + AAI_URL +
        "/aai/v14/nodes/service-instances/service-instance/" +
        SERVICE_INSTANCE_ID + "?format=resource_and_url";

    executor.logger.info("Query url" + urlGet);

    result = client.httpsRequest(urlGet, "GET", null, "AAI", "AAI",
        "application/json", true, true);
    executor.logger.info("Data received From " + urlGet + " " + result
        .toString());
    jsonObj = JSON.parse(result.toString());

    executor.logger.info(JSON.stringify(jsonObj, null, 4));
    /* Retrieve the service instance id */
    results = jsonObj['results'][0];
    putUrl = results['url'];
    service_instance = results['service-instance'];
    service_instance_id = service_instance['service-instance-id'];
    resource_version = service_instance['resource-version'];
    relationship_list = service_instance['relationship-list'];
    executor.logger.info("After Parse service_instance " + JSON.stringify(
            service_instance, null, 4) + "\n url " + putUrl +
        "\n Service instace Id " + service_instance_id);

    if (result == "") {
        aaiUpdateResult = false;
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

/* BBS Policy updates orchestration status of {{bbs-cfs-service-instance-UUID}} [ active --> assigned ] */
var putUpddateServInstance;
putUpddateServInstance = service_instance;
try {
    if (aaiUpdateResult == true) {
        putUpddateServInstance["orchestration-status"] = "active";
        executor.logger.info("ready to putAfter Parse " + JSON.stringify(
            putUpddateServInstance, null, 4));
        var urlPut = HTTP_PROTOCOL + AAI_URL +
            putUrl + "?resource_version=" + resource_version;
        result = client.httpsRequest(urlPut, "PUT", JSON.stringify(
                putUpddateServInstance), "AAI", "AAI",
            "application/json", true, true);
        executor.logger.info("Data received From " + urlPut + " " + result
            .toString());
        /* If failure to retrieve data proceed to Failure */
        if (result != "") {
            aaiUpdateResult = false;
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

if (!service_instance.hasOwnProperty('input-parameters') || !service_instance
    .hasOwnProperty('metadata')) {
    aaiUpdateResult = false;
    executor.logger.info(
        "Validate data failed. input-parameters or metadata is missing");
}
/* If Success then Fill output schema */
if (aaiUpdateResult === true) {
    executor.outFields.put("result", "SUCCESS");
    NomadicONTContext.put("result", "SUCCESS");
    NomadicONTContext.put("aai_message", JSON.stringify(service_instance));
    NomadicONTContext.put("url", putUrl);
} else {
    executor.outFields.put("result", "FAILURE");
    NomadicONTContext.put("result", "FAILURE");
}

executor.outFields.put("requestID", requestID);
executor.outFields.put("attachmentPoint", attachmentPoint);
executor.outFields.put("serviceInstanceId", executor.inFields.get(
    "serviceInstanceId"));

var returnValue = executor.isTrue;
executor.logger.info(executor.outFields);
executor.logger.info("End Execution AAIServiceAssignedTask.js");

/* Utility functions Begin */
function IsValidJSONString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}
/* Utility functions End */