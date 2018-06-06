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

package org.onap.policy.apex.service.parameters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * The main container parameter class for an Apex service.
 * <p>
 * The following parameters are defined:
 * <ol>
 * <li>engineServiceParameters: The parameters for the Apex engine service itself, such as the
 * number of engine threads to run and the deployment port number to use.
 * <li>eventOutputParameters: A map of parameters for event outputs that Apex will use to emit
 * events. Apex emits events on all outputs
 * <li>eventInputParameters: A map or parameters for event inputs from which Apex will consume
 * events. Apex reads events from all its event inputs.
 * <li>synchronousEventHandlerParameters: A map of parameters for synchronous event handlers That
 * Apex receives events from and replies immediately to those events.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexParameters extends AbstractParameters implements ApexParameterValidator {
    /**
     * Constructor to create an apex parameters instance and register the instance with the
     * parameter service.
     */
    public ApexParameters() {
        super(ContextParameters.class.getCanonicalName());
        ParameterService.registerParameters(ApexParameters.class, this);
    }

    // Parameters for the engine service and the engine threads in the engine service
    private EngineServiceParameters engineServiceParameters;

    // Parameters for the event outputs that Apex will use to send events on its outputs
    private Map<String, EventHandlerParameters> eventOutputParameters = new LinkedHashMap<>();

    // Parameters for the event inputs that Apex will use to receive events on its inputs
    private Map<String, EventHandlerParameters> eventInputParameters = new LinkedHashMap<>();

    /**
     * Gets the parameters for the Apex engine service.
     *
     * @return the engine service parameters
     */
    public EngineServiceParameters getEngineServiceParameters() {
        return engineServiceParameters;
    }

    /**
     * Sets the engine service parameters.
     *
     * @param engineServiceParameters the engine service parameters
     */
    public void setEngineServiceParameters(final EngineServiceParameters engineServiceParameters) {
        this.engineServiceParameters = engineServiceParameters;
    }

    /**
     * Gets the event output parameter map.
     *
     * @return the parameters for all event outputs
     */
    public Map<String, EventHandlerParameters> getEventOutputParameters() {
        return eventOutputParameters;
    }

    /**
     * Sets the event output parameters.
     *
     * @param eventOutputParameters the event outputs parameters
     */
    public void setEventOutputParameters(final Map<String, EventHandlerParameters> eventOutputParameters) {
        this.eventOutputParameters = eventOutputParameters;
    }

    /**
     * Gets the event input parameter map.
     *
     * @return the parameters for all event inputs
     */
    public Map<String, EventHandlerParameters> getEventInputParameters() {
        return eventInputParameters;
    }

    /**
     * Sets the event input parameters.
     *
     * @param eventInputParameters the event input parameters
     */
    public void setEventInputParameters(final Map<String, EventHandlerParameters> eventInputParameters) {
        this.eventInputParameters = eventInputParameters;
    }

    /**
     * This method formats a validation result with a header if the result is not empty.
     *
     * @param validationResultMessage The incoming message
     * @param heading The heading to prepend on the message
     * @return the formatted message
     */
    private String validationResultFormatter(final String validationResultMessage, final String heading) {
        final StringBuilder errorMessageBuilder = new StringBuilder();

        if (validationResultMessage.length() > 0) {
            errorMessageBuilder.append(heading);
            errorMessageBuilder.append(validationResultMessage);
        }

        return errorMessageBuilder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public String validate() {
        final StringBuilder errorMessageBuilder = new StringBuilder();

        if (engineServiceParameters == null) {
            errorMessageBuilder.append(" engine service parameters are not specified\n");
        } else {
            errorMessageBuilder.append(validationResultFormatter(engineServiceParameters.validate(),
                    " engine service parameters invalid\n"));
        }

        // Sanity check, we must have an entry in both output and input maps
        if (eventOutputParameters.isEmpty() || eventInputParameters.isEmpty()) {
            errorMessageBuilder.append(" at least one event output and one event input must be specified\n");
        }

        // Validate that the values of all parameters are ok
        validateEventHandlerMap("event input", errorMessageBuilder, eventInputParameters);
        validateEventHandlerMap("event output", errorMessageBuilder, eventOutputParameters);

        // Only do peer mode validate if there are no other errors
        if (errorMessageBuilder.length() == 0) {
            for (final EventHandlerPeeredMode peeredMode : EventHandlerPeeredMode.values()) {
                validatePeeredMode(errorMessageBuilder, peeredMode);
            }
        }

        // Check if we have any errors
        if (errorMessageBuilder.length() > 0) {
            errorMessageBuilder.insert(0, "Apex parameters invalid\n");
        }

        return errorMessageBuilder.toString().trim();
    }

    /**
     * This method validates the parameters in an event handler map.
     * 
     * @param eventHandlerType the type of the event handler to use on error messages
     * @param errorMessageBuilder the builder to use to return validation messages
     * @param parsForValidation The event handler parameters to validate (input or output)
     */
    // CHECKSTYLE:OFF: checkstyle:finalParameter
    private void validateEventHandlerMap(final String eventHandlerType, final StringBuilder errorMessageBuilder,
            final Map<String, EventHandlerParameters> parsForValidation) {
        // CHECKSTYLE:ON: checkstyle:finalParameter
        for (final Entry<String, EventHandlerParameters> parameterEntry : parsForValidation.entrySet()) {
            if (parameterEntry.getKey() == null || parameterEntry.getKey().trim().isEmpty()) {
                errorMessageBuilder
                        .append(" invalid " + eventHandlerType + " name \"" + parameterEntry.getKey() + "\" \n");
            } else if (parameterEntry.getValue() == null) {
                errorMessageBuilder.append(" invalid/Null event input prameters specified for " + eventHandlerType
                        + " name \"" + parameterEntry.getKey() + "\" \n");
            } else {
                errorMessageBuilder.append(validationResultFormatter(parameterEntry.getValue().validate(),
                        " " + eventHandlerType + " (" + parameterEntry.getKey() + ") parameters invalid\n"));
            }

            parameterEntry.getValue().setName(parameterEntry.getKey());

            // Validate parameters for peered mode settings
            for (final EventHandlerPeeredMode peeredMode : EventHandlerPeeredMode.values()) {
                validatePeeredModeParameters(eventHandlerType, errorMessageBuilder, parameterEntry, peeredMode);
            }
        }
    }

    /**
     * Validate parameter values for event handlers in a peered mode
     * 
     * @param eventHandlerType The event handler type we are checking
     * @param errorMessageBuilder The builder to which to append any error messages
     * @param parameterEntry The entry to check the peered mode on
     * @param peeredMode The mode to check
     */
    private void validatePeeredModeParameters(final String eventHandlerType, final StringBuilder errorMessageBuilder,
            final Entry<String, EventHandlerParameters> parameterEntry, final EventHandlerPeeredMode peeredMode) {
        final String messagePreamble = " specified peered mode \"" + peeredMode + "\"";
        final String peer = parameterEntry.getValue().getPeer(peeredMode);

        if (parameterEntry.getValue().isPeeredMode(peeredMode)) {
            if (peer == null || peer.trim().isEmpty()) {
                errorMessageBuilder.append(messagePreamble + " mandatory parameter not specified or is null on "
                        + eventHandlerType + " \"" + parameterEntry.getKey() + "\" \n");
            }
            if (parameterEntry.getValue().getPeerTimeout(peeredMode) < 0) {
                errorMessageBuilder.append(
                        messagePreamble + " timeout value \"" + parameterEntry.getValue().getPeerTimeout(peeredMode)
                                + "\" is illegal on " + eventHandlerType + " \"" + parameterEntry.getKey()
                                + "\", specify a non-negative timeout value in milliseconds\n");
            }
        } else {
            if (peer != null) {
                errorMessageBuilder.append(messagePreamble + " peer is illegal on non synchronous " + eventHandlerType
                        + " \"" + parameterEntry.getKey() + "\" \n");
            }
            if (parameterEntry.getValue().getPeerTimeout(peeredMode) != 0) {
                errorMessageBuilder.append(messagePreamble + " timeout is illegal on non synchronous "
                        + eventHandlerType + " \"" + parameterEntry.getKey() + "\" \n");
            }
        }
    }

    /**
     * This method validates that the settings are valid for the given peered mode
     * 
     * @param errorMessageBuilder The builder to which to append any error messages
     * @param peeredMode The peered mode to check
     */
    private void validatePeeredMode(final StringBuilder errorMessageBuilder, final EventHandlerPeeredMode peeredMode) {
        // Find the input and output event handlers that use this peered mode
        final Map<String, EventHandlerParameters> inputParametersUsingMode = new HashMap<>();
        final Map<String, EventHandlerParameters> outputParametersUsingMode = new HashMap<>();

        // Find input and output parameters using this mode
        for (final Entry<String, EventHandlerParameters> inputParameterEntry : eventInputParameters.entrySet()) {
            if (inputParameterEntry.getValue().isPeeredMode(peeredMode)) {
                inputParametersUsingMode.put(inputParameterEntry.getKey(), inputParameterEntry.getValue());
            }
        }
        for (final Entry<String, EventHandlerParameters> outputParameterEntry : eventOutputParameters.entrySet()) {
            if (outputParameterEntry.getValue().isPeeredMode(peeredMode)) {
                outputParametersUsingMode.put(outputParameterEntry.getKey(), outputParameterEntry.getValue());
            }
        }

        // Validate the parameters for each side of the peered mode parameters
        validatePeeredModePeers(" event input for peered mode \"" + peeredMode + "\": ", errorMessageBuilder,
                peeredMode, inputParametersUsingMode, outputParametersUsingMode);
        validatePeeredModePeers(" event output for peered mode \"" + peeredMode + "\": ", errorMessageBuilder,
                peeredMode, outputParametersUsingMode, inputParametersUsingMode);
    }

    /**
     * This method validates that the settings are valid for the event handlers on one
     * 
     * @param messagePreamble the preamble for messages indicating the peered mode side
     * @param errorMessageBuilder The builder to which to append any error messages
     * @param leftModeParameters The mode parameters being checked
     * @param rightModeParameters The mode parameters being referenced by the checked parameters
     */
    private void validatePeeredModePeers(final String messagePreamble, final StringBuilder errorMessageBuilder,
            final EventHandlerPeeredMode peeredMode, final Map<String, EventHandlerParameters> leftModeParameterMap,
            final Map<String, EventHandlerParameters> rightModeParameterMap) {

        // These sets are used to check for duplicate references on the both sides
        final Set<String> leftCheckDuplicateSet = new HashSet<>();
        final Set<String> rightCheckDuplicateSet = new HashSet<>();

        // Check for missing peers, all peers are set because we have checked them previously so no
        // need for null checks
        for (final Entry<String, EventHandlerParameters> leftModeParameterEntry : leftModeParameterMap.entrySet()) {
            final String leftSidePeer = leftModeParameterEntry.getValue().getPeer(peeredMode);

            final EventHandlerParameters leftModeParameters = leftModeParameterEntry.getValue();
            final EventHandlerParameters rightModeParameters = rightModeParameterMap.get(leftSidePeer);

            // Check that the peer reference is OK
            if (rightModeParameters == null) {
                errorMessageBuilder.append(messagePreamble + "peer \"" + leftModeParameters.getPeer(peeredMode)
                        + "\" for event handler \"" + leftModeParameterEntry.getKey()
                        + "\" does not exist or is not defined as being synchronous\n");
                continue;
            }

            // Now check that the right side peer is the left side event handler
            final String rightSidePeer = rightModeParameters.getPeer(peeredMode);
            if (!rightSidePeer.equals(leftModeParameterEntry.getKey())) {
                errorMessageBuilder
                        .append(messagePreamble + "peer value \"" + rightSidePeer + "\" on peer \"" + leftSidePeer
                                + "\" does not equal event handler \"" + leftModeParameterEntry.getKey() + "\"\n");
            } else {
                // Check for duplicates
                if (!leftCheckDuplicateSet.add(leftSidePeer)) {
                    errorMessageBuilder
                            .append(messagePreamble + "peer value \"" + leftSidePeer + "\" on event handler \""
                                    + leftModeParameterEntry.getKey() + "\" is used more than once\n");
                }
                if (!rightCheckDuplicateSet.add(rightSidePeer)) {
                    errorMessageBuilder.append(messagePreamble + "peer value \"" + rightSidePeer + "\" on peer \""
                            + leftSidePeer + "\" on event handler \"" + leftModeParameterEntry.getKey()
                            + "\" is used more than once\n");
                }
            }

            // Cross-set the timeouts if they are not specified
            if (leftModeParameters.getPeerTimeout(peeredMode) != 0) {
                if (rightModeParameters.getPeerTimeout(peeredMode) != 0) {
                    if (leftModeParameters.getPeerTimeout(peeredMode) != rightModeParameters
                            .getPeerTimeout(peeredMode)) {
                        errorMessageBuilder.append(messagePreamble + "timeout "
                                + leftModeParameters.getPeerTimeout(peeredMode) + "on event handler \""
                                + leftModeParameters.getName() + "\" does not equal timeout value "
                                + rightModeParameters.getPeerTimeout(peeredMode) + "on event handler \""
                                + rightModeParameters.getName() + "\"\n");
                    }
                } else {
                    rightModeParameters.setPeerTimeout(peeredMode, leftModeParameters.getPeerTimeout(peeredMode));
                }
            } else {
                if (rightModeParameters.getPeerTimeout(peeredMode) != 0) {
                    leftModeParameters.setPeerTimeout(peeredMode, rightModeParameters.getPeerTimeout(peeredMode));
                }
            }
        }

    }
}
