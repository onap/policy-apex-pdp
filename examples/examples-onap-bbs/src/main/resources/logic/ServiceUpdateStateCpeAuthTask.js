/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
importClass(java.io.BufferedReader);
importClass(java.io.IOException);
importClass(java.nio.file.Files);
importClass(java.nio.file.Paths);

executor.logger.info("Begin Execution ServiceUpdateStateCpeAuthTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var clEventType = Java.type(
    "org.onap.policy.controlloop.VirtualControlLoopEvent");
var clEvent = executor.inFields.get("VirtualControlLoopEvent");

var serviceInstanceId = clEvent.getAai().get(
    "service-information.hsia-cfs-service-instance-id");
var requestID = clEvent.getRequestId();

var jsonObj;
var aaiUpdateResult = true;
var wbClient = Java.type("org.onap.policy.apex.examples.bbs.WebClient");
var client = new wbClient();
var oldState = clEvent.getAai().get("cpe.old-authentication-state");
var newState = clEvent.getAai().get("cpe.new-authentication-state");
/* Get AAI URL from Configuration file. */
var AAI_URL = "localhost:8080";
var CUSTOMER_ID = requestID;
var SERVICE_INSTANCE_ID = serviceInstanceId;
var resource_version;
var HTTP_PROTOCOL = "https://";
var results;
var putUrl;
var service_instance;
var AAI_VERSION = "v14";
try {
    var br = Files.newBufferedReader(Paths.get(
        "/home/apexuser/examples/config/ONAPBBS/config.txt"));
    var line;
    while ((line = br.readLine()) != null) {
        if (line.startsWith("AAI_URL")) {
            var str = line.split("=");
            AAI_URL = str[str.length - 1];
        } else if (line.startsWith("AAI_USERNAME")) {
            var str = line.split("=");
            AAI_USERNAME = str[str.length - 1];
        } else if (line.startsWith("AAI_PASSWORD")) {
            var str = line.split("=");
            AAI_PASSWORD = str[str.length - 1];
        }else if (line.startsWith("AAI_VERSION")) {
            var str = line.split("=");
            AAI_VERSION = str[str.length - 1];
         }
     }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}

executor.logger.info("AAI_URL=>" + AAI_URL);

/* Get service instance Id from AAI */
try {
    var urlGet = HTTP_PROTOCOL + AAI_URL +
        "/aai/" + AAI_VERSION + "/nodes/service-instances/service-instance/" +
        SERVICE_INSTANCE_ID + "?format=resource_and_url"
    executor.logger.info("Query url" + urlGet);

    result = client.httpRequest(urlGet, "GET", null, AAI_USERNAME, AAI_PASSWORD,
            "application/json", true);
    executor.logger.info("Data received From " + urlGet + " " + result);
    jsonObj = JSON.parse(result);


    /* Retrieve the service instance id */
    results = jsonObj['results'][0];
    putUrl = results["url"];
    service_instance = results['service-instance'];
    resource_version = service_instance['resource-version'];
    executor.logger.info("After Parse service_instance " + JSON.stringify(
            service_instance, null, 4) + "\n url " + putUrl +
        "\n Service instace Id " + SERVICE_INSTANCE_ID);

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
if (newState == 'inService') {
    putUpddateServInstance['orchestration-status'] = "active";
}
else
{
    putUpddateServInstance['orchestration-status'] = "inActive";
}
try {
    if (aaiUpdateResult == true) {
        executor.logger.info("ready to put After Parse " + JSON.stringify(
            putUpddateServInstance, null, 4));
        var urlPut = HTTP_PROTOCOL + AAI_URL +
            putUrl + "?resource_version=" + resource_version;
        result = client.httpRequest(urlPut, "PUT", JSON.stringify(putUpddateServInstance), AAI_USERNAME, AAI_PASSWORD,
                        "application/json", true);
        executor.logger.info("Data received From " + urlPut + " " + result);
        /* If failure to retrieve data proceed to Failure */
        if (result != "") {
            aaiUpdateResult = false;
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

if (aaiUpdateResult == true) {
    executor.outFields.put("result", "SUCCCESS");
} else {
    executor.outFields.put("result", "FAILURE");
}

executor.logger.info(executor.outFields);
var returnValue = executor.isTrue;
executor.logger.info("End Execution ServiceUpdateStateCpeAuthTask.js");

