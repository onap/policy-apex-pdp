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

load("nashorn:mozilla_compat.js");
importClass(org.slf4j.LoggerFactory);

importClass(java.util.ArrayList);

importClass(org.apache.avro.generic.GenericData.Array);
importClass(org.apache.avro.generic.GenericRecord);
importClass(org.apache.avro.Schema);

var logger = executor.logger;
logger.trace("start: " + executor.subject.id);
logger.trace("-- infields: " + executor.inFields);

var rootLogger = LoggerFactory.getLogger(logger.ROOT_LOGGER_NAME);

var ifSituation = executor.inFields["situation"];

var albumCustomerMap = executor.getContextAlbum("albumCustomerMap");
var albumProblemMap = executor.getContextAlbum("albumProblemMap");

// create outfiled for decision
var decision = executor.subject.getOutFieldSchemaHelper("decision").createNewInstance();
decision.put("description", "None, everything is ok");
decision.put("decision", "IMPEDE");
decision.put("problemID", ifSituation.get("problemID"));
decision.put("customers", new ArrayList());

var problem = albumProblemMap.get(ifSituation.get("problemID"));
var returnValueType = Java.type("java.lang.Boolean");
if (problem != null && ifSituation.get("violatedSLAs").size() > 0) {
    logger.trace("-- impede by priority");
    for (var i = 0; i < problem.get("edgeUsedBy").size(); i++) {
        customerCtxt = albumCustomerMap.get(problem.get("edgeUsedBy").get(i).toString());
        if (customerCtxt.get("priority") == false) {
            decision.get("customers").add(customerCtxt.get("customerName"));
        }
    }
    var returnValue = new returnValueType(true);
} else {
    logger.trace("-- wrong problemID <" + ifSituation.get("problemID") + "> for PRIORITY task, we should not be here");
    rootLogger.error(executor.subject.id + " " + "-- wrong problemID <" + ifSituation.get("problemID")
            + "> for PRIORITY task, we should not be here");
    var returnValue = new returnValueType(false);
}

// set impededLast to decision[customers]
problem.get("impededLast").clear();
problem.get("impededLast").addAll(decision.get("customers"));

executor.outFields["decision"] = decision;
logger.trace("-- decision: " + decision);

logger.info("vpnsla: priority, impeding customers " + decision.get("customers"));

logger.trace("finished: " + executor.subject.id);
logger.debug(".d-pri");
