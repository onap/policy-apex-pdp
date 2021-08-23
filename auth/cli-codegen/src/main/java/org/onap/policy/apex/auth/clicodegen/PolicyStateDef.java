/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung Electronics Co., Ltd. All rights reserved.
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

package org.onap.policy.apex.auth.clicodegen;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.stringtemplate.v4.ST;

@Getter
@Builder
public class PolicyStateDef {

    private String policyName;
    private String version;
    private String stateName;
    private String triggerName;
    private String triggerVersion;
    private String defaultTask;
    private String defaultTaskVersion;
    private List<ST> outputs;
    private List<ST> tasks;
    private List<ST> tsLogic;
    private List<ST> finalizerLogics;
    private List<ST> ctxRefs;
}
