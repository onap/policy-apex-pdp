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

var ifEdgeName = executor.inFields["edgeName"];
var ifEdgeStatus = executor.inFields["status"];

var albumTopoEdges = executor.getContextAlbum("albumTopoEdges");

logger.trace("-- got infields, testing existing edge");

var ctxtEdge = albumTopoEdges.get(ifEdgeName);
if (ctxtEdge != null) {
    albumTopoEdges.remove(ifEdgeName);
    logger.trace("-- removed edge: <" + ifEdgeName + ">");
}

logger.trace("-- creating edge: <" + ifEdgeName + ">");
ctxtEdge = "{name:" + ifEdgeName + ", start:" + executor.inFields["start"] + ", end:" + executor.inFields["end"]
        + ", active:" + ifEdgeStatus + "}";
albumTopoEdges.put(ifEdgeName, ctxtEdge);

if (logger.isTraceEnabled()) {
    logger.trace("   >> *** Edges ***");
    if (albumTopoEdges != null) {
        for (var i = 0; i < albumTopoEdges.values().size(); i++) {
            logger.trace("   >> >> " + albumTopoEdges.values().get(i).get("name") + " \t "
                    + albumTopoEdges.values().get(i).get("start") + " --> " + albumTopoEdges.values().get(i).get("end")
                    + " \t " + albumTopoEdges.values().get(i).get("active"));
        }
    } else {
        logger.trace("   >> >> edge album is null");
    }
}

executor.outFields["report"] = "edge ctxt :: added edge " + ifEdgeName;

logger.info("vpnsla: ctxt added edge " + ifEdgeName);

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);
logger.trace("finished: " + executor.subject.id);
logger.debug(".");
