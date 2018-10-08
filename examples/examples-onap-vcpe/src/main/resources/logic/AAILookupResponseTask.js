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

var vnfID = executor.getContextAlbum("ControlLoopExecutionIDAlbum").remove(executor.executionId.toString());

executor.logger.info("Continuing execution with VNF ID: " + vnfID);

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(vnfID.toString());
executor.logger.info(vcpeClosedLoopStatus);

var aaiResponse = executor.inFields.get("AAINamedQueryResponse");

for (var iterator = aaiResponse.getInventoryResponseItems().iterator(); iterator.hasNext(); ) {
    var responseItem = iterator.next();
    
    if (responseItem.getModelName() != "vCPE") {
        continue;
    }
    
    var aaiInfo = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").getSchemaHelper().createNewSubInstance("VCPE_AAI_Type");

    aaiInfo.put("genericVnfResourceVersion",      responseItem.getGenericVnf().getResourceVersion());
    aaiInfo.put("genericVnfVnfName",              responseItem.getGenericVnf().getVnfName());
    aaiInfo.put("genericVnfProvStatus",           responseItem.getGenericVnf().getProvStatus());
    aaiInfo.put("genericVnfIsClosedLoopDisabled", responseItem.getGenericVnf().getIsClosedLoopDisabled());
    aaiInfo.put("genericVnfVnfType",              responseItem.getGenericVnf().getVnfType());
    aaiInfo.put("genericVnfInMaint",              responseItem.getGenericVnf().getInMaint());
    aaiInfo.put("genericVnfServiceId",            responseItem.getGenericVnf().getServiceId());
    aaiInfo.put("genericVnfVnfId",                responseItem.getGenericVnf().getVnfId());

    aaiInfo.put("genericVnfOrchestrationStatus",  responseItem.getVfModule().getOrchestrationStatus());

    vcpeClosedLoopStatus.put("AAI", aaiInfo);
    
    break;
}

// We should check here for the case where AAI returns an error or no data for the query


var uuidType = Java.type("java.util.UUID");
var requestID = uuidType.fromString(vcpeClosedLoopStatus.get("requestID"));

executor.outFields.put("requestID", requestID);
executor.outFields.put("vnfID", vnfID);

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
