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
executor.logger.info("Begin Execution SdncResourceUpdateTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var requestID = executor.inFields.get("requestID");
var serviceInstanceId = executor.inFields.get("serviceInstanceId");

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);
executor.logger.info(vcpeClosedLoopStatus);
var jsonObj;
var aaiUpdateResult = true;
var SDNC_URL = "localhost:8080";
try {
    var  br = Files.newBufferedReader(Paths.get("/home/apexuser/examples/config/ONAPBBS/config.txt"));
    // read line by line
    var line;
    while ((line = br.readLine()) != null) {
        if (line.startsWith("SDNC_URL")) {
            var str = line.split("=");
            SDNC_URL = str[str.length - 1];
            break;
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}
executor.logger.info("SDNC_URL=>" + SDNC_URL);


var result;
var jsonObj;
var aaiUpdateResult = true;

/* BBS Policy calls SDN-C GR-API to delete AccessConnectivity VF ID */
var json = {id: 1, someValue: "1234"};
try {
    var urlPost1 = "http://" + SDNC_URL + "/RestConfServer/rest/operations/policy/ru/createInstance";
    result = httpPost(urlPost1, JSON.stringify(json) ,"application/json").data;
    executor.logger.info("Data received From " + urlPost1 + " " +result.toString());
    jsonObj = JSON.parse(result);
    executor.logger.info("After Parse " + jsonObj.toString());

    if (result == "") {
        aaiUpdateResult = false;
    }
}catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}
/* BBS Policy calls SDN-C GR-API to create new AccessConnectivity VF  */
try {
    if (aaiUpdateResult == true) {
        var json = {id: 2, someValue: "1234"};
        var urlPost2 = "http://" + SDNC_URL + "/RestConfServer/rest/operations/policy/ru/createInstance";
        result = httpPost(urlPost2, JSON.stringify(json), "application/json").data;
        executor.logger.info("Data received From " + urlPost2 + " " +result.toString());
        jsonObj = JSON.parse(result);
        executor.logger.info("After Parse " + jsonObj.toString());

        if (result == "") {
        }
    }
}catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}


/* If Success then Fill output schema */
if (aaiUpdateResult === true) {
    vcpeClosedLoopStatus.put("result", "SUCCESS");
} else {
    vcpeClosedLoopStatus.put("result", "FAILURE");
}


executor.outFields.put("requestID", requestID);
executor.outFields.put("attachmentPoint", attachmentPoint);
executor.outFields.put("serviceInstanceId",  executor.inFields.get("serviceInstanceId"));

var returnValue = executor.isTrue;
executor.logger.info("==========>" + executor.outFields);
executor.logger.info("End Execution SdncResourceUpdateTask.js");


/* Utility functions Begin */
function httpGet(theUrl){
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "GET";
    return asresult(con);
}

function httpPost(theUrl, data, contentType){
    contentType = contentType || "application/json";
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "POST";
    con.setRequestProperty("Content-Type", contentType);
    con.doOutput=true;
    write(con.outputStream, data);
    return asresult(con);
}

function httpPut(theUrl, data, contentType){
    contentType = contentType || "application/json";
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "PUT";
    con.setRequestProperty("Content-Type", contentType);
    con.doOutput=true;
    write(con.outputStream, data);
    return asresult(con);
}

function asresult(con){
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
