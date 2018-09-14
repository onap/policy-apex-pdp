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

package org.onap.policy.apex.auth.clieditor;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.SortedMap;

import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.modelapi.ApexModelFactory;

/**
 * This class instantiates and holds the Apex model being manipulated by the editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexModelHandler {
    private static final String FAILED_FOR_COMMAND = "\" failed for command \"";
    private static final String INVOCATION_OF_SPECIFIED_METHOD = "invocation of specified method \"";
    private ApexModel apexModel = null;

    /**
     * Create the Apex Model with the properties specified.
     *
     * @param properties The properties of the Apex model
     */
    public ApexModelHandler(final Properties properties) {
        apexModel = new ApexModelFactory().createApexModel(properties, true);
    }

    /**
     * Create the Apex Model with the properties specified and load it from a file.
     *
     * @param properties The properties of the Apex model
     * @param modelFileName The name of the model file to edit
     */
    public ApexModelHandler(final Properties properties, final String modelFileName) {
        this(properties);

        if (modelFileName == null) {
            return;
        }

        final ApexApiResult result = apexModel.loadFromFile(modelFileName);
        if (result.isNok()) {
            throw new CommandLineException(result.getMessages().get(0));
        }
    }

    /**
     * Execute a command on the Apex model.
     *
     * @param command The command to execute
     * @param argumentValues Arguments of the command
     * @param writer A writer to which to write output
     * @return the result of the executed command
     */
    public ApexApiResult executeCommand(final CommandLineCommand command,
                    final SortedMap<String, CommandLineArgumentValue> argumentValues, final PrintWriter writer) {
        // Get the method
        final Method apiMethod = getCommandMethod(command);

        // Get the method arguments
        final Object[] parameterArray = getParameterArray(command, argumentValues, apiMethod);

        try {
            final Object returnObject = apiMethod.invoke(apexModel, parameterArray);

            if (returnObject instanceof ApexApiResult) {
                final ApexApiResult result = (ApexApiResult) returnObject;
                writer.println(result);
                return result;
            } else {
                throw new CommandLineException(INVOCATION_OF_SPECIFIED_METHOD + command.getApiMethod()
                                + FAILED_FOR_COMMAND + command.getName()
                                + "\" the returned object is not an instance of ApexAPIResult");
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            writer.println(INVOCATION_OF_SPECIFIED_METHOD + command.getApiMethod() + FAILED_FOR_COMMAND
                            + command.getName() + "\"");
            e.printStackTrace(writer);
            throw new CommandLineException(INVOCATION_OF_SPECIFIED_METHOD + command.getApiMethod() + FAILED_FOR_COMMAND
                            + command.getName() + "\"", e);
        } catch (final InvocationTargetException e) {
            writer.println(INVOCATION_OF_SPECIFIED_METHOD + command.getApiMethod() + FAILED_FOR_COMMAND
                            + command.getName() + "\"");
            e.getCause().printStackTrace(writer);
            throw new CommandLineException(INVOCATION_OF_SPECIFIED_METHOD + command.getApiMethod() + FAILED_FOR_COMMAND
                            + command.getName() + "\"", e);
        }
    }

    /**
     * Find the API method for the command.
     *
     * @param command The command
     * @return the API method
     */
    private Method getCommandMethod(final CommandLineCommand command) {
        final String className = command.getApiClassName();
        final String methodName = command.getApiMethodName();

        try {
            final Class<? extends Object> apiClass = Class.forName(className);
            for (final Method apiMethod : apiClass.getMethods()) {
                if (apiMethod.getName().equals(methodName)) {
                    return apiMethod;
                }
            }
            throw new CommandLineException("specified method \"" + command.getApiMethod()
                            + "\" not found for command \"" + command.getName() + "\"");
        } catch (final ClassNotFoundException e) {
            throw new CommandLineException("specified class \"" + command.getApiMethod() + "\" not found for command \""
                            + command.getName() + "\"", e);
        }
    }

    /**
     * Get the arguments of the command as an ordered array of objects ready for the method.
     *
     * @param command the command that invoked the method
     * @param argumentValues the argument values for the method
     * @param apiMethod the method itself
     * @return the argument list
     */
    private Object[] getParameterArray(final CommandLineCommand command,
                    final SortedMap<String, CommandLineArgumentValue> argumentValues, final Method apiMethod) {
        final Object[] parameterArray = new Object[argumentValues.size()];

        int item = 0;
        try {
            for (final Class<?> parametertype : apiMethod.getParameterTypes()) {
                final String parameterValue = argumentValues.get(command.getArgumentList().get(item).getArgumentName())
                                .getValue();

                if (parametertype.equals(boolean.class)) {
                    parameterArray[item] = Boolean.valueOf(parameterValue);
                } else {
                    parameterArray[item] = parameterValue;
                }
                item++;
            }
        } catch (final Exception e) {
            throw new CommandLineException("number of argument mismatch on method \"" + command.getApiMethod()
                            + "\" for command \"" + command.getName() + "\"", e);
        }

        return parameterArray;
    }

    /**
     * Save the model to a string.
     *
     * @param messageWriter the writer to write status messages to
     * @return the string
     */
    public String writeModelToString(final PrintWriter messageWriter) {
        final ApexApiResult result = apexModel.listModel();

        if (result.isOk()) {
            return result.getMessage();
        } else {
            return null;
        }
    }
}
