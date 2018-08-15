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

package org.onap.policy.apex.core.engine.executor.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;

/**
 * The Class AxStateFacade acts as a facade into the AxState class so that task logic can easily
 * access information in an AxState instance.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class AxStateFacade {
    // CHECKSTYLE:OFF: checkstyle:visibilityModifier Logic has access to this field

    /** The full definition information for the state. */
    public final AxState state;

    // CHECKSTYLE:ON: checkstyle:visibilityModifier

    /**
     * Instantiates a new AxState facade.
     *
     * @param state the state for which a facade is being presented
     */
    public AxStateFacade(final AxState state) {
        this.state = state;
    }

    /**
     * Gets the default task key of the state.
     *
     * @return the default task key
     */
    public AxArtifactKey getDefaultTaskKey() {
        return state.getDefaultTask();
    }

    /**
     * Gets the ID of the state.
     *
     * @return the ID
     */
    public String getId() {
        return state.getKey().getID();
    }

    /**
     * Gets the name of the state.
     *
     * @return the state name
     */
    public String getStateName() {
        return state.getKey().getLocalName();
    }

    /**
     * Check if a task is defined for a given task name on a state and, if so, return its key.
     *
     * @param taskName the name of the task to get
     * @return the task key or null if it does not exist
     */
    public AxArtifactKey getTaskKey(final String taskName) {
        if (taskName == null) {
            return null;
        }

        return ModelService.getModel(AxTasks.class).get(taskName).getKey();
    }

    /**
     * Check if a task is defined for a given task name on a state and, if so, return its key.
     *
     * @return unmodifiable list of names of tasks available
     */
    public List<String> getTaskNames() {
        final Set<AxArtifactKey> tasks = state.getTaskReferences().keySet();
        final List<String> ret = new ArrayList<>(tasks.size());
        for (final AxArtifactKey task : tasks) {
            ret.add(task.getName());
        }
        return Collections.unmodifiableList(ret);
    }
}
