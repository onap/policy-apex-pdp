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

package org.onap.policy.apex.core.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * This class provides the configurable parameters for Apex Tasks.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
@Getter
@Setter
public class TaskParameters {
    // If taskId is not specified, then the taskParameter is added to all tasks in the engine.
    private String taskId;

    @NotNull
    private String key;
    @NotNull
    private String value;

    public TaskParameters(String key, String value, String taskId) {
        this.key = key;
        this.value = value;
        this.taskId = taskId;
    }

    public boolean isValid() {
        return !StringUtils.isBlank(key) && !StringUtils.isEmpty(value);
    }
}
