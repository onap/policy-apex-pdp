/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationResult;

/**
 * This class holds the parameters for a single Apex engine. This parameter class holds parameters for context schemas
 * and context albums for the engine and a map of the logic flavour executors defined for the engine and the parameters
 * for each of those executors.
 *
 * <p>The context parameters for the engine are held in a {@link ContextParameters} instance. This instance holds the
 * parameters for context schema handling that will be used by the engine as well as the context album distribution,
 * locking, and persistence parameters.
 *
 * <p>In Apex, an engine can be configured to use many logic flavours. The executors for each logic flavour are
 * identified by their name. Each logic flavour executor must have an instance of {@link ExecutorParameters} defined for
 * it, which specifies the executor plugins to use for that logic flavour executor and specific parameters for those
 * executor plugins.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
public class EngineParameters implements ParameterGroup {
    private ContextParameters contextParameters = new ContextParameters();

    // Parameter group name
    private String name;

    // A map of parameters for executors of various logic types
    private Map<String, ExecutorParameters> executorParameterMap = new TreeMap<>();

    // A list of parameters to be passed to the task, so that they can be used in the logic
    private List<TaskParameters> taskParameters = new ArrayList<>();

    /**
     * Constructor to create an engine parameters instance and register the instance with the parameter service.
     */
    public EngineParameters() {
        super();

        // Set the name for the parameters
        this.name = EngineParameterConstants.MAIN_GROUP_NAME;
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);

        result.setResult("contextParameters", contextParameters.validate());

        for (Entry<String, ExecutorParameters> executorParEntry : executorParameterMap.entrySet()) {
            result.setResult("executorParameterMap", executorParEntry.getKey(), executorParEntry.getValue().validate());
        }
        for (TaskParameters taskParam : taskParameters) {
            ValidationResult taskParamValidationResult = taskParam.validate("taskParameters");
            result.setResult(taskParamValidationResult.getName(), taskParamValidationResult.getStatus(),
                taskParamValidationResult.getResult());
        }
        return result;
    }


}
