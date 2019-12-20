/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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
 * Note: The incoming closedloop message can be ONSET with both VNF-name and VNF-ID
 *       or ABATED with only VNF-name. So need to handle differently. For ABATED case,
 *       since we still keep the RequireIDVNFID context album, we can get it from there.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var utf8Type = Java.type("org.apache.avro.util.Utf8");
var uuidType = Java.type("java.util.UUID");

var clEvent = executor.inFields.get("VirtualControlLoopEvent");

executor.logger.info(clEvent.toString());
executor.logger.info(clEvent.get("closedLoopControlName"));

var requestID = uuidType.fromString(clEvent.get("requestID"));
executor.logger.info("requestID = " + requestID);
var vnfID = null;
var vcpeClosedLoopStatus = null;

if (clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_id")) != null) {
    vnfID = uuidType.fromString(clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_id")));
    executor.logger.info("vnfID = " + vnfID);
    vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(vnfID);

    if (vcpeClosedLoopStatus == null) {
        executor.logger.info("Creating context information for new vCPE VNF \"" + vnfID.toString() + "\"");

        vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").getSchemaHelper().createNewInstance();

        vcpeClosedLoopStatus.put("closedLoopControlName", clEvent.get("closedLoopControlName"));
        vcpeClosedLoopStatus.put("closedLoopAlarmStart", clEvent.get("closedLoopAlarmStart"));
        vcpeClosedLoopStatus.put("closedLoopEventClient", clEvent.get("closedLoopEventClient"));
        vcpeClosedLoopStatus.put("closedLoopEventStatus", clEvent.get("closedLoopEventStatus"));
        vcpeClosedLoopStatus.put("version", clEvent.get("version"));
        vcpeClosedLoopStatus.put("requestID", clEvent.get("requestID"));
        vcpeClosedLoopStatus.put("target_type", clEvent.get("target_type"));
        vcpeClosedLoopStatus.put("target", clEvent.get("target"));
        vcpeClosedLoopStatus.put("from", clEvent.get("from"));
        vcpeClosedLoopStatus.put("policyScope", "vCPE");
        vcpeClosedLoopStatus.put("policyName", "ONAPvCPEPolicyModel");
        vcpeClosedLoopStatus.put("policyVersion", "0.0.1");
        vcpeClosedLoopStatus.put("notification", "ACTIVE");
        vcpeClosedLoopStatus.put("notificationTime", java.lang.System.currentTimeMillis());
        vcpeClosedLoopStatus.put("message", "");

        var aaiInfo = executor.getContextAlbum("ControlLoopStatusAlbum").getSchemaHelper().createNewSubInstance(
                "VCPE_AAI_Type");

        aaiInfo.put("genericVnfResourceVersion", clEvent.get("AAI").get(
                new utf8Type("generic_DasH_vnf_DoT_resource_DasH_version")));
        aaiInfo.put("genericVnfVnfName", clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_name")));
        aaiInfo.put("genericVnfProvStatus", clEvent.get("AAI").get(
                new utf8Type("generic_DasH_vnf_DoT_prov_DasH_status")));
        aaiInfo.put("genericVnfIsClosedLoopDisabled", clEvent.get("AAI").get(
                new utf8Type("generic_DasH_vnf_DoT_is_DasH_closed_DasH_loop_DasH_disabled")));
        aaiInfo.put("genericVnfOrchestrationStatus", clEvent.get("AAI").get(
                new utf8Type("generic_DasH_vnf_DoT_orchestration_DasH_status")));
        aaiInfo.put("genericVnfVnfType", clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_type")));
        aaiInfo.put("genericVnfInMaint", clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_in_DasH_maint")));
        aaiInfo
                .put("genericVnfServiceId", clEvent.get("AAI")
                        .get(new utf8Type("generic_DasH_vnf_DoT_service_DasH_id")));
        aaiInfo.put("genericVnfVnfId", clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_id")));
        aaiInfo.put("vserverIsClosedLoopDisabled", clEvent.get("AAI").get(
                new utf8Type("vserver_DoT_is_DasH_closed_DasH_loop_DasH_disabled")));
        aaiInfo.put("vserverProvStatus", clEvent.get("AAI").get(new utf8Type("vserver_DoT_prov_DasH_status")));
        aaiInfo.put("vserverName", clEvent.get("AAI").get(new utf8Type("vserver_DoT_vserver_DasH_name")));

        vcpeClosedLoopStatus.put("AAI", aaiInfo);

        if (clEvent.get("closedLoopAlarmEnd") != null) {
            vcpeClosedLoopStatus.put("closedLoopAlarmEnd", clEvent.get("closedLoopAlarmEnd"));
        } else {
            vcpeClosedLoopStatus.put("closedLoopAlarmEnd", java.lang.Long.valueOf(0));
        }

        executor.getContextAlbum("ControlLoopStatusAlbum").put(vnfID.toString(), vcpeClosedLoopStatus);

        executor.logger.info("Created context information for new vCPE VNF \"" + vnfID.toString() + "\"");
    }

    executor.outFields.put("requestID", requestID);
    executor.outFields.put("vnfID", vnfID);

    executor.logger.info(executor.outFields);
} else {
    executor.logger.info("No vnf-id in VirtualControlLoopEvent, status:"
            + clEvent.get("closedLoopEventStatus").toString());
    var vnfName = clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_name"));
    executor.logger.info("No vnf-id in VirtualControlLoopEvent for " + vnfName);

    vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(vnfName);

    if (vcpeClosedLoopStatus == null) {
        executor.logger.info("Creating context information for new vCPE VNF \"" + vnfName + "\"");

        vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").getSchemaHelper().createNewInstance();

        vcpeClosedLoopStatus.put("closedLoopControlName", clEvent.get("closedLoopControlName"));
        vcpeClosedLoopStatus.put("closedLoopAlarmStart", clEvent.get("closedLoopAlarmStart"));
        vcpeClosedLoopStatus.put("closedLoopEventClient", clEvent.get("closedLoopEventClient"));
        vcpeClosedLoopStatus.put("closedLoopEventStatus", clEvent.get("closedLoopEventStatus("));
        vcpeClosedLoopStatus.put("version", clEvent.get("version"));
        vcpeClosedLoopStatus.put("requestID", clEvent.get("requestID"));
        vcpeClosedLoopStatus.put("target_type", clEvent.get("targetType"));
        vcpeClosedLoopStatus.put("target", clEvent.get("target"));
        vcpeClosedLoopStatus.put("from", clEvent.get("from"));
        vcpeClosedLoopStatus.put("policyScope", "vCPE");
        vcpeClosedLoopStatus.put("policyName", "ONAPvCPEPolicyModel");
        vcpeClosedLoopStatus.put("policyVersion", "0.0.1");
        vcpeClosedLoopStatus.put("notification", "ACTIVE");
        vcpeClosedLoopStatus.put("notificationTime", java.lang.System.currentTimeMillis());
        vcpeClosedLoopStatus.put("message", "");

        var aaiInfo = executor.getContextAlbum("ControlLoopStatusAlbum").getSchemaHelper().createNewSubInstance(
                "VCPE_AAI_Type");

        aaiInfo.put("genericVnfVnfName", clEvent.get("AAI").get(new utf8Type("generic_DasH_vnf_DoT_vnf_DasH_name")));
        vcpeClosedLoopStatus.put("AAI", aaiInfo);

        if (clEvent.get("closedLoopAlarmEnd") != null) {
            vcpeClosedLoopStatus.put("closedLoopAlarmEnd", clEvent.get("closedLoopAlarmEnd"));
        } else {
            vcpeClosedLoopStatus.put("closedLoopAlarmEnd", java.lang.Long.valueOf(0));
        }

        executor.getContextAlbum("ControlLoopStatusAlbum").put(vnfName, vcpeClosedLoopStatus);

        executor.logger.info("Created context information for new vCPE VNF \"" + vnfName + "\"");
    }
    executor.outFields.put("requestID", requestID);
    executor.outFields.put("vnfName", vnfName);
    executor.logger.info(executor.outFields);
}

returnValue = executor.isTrue;
