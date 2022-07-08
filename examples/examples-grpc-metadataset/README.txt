/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2022 Bell Canada. All rights reserved.
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

This module serves as an example for generating metadataSets for policies in a separate Tosca template with APEX CLI editor.
The generated metadataSet file that comprises policy model, can be persisted in the database via policy API endpoints.
A tosca policy can also be generated independently without policy_type_impl entity embedded in it.

The output files generated are :
1. Policy model json file: {BASE_DIR}/target/classes/APEXgRPCPolicy.json
2. Tosca policy json file excluding policy_type_impl: {BASE_DIR}/target/classes/APEXgRPCToscaPolicy.json
3. Tosca metadataSet json file containing policy model as tosca node templates: {BASE_DIR}/target/classes/APEXgRPCToscaPolicy.metadataSet.json