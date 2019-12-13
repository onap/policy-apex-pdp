/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.engdep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void registerActionListener(String listenerName, ApexEventListener listener) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void deregisterActionListener(String listenerName) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public EngineServiceEventInterface getEngineServiceEventInterface() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getKey() {
        return new AxArtifactKey("DummyEngineService:0.0.1");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<AxArtifactKey> getEngineKeys() {
        List<AxArtifactKey> keys = new ArrayList<>();
        keys.add(new AxArtifactKey("DummyEngineService:0.0.1"));
        return keys;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getApexModelKey() {
        modelKeyGetCalled++;

        return new AxArtifactKey("DummyApexModelKey:0.0.1");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateModel(AxArtifactKey engineServiceKey, String apexModelString, boolean forceFlag)
                    throws ApexException {
        updateModelKey = engineServiceKey;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void updateModel(AxArtifactKey engineServiceKey, AxPolicyModel apexModel, boolean forceFlag)
                    throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxEngineState getState() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxEngineModel> getEngineStats() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void startAll() throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void start(AxArtifactKey engineKey) throws ApexException {
        startEngineKey = engineKey;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() throws ApexException {
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop(AxArtifactKey engineKey) throws ApexException {
        stopEngineKey = engineKey;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clear() throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clear(AxArtifactKey engineKey) throws ApexException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isStarted() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isStarted(AxArtifactKey engineKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isStopped() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isStopped(AxArtifactKey engineKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void startPeriodicEvents(long period) throws ApexException {
        periodicPeriod = period;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stopPeriodicEvents() throws ApexException {
        periodicPeriod = 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getStatus(AxArtifactKey engineKey) throws ApexException {
        statusKey = engineKey;
        return "The Status";
    }

    /**
     * {@inheritDoc}.
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
