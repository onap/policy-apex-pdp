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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.FileCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation an Apex event consumer that reads events from a file. This consumer also implements
 * ApexEventProducer and therefore can be used as a synchronous consumer.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexFileEventConsumer implements ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexFileEventConsumer.class);

    // Recurring string constants
    private static final String APEX_FILE_CONSUMER_PREAMBLE = "ApexFileConsumer \"";

    // The input stream to read events from
    private InputStream eventInputStream;

    // The text block reader that will read text blocks from the contents of the file
    private TextBlockReader textBlockReader;

    // The event receiver that will receive asynchronous events from this consumer
    private ApexEventReceiver eventReceiver = null;

    // The consumer thread and stopping flag
    private Thread consumerThread;

    // The name for this consumer
    private String consumerName = null;

    // The specific carrier technology parameters for this consumer
    private FileCarrierTechnologyParameters fileCarrierTechnologyParameters;

    // The peer references for this event handler
    private final Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(
                    EventHandlerPeeredMode.class);

    // Holds the next identifier for event execution.
    private static AtomicLong nextExecutionID = new AtomicLong(0L);

    /**
     * Private utility to get the next candidate value for a Execution ID. This value will always be unique in a single
     * JVM
     * 
     * @return the next candidate value for a Execution ID
     */
    private static synchronized long getNextExecutionId() {
        return nextExecutionID.getAndIncrement();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.consumer.ApexEventConsumer#init(org.onap.policy.apex.apps.
     * uservice.consumer.ApexEventReceiver)
     */
    @Override
    public void init(final String name, final EventHandlerParameters consumerParameters,
                    final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.consumerName = name;

        // Get and check the Apex parameters from the parameter service
        if (consumerParameters == null) {
            final String errorMessage = "Consumer parameters for ApexFileConsumer \"" + consumerName + "\" is null";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check and get the file Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof FileCarrierTechnologyParameters)) {
            final String errorMessage = "specified consumer properties for ApexFileConsumer \"" + consumerName
                            + "\" are not applicable to a File consumer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        fileCarrierTechnologyParameters = (FileCarrierTechnologyParameters) consumerParameters
                        .getCarrierTechnologyParameters();

        // Open the file producing events
        try {
            if (fileCarrierTechnologyParameters.isStandardIo()) {
                eventInputStream = System.in;
            } else {
                eventInputStream = new FileInputStream(fileCarrierTechnologyParameters.getFileName());
            }

            // Get an event composer for our event source
            textBlockReader = new TextBlockReaderFactory().getTaggedReader(eventInputStream,
                            consumerParameters.getEventProtocolParameters());
        } catch (final IOException e) {
            final String errorMessage = APEX_FILE_CONSUMER_PREAMBLE + consumerName
                            + "\" failed to open file for reading: \"" + fileCarrierTechnologyParameters.getFileName()
                            + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        if (fileCarrierTechnologyParameters.getStartDelay() > 0) {
            ThreadUtilities.sleep(fileCarrierTechnologyParameters.getStartDelay());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getName()
     */
    @Override
    public String getName() {
        return consumerName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.
     * policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.
     * policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode,
     * org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#start()
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + " : " + consumerName;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // Check that we have been initialized in async or sync mode
        if (eventReceiver == null) {
            LOGGER.warn("\"{}\" has not been initilaized for either asynchronous or synchronous event handling",
                            consumerName);
            return;
        }

        // Read the events from the file while there are still events in the file
        try {
            // Read all the text blocks
            TextBlock textBlock;
            do {
                // Read the text block
                textBlock = textBlockReader.readTextBlock();

                // Process the event from the text block if there is one there
                if (textBlock.getText() != null) {
                    eventReceiver.receiveEvent(getNextExecutionId(), textBlock.getText());
                }
            }
            while (!textBlock.isEndOfText());
        } catch (final Exception e) {
            LOGGER.warn("\"" + consumerName + "\" failed to read event from file: \""
                            + fileCarrierTechnologyParameters.getFileName() + "\"", e);
        } finally {
            try {
                eventInputStream.close();
            } catch (final IOException e) {
                LOGGER.warn(APEX_FILE_CONSUMER_PREAMBLE + consumerName + "\" failed to close file: \""
                                + fileCarrierTechnologyParameters.getFileName() + "\"", e);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        try {
            eventInputStream.close();
        } catch (final IOException e) {
            LOGGER.warn(APEX_FILE_CONSUMER_PREAMBLE + consumerName + "\" failed to close file for reading: \""
                            + fileCarrierTechnologyParameters.getFileName() + "\"", e);
        }

        if (consumerThread.isAlive() && !consumerThread.isInterrupted()) {
            consumerThread.interrupt();
        }
    }
}
