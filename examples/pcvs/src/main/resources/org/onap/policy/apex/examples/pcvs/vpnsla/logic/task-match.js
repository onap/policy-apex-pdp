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

var now = new Date().getTime();
executor.outFields["matchStart"] = now;

importClass(org.slf4j.LoggerFactory);

var logger = executor.logger;
logger.trace("start: " + executor.subject.id);
logger.trace("-- infields: " + executor.inFields);

var rootLogger = LoggerFactory.getLogger(logger.ROOT_LOGGER_NAME);

var ifEdgeName = executor.inFields["edgeName"];
var ifLinkStatus = executor.inFields["status"];

var albumTopoEdges = executor.getContextAlbum("albumTopoEdges");

logger.trace("-- got infields, checking albumTopoEdges changes");

var active = false;
switch (ifLinkStatus.toString()) {
case "UP":
    active = true;
    break;
case "DOWN":
    active = false;
    break;
default:
    active = false;
    logger.error("-- trigger sent unknown link status <" + ifLinkStatus + "> for link <" + ifEdgeName + ">");
    rootLogger.error(executor.subject.id + " " + "-- trigger sent unknown link status <" + ifLinkStatus
            + "> for link <" + ifEdgeName + ">");
}

var link = albumTopoEdges.get(ifEdgeName);
if (link == null) {
    logger.trace("-- link <" + ifEdgeName + "> not in albumTopoEdges");
} else {
    logger.trace("-- found link <" + link + "> in albumTopoEdges");
    logger.trace("-- active <" + active + "> : link.active <" + link.get("active") + ">");
    if (active != link.get("active")) {
        link.put("active", active);
        logger.trace("-- link <" + ifEdgeName + "> status changed to <active:" + link.get("active") + ">");
        executor.outFields["hasChanged"] = true;
    } else {
        logger.trace("-- link <" + ifEdgeName + "> status not changed <active:" + link.get("active") + ">");
        executor.outFields["hasChanged"] = false;
    }
}

executor.outFields["edgeName"] = ifEdgeName;
executor.outFields["status"] = ifLinkStatus;

logger.info("vpnsla: detected " + ifEdgeName + " as " + ifLinkStatus);

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);
logger.trace("finished: " + executor.subject.id);
logger.debug(".m");
