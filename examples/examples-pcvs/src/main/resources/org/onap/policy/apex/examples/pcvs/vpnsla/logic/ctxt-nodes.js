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

var logger = executor.logger;
logger.trace("start: " + executor.subject.id);
logger.trace("-- infields: " + executor.inFields);

var ifNodeName = executor.inFields["nodeName"];
var ifMininetName = executor.inFields["mininetName"];

var albumTopoNodes = executor.getContextAlbum("albumTopoNodes");

logger.trace("-- got infields, testing existing node");

var ctxtNode = albumTopoNodes.get(ifNodeName);
if (ctxtNode != null) {
    albumTopoNodes.remove(ifNodeName);
    logger.trace("-- removed node: <" + ifNodeName + ">");
}

logger.trace("-- creating node: <" + ifNodeName + ">");
ctxtNode = "{name:" + ifNodeName + ", mnname:" + ifMininetName + "}";
albumTopoNodes.put(ifNodeName, ctxtNode);

if (logger.isTraceEnabled()) {
    logger.trace("   >> *** Nodes ***");
    if (albumTopoNodes != null) {
        for (var i = 0; i < albumTopoNodes.values().size(); i++) {
            logger.trace("   >> >> " + albumTopoNodes.values().get(i).get("name") + " : "
                    + albumTopoNodes.values().get(i).get("mnname"));
        }
    } else {
        logger.trace("   >> >> node album is null");
    }
}

executor.outFields["report"] = "node ctxt :: added node " + ifNodeName;

logger.info("vpnsla: ctxt added node " + ifNodeName);

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);
logger.trace("finished: " + executor.subject.id);
logger.debug(".");
