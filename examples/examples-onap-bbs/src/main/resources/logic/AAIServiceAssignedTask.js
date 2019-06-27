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

var NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(attachmentPoint);
executor.logger.info(NomadicONTContext);

var jsonObj;
var aaiUpdateResult = true;

var wbClient = Java.type("org.onap.policy.apex.examples.bbs.WebClient");
var client = new wbClient();

/* Get AAI URL from Configuration file. */
var AAI_URL = "localhost:8080";
var CUSTOMER_ID = requestID;
var SERVICE_INSTANCE_ID = serviceInstanceId;
var AAI_VERSION = "v14";
var resource_version;
var relationship_list;
var HTTP_PROTOCOL = "https://";
var results;
var putUrl;
var service_instance;
try {
    var br = Files.newBufferedReader(Paths.get("/home/apexuser/examples/config/ONAPBBS/config.txt"));
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

/* Get service instance Id from AAI */
try {
    var urlGet = HTTP_PROTOCOL + AAI_URL + "/aai/" + AAI_VERSION + "/nodes/service-instances/service-instance/"
            + SERVICE_INSTANCE_ID + "?format=resource_and_url";

    executor.logger.info("Query url" + urlGet);

    result = client.httpRequest(urlGet, "GET", null, AAI_USERNAME, AAI_PASSWORD, "application/json", true);
    executor.logger.info("Data received From " + urlGet + " " + result);
    jsonObj = JSON.parse(result.toString());

    executor.logger.info(JSON.stringify(jsonObj, null, 4));
    /* Retrieve the service instance id */
    results = jsonObj['results'][0];
    putUrl = results['url'];
    service_instance = results['service-instance'];
    service_instance_id = service_instance['service-instance-id'];
    resource_version = service_instance['resource-version'];
    relationship_list = service_instance['relationship-list'];
    executor.logger.info("After Parse service_instance " + JSON.stringify(service_instance, null, 4) + "\n url "
            + putUrl + "\n Service instace Id " + service_instance_id);

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
        executor.logger.info("ready to putAfter Parse " + JSON.stringify(putUpddateServInstance, null, 4));
        var urlPut = HTTP_PROTOCOL + AAI_URL + putUrl + "?resource_version=" + resource_version;
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

if (!service_instance.hasOwnProperty('input-parameters') || !service_instance.hasOwnProperty('metadata')) {
    aaiUpdateResult = false;
    executor.logger.info("Validate data failed. input-parameters or metadata is missing");
}

/* update logical link in pnf */
var oldLinkName = "";
try {
    if (aaiUpdateResult == true) {
        var pnfName = "";
        var pnfResponse;
        var pnfUpdate;
        var relationShips = relationship_list["relationship"];

        for (var i = 0; i < relationShips.length; i++) {
            if (relationShips[i]["related-to"] == "pnf") {
                var relationship_data = relationShips[i]["relationship-data"];
                for (var j = 0; j < relationship_data.length; j++) {
                    if (relationship_data[j]["relationship-key"] == "pnf.pnf-name") {
                        pnfName = relationship_data[j]['relationship-value'];
                        break;
                    }
                }
            }
        }
        executor.logger.info("pnf-name found " + pnfName);

        /* 1. Get PNF */
        var urlGetPnf = HTTP_PROTOCOL + AAI_URL + "/aai/" + AAI_VERSION + "/network/pnfs/pnf/" + pnfName;
        pnfResponse = client.httpRequest(urlGetPnf, "GET", null, AAI_USERNAME, AAI_PASSWORD, "application/json", true);
        executor.logger.info("Data received From " + urlGetPnf + " " + pnfResponse);
        /* If failure to retrieve data proceed to Failure */
        if (result != "") {
            aaiUpdateResult = false;
        }
        pnfUpdate = JSON.parse(pnfResponse.toString());
        executor.logger.info(JSON.stringify(pnfUpdate, null, 4));

        /*2. Create logical link */
        var link_name = attachmentPoint;
        var logicalLink = {
            "link-name" : link_name,
            "in-maint" : false,
            "link-type" : "attachment-point"
        };
        var urlNewLogicalLink = HTTP_PROTOCOL + AAI_URL + "/aai/" + AAI_VERSION
                + "/network/logical-links/logical-link/" + link_name;
        result = client.httpRequest(urlNewLogicalLink, "PUT", JSON.stringify(logicalLink), AAI_USERNAME, AAI_PASSWORD,
                "application/json", true);
        executor.logger.info("Data received From " + urlNewLogicalLink + " " + result);
        /* If failure to retrieve data proceed to Failure */
        if (result != "") {
            aaiUpdateResult = false;
        }

        /*3. Update pnf with new relation*/
        for (var i = 0; i < pnfUpdate["relationship-list"]["relationship"].length; i++) {
            if (pnfUpdate["relationship-list"]["relationship"][i]['related-to'] == 'logical-link') {
                pnfUpdate["relationship-list"]["relationship"][i]['related-link'] = "/aai/" + AAI_VERSION
                        + "/network/logical-links/logical-link/" + link_name;
                for (var j = 0; j < pnfUpdate["relationship-list"]["relationship"][i]['relationship-data'].length; j++) {
                    if (pnfUpdate["relationship-list"]["relationship"][i]['relationship-data'][j]['relationship-key'] == "logical-link.link-name") {
                        oldLinkName = pnfUpdate["relationship-list"]["relationship"][i]['relationship-data'][j]['relationship-value'];
                        pnfUpdate["relationship-list"]["relationship"][i]['relationship-data'][j]['relationship-value'] = link_name;
                        break;
                    }
                }
                break;
            }
        }

        executor.logger.info("Put pnf to aai " + JSON.stringify(pnfUpdate, null, 4));
        var urlPutPnf = HTTP_PROTOCOL + AAI_URL + "/aai/" + AAI_VERSION + "/network/pnfs/pnf/" + pnfName;
        result = client.httpRequest(urlPutPnf, "PUT", JSON.stringify(pnfUpdate), AAI_USERNAME, AAI_PASSWORD,
                "application/json", true);
        executor.logger.info("Data received From " + urlPutPnf + " " + result);

        /* If failure to retrieve data proceed to Failure */
        if (result != "") {
            aaiUpdateResult = false;
        }

        /* Get and Delete the Stale logical link */
        var oldLinkResult;
        var linkResult;
        var urlOldLogicalLink = HTTP_PROTOCOL + AAI_URL + "/aai/" + AAI_VERSION
                + "/network/logical-links/logical-link/" + oldLinkName;
        linkResult = client
                .httpRequest(urlOldLogicalLink, "GET", null, AAI_USERNAME, AAI_PASSWORD, "application/json", true);
        executor.logger.info("Data received From " + urlOldLogicalLink + " " + linkResult + " "
                + linkResult.hasOwnProperty("link-name"));
        oldLinkResult = JSON.parse(linkResult.toString());
        if (oldLinkResult.hasOwnProperty("link-name") == true) {
            var res_version = oldLinkResult["resource-version"];
            var urlDelOldLogicalLink = urlOldLogicalLink + "?resource-version=" + res_version;
            executor.logger.info("Delete called for " + urlDelOldLogicalLink);
            result = client.httpRequest(urlDelOldLogicalLink, "DELETE", null, AAI_USERNAME, AAI_PASSWORD,
                    "application/json", true);
            executor.logger.info("Delete called for " + urlDelOldLogicalLink + " result " + result);
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
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
executor.outFields.put("serviceInstanceId", executor.inFields.get("serviceInstanceId"));

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
