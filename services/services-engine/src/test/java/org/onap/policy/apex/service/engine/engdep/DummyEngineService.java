/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.engdep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;

/**
 * A dummy engine service class.
 */
public class DummyEngineService implements EngineService {

    private AxArtifactKey startEngineKey;
    private AxArtifactKey stopEngineKey;
    private long periodicPeriod;
    private AxArtifactKey statusKey;
    private AxArtifactKey runtimeInfoKey;
    private int modelKeyGetCalled;
    private AxArtifactKey updateModelKey;

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#registerActionListener(java.lang.String,
     * org.onap.policy.apex.service.engine.runtime.ApexEventListener)
     */
    @Override
    public void registerActionListener(String listenerName, ApexEventListener listener) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#deregisterActionListener(java.lang.String)
     */
    @Override
    public void deregisterActionListener(String listenerName) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getEngineServiceEventInterface()
     */
    @Override
    public EngineServiceEventInterface getEngineServiceEventInterface() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return new AxArtifactKey("DummyEngineService:0.0.1");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getEngineKeys()
     */
    @Override
    public Collection<AxArtifactKey> getEngineKeys() {
        List<AxArtifactKey> keys = new ArrayList<>();
        keys.add(new AxArtifactKey("DummyEngineService:0.0.1"));
        return keys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getApexModelKey()
     */
    @Override
    public AxArtifactKey getApexModelKey() {
        modelKeyGetCalled++;
        
        return new AxArtifactKey("DummyApexModelKey:0.0.1");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#updateModel(org.onap.policy.apex.model.basicmodel.
     * concepts.AxArtifactKey, java.lang.String, boolean)
     */
    @Override
    public void updateModel(AxArtifactKey engineServiceKey, String apexModelString, boolean forceFlag)
                    throws ApexException {
        updateModelKey = engineServiceKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#updateModel(org.onap.policy.apex.model.basicmodel.
     * concepts.AxArtifactKey, org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel, boolean)
     */
    @Override
    public void updateModel(AxArtifactKey engineServiceKey, AxPolicyModel apexModel, boolean forceFlag)
                    throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getState()
     */
    @Override
    public AxEngineState getState() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#startAll()
     */
    @Override
    public void startAll() throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#start(org.onap.policy.apex.model.basicmodel.concepts.
     * AxArtifactKey)
     */
    @Override
    public void start(AxArtifactKey engineKey) throws ApexException {
        startEngineKey = engineKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stop()
     */
    @Override
    public void stop() throws ApexException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#stop(org.onap.policy.apex.model.basicmodel.concepts.
     * AxArtifactKey)
     */
    @Override
    public void stop(AxArtifactKey engineKey) throws ApexException {
        stopEngineKey = engineKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#clear()
     */
    @Override
    public void clear() throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#clear(org.onap.policy.apex.model.basicmodel.concepts.
     * AxArtifactKey)
     */
    @Override
    public void clear(AxArtifactKey engineKey) throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStarted()
     */
    @Override
    public boolean isStarted() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStarted(org.onap.policy.apex.model.basicmodel.
     * concepts.AxArtifactKey)
     */
    @Override
    public boolean isStarted(AxArtifactKey engineKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStopped()
     */
    @Override
    public boolean isStopped() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStopped(org.onap.policy.apex.model.basicmodel.
     * concepts.AxArtifactKey)
     */
    @Override
    public boolean isStopped(AxArtifactKey engineKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#startPeriodicEvents(long)
     */
    @Override
    public void startPeriodicEvents(long period) throws ApexException {
        periodicPeriod = period;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stopPeriodicEvents()
     */
    @Override
    public void stopPeriodicEvents() throws ApexException {
        periodicPeriod = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getStatus(org.onap.policy.apex.model.basicmodel.
     * concepts.AxArtifactKey)
     */
    @Override
    public String getStatus(AxArtifactKey engineKey) throws ApexException {
        statusKey = engineKey;
        return "The Status";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#getRuntimeInfo(org.onap.policy.apex.model.basicmodel.
     * concepts.AxArtifactKey)
     */
    @Override
    public String getRuntimeInfo(AxArtifactKey engineKey) throws ApexException {
        runtimeInfoKey = engineKey;
        return "The Runtime Info";
    }

    public AxArtifactKey getStartEngineKey() {
        return startEngineKey;
    }

    public AxArtifactKey getStopEngineKey() {
        return stopEngineKey;
    }

    public long getPeriodicPeriod() {
        return periodicPeriod;
    }

    public AxArtifactKey getStatusKey() {
        return statusKey;
    }

    public AxArtifactKey getRuntimeInfoKey() {
        return runtimeInfoKey;
    }

    public int getModelKeyGetCalled() {
        return modelKeyGetCalled;
    }

    public AxArtifactKey getUpdateModelKey() {
        return updateModelKey;
    }
}
