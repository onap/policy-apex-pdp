/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

executor.logger.info("Begin Execution AAIServiceCreateTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var requestID = executor.inFields.get("requestID");
var serviceInstanceId = executor.inFields.get("serviceInstanceId");

var NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(attachmentPoint);
executor.logger.info(NomadicONTContext);

// Get the AAI URL from configuraiotn file
var AAI_URL = "localhost:8080";
var CUSTOMER_ID = requestID;
var BBS_CFS_SERVICE_TYPE = "BBS-CFS-Access_Test";
var SERVICE_INSTANCE_ID = serviceInstanceId;
var AAI_VERSION = "v14";
var HTTP_PROTOCOL = "https://";
var wbClient = org.onap.policy.apex.examples.bbs.WebClient;
var client = new wbClient();
var AAI_USERNAME = null;
var AAI_PASSWORD = null;
try {
    var br = Files.newBufferedReader(Paths.get("/home/apexuser/examples/config/ONAPBBS/config.txt"));
    // read line by line
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
        } else if (line.startsWith("AAI_VERSION")) {
            var str = line.split("=");
            AAI_VERSION = str[str.length - 1];
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}
executor.logger.info("AAI_URL " + AAI_URL);
var aaiUpdateResult = true;
/* Get service instance Id from AAI */
try {
    var urlGet = HTTP_PROTOCOL + AAI_URL + "/aai/" + AAI_VERSION + "/nodes/service-instances/service-instance/"
            + SERVICE_INSTANCE_ID + "?format=resource_and_url";

    executor.logger.info("Query url" + urlGet);

    result = client.httpRequest(urlGet, "GET", null, AAI_USERNAME, AAI_PASSWORD, "application/json");
    executor.logger.info("Data received From " + urlGet + " " + result);
    jsonObj = JSON.parse(result);

    executor.logger.info(JSON.stringify(jsonObj, null, 4));
    /* Retrieve the service instance id */
    results = jsonObj['results'][0];
    putUrl = results['url'];
    service_instance = results['service-instance'];
    executor.logger.info("After Parse service_instance " + JSON.stringify(service_instance, null, 4) + "\n url "
            + putUrl + "\n Service instace Id " + SERVICE_INSTANCE_ID);

    if (result == "") {
        aaiUpdateResult = false;
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

var putUpddateServInstance = service_instance;
putUpddateServInstance['orchestration-status'] = "created";
executor.logger.info(" string" + JSON.stringify(putUpddateServInstance, null, 4));
var resource_version = putUpddateServInstance['resource-version'];
var putUrl = NomadicONTContext.get("url");

/* BBS Policy updates {{bbs-cfs-service-instance-UUID}} orchestration-status [ assigned --> created ] */
try {
    if (aaiUpdateResult == true) {
        executor.logger.info("ready to putAfter Parse " + JSON.stringify(putUpddateServInstance, null, 4));
        var urlPut = HTTP_PROTOCOL + AAI_URL + putUrl + "?resource_version=" + resource_version;
        result = client.httpRequest(urlPut, "PUT", JSON.stringify(putUpddateServInstance), AAI_USERNAME, AAI_PASSWORD,
                "application/json");
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
/* If Success then Fill output schema */
if (aaiUpdateResult == true) {
    NomadicONTContext.put("result", "SUCCESS");
} else {
    NomadicONTContext.put("result", "FAILURE");

}

executor.outFields.put("requestID", requestID);
executor.outFields.put("attachmentPoint", attachmentPoint);
executor.outFields.put("serviceInstanceId", executor.inFields.get("serviceInstanceId"));

executor.logger.info(executor.outFields);
executor.logger.info("End Execution AAIServiceCreateTask.js");

true;

