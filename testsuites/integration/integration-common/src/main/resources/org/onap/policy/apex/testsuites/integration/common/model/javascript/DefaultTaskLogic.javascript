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

executor.logger.debug(executor.subject.id);
var gc = executor.getContextAlbum("GlobalContextAlbum");
executor.logger.debug(gc.name);
executor.logger.debug(executor.inFields);

var caseSelectedType = Java.type("java.lang.Byte");
executor.outFields.put("Test<STATE_NAME>CaseSelected", new caseSelectedType(<RANDOM_BYTE_VALUE>));

executor.outFields.put("Test<STATE_NAME>StateTime", java.lang.System.nanoTime());
executor.logger.debug(executor.eo);

var returnValue = executor.isTrue;
