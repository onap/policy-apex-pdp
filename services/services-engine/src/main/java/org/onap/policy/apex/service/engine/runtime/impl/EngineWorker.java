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
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.core.engine.engine.ApexEngine;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelWriter;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.impl.enevent.ApexEvent2EnEventConverter;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The Class EngineWorker encapsulates a core {@link ApexEngine} instance, which runs policies
 * defined in the {@link org.onap.policy.apex.model.basicmodel.concepts.AxModelAxModel}. Each policy
 * is triggered by an Apex event, and when the policy is triggered it runs through to completion in
 * the ApexEngine.
 *
 * <p>This class acts as a container for an {@link ApexEngine}, running it in a thread, sending it
 * events, and receiving events from it.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
final class EngineWorker implements EngineService {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngineService.class);

    // The ID of this engine
    private final AxArtifactKey engineWorkerKey;

    // The Apex engine which is running the policies in this worker
    private final ApexEngine engine;

    // The event processor is an inner class, an instance of which runs as a thread that reads
    // incoming events from a queue and forwards them to the Apex engine
    private EventProcessor processor = null;

    // Thread handling for the worker
    private final ApplicationThreadFactory threadFactory;
    private Thread processorThread;

    // Converts ApexEvent instances to and from EnEvent instances
    private ApexEvent2EnEventConverter apexEnEventConverter = null;

    /**
     * Constructor that creates an Apex engine, an event processor for events to be sent to that
     * engine, and an {@link ApexModelReader} instance to read Apex models using JAXB.
     *
     * @param engineWorkerKey the engine worker key
     * @param queue the queue on which events for this Apex worker will come
     * @param threadFactory the thread factory to use for creating the event processing thread
     * @throws ApexException thrown on errors on worker instantiation
     */
    EngineWorker(final AxArtifactKey engineWorkerKey, final BlockingQueue<ApexEvent> queue,
            final ApplicationThreadFactory threadFactory) throws ApexException {
        LOGGER.entry(engineWorkerKey);

        this.engineWorkerKey = engineWorkerKey;
        this.threadFactory = threadFactory;

        // Create the Apex engine
        engine = new ApexEngineFactory().createApexEngine(engineWorkerKey);

        // Create and run the event processor
        processor = new EventProcessor(queue);

        // Set the Event converter up
        apexEnEventConverter = new ApexEvent2EnEventConverter(engine);

        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#registerActionListener(java.lang.
     * String, org.onap.policy.apex.service.engine.runtime.ApexEventListener)
     */
    @Override
    public void registerActionListener(final String listenerName, final ApexEventListener apexEventListener) {
        // Sanity checks on the Apex model
        if (engine == null) {
            LOGGER.warn("listener registration on engine with key " + engineWorkerKey.getID()
                    + ", failed, listener is null");
            return;
        }

        engine.addEventListener(listenerName, new EnEventListenerImpl(apexEventListener, apexEnEventConverter));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#deregisterActionListener(java.lang.
     * String)
     */
    @Override
    public void deregisterActionListener(final String listenerName) {
        // Sanity checks on the Apex model
        if (engine == null) {
            LOGGER.warn("listener deregistration on engine with key " + engineWorkerKey.getID()
                    + ", failed, listener is null");
            return;
        }

        engine.removeEventListener(listenerName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#getEngineServiceEventInterface()
     */
    @Override
    public EngineServiceEventInterface getEngineServiceEventInterface() {
        throw new UnsupportedOperationException(
                "getEngineServiceEventInterface() call is not allowed on an Apex Engine Worker");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return engineWorkerKey;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getInfo()
     */
    @Override
    public Collection<AxArtifactKey> getEngineKeys() {
        return Arrays.asList(engineWorkerKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getApexModelKey()
     */
    @Override
    public AxArtifactKey getApexModelKey() {
        if (ModelService.existsModel(AxPolicyModel.class)) {
            return ModelService.getModel(AxPolicyModel.class).getKey();
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#updateModel(org.onap.policy.apex.
     * model. basicmodel.concepts.AxArtifactKey, java.lang.String, boolean)
     */
    @Override
    public void updateModel(final AxArtifactKey engineKey, final String engineModel, final boolean forceFlag)
            throws ApexException {
        LOGGER.entry(engineKey);

        // Read the Apex model into memory using the Apex Model Reader
        AxPolicyModel apexPolicyModel = null;
        try {
            final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
            apexPolicyModel = modelReader.read(new ByteArrayInputStream(engineModel.getBytes()));
        } catch (final ApexModelException e) {
            LOGGER.error("failed to unmarshal the apex model on engine " + engineKey.getID(), e);
            throw new ApexException("failed to unmarshal the apex model on engine " + engineKey.getID(), e);
        }

        if (apexPolicyModel == null) {
            LOGGER.error("apex model null on engine " + engineKey.getID());
            throw new ApexException("apex model null on engine " + engineKey.getID());
        }

        // Update the Apex model in the Apex engine
        updateModel(engineKey, apexPolicyModel, forceFlag);

        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#updateModel(org.onap.policy.apex.
     * model. basicmodel.concepts.AxArtifactKey,
     * org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel, boolean)
     */
    @Override
    public void updateModel(final AxArtifactKey engineKey, final AxPolicyModel apexModel, final boolean forceFlag)
            throws ApexException {
        LOGGER.entry(engineKey);

        // Check if the key on the update request is correct
        if (!engineWorkerKey.equals(engineKey)) {
            LOGGER.warn("engine key " + engineKey.getID() + " does not match the key" + engineWorkerKey.getID()
                    + " of this engine");
            throw new ApexException("engine key " + engineKey.getID() + " does not match the key"
                    + engineWorkerKey.getID() + " of this engine");
        }

        // Sanity checks on the Apex model
        if (engine == null) {
            LOGGER.warn("engine with key " + engineKey.getID() + " not initialized");
            throw new ApexException("engine with key " + engineKey.getID() + " not initialized");
        }

        // Check model compatibility
        if (ModelService.existsModel(AxPolicyModel.class)) {
            // The current policy model may or may not be defined
            final AxPolicyModel currentModel = ModelService.getModel(AxPolicyModel.class);
            if (!currentModel.getKey().isCompatible(apexModel.getKey())) {
                if (forceFlag) {
                    LOGGER.warn("apex model update forced, supplied model with key \"" + apexModel.getKey().getID()
                            + "\" is not a compatible model update from the existing engine model with key \""
                            + currentModel.getKey().getID() + "\"");
                } else {
                    throw new ContextException(
                            "apex model update failed, supplied model with key \"" + apexModel.getKey().getID()
                                    + "\" is not a compatible model update from the existing engine model with key \""
                                    + currentModel.getKey().getID() + "\"");
                }
            }
        }

        // Update the Apex model in the Apex engine
        engine.updateModel(apexModel);

        LOGGER.debug("engine model {} added to the engine-{}", apexModel.getKey().getID(), engineWorkerKey);
        LOGGER.exit();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#getState()
     */
    @Override
    public AxEngineState getState() {
        return engine.getState();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#startAll()
     */
    @Override
    public void startAll() throws ApexException {
        start(this.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#start(org.onap.policy.apex.core.
     * model. concepts.AxArtifactKey)
     */
    @Override
    public void start(final AxArtifactKey engineKey) throws ApexException {
        LOGGER.entry(engineKey);

        // Check if the key on the start request is correct
        if (!engineWorkerKey.equals(engineKey)) {
            LOGGER.warn("engine key " + engineKey.getID() + " does not match the key" + engineWorkerKey.getID()
                    + " of this engine");
            throw new ApexException("engine key " + engineKey.getID() + " does not match the key"
                    + engineWorkerKey.getID() + " of this engine");
        }

        if (engine == null) {
            LOGGER.error("apex engine for engine key" + engineWorkerKey.getID() + " null");
            throw new ApexException("apex engine for engine key" + engineWorkerKey.getID() + " null");
        }

        // Starts the event processing thread that handles incoming events
        if (processorThread != null && processorThread.isAlive()) {
            LOGGER.error("apex engine for engine key" + engineWorkerKey.getID() + " is already running with state "
                    + getState());
            throw new ApexException("apex engine for engine key" + engineWorkerKey.getID()
                    + " is already running with state " + getState());
        }

        // Start the engine
        engine.start();

        // Start a thread to process events for the engine
        processorThread = threadFactory.newThread(processor);
        processorThread.start();

        LOGGER.exit(engineKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stop()
     */
    @Override
    public void stop() throws ApexException {
        stop(this.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#stop(org.onap.policy.apex.core.
     * model. concepts.AxArtifactKey)
     */
    @Override
    public void stop(final AxArtifactKey engineKey) throws ApexException {
        // Check if the key on the start request is correct
        if (!engineWorkerKey.equals(engineKey)) {
            LOGGER.warn("engine key " + engineKey.getID() + " does not match the key" + engineWorkerKey.getID()
                    + " of this engine");
            throw new ApexException("engine key " + engineKey.getID() + " does not match the key"
                    + engineWorkerKey.getID() + " of this engine");
        }

        if (engine == null) {
            LOGGER.error("apex engine for engine key" + engineWorkerKey.getID() + " null");
            throw new ApexException("apex engine for engine key" + engineWorkerKey.getID() + " null");
        }

        // Interrupt the worker to stop its thread
        if (processorThread == null || !processorThread.isAlive()) {
            processorThread = null;

            LOGGER.warn("apex engine for engine key" + engineWorkerKey.getID() + " is already stopped with state "
                    + getState());
            return;
        }

        // Interrupt the thread that is handling events toward the engine
        processorThread.interrupt();

        // Stop the engine
        engine.stop();
        engine.clear();

        LOGGER.exit(engineKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStarted()
     */
    @Override
    public boolean isStarted() {
        return isStarted(this.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#isStarted(org.onap.policy.apex.
     * model. basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public boolean isStarted(final AxArtifactKey engineKey) {
        final AxEngineState engstate = getState();
        switch (engstate) {
            case STOPPED:
            case STOPPING:
            case UNDEFINED:
                return false;
            case EXECUTING:
            case READY:
                return processorThread != null && processorThread.isAlive() && !processorThread.isInterrupted();
            default:
                break;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#isStopped()
     */
    @Override
    public boolean isStopped() {
        return isStopped(this.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#isStopped(org.onap.policy.apex.
     * model. basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public boolean isStopped(final AxArtifactKey engineKey) {
        final AxEngineState engstate = getState();
        switch (engstate) {
            case STOPPING:
            case UNDEFINED:
            case EXECUTING:
            case READY:
                return false;
            case STOPPED:
                return processorThread == null || !processorThread.isAlive();
            default:
                break;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#startPeriodicEvents(long)
     */
    @Override
    public void startPeriodicEvents(final long period) {
        throw new UnsupportedOperationException("startPeriodicEvents() call is not allowed on an Apex Engine Worker");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.runtime.EngineService#stopPeriodicEvents()
     */
    @Override
    public void stopPeriodicEvents() {
        throw new UnsupportedOperationException("stopPeriodicEvents() call is not allowed on an Apex Engine Worker");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#getStatus(org.onap.policy.apex.core
     * .model .concepts.AxArtifactKey)
     */
    @Override
    public String getStatus(final AxArtifactKey engineKey) {
        // Get the information from the engine that we want to return
        final AxEngineModel apexEngineModel = engine.getEngineStatus();
        apexEngineModel.getKeyInformation().generateKeyInfo(apexEngineModel);

        // Convert that information into a string
        try {
            final ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            final ApexModelWriter<AxEngineModel> modelWriter = new ApexModelWriter<>(AxEngineModel.class);
            modelWriter.write(apexEngineModel, baOutputStream);
            return baOutputStream.toString();
        } catch (final Exception e) {
            LOGGER.warn("error outputting runtime information for engine {}", engineWorkerKey, e);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineService#getRuntimeInfo(org.onap.policy.apex
     * .core.model.concepts.AxArtifactKey)
     */
    @Override
    public String getRuntimeInfo(final AxArtifactKey engineKey) {
        // We'll build up the JSON string for runtime information bit by bit
        final StringBuilder runtimeJsonStringBuilder = new StringBuilder();

        // Get the engine information
        final AxEngineModel engineModel = engine.getEngineStatus();
        final Map<AxArtifactKey, Map<String, Object>> engineContextAlbums = engine.getEngineContext();

        // Use GSON to convert our context information into JSON
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Get context into a JSON string
        runtimeJsonStringBuilder.append("{\"TimeStamp\":");
        runtimeJsonStringBuilder.append(engineModel.getTimestamp());
        runtimeJsonStringBuilder.append(",\"State\":");
        runtimeJsonStringBuilder.append(engineModel.getState());
        runtimeJsonStringBuilder.append(",\"Stats\":");
        runtimeJsonStringBuilder.append(gson.toJson(engineModel.getStats()));

        // Get context into a JSON string
        runtimeJsonStringBuilder.append(",\"ContextAlbums\":[");

        boolean firstAlbum = true;
        for (final Entry<AxArtifactKey, Map<String, Object>> contextAlbumEntry : engineContextAlbums.entrySet()) {
            if (firstAlbum) {
                firstAlbum = false;
            } else {
                runtimeJsonStringBuilder.append(",");
            }

            runtimeJsonStringBuilder.append("{\"AlbumKey\":");
            runtimeJsonStringBuilder.append(gson.toJson(contextAlbumEntry.getKey()));
            runtimeJsonStringBuilder.append(",\"AlbumContent\":[");


            // Get the schema helper to use to marshal context album objects to JSON
            final AxContextAlbum axContextAlbum =
                    ModelService.getModel(AxContextAlbums.class).get(contextAlbumEntry.getKey());
            SchemaHelper schemaHelper = null;

            try {
                // Get a schema helper to manage the translations between objects on the album map
                // for this album
                schemaHelper = new SchemaHelperFactory().createSchemaHelper(axContextAlbum.getKey(),
                        axContextAlbum.getItemSchema());
            } catch (final ContextRuntimeException e) {
                final String resultString =
                        "could not find schema helper to marshal context album \"" + axContextAlbum + "\" to JSON";
                LOGGER.warn(resultString, e);

                // End of context album entry
                runtimeJsonStringBuilder.append(resultString);
                runtimeJsonStringBuilder.append("]}");

                continue;
            }

            boolean firstEntry = true;
            for (final Entry<String, Object> contextEntry : contextAlbumEntry.getValue().entrySet()) {
                if (firstEntry) {
                    firstEntry = false;
                } else {
                    runtimeJsonStringBuilder.append(",");
                }
                runtimeJsonStringBuilder.append("{\"EntryName\":");
                runtimeJsonStringBuilder.append(gson.toJson(contextEntry.getKey()));
                runtimeJsonStringBuilder.append(",\"EntryContent\":");
                runtimeJsonStringBuilder.append(gson.toJson(schemaHelper.marshal2String(contextEntry.getValue())));

                // End of context entry
                runtimeJsonStringBuilder.append("}");
            }

            // End of context album entry
            runtimeJsonStringBuilder.append("]}");
        }

        runtimeJsonStringBuilder.append("]}");

        // Tidy up the JSON string
        final JsonParser jsonParser = new JsonParser();
        final JsonElement jsonElement = jsonParser.parse(runtimeJsonStringBuilder.toString());
        final String tidiedRuntimeString = gson.toJson(jsonElement);

        LOGGER.debug("runtime information=" + tidiedRuntimeString);

        return tidiedRuntimeString;
    }

    /**
     * This is an event processor thread, this class decouples the events handling logic from core
     * business logic. This class runs its own thread and continuously querying the blocking queue
     * for the events that have been sent to the worker for processing by the Apex engine.
     *
     * @author Liam Fallon (liam.fallon@ericsson.com)
     */
    private class EventProcessor implements Runnable {
        private final boolean debugEnabled = LOGGER.isDebugEnabled();
        // the events queue
        private BlockingQueue<ApexEvent> eventProcessingQueue = null;

        /**
         * Constructor accepts {@link ApexEngine} and {@link BlockingQueue} type objects.
         *
         * @param eventProcessingQueue is reference of {@link BlockingQueue} which contains trigger
         *        events.
         */
        EventProcessor(final BlockingQueue<ApexEvent> eventProcessingQueue) {
            this.eventProcessingQueue = eventProcessingQueue;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            LOGGER.debug("Engine {} processing ... ", engineWorkerKey);

            // Take events from the event processing queue of the worker and pass them to the engine
            // for processing
            while (!processorThread.isInterrupted()) {
                ApexEvent event = null;
                try {
                    event = eventProcessingQueue.take();
                } catch (final InterruptedException e) {
                    // restore the interrupt status
                    Thread.currentThread().interrupt();
                    LOGGER.debug("Engine {} processing interrupted ", engineWorkerKey);
                    break;
                }

                try {
                    if (event != null) {
                        if (debugEnabled) {
                            LOGGER.debug("Trigger Event {} forwarded to the Apex engine", event);
                        }
                        final EnEvent enevent = apexEnEventConverter.fromApexEvent(event);
                        engine.handleEvent(enevent);
                    }
                } catch (final ApexException e) {
                    LOGGER.warn("Engine {} failed to process event {}", engineWorkerKey, event.toString(), e);
                } catch (final Exception e) {
                    LOGGER.warn("Engine {} terminated processing event {}", engineWorkerKey, event.toString(), e);
                    break;
                }
            }
            LOGGER.debug("Engine {} completed processing", engineWorkerKey);
        }
    }
}
