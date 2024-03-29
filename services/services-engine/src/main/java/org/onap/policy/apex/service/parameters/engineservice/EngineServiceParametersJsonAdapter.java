/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.service.parameters.engineservice;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.context.parameters.SchemaHelperParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.TaskParameters;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterRuntimeException;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class deserializes engine service parameters from JSON format. The class produces an
 * {@link EngineServiceParameters} instance from incoming JSON read from a configuration file in JSON format.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngineServiceParametersJsonAdapter
                implements JsonSerializer<EngineParameters>, JsonDeserializer<EngineParameters> {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngineServiceParametersJsonAdapter.class);

    private static final String PARAMETER_CLASS_NAME = "parameterClassName";

    // @formatter:off
    private static final String CONTEXT_PARAMETERS      = "contextParameters";
    private static final String DISTRIBUTOR_PARAMETERS  = "distributorParameters";
    private static final String LOCK_MANAGER_PARAMETERS = "lockManagerParameters";
    private static final String PERSISTOR_PARAMETERS    = "persistorParameters";
    private static final String SCHEMA_PARAMETERS       = "schemaParameters";
    private static final String EXECUTOR_PARAMETERS     = "executorParameters";
    // @formatter:on

    private static StandardCoder standardCoder = new StandardCoder();

    /**
     * {@inheritDoc}.
     */
    @Override
    public JsonElement serialize(final EngineParameters src, final Type typeOfSrc,
                    final JsonSerializationContext context) {
        final var returnMessage = "serialization of Apex parameters to Json is not supported";
        LOGGER.error(returnMessage);
        throw new ParameterRuntimeException(returnMessage);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public EngineParameters deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context) {
        final var engineParametersJsonObject = json.getAsJsonObject();

        final var engineParameters = new EngineParameters();

        // Deserialise context parameters, they may be a subclass of the ContextParameters class
        engineParameters.setContextParameters(
                        (ContextParameters) context.deserialize(engineParametersJsonObject, ContextParameters.class));

        // Context parameter wrangling
        getContextParameters(engineParametersJsonObject, engineParameters, context);

        // Executor parameter wrangling
        getExecutorParameters(engineParametersJsonObject, engineParameters, context);

        // Task parameter wrangling
        getTaskParametersList(engineParametersJsonObject, engineParameters);
        return engineParameters;
    }

    /**
     * Method to get the task parameters list for Apex.
     *
     * @param engineParametersJsonObject The input JSON
     * @param engineParameters The output parameters
     */
    private void getTaskParametersList(JsonObject engineParametersJsonObject, EngineParameters engineParameters) {
        final JsonElement parametersElement = engineParametersJsonObject.get("taskParameters");

        // configurable parameters are optional so if the element does not exist, just return
        if (parametersElement == null) {
            return;
        }
        List<TaskParameters> parametersList = new ArrayList<>();
        parametersElement.getAsJsonArray().forEach(taskParam -> {
            TaskParameters parameters = null;
            try {
                parameters = standardCoder.decode(standardCoder.encode(taskParam), TaskParameters.class);
            } catch (CoderException e) {
                throw new ParameterRuntimeException("Error reading taskParameters from the config json provided.");
            }
            parametersList.add(parameters);
        });
        engineParameters.setTaskParameters(parametersList);
    }

    /**
     * Get the context parameters for Apex.
     *
     * @param engineParametersJsonObject The input JSON
     * @param engineParameters The output parameters
     * @param context the JSON context
     */
    private void getContextParameters(final JsonObject engineParametersJsonObject,
                    final EngineParameters engineParameters, final JsonDeserializationContext context) {
        final JsonElement contextParametersElement = engineParametersJsonObject.get(CONTEXT_PARAMETERS);

        // Context parameters are optional so if the element does not exist, just return
        if (contextParametersElement == null) {
            return;
        }

        // We do this because the JSON parameters may be for a subclass of ContextParameters
        final var contextParameters = (ContextParameters) deserializeParameters(CONTEXT_PARAMETERS,
                        contextParametersElement, context);

        // We know this will work because if the context parameters was not a Json object, the
        // previous deserializeParameters() call would not have worked
        final var contextParametersObject = engineParametersJsonObject.get(CONTEXT_PARAMETERS).getAsJsonObject();

        // Now get the distributor, lock manager, and persistence parameters
        final JsonElement distributorParametersElement = contextParametersObject.get(DISTRIBUTOR_PARAMETERS);
        if (distributorParametersElement != null) {
            contextParameters.setDistributorParameters((DistributorParameters) deserializeParameters(
                            DISTRIBUTOR_PARAMETERS, distributorParametersElement, context));
        }

        final JsonElement lockManagerParametersElement = contextParametersObject.get(LOCK_MANAGER_PARAMETERS);
        if (lockManagerParametersElement != null) {
            contextParameters.setLockManagerParameters((LockManagerParameters) deserializeParameters(
                            LOCK_MANAGER_PARAMETERS, lockManagerParametersElement, context));
        }

        final JsonElement persistorParametersElement = contextParametersObject.get(PERSISTOR_PARAMETERS);
        if (persistorParametersElement != null) {
            contextParameters.setPersistorParameters((PersistorParameters) deserializeParameters(PERSISTOR_PARAMETERS,
                            persistorParametersElement, context));
        }

        // Schema Handler parameter wrangling
        getSchemaHandlerParameters(contextParametersObject, contextParameters, context);

        // Get the engine plugin parameters
        engineParameters.setContextParameters(contextParameters);
    }

    /**
     * Get the executor parameters for Apex.
     *
     * @param engineParametersJsonObject The input JSON
     * @param engineParameters The output parameters
     * @param context the JSON context
     */
    private void getExecutorParameters(final JsonObject engineParametersJsonObject,
                    final EngineParameters engineParameters, final JsonDeserializationContext context) {
        final JsonElement executorParametersElement = engineParametersJsonObject.get(EXECUTOR_PARAMETERS);

        // Executor parameters are mandatory so if the element does not exist throw an exception
        if (executorParametersElement == null) {
            final String returnMessage = "no \"" + EXECUTOR_PARAMETERS
                            + "\" entry found in parameters, at least one executor parameter entry must be specified";
            LOGGER.error(returnMessage);
            throw new ParameterRuntimeException(returnMessage);
        }

        // Deserialize the executor parameters
        final var executorParametersJsonObject = engineParametersJsonObject.get(EXECUTOR_PARAMETERS)
                        .getAsJsonObject();

        for (final Entry<String, JsonElement> executorEntries : executorParametersJsonObject.entrySet()) {
            final var executorParameters = (ExecutorParameters) deserializeParameters(
                            EXECUTOR_PARAMETERS + ':' + executorEntries.getKey(), executorEntries.getValue(), context);
            engineParameters.getExecutorParameterMap().put(executorEntries.getKey(), executorParameters);
        }
    }

    /**
     * Get the schema parameters for Apex.
     *
     * @param contextParametersJsonObject The input JSON
     * @param contextParameters The output parameters
     * @param context the JSON context
     */
    private void getSchemaHandlerParameters(final JsonObject contextParametersJsonObject,
                    final ContextParameters contextParameters, final JsonDeserializationContext context) {
        final JsonElement schemaParametersElement = contextParametersJsonObject.get(SCHEMA_PARAMETERS);

        // Insert the default Java schema helper
        contextParameters.getSchemaParameters().getSchemaHelperParameterMap()
                        .put(SchemaParameters.DEFAULT_SCHEMA_FLAVOUR, new JavaSchemaHelperParameters());

        // Context parameters are optional so if the element does not exist, just return
        if (schemaParametersElement == null) {
            return;
        }

        // Deserialize the executor parameters
        final var schemaHelperParametersJsonObject = contextParametersJsonObject.get(SCHEMA_PARAMETERS)
                        .getAsJsonObject();

        for (final Entry<String, JsonElement> schemaHelperEntries : schemaHelperParametersJsonObject.entrySet()) {
            contextParameters.getSchemaParameters().getSchemaHelperParameterMap().put(schemaHelperEntries.getKey(),
                            (SchemaHelperParameters) deserializeParameters(
                                            SCHEMA_PARAMETERS + ':' + schemaHelperEntries.getKey(),
                                            schemaHelperEntries.getValue(), context));
        }
    }

    /**
     * Deserialize a parameter object that's a superclass of the AbstractParameters class.
     *
     * @param parametersLabel Label to use for error messages
     * @param parametersElement The JSON object holding the parameters
     * @param context The GSON context
     * @return the parameters
     * @throws ParameterRuntimeException on errors reading the parameters
     */
    private ParameterGroup deserializeParameters(final String parametersLabel, final JsonElement parametersElement,
                    final JsonDeserializationContext context) {
        JsonObject parametersObject = null;

        // Check that the JSON element is a JSON object
        if (parametersElement.isJsonObject()) {
            parametersObject = parametersElement.getAsJsonObject();
        } else {
            final String returnMessage = "value of \"" + parametersLabel + "\" entry is not a parameter JSON object";
            LOGGER.error(returnMessage);
            throw new ParameterRuntimeException(returnMessage);
        }

        // Get the parameter class name for instantiation in deserialization
        final JsonElement parameterClassNameElement = parametersObject.get(PARAMETER_CLASS_NAME);
        if (parameterClassNameElement == null) {
            final String returnMessage = "could not find field \"" + PARAMETER_CLASS_NAME + "\" in \"" + parametersLabel
                            + "\" entry";
            LOGGER.error(returnMessage);
            throw new ParameterRuntimeException(returnMessage);
        }

        // Check the parameter is a JSON primitive
        if (!parameterClassNameElement.isJsonPrimitive()) {
            final String returnMessage = "value for field \"" + PARAMETER_CLASS_NAME + "\" of \"" + parametersLabel
                            + "\" entry is not a plain string";
            LOGGER.error(returnMessage);
            throw new ParameterRuntimeException(returnMessage);
        }

        // Check the parameter has a value
        final var parameterClassName = parameterClassNameElement.getAsString();
        if (parameterClassName == null || parameterClassName.trim().length() == 0) {
            final String returnMessage = "value for field \"" + PARAMETER_CLASS_NAME + "\" in \"" + parametersLabel
                            + "\" entry is not specified or is blank";
            LOGGER.error(returnMessage);
            throw new ParameterRuntimeException(returnMessage);
        }

        // Deserialize the parameters using GSON
        ParameterGroup parameters = null;
        try {
            parameters = context.deserialize(parametersObject, Class.forName(parameterClassName));
        } catch (JsonParseException | ClassNotFoundException e) {
            final String returnMessage = "failed to deserialize the parameters for \"" + parametersLabel + "\" "
                            + "to parameter class \"" + parameterClassName + "\"\n" + e.getClass().getName() + ": "
                            + e.getMessage();
            LOGGER.error(returnMessage, e);
            throw new ParameterRuntimeException(returnMessage, e);
        }

        return parameters;
    }
}
