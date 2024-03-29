/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.parameters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class holds constants used when managing parameter groups in apex.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApexParameterConstants {
    public static final String MAIN_GROUP_NAME = "APEX_PARAMETERS";
    public static final String ENGINE_SERVICE_GROUP_NAME = "ENGINE_SERVICE_PARAMETERS";
    public static final String EVENT_HANDLER_GROUP_NAME = "EVENT_HANDLER_PARAMETERS";
}
