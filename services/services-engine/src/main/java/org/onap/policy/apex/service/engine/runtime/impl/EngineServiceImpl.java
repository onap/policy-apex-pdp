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

package org.onap.policy.apex.service.engine.runtime.impl;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexPeriodicEventGenerator;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class EngineServiceImpl controls a thread pool that runs a set of Apex engine workers, each of which is running
 * on an identical Apex model. This class handles the management of the engine worker instances, their threads, and
 * event forwarding to and from the engine workers.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @author John Keeney (john.keeney@ericsson.com)
 */
public final class EngineServiceImpl implements EngineService, EngineServiceEventInterface {
    // Logging static variables
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngineServiceImpl.class);
    private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

    // Recurring string constants
    private static final String ENGINE_KEY_PREAMBLE = "engine with key ";
    private static final String NOT_FOUND_SUFFIX = " not found in engine service";
    private static final String ENGINE_KEY_NOT_SPECIFIED = "engine key must be specified and may not be null";

    // Constants for timing
    private static final long MAX_START_WAIT_TIME = 5000; // 5 seconds
    private static final long MAX_STOP_WAIT_TIME = 5000; // 5 seconds
    private static final int ENGINE_SERVICE_STOP_START_WAIT_INTERVAL = 200;

    // The ID of this engine
    private AxArtifactKey engineServiceKey = null;

    // The Apex engine workers this engine service is handling
    private final Map<AxArtifactKey, EngineService> engineWorkerMap = Collections
                    .synchronizedMap(new LinkedHashMap<AxArtifactKey, EngineService>());

    // Event queue for events being sent into the Apex engines, it used by all engines within a
    // group.
    private final BlockingQueue<ApexEvent> queue = new LinkedBlockingQueue<>();

    // Thread factory for thread management
    private final ApplicationThreadFactory atFactory = new ApplicationThreadFactory("apex-engine-service", 512);

    // Periodic event generator and its period in milliseconds
    private ApexPeriodicEventGenerator periodicEventGenerator = null;
    private long periodicEventPeriod;

    /**
     * This constructor instantiates engine workers and adds them to the set of engine workers to be managed. The
     * constructor is private to prevent subclassing.
     *
     * @param engineServiceKey the engine service key
     * @param threadCount the thread count, the number of engine workers to start
     * @param periodicEventPeriod the period in milliseconds at which periodic events are generated
     * @throws ApexException on worker instantiation errors
     */
    private EngineServiceImpl(final AxArtifactKey engineServiceKey, final int threadCount,
                    final long periodicEventPeriod) {
        LOGGER.entry(engineServiceKey, threadCount);

        this.engineServiceKey = engineServiceKey;
        this.periodicEventPeriod = periodicEventPeriod;

        // Start engine workers
        for (int engineCounter = 0; engineCounter < threadCount; engineCounter++) {
            final AxArtifactKey engineWorkerKey = new AxArtifactKey(engineServiceKey.getName() + '-' + engineCounter,
                            engineServiceKey.getVersion());
            engineWorkerMap.put(engineWorkerKey, new EngineWorker(engineWorkerKey, queue, atFactory));
            LOGGER.info("Created apex engine {} .", engineWorkerKey.getId());
        }

        LOGGER.info("APEX service created.");
        LOGGER.exit();
    }

    /**
     * Create an Apex Engine Service instance. This method does not load the policy so
     * {@link #updateModel(AxArtifactKey, AxPolicyModel, boolean)} or
     * {@link #updateModel(AxArtifactKey, AxPolicyModel, boolean)} must be used to load a model. This method does not
     * start the Engine Service so {@link #start(AxArtifactKey)} or {@link #startAll()} must be used.
     *
     * @param config the configuration for this Apex Engine Service.
     * @return the Engine Service instance
     * @throws ApexException on worker instantiation errors
     */
    public static EngineServiceImpl create(final EngineServiceParameters config) throws ApexException {
        if (config == null) {
            LOGGER.warn("Engine service configuration parameters is null");
            throw new ApexException("engine service configuration parameters are null");
        }
        
        final GroupValidationResult validation = config.validate();
        if (!validation.isValid()) {
            LOGGER.warn("Invalid engine service configuration parameters: {}" + validation.getResult());
            throw new ApexException("Invalid engine service configuration parameters: " + validation);
        }
        
        final AxArtifactKey engineServiceKey = config.getEngineKey();
        final int threadCount = config.getInstanceCount();

        return new EngineServiceImpl(engineServiceKey, threadCount, config.getPeriodicEventPeriod());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#registerActionListener(java.lang. String,
     * org.onap.policy.apex.service.engine.runtime.ApexEventListener)
     */
    @Override
    public void registerActionListener(final String listenerName, final ApexEventListener apexEventListener) {
        LOGGER.entry(apexEventListener);

        if (listenerName == null) {
            String message = "listener name must be specified and may not be null";
            LOGGER.warn(message);
            return;
        }

        if (apexEventListener == null) {
            String message = "apex event listener must be specified and may not be null";
            LOGGER.warn(message);
            return;
        }

        // Register the Apex event listener on all engine workers, each worker will return Apex
        // events to the listening application
        for (final EngineService engineWorker : engineWorkerMap.values()) {
            engineWorker.registerActionListener(listenerName, apexEventListener);
        }

        LOGGER.info("Added the action listener to the engine");
        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#deregisterActionListener(java.lang. String)
     */
    @Override
    public void deregisterActionListener(final String listenerName) {
        LOGGER.entry(listenerName);

        // Register the Apex event listener on all engine workers, each worker will return Apex
        // events to the listening application
        for (final EngineService engineWorker : engineWorkerMap.values()) {
            engineWorker.deregisterActionListener(listenerName);
        }

        LOGGER.info("Removed the action listener from the engine");
        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getEngineServiceEventInterface()
     */
    @Override
    public EngineServiceEventInterface getEngineServiceEventInterface() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return engineServiceKey;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getInfo()
     */
    @Override
    public Collection<AxArtifactKey> getEngineKeys() {
        return engineWorkerMap.keySet();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getApexModelKey()
     */
    @Override
    public AxArtifactKey getApexModelKey() {
        if (engineWorkerMap.size() == 0) {
            return null;
        }

        return engineWorkerMap.entrySet().iterator().next().getValue().getApexModelKey();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#updateModel(org.onap.policy.apex.model.
     * basicmodel.concepts.AxArtifactKey, java.lang.String, boolean)
     */
    @Override
    public void updateModel(final AxArtifactKey incomingEngineServiceKey, final String apexModelString,
                    final boolean forceFlag) throws ApexException {
        // Check if the engine service key specified is sane
        if (incomingEngineServiceKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if the Apex model specified is sane
        if (apexModelString == null || apexModelString.trim().length() == 0) {
            String emptyModelMessage = "model for updating engine service with key "
                            + incomingEngineServiceKey.getId() + " is empty";
            LOGGER.warn(emptyModelMessage);
            throw new ApexException(emptyModelMessage);
        }

        // Read the Apex model into memory using the Apex Model Reader
        AxPolicyModel apexPolicyModel = null;
        try {
            final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
            apexPolicyModel = modelReader.read(new ByteArrayInputStream(apexModelString.getBytes()));
        } catch (final ApexModelException e) {
            String message = "failed to unmarshal the apex model on engine service " + incomingEngineServiceKey.getId();
            LOGGER.error(message, e);
            throw new ApexException(message, e);
        }

        // Update the model
        updateModel(incomingEngineServiceKey, apexPolicyModel, forceFlag);

        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#updateModel(org.onap.policy.apex.model.
     * basicmodel.concepts.AxArtifactKey, org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel, boolean)
     */
    @Override
    public void updateModel(final AxArtifactKey incomingEngineServiceKey, final AxPolicyModel apexModel,
                    final boolean forceFlag) throws ApexException {
        LOGGER.entry(incomingEngineServiceKey);

        // Check if the engine service key specified is sane
        if (incomingEngineServiceKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if the Apex model specified is sane
        if (apexModel == null) {
            LOGGER.warn("model for updating on engine service with key " + incomingEngineServiceKey.getId()
                            + " is null");
            throw new ApexException("model for updating on engine service with key " + incomingEngineServiceKey.getId()
                            + " is null");
        }

        // Check if the key on the update request is correct
        if (!this.engineServiceKey.equals(incomingEngineServiceKey)) {
            LOGGER.warn("engine service key " + incomingEngineServiceKey.getId() + " does not match the key"
                            + engineServiceKey.getId() + " of this engine service");
            throw new ApexException("engine service key " + incomingEngineServiceKey.getId() + " does not match the key"
                            + engineServiceKey.getId() + " of this engine service");
        }

        // Check model compatibility
        if (ModelService.existsModel(AxPolicyModel.class)) {
            // The current policy model may or may not be defined
            final AxPolicyModel currentModel = ModelService.getModel(AxPolicyModel.class);
            if (!currentModel.getKey().isCompatible(apexModel.getKey())) {
                handleIncompatibility(apexModel, forceFlag, currentModel);
            }
        }

        executeModelUpdate(incomingEngineServiceKey, apexModel, forceFlag);

        LOGGER.exit();
    }

    /**
     * Execute the model update on the engine instances.
     * 
     * @param incomingEngineServiceKey the engine service key to update
     * @param apexModel the model to update the engines with
     * @param forceFlag if true, ignore compatibility problems
     * @throws ApexException on model update errors
     */
    private void executeModelUpdate(final AxArtifactKey incomingEngineServiceKey, final AxPolicyModel apexModel,
                    final boolean forceFlag) throws ApexException {
        
        if (!isStopped()) {
            stopEngines(incomingEngineServiceKey);
        }

        // Update the engines
        for (final Entry<AxArtifactKey, EngineService> engineWorkerEntry : engineWorkerMap.entrySet()) {
            LOGGER.info("Registering apex model on engine {}", engineWorkerEntry.getKey().getId());
            engineWorkerEntry.getValue().updateModel(engineWorkerEntry.getKey(), apexModel, forceFlag);
        }

        // start all engines on this engine service if it was not stopped before the update
        startAll();
        final long starttime = System.currentTimeMillis();
        while (!isStarted() && System.currentTimeMillis() - starttime < MAX_START_WAIT_TIME) {
            ThreadUtilities.sleep(ENGINE_SERVICE_STOP_START_WAIT_INTERVAL);
        }
        // Check if all engines are running
        final StringBuilder notRunningEngineIdBuilder = new StringBuilder();
        for (final Entry<AxArtifactKey, EngineService> engineWorkerEntry : engineWorkerMap.entrySet()) {
            if (engineWorkerEntry.getValue().getState() != AxEngineState.READY
                            && engineWorkerEntry.getValue().getState() != AxEngineState.EXECUTING) {
                notRunningEngineIdBuilder.append(engineWorkerEntry.getKey().getId());
                notRunningEngineIdBuilder.append('(');
                notRunningEngineIdBuilder.append(engineWorkerEntry.getValue().getState());
                notRunningEngineIdBuilder.append(") ");
            }
        }
        if (notRunningEngineIdBuilder.length() > 0) {
            final String errorString = "engine start error on model update on engine service with key "
                            + incomingEngineServiceKey.getId() + ", engines not running are: "
                            + notRunningEngineIdBuilder.toString().trim();
            LOGGER.warn(errorString);
            throw new ApexException(errorString);
        }
    }

    /**
     * Stop engines for a model update.
     * @param incomingEngineServiceKey the engine service key for the engines that are to be stopped
     * @throws ApexException on errors stopping engines
     */
    private void stopEngines(final AxArtifactKey incomingEngineServiceKey) throws ApexException {
        // Stop all engines on this engine service
        stop();
        final long stoptime = System.currentTimeMillis();
        while (!isStopped() && System.currentTimeMillis() - stoptime < MAX_STOP_WAIT_TIME) {
            ThreadUtilities.sleep(ENGINE_SERVICE_STOP_START_WAIT_INTERVAL);
        }
        // Check if all engines are stopped
        final StringBuilder notStoppedEngineIdBuilder = new StringBuilder();
        for (final Entry<AxArtifactKey, EngineService> engineWorkerEntry : engineWorkerMap.entrySet()) {
            if (engineWorkerEntry.getValue().getState() != AxEngineState.STOPPED) {
                notStoppedEngineIdBuilder.append(engineWorkerEntry.getKey().getId());
                notStoppedEngineIdBuilder.append('(');
                notStoppedEngineIdBuilder.append(engineWorkerEntry.getValue().getState());
                notStoppedEngineIdBuilder.append(") ");
            }
        }
        if (notStoppedEngineIdBuilder.length() > 0) {
            final String errorString = "cannot update model on engine service with key "
                            + incomingEngineServiceKey.getId() + ", engines not stopped after " + MAX_STOP_WAIT_TIME
                            + "ms are: " + notStoppedEngineIdBuilder.toString().trim();
            LOGGER.warn(errorString);
            throw new ApexException(errorString);
        }
    }

    /**
     * Issue compatibility warning or error message.
     * @param apexModel The model name
     * @param forceFlag true if we are forcing the update
     * @param currentModel the existing model that is loaded
     * @throws ContextException on compatibility errors
     */
    private void handleIncompatibility(final AxPolicyModel apexModel, final boolean forceFlag,
                    final AxPolicyModel currentModel) throws ContextException {
        if (forceFlag) {
            LOGGER.warn("apex model update forced, supplied model with key \"" + apexModel.getKey().getId()
                            + "\" is not a compatible model update from the existing engine model with key \""
                            + currentModel.getKey().getId() + "\"");
        } else {
            throw new ContextException("apex model update failed, supplied model with key \""
                            + apexModel.getKey().getId()
                            + "\" is not a compatible model update from the existing engine model with key \""
                            + currentModel.getKey().getId() + "\"");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getState()
     */
    @Override
    public AxEngineState getState() {
        // If one worker is running then we are running, otherwise we are stopped
        for (final EngineService engine : engineWorkerMap.values()) {
            if (engine.getState() != AxEngineState.STOPPED) {
                return AxEngineState.EXECUTING;
            }
        }

        return AxEngineState.STOPPED;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#startAll()
     */
    @Override
    public void startAll() throws ApexException {
        for (final EngineService engine : engineWorkerMap.values()) {
            start(engine.getKey());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#start(org.onap.policy.apex.core.model.
     * concepts.AxArtifactKey)
     */
    @Override
    public void start(final AxArtifactKey engineKey) throws ApexException {
        LOGGER.entry(engineKey);

        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            String message = ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Start the engine
        engineWorkerMap.get(engineKey).start(engineKey);
        
        // Check if periodic events should be turned on
        if (periodicEventPeriod > 0) {
            startPeriodicEvents(periodicEventPeriod);
        }

        LOGGER.exit(engineKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stop()
     */
    @Override
    public void stop() throws ApexException {
        LOGGER.entry();

        if (periodicEventGenerator != null) {
            periodicEventGenerator.cancel();
            periodicEventGenerator = null;
        }
        
        // Stop each engine
        for (final EngineService engine : engineWorkerMap.values()) {
            if (engine.getState() != AxEngineState.STOPPED) {
                engine.stop();
            }
        }

        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stop(org.onap.policy.apex.core.model.
     * concepts.AxArtifactKey)
     */
    @Override
    public void stop(final AxArtifactKey engineKey) throws ApexException {
        LOGGER.entry(engineKey);

        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            LOGGER.warn(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
            throw new ApexException(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
        }

        // Stop the engine
        engineWorkerMap.get(engineKey).stop(engineKey);

        LOGGER.exit(engineKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#clear()
     */
    @Override
    public void clear() throws ApexException {
        LOGGER.entry();

        // Stop each engine
        for (final EngineService engine : engineWorkerMap.values()) {
            if (engine.getState() == AxEngineState.STOPPED) {
                engine.clear();
            }
        }

        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#clear(org.onap.policy.apex.core.model.
     * concepts.AxArtifactKey)
     */
    @Override
    public void clear(final AxArtifactKey engineKey) throws ApexException {
        LOGGER.entry(engineKey);

        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            LOGGER.warn(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
            throw new ApexException(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
        }

        // Clear the engine
        if (engineWorkerMap.get(engineKey).getState() == AxEngineState.STOPPED) {
            engineWorkerMap.get(engineKey).stop(engineKey);
        }

        LOGGER.exit(engineKey);
    }

    /**
     * Check all engines are started.
     *
     * @return true if <i>all</i> engines are started
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStarted()
     */
    @Override
    public boolean isStarted() {
        for (final EngineService engine : engineWorkerMap.values()) {
            if (!engine.isStarted()) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStarted(org.onap.policy.apex.model.
     * basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public boolean isStarted(final AxArtifactKey engineKey) {
        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            return false;
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            LOGGER.warn(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
            return false;
        }
        return engineWorkerMap.get(engineKey).isStarted();
    }

    /**
     * Check all engines are stopped.
     *
     * @return true if <i>all</i> engines are stopped
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStopped()
     */
    @Override
    public boolean isStopped() {
        for (final EngineService engine : engineWorkerMap.values()) {
            if (!engine.isStopped()) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStopped(org.onap.policy.apex.model.
     * basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public boolean isStopped(final AxArtifactKey engineKey) {
        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            return true;
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            LOGGER.warn(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
            return true;
        }
        return engineWorkerMap.get(engineKey).isStopped();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#startPeriodicEvents(long)
     */
    @Override
    public void startPeriodicEvents(final long period) throws ApexException {
        // Check if periodic events are already started
        if (periodicEventGenerator != null) {
            String message = "Peiodic event geneation already running on engine " + engineServiceKey.getId() + ", "
                            + periodicEventGenerator.toString();
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Set up periodic event execution, its a Java Timer/TimerTask
        periodicEventGenerator = new ApexPeriodicEventGenerator(this.getEngineServiceEventInterface(), period);

        // Record the periodic event period because it may have been set over the Web Socket admin
        // interface
        this.periodicEventPeriod = period;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stopPeriodicEvents()
     */
    @Override
    public void stopPeriodicEvents() throws ApexException {
        // Check if periodic events are already started
        if (periodicEventGenerator == null) {
            LOGGER.warn("Peiodic event geneation not running on engine " + engineServiceKey.getId());
            throw new ApexException("Peiodic event geneation not running on engine " + engineServiceKey.getId());
        }

        // Stop periodic events
        periodicEventGenerator.cancel();
        periodicEventGenerator = null;
        periodicEventPeriod = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getStatus(org.onap.policy.apex.core.model
     * .concepts.AxArtifactKey)
     */
    @Override
    public String getStatus(final AxArtifactKey engineKey) throws ApexException {
        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            LOGGER.warn(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
            throw new ApexException(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
        }

        // Return the information for this worker
        return engineWorkerMap.get(engineKey).getStatus(engineKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getRuntimeInfo(org.onap.policy.apex.core.
     * model.concepts.AxArtifactKey)
     */
    @Override
    public String getRuntimeInfo(final AxArtifactKey engineKey) throws ApexException {
        if (engineKey == null) {
            String message = ENGINE_KEY_NOT_SPECIFIED;
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        // Check if we have this key on our map
        if (!engineWorkerMap.containsKey(engineKey)) {
            LOGGER.warn(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
            throw new ApexException(ENGINE_KEY_PREAMBLE + engineKey.getId() + NOT_FOUND_SUFFIX);
        }

        // Return the information for this worker
        return engineWorkerMap.get(engineKey).getRuntimeInfo(engineKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface#sendEvent(org.onap.policy.
     * apex.service.engine.event.ApexEvent)
     */
    @Override
    public void sendEvent(final ApexEvent event) {
        if (event == null) {
            LOGGER.warn("Null events cannot be processed, in engine service " + engineServiceKey.getId());
            return;
        }

        // Check if we have this key on our map
        if (getState() == AxEngineState.STOPPED) {
            LOGGER.warn("event " + event.getName() + " not processed, no engines on engine service "
                            + engineServiceKey.getId() + " are running");
            return;
        }

        if (DEBUG_ENABLED) {
            LOGGER.debug("Forwarding Apex Event {} to the processing engine", event);
        }

        // Add the incoming event to the queue, the next available worker will process it
        queue.add(event);
    }
}
