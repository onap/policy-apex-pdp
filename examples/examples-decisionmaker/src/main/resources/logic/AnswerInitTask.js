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

var answerAlbum = executor.getContextAlbum("AnswerAlbum");

answerAlbum.put("a0", executor.inFields.get("a0"));
answerAlbum.put("a1", executor.inFields.get("a1"));
answerAlbum.put("a2", executor.inFields.get("a2"));
answerAlbum.put("a3", executor.inFields.get("a3"));
answerAlbum.put("a4", executor.inFields.get("a4"));
answerAlbum.put("a5", executor.inFields.get("a5"));
answerAlbum.put("a6", executor.inFields.get("a6"));

var lastAnswerAlbum = executor.getContextAlbum("LastAnswerAlbum");
lastAnswerAlbum.put("lastAnswer", answerAlbum.size() - 1);

executor.outFields.put("a0", answerAlbum.get("a0"));
executor.outFields.put("a1", answerAlbum.get("a1"));
executor.outFields.put("a2", answerAlbum.get("a2"));
executor.outFields.put("a3", answerAlbum.get("a3"));
executor.outFields.put("a4", answerAlbum.get("a4"));
executor.outFields.put("a5", answerAlbum.get("a5"));
executor.outFields.put("a6", answerAlbum.get("a6"));

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
