/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

var vnfId = executor.inFields.get("vnfID").toString();
var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(vnfId);
var serviceId = vcpeClosedLoopStatus.get("AAI").get("genericVnfServiceId");
var blackFlag = executor.getContextAlbum("ServiceIdWhiteBlackListAlbum").get(serviceId.toString());

executor.logger.info("vnfId=" + vnfId + ", serviceId=" + serviceId + ", blackFlag=" + blackFlag);

if (blackFlag != null && blackFlag === true) {
    vcpeClosedLoopStatus.put("notificationTime", java.lang.System.currentTimeMillis());
    vcpeClosedLoopStatus.put("notification", "BLACKLIST");
    var message = vcpeClosedLoopStatus.get("message");

    if (message != null) {
        vcpeClosedLoopStatus.put("message", message + ":Service ID blacklisted");
    }
    else {
        vcpeClosedLoopStatus.put("message", "Service ID blacklisted");
    }
}

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
