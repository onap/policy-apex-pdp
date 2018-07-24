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

var size = executor.getContextAlbum("AnswerAlbum").size();

var selection = Math.floor(Math.random() * size / 2);

var selectionA = "a" + selection;

executor.logger.info(size);
executor.logger.info(selectionA);

executor.outFields.put("decision", executor.getContextAlbum("AnswerAlbum").get(selectionA));

executor.logger.info(executor.outFields);

var returnValue = executor.TRUE;