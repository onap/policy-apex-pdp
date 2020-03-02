/*-
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
executor.logger.info(executor.subject.getId());
var gc = executor.getContextAlbum("BasicContextAlbum");
executor.logger.info(gc.getName());
executor.logger.info("incoming value: " + executor.inFields.get("intPar").toString());

var intPar = executor.inFields.get("intPar");

executor.logger.info("read value: " + intPar.toString());

var intParBy2 = intPar * 2;

executor.outFields.put("intPar", intParBy2);

executor.logger.info("outgoing value: " + executor.outFields.get("intPar").toString());

var returnValue = executor.isTrue;