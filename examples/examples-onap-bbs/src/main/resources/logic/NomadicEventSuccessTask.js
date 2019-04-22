/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Huawei. All rights reserved.
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
importClass(org.apache.avro.Schema);

executor.logger.info("Begin Execution NomadicEventSuccess.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var NomadicONTContext = executor.getContextAlbum("NomadicONTContextAlbum").get(
    attachmentPoint);

executor.logger.info(executor.outFields);
executor.logger.info(executor.inFields);

result = NomadicONTContext.get("result");

if (result === "SUCCESS") {
    returnValue = executor.isTrue;
    executor.outFields.put("result", "SUCCCESS");
    executor.logger.info("BBS policy Execution Done");
} else {
    executor.logger.info("BBS policy Execution Failed");
    executor.outFields.put("result", "FAILURE");
    returnValue = executor.isFalse;
}

var returnValue = executor.isTrue;
executor.logger.info("End Execution NomadicEventSuccess.js");