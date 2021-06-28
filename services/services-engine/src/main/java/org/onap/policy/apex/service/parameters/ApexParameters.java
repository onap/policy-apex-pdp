/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Nordix Foundation.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Size;
import org.onap.policy.common.parameters.annotations.Valid;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.models.base.Validated;

/**
 * The main container parameter class for an Apex service.
 *
 * <p>The following parameters are defined: <ol> <li>engineServiceParameters: The parameters for the Apex engine service
 * itself, such as the number of engine threads to run and the deployment port number to use. <li>eventOutputParameters:
 * A map of parameters for event outputs that Apex will use to emit events. Apex emits events on all outputs
 * <li>eventInputParameters: A map or parameters for event inputs from which Apex will consume events. Apex reads events
 * from all its event inputs. </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexParameters implements ParameterGroup {
    // Parameter group name
    @Getter
    @Setter
    private String name;

    // Constants for recurring strings
    private static final String JAVA_PROPERTIES = "javaProperties";
    private static final String PEER_STRING = "peer ";
    private static final String EVENT_INPUT_PARAMETERS_STRING = "eventInputParameters";
    private static final String EVENT_OUTPUT_PARAMETERS_STRING = "eventOutputParameters";
    private static final String FOR_PEERED_MODE_STRING = " for peered mode ";

    // Properties for the Java JVM
    @Getter
    private String[][] javaProperties = null;

    // Parameters for the engine service and the engine threads in the engine service
    @Getter
    @Setter
    private @NotNull @Valid EngineServiceParameters engineServiceParameters;

    // Parameters for the event outputs that Apex will use to send events on its outputs
    @Getter
    @Setter
    @Size(min = 1)
    private Map<@NotNull @NotBlank String, @NotNull @Valid EventHandlerParameters> eventOutputParameters =
                    new LinkedHashMap<>();

    // Parameters for the event inputs that Apex will use to receive events on its inputs
    @Getter
    @Setter
    @Size(min = 1)
    private Map<@NotNull @NotBlank String, @NotNull @Valid EventHandlerParameters> eventInputParameters =
                    new LinkedHashMap<>();

    /**
     * Constructor to create an apex parameters instance and register the instance with the parameter service.
     */
    public ApexParameters() {
        super();

        // Set the name for the parameters
        this.name = ApexParameterConstants.MAIN_GROUP_NAME;
    }

    /**
     * Check if Java properties have been specified.
     *
     * @return true if Java properties have been specified
     */
    public boolean checkJavaPropertiesSet() {
        return javaProperties != null && javaProperties.length > 0;
    }

    @Override
    public BeanValidationResult validate() {
        BeanValidationResult result = new BeanValidator().validateTop(getClass().getSimpleName(), this);

        result.addResult(validateJavaProperties());

        // Validate that the values of all parameters are ok
        validateEventHandlerMap(EVENT_INPUT_PARAMETERS_STRING, result, eventInputParameters);
        validateEventHandlerMap(EVENT_OUTPUT_PARAMETERS_STRING, result, eventOutputParameters);

        // Only do peer mode validate if there are no other errors
        if (result.isValid()) {
            for (final EventHandlerPeeredMode peeredMode : EventHandlerPeeredMode.values()) {
                validatePeeredMode(result, peeredMode);
            }
        }

        return result;
    }

    /**
     * This method validates the java properties variable if it is present.
     */
    private ValidationResult validateJavaProperties() {
        if (javaProperties == null) {
            return null;
        }

        var result = new BeanValidationResult(JAVA_PROPERTIES, javaProperties);
        var item = 0;
        for (String[] javaProperty : javaProperties) {
            final String label = "entry " + (item++);
            final List<String> value = (javaProperty == null ? null : Arrays.asList(javaProperty));
            var result2 = new BeanValidationResult(label, value);

            if (javaProperty == null) {
                // note: add to result, not result2
                result.addResult(label, null, ValidationStatus.INVALID, Validated.IS_NULL);

            } else if (javaProperty.length != 2) {
                // note: add to result, not result2
                result.addResult(label, value, ValidationStatus.INVALID, "must have one key and one value");

            } else if (!ParameterValidationUtils.validateStringParameter(javaProperty[0])) {
                result2.addResult("key", javaProperty[0], ValidationStatus.INVALID, Validated.IS_BLANK);

            } else if (!ParameterValidationUtils.validateStringParameter(javaProperty[1])) {
                result2.addResult("value", javaProperty[1], ValidationStatus.INVALID, Validated.IS_BLANK);
            }

            result.addResult(result2);
        }

        return result;
    }

    /**
     * This method validates the parameters in an event handler map.
     *
     * @param eventHandlerType the type of the event handler to use on error messages
     * @param result the result object to use to return validation messages
     * @param parsForValidation The event handler parameters to validate (input or output)
     */
    private void validateEventHandlerMap(final String eventHandlerType, final BeanValidationResult result,
        final Map<String, EventHandlerParameters> parsForValidation) {
        for (final Entry<String, EventHandlerParameters> parameterEntry : parsForValidation.entrySet()) {
            parameterEntry.getValue().setName(parameterEntry.getKey());

            // Validate parameters for peered mode settings
            for (final EventHandlerPeeredMode peeredMode : EventHandlerPeeredMode.values()) {
                validatePeeredModeParameters(eventHandlerType, result, parameterEntry, peeredMode);
            }
        }
    }

    /**
     * Validate parameter values for event handlers in a peered mode.
     *
     * @param eventHandlerType The event handler type we are checking
     * @param result The result object to which to append any error messages
     * @param parameterEntry The entry to check the peered mode on
     * @param peeredMode The mode to check
     */
    private void validatePeeredModeParameters(final String eventHandlerType, final BeanValidationResult result,
        final Entry<String, EventHandlerParameters> parameterEntry, final EventHandlerPeeredMode peeredMode) {
        final String messagePreamble = "specified peered mode \"" + peeredMode + "\"";
        final String peer = parameterEntry.getValue().getPeer(peeredMode);

        if (parameterEntry.getValue().isPeeredMode(peeredMode)) {
            if (peer == null || peer.trim().isEmpty()) {
                result.addResult(eventHandlerType, parameterEntry.getKey(), ValidationStatus.INVALID,
                    messagePreamble + " mandatory parameter not specified or is null");
            }
            if (parameterEntry.getValue().getPeerTimeout(peeredMode) < 0) {
                result.addResult(eventHandlerType, parameterEntry.getKey(), ValidationStatus.INVALID,
                    messagePreamble + " timeout value \"" + parameterEntry.getValue().getPeerTimeout(peeredMode)
                        + "\" is illegal, specify a non-negative timeout value in milliseconds");
            }
        } else {
            if (peer != null) {
                result.addResult(eventHandlerType, parameterEntry.getKey(), ValidationStatus.INVALID, messagePreamble
                    + " peer is illegal");
            }
            if (parameterEntry.getValue().getPeerTimeout(peeredMode) != 0) {
                result.addResult(eventHandlerType, parameterEntry.getKey(), ValidationStatus.INVALID, messagePreamble
                    + " timeout is illegal");
            }
        }
    }

    /**
     * This method validates that the settings are valid for the given peered mode.
     *
     * @param result The result object to which to append any error messages
     * @param peeredMode The peered mode to check
     */
    private void validatePeeredMode(final BeanValidationResult result, final EventHandlerPeeredMode peeredMode) {
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
        validatePeeredModePeers(EVENT_INPUT_PARAMETERS_STRING, result, peeredMode, inputParametersUsingMode,
            outputParametersUsingMode);
        validatePeeredModePeers(EVENT_OUTPUT_PARAMETERS_STRING, result, peeredMode, outputParametersUsingMode,
            inputParametersUsingMode);
    }

    /**
     * This method validates that the settings are valid for the event handlers on one.
     *
     * @param handlerMapVariableName the variable name of the map on which the paired parameters are being checked
     * @param result The result object to which to append any error messages
     * @param leftModeParameterMap The mode parameters being checked
     * @param rightModeParameterMap The mode parameters being referenced by the checked parameters
     */
    private void validatePeeredModePeers(final String handlerMapVariableName, final BeanValidationResult result,
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
                result.addResult(handlerMapVariableName, leftModeParameterEntry.getKey(), ValidationStatus.INVALID,
                    PEER_STRING + '"' + leftModeParameters.getPeer(peeredMode) + FOR_PEERED_MODE_STRING + peeredMode
                        + " does not exist or is not defined with the same peered mode");
                continue;
            }

            // Now check that the right side peer is the left side event handler
            final String rightSidePeer = rightModeParameters.getPeer(peeredMode);
            if (!rightSidePeer.equals(leftModeParameterEntry.getKey())) {
                result.addResult(handlerMapVariableName, leftModeParameterEntry.getKey(), ValidationStatus.INVALID,
                                PEER_STRING + '"' + leftModeParameters.getPeer(peeredMode) + '"'
                                                + FOR_PEERED_MODE_STRING + peeredMode + ", value \"" + rightSidePeer
                                                + "\" on peer does not equal event handler");
            } else {
                // Check for duplicates
                if (!leftCheckDuplicateSet.add(leftSidePeer)) {
                    result.addResult(handlerMapVariableName, leftModeParameterEntry.getKey(), ValidationStatus.INVALID,
                        PEER_STRING + '"' + leftModeParameters.getPeer(peeredMode) + FOR_PEERED_MODE_STRING + peeredMode
                            + ", peer value \"" + leftSidePeer + "\" on event handler \""
                            + leftModeParameterEntry.getKey() + "\" is used more than once");
                }
                if (!rightCheckDuplicateSet.add(rightSidePeer)) {
                    result.addResult(handlerMapVariableName, leftModeParameterEntry.getKey(), ValidationStatus.INVALID,
                        PEER_STRING + '"' + leftModeParameters.getPeer(peeredMode) + FOR_PEERED_MODE_STRING + peeredMode
                            + ", peer value \"" + rightSidePeer + "\" on peer \"" + leftSidePeer
                            + "\" on event handler \"" + leftModeParameterEntry.getKey() + "\" is used more than once");
                }
            }

            if (!crossCheckPeeredTimeoutValues(leftModeParameters, rightModeParameters, peeredMode)) {
                result.addResult(handlerMapVariableName, leftModeParameterEntry.getKey(), ValidationStatus.INVALID,
                                PEER_STRING + '"' + leftModeParameters.getPeer(peeredMode) + '"'
                                                + FOR_PEERED_MODE_STRING + peeredMode + " timeout "
                                                + leftModeParameters.getPeerTimeout(peeredMode)
                                                + " does not equal peer timeout "
                                                + rightModeParameters.getPeerTimeout(peeredMode));

            }
        }
    }

    /**
     * Validate the timeout values on two peers.
     *
     * @param leftModeParameters The parameters of the left hand peer
     * @param peeredMode The peered mode being checked
     * @return true if the timeout values are cross checked as being OK
     */
    private boolean crossCheckPeeredTimeoutValues(final EventHandlerParameters leftModeParameters,
        final EventHandlerParameters rightModeParameters, final EventHandlerPeeredMode peeredMode) {
        // Cross-set the timeouts if they are not specified
        if (leftModeParameters.getPeerTimeout(peeredMode) != 0) {
            if (rightModeParameters.getPeerTimeout(peeredMode) != 0) {
                if (leftModeParameters.getPeerTimeout(peeredMode) != rightModeParameters.getPeerTimeout(peeredMode)) {
                    return false;
                }
            } else {
                rightModeParameters.setPeerTimeout(peeredMode, leftModeParameters.getPeerTimeout(peeredMode));
            }
        } else {
            if (rightModeParameters.getPeerTimeout(peeredMode) != 0) {
                leftModeParameters.setPeerTimeout(peeredMode, rightModeParameters.getPeerTimeout(peeredMode));
            }
        }
        return true;
    }
}
