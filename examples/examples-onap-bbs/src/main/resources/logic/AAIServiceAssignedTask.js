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

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);
executor.logger.info(vcpeClosedLoopStatus);

var jsonObj;
var aaiUpdateResult = true;

/* Get AAI URL from Configuration file. */
var AAI_URL = "localhost:8080";
var CUSTOMER_ID = requestID;
var BBS_CFS_SERVICE_TYPE = "BBS-CFS-Access_Test";
var SERVICE_INSTANCE_UUID = serviceInstanceId;
var service_instance_id;
var resource_version;
var relationship_list;
var orchStatus;
try {
    var  br = Files.newBufferedReader(Paths.get("/home/apexuser/examples/config/ONAPBBS/config.txt"));
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

executor.logger.info("AAI_URL=>" + AAI_URL);

/* Get service instance Id from AAI */
try {
    //var urlGet = "https://" + AAI_URL + "/aai/v14/business/customers/customer/" + CUSTOMER_ID + "/service-subscriptions/service-subscription/" + BBS_CFS_SERVICE_TYPE + "/service-instances/service-instance/" + SERVICE_INSTANCE_UUID
    var urlGet = "http://" + AAI_URL + "/RestConfServer/rest/operations/policy/su1/getService";
    executor.logger.info("Query url" + urlGet);

    result = httpGet(urlGet).data;
    executor.logger.info("Data received From " + urlGet + " " + result.toString());
    jsonObj = JSON.parse(result);


    /* Retrieve the service instance id */
    service_instance_id = jsonObj['service-instance-id'];
    resource_version = jsonObj['resource-version'];
    relationship_list = jsonObj['relationship-list'];
    executor.logger.info("After Parse " + JSON.stringify(jsonObj, null, 4));

    if (result == "") {
        aaiUpdateResult = false;
    }
}catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

/* BBS Policy updates orchestration status of {{bbs-cfs-service-instance-UUID}} [ active --> assigned ] */
orchStatus = {
            "service-instance-id": service_instance_id,
            "resource-version": resource_version,
            "orchestration-status": "assigned",
            "relationship-list": relationship_list
        };
try {
    if (aaiUpdateResult == true) {
        executor.logger.info("ready to putAfter Parse " + JSON.stringify(orchStatus, null, 4));

        //var urlPut =   "https://" + AAI_URL + "/aai/v14/business/customers/customer/" + CUSTOMER_ID + "/service-subscriptions/service-subscription/" + BBS_CFS_SERVICE_TYPE + "/service-instances/service-instance/" + SERVICE_INSTANCE_UUID;
        var urlPut = "http://" + AAI_URL + "/RestConfServer/rest/operations/policy/su1/putOrchStatus";
        result = httpPut(urlPut, JSON.stringify(orchStatus)).data;
        executor.logger.info("Data received From " + urlPut + " " +result.toString());
        jsonObj = JSON.parse(result);
        executor.logger.info("After Parse " + JSON.stringify(jsonObj, null, 4));

        /* If failure to retrieve data proceed to Failure */
        if (result == "") {
            aaiUpdateResult = false;
        }
    }
}catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

/* BBS Policy fetches from AAI {{bbs-cfs-service-instance-UUID}} relationship-list, including: CPE PNF ID, AccessConnectivity VF ID, InternetProfile VF ID  */
try {
    if (aaiUpdateResult == true) {
        var urlGet2 = "http://" + AAI_URL + "/RestConfServer/rest/operations/policy/su1/getHsia";
        //var urlGet2 = "https://"+ AAI_URL + "/aai/v11/business/customers/customer/" + CUSTOMER_ID + "/service-subscriptions/service-subscription/" + BBS_CFS_SERVICE_TYPE +"/service-instances/service-instance/"+ SERVICE_INSTANCE_UUID + "?depth=all"
        result = httpGet(urlGet2).data;
        executor.logger.info("Data received From " + urlGet2 + " " +result.toString());
        jsonObj = JSON.parse(result);
        executor.logger.info("After Parse " + JSON.stringify(jsonObj, null, 4));

        /* If failure to retrieve data proceed to Failure */
        if ((result == "") || (jsonObj['orchestration-status'] != 'assigned')) {
            executor.logger.info("Failed to get assigned status ");
            aaiUpdateResult = false;
        }
    }
}catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}
/* If Success then Fill output schema */
if (aaiUpdateResult === true) {
    vcpeClosedLoopStatus.put("result", "SUCCESS");
    vcpeClosedLoopStatus.put("aai_message", JSON.stringify(orchStatus));
} else {
    vcpeClosedLoopStatus.put("result", "FAILURE");
}

executor.outFields.put("requestID", requestID);
executor.outFields.put("attachmentPoint", attachmentPoint);
executor.outFields.put("serviceInstanceId",  executor.inFields.get("serviceInstanceId"));

var returnValue = executor.isTrue;
executor.logger.info("==========>" + executor.outFields);
executor.logger.info("End Execution AAIServiceAssignedTask.js");


/* Utility functions Begin */
function httpGet(theUrl){
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "GET";
    return asResponse(con);
}

function httpPost(theUrl, data, contentType){
    contentType = contentType || "application/json";
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "POST";
    con.setRequestProperty("Content-Type", contentType);
    con.doOutput=true;
    write(con.outputStream, data);
    return asResponse(con);
}

function httpPut(theUrl, data, contentType){
    contentType = contentType || "application/json";
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "PUT";
    con.setRequestProperty("Content-Type", contentType);
    con.doOutput=true;
    write(con.outputStream, data);
    return asResponse(con);
}

function asResponse(con){
    var d = read(con.inputStream);
    return {data : d, statusCode : con.resultCode};
}

function write(outputStream, data){
    var wr = new java.io.DataOutputStream(outputStream);
    wr.writeBytes(data);
    wr.flush();
    wr.close();
}

function read(inputStream){
    var inReader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
    var inputLine;
    var result = new java.lang.StringBuffer();

    while ((inputLine = inReader.readLine()) != null) {
           result.append(inputLine);
    }
    inReader.close();
    return result.toString();
}

/* Utility functions End */

