/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
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

executor.logger.info("Begin Execution NomadicEventSuccess.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(
    attachmentPoint);

executor.logger.info(executor.outFields);
executor.logger.info(executor.inFields);

result = NomadicONTContext.get("result");
var returnValue = true;

if (result == "SUCCESS") {
    executor.outFields.put("result", "SUCCCESS");
    executor.logger.info("BBS policy Execution Done");
} else {
    executor.logger.info("BBS policy Execution Failed");
    executor.outFields.put("result", "FAILURE");
    returnValue = false;
}

executor.logger.info("End Execution NomadicEventSuccess.js");

returnValue;
