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

var logger = executor.logger;
logger.trace("start: " + executor.subject.id + " - TSL");

var rootLogger = LoggerFactory.getLogger(logger.ROOT_LOGGER_NAME);

var ifSituation = executor.inFields["situation"];

var albumProblemMap = executor.getContextAlbum("albumProblemMap");

var returnValueType = Java.type("java.lang.Boolean");
if (ifSituation.get("problemID") == "NONE") {
    logger.trace("-- situation has no problem, selecting <VpnSlaPolicyDecideNoneTask>");
    executor.subject.getTaskKey("VpnSlaPolicyDecideNoneTask").copyTo(executor.selectedTask);
    var returnValue = new returnValueType(true);
} else if (albumProblemMap.get(ifSituation.get("problemID")).get("status") == "SOLVED") {
    logger.trace("-- situation is solved, selecting <VpnSlaPolicyDecideSolvedTask>");
    executor.subject.getTaskKey("VpnSlaPolicyDecideSolvedTask").copyTo(executor.selectedTask);
    var returnValue = new returnValueType(true);
} else if (ifSituation.get("violatedSLAs") != null && ifSituation.get("violatedSLAs").size() > 0) {
    logger.trace("-- situation is problem with violations, selecting <VpnSlaPolicyDecidePriorityTask>");
    executor.subject.getTaskKey("VpnSlaPolicyDecidePriorityTask").copyTo(executor.selectedTask);
    var returnValue = new returnValueType(true);
} else if (ifSituation.get("violatedSLAs") != null && ifSituation.get("violatedSLAs").size() == 0) {
    logger.trace("-- situation is problem without violations, selecting <VpnSlaPolicyDecideSlaTask>");
    executor.subject.getTaskKey("VpnSlaPolicyDecideSlaTask").copyTo(executor.selectedTask);
    var returnValue = new returnValueType(true);
} else {
    logger.error("-- detected unknown decision for situation <" + ifSituation.get("problemID") + ">");
    rootLogger.error(executor.subject.id + " " + "-- detected unknown decision for situation <"
            + ifSituation.get("problemID") + ">");
    var returnValue = new returnValueType(false);
}

logger.trace("finished: " + executor.subject.id);
logger.debug(".d-tsl");
