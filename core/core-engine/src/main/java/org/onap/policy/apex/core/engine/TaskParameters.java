/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.core.engine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * This class provides the configurable parameters for Apex Tasks.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskParameters {
    private String name = "taskParameters";

    // If taskId is not specified, then the taskParameter is added to all tasks in the engine.
    private String taskId;

    @NotNull
    @NotBlank
    private String key;
    @NotNull
    @NotBlank
    private String value;

    /**
     * Full constructor.
     *
     * @param key the task parameter key
     * @param value the task parameter value
     * @param taskId the task ID of this task parameter
     */
    public TaskParameters(String key, String value, String taskId) {
        this();
        this.key = key;
        this.value = value;
        this.taskId = taskId;
    }

    /**
     * Validates the parameters.
     *
     * @param resultName name of the result
     *
     * @return the validation result
     */
    public ValidationResult validate(String resultName) {
        return new BeanValidator().validateTop(resultName, this);
    }
}
