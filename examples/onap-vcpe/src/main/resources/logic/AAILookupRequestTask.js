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

var guardDecisionAttributes = executor.subject.getOutFieldSchemaHelper("decisionAttributes").createNewInstance();

guardDecisionAttributes.put("actor", "APPC");
guardDecisionAttributes.put("recipe", "Restart");
guardDecisionAttributes.put("target", executor.inFields.get("vnfID").toString());
guardDecisionAttributes.put("clname", "APEXvCPEImplementation");

executor.logger.info(guardDecisionAttributes);

executor.outFields.put("decisionAttributes", guardDecisionAttributes);
executor.outFields.put("onapName", "APEX");

executor.getContextAlbum("ControlLoopExecutionIDAlbum").put(executor.executionID.toString(),
        executor.inFields.get("vnfID"));

executor.logger.info(executor.outFields);

var returnValue = executor.TRUE;