/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

var pmSubscriptionInfo = executor.getContextAlbum("PMSubscriptionAlbum").get(executor.inFields.get("albumID").toString())
var changeType = pmSubscriptionInfo.get("changeType").toString()

if ("CREATE".equals(changeType)) {
    executor.subject.getTaskKey("CreateSubscriptionPayloadTask").copyTo(executor.selectedTask);
}
else if ("DELETE".equals(changeType)) {
    executor.subject.getTaskKey("DeleteSubscriptionPayloadTask").copyTo(executor.selectedTask);
}

true;