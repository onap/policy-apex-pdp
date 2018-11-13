/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
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

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

import java.util.Map;
import java.util.Set;

public class AxStateParamsBuilder {
    private AxReferenceKey key;
    private AxArtifactKey trigger;
    private Map<String, AxStateOutput> stateOutputs;
    private Set<AxArtifactKey> contextAlbumReferenceSet;
    private AxTaskSelectionLogic taskSelectionLogic;
    private Map<String, AxStateFinalizerLogic> stateFinalizerLogicMap;
    private AxArtifactKey defaultTask;
    private Map<AxArtifactKey, AxStateTaskReference> taskReferenceMap;

    public AxReferenceKey getKey() {
        return key;
    }

    public AxArtifactKey getTrigger() {
        return trigger;
    }

    public Map<String, AxStateOutput> getStateOutputs() {
        return stateOutputs;
    }

    public Set<AxArtifactKey> getContextAlbumReferenceSet() {
        return contextAlbumReferenceSet;
    }

    public AxTaskSelectionLogic getTaskSelectionLogic() {
        return taskSelectionLogic;
    }

    public Map<String, AxStateFinalizerLogic> getStateFinalizerLogicMap() {
        return stateFinalizerLogicMap;
    }

    public AxArtifactKey getDefaultTask() {
        return defaultTask;
    }

    public Map<AxArtifactKey, AxStateTaskReference> getTaskReferenceMap() {
        return taskReferenceMap;
    }

    /**
     * Setter method
     *
     * @param key the reference key of the state
     * @return builder object
     */
    public AxStateParamsBuilder key(AxReferenceKey key) {
        this.key = key;
        return this;
    }

    /**
     * Setter method
     *
     * @param trigger the event that triggers the state
     * @return builder object
     */
    public AxStateParamsBuilder trigger(AxArtifactKey trigger) {
        this.trigger = trigger;
        return this;
    }

    /**
     * Setter method
     *
     * @param stateOutputs the possible state outputs for the state
     * @return builder object
     */
    public AxStateParamsBuilder stateOutputs(Map<String, AxStateOutput> stateOutputs) {
        this.stateOutputs = stateOutputs;
        return this;
    }

    /**
     * Setter method
     *
     * @param contextAlbumReferenceSet the context album reference set defines the context that may
     *                                 be used by Task Selection Logic and State Finalizer Logic in the state
     * @return builder object
     */
    public AxStateParamsBuilder contextAlbumReferenceSet(Set<AxArtifactKey> contextAlbumReferenceSet) {
        this.contextAlbumReferenceSet = contextAlbumReferenceSet;
        return this;
    }

    /**
     * Setter method
     *
     * @param taskSelectionLogic the task selection logic that selects the task a state executes in
     *                           an execution cycle
     * @return builder object
     */
    public AxStateParamsBuilder taskSelectionLogic(AxTaskSelectionLogic taskSelectionLogic) {
        this.taskSelectionLogic = taskSelectionLogic;
        return this;
    }

    /**
     * Setter method
     *
     * @param stateFinalizerLogicMap the state finalizer logic instances that selects the state
     *                               output to use after a task executes in a state execution cycle
     * @return builder object
     */
    public AxStateParamsBuilder stateFinalizerLogicMap(
            Map<String, AxStateFinalizerLogic> stateFinalizerLogicMap) {
        this.stateFinalizerLogicMap = stateFinalizerLogicMap;
        return this;
    }

    /**
     * Setter method
     *
     * @param defaultTask the default task that will execute in a state if Task Selection Logic is
     *                    not specified
     * @return builder object
     */
    public AxStateParamsBuilder defaultTask(AxArtifactKey defaultTask) {
        this.defaultTask = defaultTask;
        return this;
    }

    /**
     * Setter method
     *
     * @param taskReferenceMap the task reference map that defines the tasks for the state and how
     * @return builder object
     */
    public AxStateParamsBuilder taskReferenceMap(Map<AxArtifactKey, AxStateTaskReference> taskReferenceMap) {
        this.taskReferenceMap = taskReferenceMap;
        return this;
    }
}
