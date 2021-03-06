/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

// Pass through to the log state

executor.logger.info(executor.outFields);

var targetType = executor.inFields.get("targetType");
var target = executor.inFields.get("target");
var black = executor.inFields.get("black");

var returnValue = true;

if (targetType == "VNF") {
    executor.getContextAlbum("VnfIdWhiteBlackListAlbum").put(target, black);
    executor.logger.info("added VNF ID \"" + target + "\" with black flag \"" + black + "\" to VNF ID list");
}
else if (targetType == "Service") {
    executor.getContextAlbum("ServiceIdWhiteBlackListAlbum").put(target, black);
    executor.logger.info("added Service ID \"" + target + "\" with black flag \"" + black + "\" to Service ID list");
}
else if (targetType == "VServer") {
    executor.getContextAlbum("VServerIdWhiteBlackListAlbum").put(target, black);
    executor.logger.info("added VServer ID \"" + target + "\" with black flag \"" + black + "\" to VServer ID list");
}
else {
    executor.logger.warn("unknown target type \"" + targetType + "\" specified");
    returnValue = false;
}

returnValue;

