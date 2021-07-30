/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.parameters.eventhandler;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.service.parameters.ApexParameterConstants;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The parameters for a single event producer, event consumer or synchronous event handler.
 *
 * <p>Event producers, consumers, and synchronous event handlers all use a carrier technology and an event protocol so
 * the actual parameters for each one are the same. Therefore, we use the same class for the parameters of each one.
 *
 * <p>The following parameters are defined: <ol> <li>carrierTechnologyParameters: The carrier technology is the type of
 * messaging infrastructure used to carry events. Examples are File, Kafka or REST. <li>eventProtocolParameters: The
 * format that the events are in when being carried. Examples are JSON, XML, or Java Beans. carrier technology
 * <li>synchronousMode: true if the event handler is working in synchronous mode, defaults to false <li>synchronousPeer:
 * the peer event handler (consumer for producer or producer for consumer) of this event handler in synchronous mode
 * <li>synchronousTimeout: the amount of time to wait for the reply to synchronous events before they are timed out
 * <li>requestorMode: true if the event handler is working in requestor mode, defaults to false <li>requestorPeer: the
 * peer event handler (consumer for producer or producer for consumer) of this event handler in requestor mode
 * <li>requestorTimeout: the amount of time to wait for the reply to synchronous events before they are timed out
 * <li>eventNameFilter: a regular expression to apply to events on this event handler. If specified, events not matching
 * the given regular expression are ignored. If it is null, all events are handledDefaults to null. </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@ToString
public class EventHandlerParameters implements ParameterGroup {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerParameters.class);

    @Setter
    private String name = null;
    @Setter
    private @NotNull @Valid CarrierTechnologyParameters carrierTechnologyParameters = null;
    @Setter
    private @NotNull @Valid EventProtocolParameters eventProtocolParameters = null;
    private boolean synchronousMode = false;
    private String synchronousPeer = null;
    private long synchronousTimeout = 0;
    private boolean requestorMode = false;
    private String requestorPeer = null;
    private long requestorTimeout = 0;
    @Setter
    private String eventName = null;
    @Setter
    private String eventNameFilter = null;

    /**
     * Constructor to create an event handler parameters instance.
     */
    public EventHandlerParameters() {
        super();

        // Set the name for the parameters
        this.name = ApexParameterConstants.EVENT_HANDLER_GROUP_NAME;
    }

    /**
     * Checks if the name of the event handler is set.
     *
     * @return true if the name is set
     */
    public boolean checkSetName() {
        return !(name == null || name.trim().length() == 0);
    }

    /**
     * Checks if the event handler is in the given peered mode.
     *
     * @param peeredMode the peer mode
     * @return true, if the event handler is in the peered mode
     */
    public boolean isPeeredMode(final EventHandlerPeeredMode peeredMode) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                return synchronousMode;
            case REQUESTOR:
                return requestorMode;
            default:
                return false;
        }
    }

    /**
     * Sets a peered mode as true or false on the event handler.
     *
     * @param peeredMode the peered mode to set
     * @param peeredModeValue the value to set the peered mode to
     */
    public void setPeeredMode(final EventHandlerPeeredMode peeredMode, final boolean peeredModeValue) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                synchronousMode = peeredModeValue;
                return;
            case REQUESTOR:
                requestorMode = peeredModeValue;
                return;
            default:
                return;
        }
    }

    /**
     * Gets the peer for the event handler in this peered mode.
     *
     * @param peeredMode the peered mode to get the peer for
     * @return the peer
     */
    public String getPeer(final EventHandlerPeeredMode peeredMode) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                return synchronousPeer;
            case REQUESTOR:
                return requestorPeer;
            default:
                return null;
        }
    }

    /**
     * Sets the peer for the event handler in this peered mode.
     *
     * @param peeredMode the peered mode to set the peer for
     * @param peer the peer
     */
    public void setPeer(final EventHandlerPeeredMode peeredMode, final String peer) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                synchronousPeer = peer;
                return;
            case REQUESTOR:
                requestorPeer = peer;
                return;
            default:
                return;
        }
    }

    /**
     * Get the timeout value for the event handler in peered mode.
     *
     * @param peeredMode the peered mode to get the timeout for
     * @return the timeout value
     */
    public long getPeerTimeout(final EventHandlerPeeredMode peeredMode) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                return synchronousTimeout;
            case REQUESTOR:
                return requestorTimeout;
            default:
                return -1;
        }
    }

    /**
     * Set the timeout value for the event handler in peered mode.
     *
     * @param peeredMode the peered mode to set the timeout for
     * @param timeout the timeout value
     */
    public void setPeerTimeout(final EventHandlerPeeredMode peeredMode, final long timeout) {
        switch (peeredMode) {
            case SYNCHRONOUS:
                synchronousTimeout = timeout;
                return;
            case REQUESTOR:
                requestorTimeout = timeout;
                return;
            default:
                return;
        }
    }

    /**
     * Check if an event name is being used.
     *
     * @return true if an event name is being used
     */
    public boolean isSetEventName() {
        return eventName != null;
    }

    /**
     * Check if event name filtering is being used.
     *
     * @return true if event name filtering is being used
     */
    public boolean isSetEventNameFilter() {
        return eventNameFilter != null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = new BeanValidator().validateTop(getClass().getSimpleName(), this);

        if (eventNameFilter != null) {
            try {
                Pattern.compile(eventNameFilter);
            } catch (final PatternSyntaxException pse) {
                String message = "event handler eventNameFilter is not a valid regular expression: " + pse.getMessage();
                LOGGER.trace(message, pse);
                result.addResult("eventNameFilter", eventNameFilter, ValidationStatus.INVALID, message);
            }
        }

        return result;
    }
}
