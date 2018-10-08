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

var aaiRequest = new org.onap.policy.aai.AaiNqRequest;
aaiRequest.setQueryParameters(new org.onap.policy.aai.AaiNqQueryParameters);
aaiRequest.setInstanceFilters(new org.onap.policy.aai.AaiNqInstanceFilters);

aaiRequest.getQueryParameters().setNamedQuery(new org.onap.policy.aai.AaiNqNamedQuery);
aaiRequest.getQueryParameters().getNamedQuery().setNamedQueryUuid(executor.inFields.get("requestID"));

var genericVnfInstanceFilterMap = new java.util.HashMap();
genericVnfInstanceFilterMap.put("vnf-id", vcpeClosedLoopStatus.get("AAI").get("genericVnfVnfId"));

var genericVnfFilterMap = new java.util.HashMap();
genericVnfFilterMap.put("generic-vnf", genericVnfInstanceFilterMap);

aaiRequest.getInstanceFilters().getInstanceFilter().add(genericVnfFilterMap);

executor.logger.info(aaiRequest);

executor.outFields.put("AAINamedQueryRequest", aaiRequest);

executor.getContextAlbum("ControlLoopExecutionIDAlbum").put(executor.executionId.toString(),
        executor.inFields.get("vnfID"));

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;