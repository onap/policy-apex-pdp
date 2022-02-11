/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020,2022 Nordix Foundation.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Valid;

/**
 * This class holds the parameters for a single Apex engine. This parameter class holds parameters for context schemas
 * and context albums for the engine and a map of the logic flavour executors defined for the engine and the parameters
 * for each of those executors.
 *
 * <p>The context parameters for the engine are held in a {@link ContextParameters} instance. This instance holds the
 * parameters for context schema handling that will be used by the engine as well as the context album distribution and
 * locking parameters.
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
@NotNull
public class EngineParameters extends ParameterGroupImpl {
    private @Valid ContextParameters contextParameters = new ContextParameters();


    // A map of parameters for executors of various logic types
    private Map<String, @NotNull @Valid ExecutorParameters> executorParameterMap = new TreeMap<>();

    // A list of parameters to be passed to the task, so that they can be used in the logic
    private List<@NotNull @Valid TaskParameters> taskParameters = new ArrayList<>();

    /**
     * Constructor to create an engine parameters instance and register the instance with the parameter service.
     */
    public EngineParameters() {
        super(EngineParameterConstants.MAIN_GROUP_NAME);
    }
}
