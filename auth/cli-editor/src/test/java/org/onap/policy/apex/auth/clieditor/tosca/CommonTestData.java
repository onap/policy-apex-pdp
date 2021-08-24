/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.auth.clieditor.tosca;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class to hold/create all parameters for test cases.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonTestData {

    public static final String INPUT_TOSCA_TEMPLATE_FILE_NAME = "src/test/resources/tosca/ToscaTemplate.json";
    public static final String APEX_CONFIG_FILE_NAME = "src/test/resources/tosca/ApexConfig.json";
    public static final String COMMAND_FILE_NAME = "src/test/resources/tosca/PolicyModel.apex";
    public static final String POLICY_MODEL_FILE_NAME = "src/test/resources/tosca/PolicyModel.json";
}
