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

importPackage(org.json.XML);

executor.logger.info("Begin Execution SdncResourceUpdateTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var requestID = executor.inFields.get("requestID");
var serviceInstanceId = executor.inFields.get("serviceInstanceId");
var uuidType = Java.type("java.util.UUID");

var wbClient = Java.type("org.onap.policy.apex.examples.bbs.WebClient");
var client = new wbClient();

var NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(attachmentPoint);
var sdncUUID = uuidType.randomUUID();
executor.logger.info(NomadicONTContext);
var jsonObj;
var aaiUpdateResult = true;
var SDNC_URL = "localhost:8080";
var HTTP_PROTOCOL = "http://"
var SVC_NOTIFICATION_URL;
var putUpddateServInstance = JSON.parse(NomadicONTContext.get("aai_message"));
var input_param = JSON.parse(putUpddateServInstance['input-parameters']);
try {
    var br = Files.newBufferedReader(Paths.get("/home/apexuser/examples/config/ONAPBBS/config.txt"));
    var line;
    while ((line = br.readLine()) != null) {
        if (line.startsWith("SDNC_URL")) {
            var str = line.split("=");
            SDNC_URL = str[str.length - 1];
        } else if (line.startsWith("SVC_NOTIFICATION_URL")) {
            var str = line.split("=");
            SVC_NOTIFICATION_URL = str[str.length - 1];
        } else if (line.startsWith("SDNC_USERNAME")) {
            var str = line.split("=");
            SDNC_USERNAME = str[str.length - 1];
        } else if (line.startsWith("SDNC_PASSWORD")) {
            var str = line.split("=");
            SDNC_PASSWORD = str[str.length - 1];
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}
executor.logger.info("SDNC_URL " + SDNC_URL);

var result;
var jsonObj;
var sdncUpdateResult = true;

/* BBS Policy calls SDN-C GR-API to delete AccessConnectivity VF ID */
/* Prepare Data */
var xmlDeleteAccess = "";
try {
    var br = Files.newBufferedReader(Paths
            .get("/home/apexuser/examples/config/ONAPBBS/sdnc_DeleteAccessConnectivityInstance.txt"));
    var line;
    while ((line = br.readLine()) != null) {
        xmlDeleteAccess += line;
    }

} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}

/* BBS Policy calls SDN-C GR-API to delete AccessConnectivity */
xmlDeleteAccess = xmlDeleteAccess.replace("svc_request_id_value", sdncUUID);
xmlDeleteAccess = xmlDeleteAccess.replace("svc_notification_url_value", SVC_NOTIFICATION_URL);
xmlDeleteAccess = xmlDeleteAccess.replace("request_id_value", sdncUUID);
xmlDeleteAccess = xmlDeleteAccess.replace("service_id_value", sdncUUID);
xmlDeleteAccess = xmlDeleteAccess.replace("service_instance_id_value", putUpddateServInstance['service-instance-id']);
xmlDeleteAccess = xmlDeleteAccess.replace("service_type_value", input_param['service']['serviceType']);
xmlDeleteAccess = xmlDeleteAccess.replace("customer_id_value", input_param['service']['globalSubscriberId']);
xmlDeleteAccess = xmlDeleteAccess.replace("customer_name_value", input_param['service']['globalSubscriberId']);

xmlDeleteAccess = xmlDeleteAccess.replace("srv_info_model_inv_uuid_value", getResourceInvariantUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlDeleteAccess = xmlDeleteAccess.replace("srv_info_model_custom_uuid_value", getResourceCustomizationUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlDeleteAccess = xmlDeleteAccess.replace("srv_info_model_uuid_value", getResourceUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlDeleteAccess = xmlDeleteAccess.replace("srv_info_model_name_value", "AccessConnectivity");
xmlDeleteAccess = xmlDeleteAccess.replace("network_info_model_inv_uuid_value", getResourceInvariantUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlDeleteAccess = xmlDeleteAccess.replace("network_info_model_custom_uuid_value", getResourceCustomizationUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlDeleteAccess = xmlDeleteAccess.replace("network_info_model_uuid_value", getResourceUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlDeleteAccess = xmlDeleteAccess.replace("network_info_model_name_value", "AccessConnectivity");

xmlDeleteAccess = xmlDeleteAccess.replace("vendor_value",
        input_param['service']['parameters']['requestInputs']['ont_ont_manufacturer']);
xmlDeleteAccess = xmlDeleteAccess.replace("service_id_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'controller-service-id'));
executor.logger.info(client.toPrettyString(xmlDeleteAccess, 4));

try {
    var urlPost1 = HTTP_PROTOCOL + SDNC_URL + "/restconf/operations/GENERIC-RESOURCE-API:network-topology-operation";
    result = client.httpRequest(urlPost1, "POST", xmlDeleteAccess, SDNC_USERNAME, SDNC_PASSWORD, "application/xml",
            false);
    executor.logger.info("Data received From " + urlPost1 + " " + result);
    if (result == "") {
        sdncUpdateResult = false;
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    sdncUpdateResult = false;
}

/* BBS Policy calls SDN-C GR-API to create new AccessConnectivity VF */

/* Prepare Data */
var xmlCreateAccess = "";
try {
    var br = Files.newBufferedReader(Paths
            .get("/home/apexuser/examples/config/ONAPBBS/sdnc_CreateAccessConnectivityInstance.txt"));
    var line;
    while ((line = br.readLine()) != null) {
        xmlCreateAccess += line;
    }

} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}
xmlCreateAccess = xmlCreateAccess.replace("svc_request_id_value", sdncUUID);
xmlCreateAccess = xmlCreateAccess.replace("svc_notification_url_value", SVC_NOTIFICATION_URL);
xmlCreateAccess = xmlCreateAccess.replace("request_id_value", requestID);
xmlCreateAccess = xmlCreateAccess.replace("service_id_value", sdncUUID);
xmlCreateAccess = xmlCreateAccess.replace("service_instance_id_value", putUpddateServInstance['service-instance-id']);
xmlCreateAccess = xmlCreateAccess.replace("service_type_value", input_param['service']['serviceType']);
xmlCreateAccess = xmlCreateAccess.replace("customer_id_value", input_param['service']['globalSubscriberId']);
xmlCreateAccess = xmlCreateAccess.replace("customer_name_value", input_param['service']['globalSubscriberId']);

xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_inv_uuid_value", getResourceInvariantUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_custom_uuid_value", getResourceCustomizationUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_uuid_value", getResourceUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_name_value", "AccessConnectivity");
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_inv_uuid_value", getResourceInvariantUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_custom_uuid_value", getResourceCustomizationUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_uuid_value", getResourceUuid(
        input_param['service']['parameters']['resources'], 'AccessConnectivity'));
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_name_value", "AccessConnectivity");

xmlCreateAccess = xmlCreateAccess.replace("vendor_value",
        input_param['service']['parameters']['requestInputs']['ont_ont_manufacturer']);
xmlCreateAccess = xmlCreateAccess.replace("ont_sn_value",
        input_param['service']['parameters']['requestInputs']['ont_ont_serial_num']);
xmlCreateAccess = xmlCreateAccess.replace("s_vlan_value", getMetaValue(putUpddateServInstance['metadata']['metadatum'],
        'svlan'));
xmlCreateAccess = xmlCreateAccess.replace("c_vlan_value", getMetaValue(putUpddateServInstance['metadata']['metadatum'],
        'cvlan'));
xmlCreateAccess = xmlCreateAccess.replace("remote_id_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'remote-id'));
executor.logger.info(client.toPrettyString(xmlCreateAccess, 4));

try {
    if (sdncUpdateResult == true) {
        var urlPost2 = HTTP_PROTOCOL + SDNC_URL
                + "/restconf/operations/GENERIC-RESOURCE-API:network-topology-operation";
        result = client.httpRequest(urlPost2, "POST", xmlCreateAccess, SDNC_USERNAME, SDNC_PASSWORD, "application/xml",
                false);
        executor.logger.info("Data received From " + urlPost2 + " " + result);
        if (result == "") {
            sdncUpdateResult = false;
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    sdncUpdateResult = false;
}

/* BBS Policy calls SDN-C GR-API to create change Internet Profile */
var xmlChangeProfile = "";
try {
    var br = Files.newBufferedReader(Paths
            .get("/home/apexuser/examples/config/ONAPBBS/sdnc_ChangeInternetProfileInstance.txt"));
    var line;
    while ((line = br.readLine()) != null) {
        xmlChangeProfile += line;
    }

} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}

xmlChangeProfile = xmlChangeProfile.replace("svc_request_id_value", sdncUUID);
xmlChangeProfile = xmlChangeProfile.replace("svc_notification_url_value", SVC_NOTIFICATION_URL);
xmlChangeProfile = xmlChangeProfile.replace("request_id_value", requestID);
xmlChangeProfile = xmlChangeProfile.replace("service_id_value", sdncUUID);
xmlChangeProfile = xmlChangeProfile.replace("service_instance_id_value", putUpddateServInstance['service-instance-id']);
xmlChangeProfile = xmlChangeProfile.replace("service_type_value", input_param['service']['serviceType']);
xmlChangeProfile = xmlChangeProfile.replace("customer_id_value", input_param['service']['globalSubscriberId']);
xmlChangeProfile = xmlChangeProfile.replace("customer_name_value", input_param['service']['globalSubscriberId']);

xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_inv_uuid_value", getResourceInvariantUuid(
        input_param['service']['parameters']['resources'], 'InternetProfile'));
xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_custom_uuid_value", getResourceCustomizationUuid(
        input_param['service']['parameters']['resources'], 'InternetProfile'));
xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_uuid_value", getResourceUuid(
        input_param['service']['parameters']['resources'], 'InternetProfile'));
xmlCreateAccess = xmlCreateAccess.replace("srv_info_model_name_value", "InternetProfile");
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_inv_uuid_value", getResourceInvariantUuid(
        input_param['service']['parameters']['resources'], 'InternetProfile'));
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_custom_uuid_value", getResourceCustomizationUuid(
        input_param['service']['parameters']['resources'], 'InternetProfile'));
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_uuid_value", getResourceUuid(
        input_param['service']['parameters']['resources'], 'InternetProfile'));
xmlCreateAccess = xmlCreateAccess.replace("network_info_model_name_value", "InternetProfile");

xmlChangeProfile = xmlChangeProfile.replace("vendor_value",
        input_param['service']['parameters']['requestInputs']['ont_ont_manufacturer']);
xmlChangeProfile = xmlChangeProfile.replace("service_id_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'controller-service-id'));
xmlChangeProfile = xmlChangeProfile.replace("remote_id_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'remote-id'));
xmlChangeProfile = xmlChangeProfile.replace("ont_sn_value",
        input_param['service']['parameters']['requestInputs']['ont_ont_serial_num']);
xmlChangeProfile = xmlChangeProfile.replace("service_type_value", input_param['service']['serviceType']);
xmlChangeProfile = xmlChangeProfile.replace("mac_value", getMetaValue(putUpddateServInstance['metadata']['metadatum'],
        'rgw-mac-address'));
xmlChangeProfile = xmlChangeProfile.replace("up_speed_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'up-speed'));
xmlChangeProfile = xmlChangeProfile.replace("down_speed_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'down-speed'));
xmlChangeProfile = xmlChangeProfile.replace("s_vlan_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'svlan'));
xmlChangeProfile = xmlChangeProfile.replace("c_vlan_value", getMetaValue(
        putUpddateServInstance['metadata']['metadatum'], 'cvlan'));

executor.logger.info(client.toPrettyString(xmlChangeProfile, 4));
try {
    if (sdncUpdateResult == true) {
        var urlPost3 = HTTP_PROTOCOL + SDNC_URL
                + "/restconf/operations/GENERIC-RESOURCE-API:network-topology-operation";
        result = client.httpRequest(urlPost3, "POST", xmlChangeProfile, SDNC_USERNAME, SDNC_PASSWORD,
                "application/xml", false);
        executor.logger.info("Data received From " + urlPost3 + " " + result);
        if (result == "") {
            sdncUpdateResult = false;
        }
    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    sdncUpdateResult = false;
}

/* If Success then Fill output schema */

if (sdncUpdateResult === true) {
    NomadicONTContext.put("result", "SUCCESS");
    executor.outFields.put("result", "SUCCESS");
} else {
    NomadicONTContext.put("result", "FAILURE");
    executor.outFields.put("result", "FAILURE");
}

executor.outFields.put("requestID", requestID);
executor.outFields.put("attachmentPoint", attachmentPoint);
executor.outFields.put("serviceInstanceId", executor.inFields.get("serviceInstanceId"));

var returnValue = executor.isTrue;
executor.logger.info(executor.outFields);
executor.logger.info("End Execution SdncResourceUpdateTask.js");

function getMetaValue(metaJson, metaname) {
    for (var i = 0; i < metaJson.length; i++) {
        if (metaJson[i]['metaname'] == metaname) {
            var metaValue = metaJson[i]['metaval'];
            return metaValue;
        }
    }

}

function getResourceInvariantUuid(resJson, resourceName) {
    for (var i = 0; i < resJson.length; i++) {
        if (resJson[i]['resourceName'] == resourceName) {
            var resValue = resJson[i]['resourceInvariantUuid'];
            return resValue;
        }
    }

}

function getResourceUuid(resJson, resourceName) {
    for (var i = 0; i < resJson.length; i++) {
        if (resJson[i]['resourceName'] == resourceName) {
            var resValue = resJson[i]['resourceUuid'];
            return resValue;
        }
    }

}

function getResourceCustomizationUuid(resJson, resourceName) {
    for (var i = 0; i < resJson.length; i++) {
        if (resJson[i]['resourceName'] == resourceName) {
            var resValue = resJson[i]['resourceCustomizationUuid'];
            return resValue;
        }
    }

}

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