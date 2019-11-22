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

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());

executor.logger.info("Executing A&AI Lookup");
executor.logger.info(vcpeClosedLoopStatus);

var aaiInfo = vcpeClosedLoopStatus.get("AAI");

if (aaiInfo.get("vserverName") == null) {
    executor.message = "the field vserver.vserver-name must exist in the onset control loop event";
    executor.logger.warn(executor.message);
    var returnValue = executor.isFalse;
}
else if (aaiInfo.get("genericVnfVnfId") == null && aaiInfo.get("genericVnfVnfName") == null) {
    executor.message = "either the field generic-vnf.vnf-id or generic-vnf.vnf-name must exist"
        + " in the onset control loop event";
    executor.logger.warn(executor.message);
    var returnValue = executor.isFalse;
}
else {
    var restManager = new org.onap.policy.rest.RestManager;
    var aaiManager = new org.onap.policy.aai.AaiManager(restManager);

    // We need to instantiate the type in order to trigger the static JAXB handling
    // in the AaiCqResponse class
    var aaiCqResponseType = Java.type("org.onap.policy.aai.AaiCqResponse");

    var aaiResponse = aaiManager.getCustomQueryResponse(
            "http://localhost:54321/OnapVCpeSim/sim",
            "aai.username",
            "aai.password",
            executor.inFields.get("requestID"),
            vcpeClosedLoopStatus.get("AAI").get("vserverName")
    );

    var genericVnf;

    if (aaiInfo.get("genericVnfVnfId") != null) {
        genericVnf = aaiResponse.getGenericVnfByModelInvariantId(aaiInfo.get("genericVnfVnfId"));
    }
    else {
        genericVnf = aaiResponse.getGenericVnfByModelInvariantId(aaiInfo.get("genericVnfVnfId"));
    }

    aaiInfo.put("genericVnfResourceVersion",      genericVnf.getResourceVersion());
    aaiInfo.put("genericVnfVnfName",              genericVnf.getVnfName());
    aaiInfo.put("genericVnfProvStatus",           genericVnf.getProvStatus());
    aaiInfo.put("genericVnfIsClosedLoopDisabled", genericVnf.isIsClosedLoopDisabled().toString());
    aaiInfo.put("genericVnfVnfType",              genericVnf.getVnfType());
    aaiInfo.put("genericVnfInMaint",              genericVnf.isInMaint().toString());
    aaiInfo.put("genericVnfServiceId",            genericVnf.getServiceId());
    aaiInfo.put("genericVnfVnfId",                genericVnf.getVnfId());
    aaiInfo.put("genericVnfOrchestrationStatus",
            genericVnf.getVfModules().getVfModule().get(0).getOrchestrationStatus());

    executor.outFields.put("requestID", executor.inFields.get("requestID"));
    executor.outFields.put("vnfID", executor.inFields.get("vnfID"));

    executor.logger.info(executor.outFields);

    var returnValue = executor.isTrue;
}