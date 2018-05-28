/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.model.policymodel.concepts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This enumeration defines the type of state output selection that is defined for a task in a
 * state. The {@link AxStateTaskReference} instance for each task uses this enumeration to decide
 * what type of output selection to use when a task has completed execution.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxStateTaskOutputType", namespace = "http://www.onap.org/policy/apex-pdp")

public enum AxStateTaskOutputType {
    /** The state output selection for the task has not been defined. */
    UNDEFINED,
    /**
     * Direct state output selection has been selected, the task will select a {@link AxStateOutput}
     * directly.
     */
    DIRECT,
    /**
     * Logic state output selection has been selected, the task will select a {@link AxStateOutput}
     * using logic defined in a {@link AxStateFinalizerLogic} instance.
     */
    LOGIC
}
