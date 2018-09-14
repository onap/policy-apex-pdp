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

package org.onap.policy.apex.service.parameters.eventhandler;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.onap.policy.apex.service.parameters.ApexParameterConstants;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;
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
public class EventHandlerParameters implements ParameterGroup {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerParameters.class);

    private String name = null;
    private CarrierTechnologyParameters carrierTechnologyParameters = null;
    private EventProtocolParameters eventProtocolParameters = null;
    private boolean synchronousMode = false;
    private String synchronousPeer = null;
    private long synchronousTimeout = 0;
    private boolean requestorMode = false;
    private String requestorPeer = null;
    private long requestorTimeout = 0;
    private String eventName = null;
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
     * Gets the name of the event handler.
     *
     * @return the event handler name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event handler.
     *
     * @param name the event handler name
     */
    public void setName(final String name) {
        this.name = name;
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
     * Gets the carrier technology parameters of the event handler.
     *
     * @return the carrierTechnologyParameters of the event handler
     */
    public CarrierTechnologyParameters getCarrierTechnologyParameters() {
        return carrierTechnologyParameters;
    }

    /**
     * Sets the carrier technology parameters of the event handler.
     *
     * @param carrierTechnologyParameters the carrierTechnologyParameters to set
     */
    public void setCarrierTechnologyParameters(final CarrierTechnologyParameters carrierTechnologyParameters) {
        this.carrierTechnologyParameters = carrierTechnologyParameters;
    }

    /**
     * Gets the event protocol parameters of the event handler.
     *
     * @return the eventProtocolParameters
     */
    public EventProtocolParameters getEventProtocolParameters() {
        return eventProtocolParameters;
    }

    /**
     * Sets the event protocol parameters.
     *
     * @param eventProtocolParameters the eventProtocolParameters to set
     */
    public void setEventProtocolParameters(final EventProtocolParameters eventProtocolParameters) {
        this.eventProtocolParameters = eventProtocolParameters;
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
     * Gets the event name for this event handler.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the event name for this event handler.
     *
     * @param eventName the event name
     */
    public void setEventName(final String eventName) {
        this.eventName = eventName;
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
     * Gets the event name filter for this event handler.
     *
     * @return the event name filter
     */
    public String getEventNameFilter() {
        return eventNameFilter;
    }

    /**
     * Sets the event name filter for this event handler.
     *
     * @param eventNameFilter the event name filter
     */
    public void setEventNameFilter(final String eventNameFilter) {
        this.eventNameFilter = eventNameFilter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);

        if (eventProtocolParameters == null) {
            result.setResult("eventProtocolParameters", ValidationStatus.INVALID,
                            "event handler eventProtocolParameters not specified or blank");
        } else {
            result.setResult("eventProtocolParameters", eventProtocolParameters.validate());
        }

        if (carrierTechnologyParameters == null) {
            result.setResult("carrierTechnologyParameters", ValidationStatus.INVALID,
                            "event handler carrierTechnologyParameters not specified or blank");
        } else {
            result.setResult("carrierTechnologyParameters", carrierTechnologyParameters.validate());
        }

        if (eventNameFilter != null) {
            try {
                Pattern.compile(eventNameFilter);
            } catch (final PatternSyntaxException pse) {
                String message = "event handler eventNameFilter is not a valid regular expression: " + pse.getMessage();
                LOGGER.trace(message, pse);
                result.setResult("eventNameFilter", ValidationStatus.INVALID, message);
            }
        }

        return result;
    }

    /**
     * Check if we're using synchronous mode.
     * 
     * @return true if if we're using synchronous mode
     */
    public boolean isSynchronousMode() {
        return synchronousMode;
    }

    /**
     * The synchronous peer for this event handler.
     * 
     * @return the synchronous peer for this event handler
     */
    public String getSynchronousPeer() {
        return synchronousPeer;
    }

    /**
     * Get the timeout for synchronous operations.
     * 
     * @return the timeout for synchronous operations
     */
    public long getSynchronousTimeout() {
        return synchronousTimeout;
    }

    /**
     * Check if this event handler will use requestor mode.
     * 
     * @return true if this event handler will use requestor mode
     */
    public boolean isRequestorMode() {
        return requestorMode;
    }

    /**
     * The requestor peer for this event handler.
     * 
     * @return the requestor peer for this event handler
     */
    public String getRequestorPeer() {
        return requestorPeer;
    }

    /**
     * Get the requestor timeout.
     * 
     * @return the requestorTimeout.
     */
    public long getRequestorTimeout() {
        return requestorTimeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EventHandlerParameters [name=" + name + ", carrierTechnologyParameters=" + carrierTechnologyParameters
                        + ", eventProtocolParameters=" + eventProtocolParameters + ", synchronousMode="
                        + synchronousMode + ", synchronousPeer=" + synchronousPeer + ", synchronousTimeout="
                        + synchronousTimeout + ", requestorMode=" + requestorMode + ", requestorPeer=" + requestorPeer
                        + ", requestorTimeout=" + requestorTimeout + ", eventName=" + eventName + ", eventNameFilter="
                        + eventNameFilter + "]";
    }
}
