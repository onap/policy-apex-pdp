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

var vcpeClosedLoopStatus = null;
if (executor.inFields.get("vnfID") == null) {
    executor.logger.info("AbatedTask: vnfID is null");
    var vnfName = executor.inFields.get("vnfName");
    vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum")
            .get(executor.inFields.get("vnfName"));
} else {
    vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum")
            .get(executor.inFields.get("vnfID").toString());
}

vcpeClosedLoopStatus.put("notificationTime", java.lang.Long.valueOf(Date.now()));

var message = vcpeClosedLoopStatus.get("message");
if (message == null || message == "") {
    vcpeClosedLoopStatus.put("message", "situation has been abated");
}
else {
    vcpeClosedLoopStatus.put("notification", "FINAL_FAILURE");
}

executor.logger.info(executor.outFields);

true;
