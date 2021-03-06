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
executor.logger.info("hello world");

executor.logger.info(executor.subject.getId());
var gc = executor.getContextAlbum("TestContextAlbum");
executor.logger.info(gc.getName());
executor.logger.info(executor.inFields.get("par0"));

executor.outFields.put("par0", "returnVal0");
executor.outFields.put("par1", "returnVal1");

executor.logger.info(executor.outFields.get("par0"));
executor.logger.info(executor.outFields.get("par1"));

true;
