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

package org.onap.policy.apex.service.engine.runtime;

import java.util.Collection;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * The administration interface for Apex engine users. Apex engine implementations expose this
 * interface and external users use it to manage Apex engines.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public interface EngineService {
    /**
     * A method to attach a listener to the engine.
     *
     * @param listenerName a unique name for the listener
     * @param listener is a callback interface to the engine.
     */
    void registerActionListener(String listenerName, ApexEventListener listener);

    /**
     * A method to detach a listener from the engine.
     *
     * @param listenerName the unique name of the listener to deregister
     */
    void deregisterActionListener(String listenerName);

    /**
     * This method gets the current runtime information for the running Apex engines.
     *
     * @return the engine service event interface
     */
    EngineServiceEventInterface getEngineServiceEventInterface();

    /**
     * Gets the key of the engine service or worker.
     *
     * @return the key
     */
    AxArtifactKey getKey();

    /**
     * This method gets the keys of the engines on the engine service.
     *
     * @return the engine keys
     */
    Collection<AxArtifactKey> getEngineKeys();

    /**
     * The the key of the Apex model the engine service is running on.
     *
     * @return the Apex model key
     */
    AxArtifactKey getApexModelKey();

    /**
     * This method updates the Apex model on Apex execution engines using a string representation of
     * the model.
     *
     * @param engineServiceKey The key of the engine service on which to update the model
     * @param apexModelString the apex model string
     * @param forceFlag if true, model updates will be executed even on incompatible models
     *        (different model names) and versions (different model version)
     * @throws ApexException on model update errors
     */
    void updateModel(AxArtifactKey engineServiceKey, String apexModelString, boolean forceFlag) throws ApexException;

    /**
     * This method updates the Apex model on Apex execution engines using a policy model as input.
     *
     * @param engineServiceKey The key of the engine service on which to update the model
     * @param apexModel is a policy definition model
     * @param forceFlag if true, model updates will be executed even on incompatible models
     *        (different model names) and versions (different model version)
     * @throws ApexException on model update errors
     */
    void updateModel(AxArtifactKey engineServiceKey, AxPolicyModel apexModel, boolean forceFlag) throws ApexException;

    /**
     * This method returns the state of an engine service or engine.
     *
     * @return The engine service or engine state
     */
    AxEngineState getState();

    /**
     * This method starts all Apex engines in the engine service.
     *
     * @throws ApexException on start errors
     */
    void startAll() throws ApexException;

    /**
     * This method starts an Apex engine in the engine service.
     *
     * @param engineKey The key of the Apex engine to start
     * @throws ApexException on start errors
     */
    void start(AxArtifactKey engineKey) throws ApexException;

    /**
     * This method stops all Apex engines in the engine service.
     *
     * @throws ApexException on stop errors
     */
    void stop() throws ApexException;

    /**
     * This method stops an Apex engine in the engine service.
     *
     * @param engineKey The key of the Apex engine to stop
     * @throws ApexException on stop errors
     */
    void stop(AxArtifactKey engineKey) throws ApexException;

    /**
     * This method checks if all Apex engines in the engine service are started.
     * 
     * <p>Note: an engine can be both not stopped and not started, for example, when it is starting or
     * stopping
     *
     * @return true if all Apex engines in the engine service are started.
     */
    boolean isStarted();

    /**
     * This method checks if an Apex engine in the engine service is started.
     * 
     * <p>Note: an engine can be both not stopped and not started, for example, when it is starting or
     * stopping
     *
     * @param engineKey The key of the Apex engine to check
     * @return true if all Apex engines in the engine service are started.
     */
    boolean isStarted(AxArtifactKey engineKey);

    /**
     * This method checks if all Apex engines in the engine service are stopped.
     * 
     * <p>Note: an engine can be both not stopped and not started, for example, when it is starting or
     * stopping
     *
     * @return true if all Apex engines in the engine service are stopped.
     */
    boolean isStopped();

    /**
     * This method checks if an Apex engine in the engine service is stopped.
     * 
     * <p>Note: an engine can be both not stopped and not started, for example, when it is starting or
     * stopping
     *
     * @param engineKey The key of the Apex engine to check
     * @return true if all Apex engines in the engine service are stopped.
     */
    boolean isStopped(AxArtifactKey engineKey);

    /**
     * This method starts periodic event generation.
     *
     * @param period The period in milliseconds between periodic events
     * @throws ApexException On periodic event start errors
     */
    void startPeriodicEvents(long period) throws ApexException;

    /**
     * This method stops periodic event generation.
     *
     * @throws ApexException On periodic event stop errors
     */
    void stopPeriodicEvents() throws ApexException;

    /**
     * This method gets the status of an Apex engine in the engine service.
     *
     * @param engineKey the engine key
     * @return the engine runtime information
     * @throws ApexException on status read errors
     */
    String getStatus(AxArtifactKey engineKey) throws ApexException;

    /**
     * This method gets the runtime information of all Apex engines in the engine service.
     *
     * @param engineKey the engine key
     * @return the engine runtime information
     * @throws ApexException on runtime information read errors
     */
    String getRuntimeInfo(AxArtifactKey engineKey) throws ApexException;
}
