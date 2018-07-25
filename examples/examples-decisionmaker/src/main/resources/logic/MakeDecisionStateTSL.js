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

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var returnValue = executor.TRUE;

if (executor.inFields.get("mode").equals("random")) {
    executor.subject.getTaskKey("RandomAnswerTask").copyTo(executor.selectedTask);
}
else if (executor.inFields.get("mode").equals("pessimistic")) {
    executor.subject.getTaskKey("PessimisticAnswerTask").copyTo(executor.selectedTask);
}
else if (executor.inFields.get("mode").equals("optimistic")) {
    executor.subject.getTaskKey("OptimisticAnswerTask").copyTo(executor.selectedTask);
}
else if (executor.inFields.get("mode").equals("dithering")) {
    executor.subject.getTaskKey("DitheringAnswerTask").copyTo(executor.selectedTask);
}
//else if (executor.inFields.get("mode").equals("roundrobin")) {
//    executor.subject.getTaskKey("RoundRobinAnswerTask").copyTo(executor.selectedTask);
//}

executor.logger.info("Answer Selected Task:" + executor.selectedTask);