/*
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

package org.onap.policy.apex.model.utilities;

/**
 * The Class Assertions is a template class that is used as a shorthand for assertions in the source code.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class Assertions {
    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private Assertions() {
    }

    /**
     * Checks if a string parameter matches a regular expression.
     *
     * @param parameterName the string parameter name
     * @param parameterValue the string parameter value
     * @param pattern The regular expression
     * @return the trimmed string
     */
    public static String validateStringParameter(final String parameterName, final String parameterValue, final String pattern) {
        argumentNotNull(parameterName, "parameter name is null");
        argumentNotNull(parameterValue, "parameter \"" + parameterName + "\" is null");
        argumentNotNull(pattern, "parameter pattern is null");

        final String trimmedValue = parameterValue.trim();
        if (trimmedValue.matches(pattern)) {
            return trimmedValue;
        }
        else {
            throw new IllegalArgumentException(
                    "parameter \"" + parameterName + "\": value \"" + parameterValue + "\", does not match regular expression \"" + pattern + "\"");
        }
    }

    /**
     * Used as a shorthand to check that method arguments are not null, throws IllegalArgumentException on error.
     *
     * @param <T> the generic type of the argument to check
     * @param value the value of the type
     * @param message the error message to issue
     */
    public static <T> void argumentNotNull(final T value, final String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Used as a shorthand to check that method arguments are not false, throws IllegalArgumentException on error.
     *
     * @param value the value to check if false
     * @param message the error message to issue
     */
    public static void argumentNotFalse(final boolean value, final String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Used as a shorthand to check that method arguments are not null, throws an exception of the specified type on error.
     *
     * @param <T> the generic type of the argument to check
     * @param <E> the exception to throw if incoming value is null
     * @param value the value of the type
     * @param exceptionClass the class of exception to return an instance of
     * @param message the error message to issue
     * @throws E an instance of the passed Exception Class
     */
    public static <T, E extends Exception> void argumentNotNull(final T value, final Class<E> exceptionClass, final String message) throws E {
        if (value == null) {
            // Instantiate the exception and throw it
            try {
                throw exceptionClass.getConstructor(String.class).newInstance(message);
            }
            catch (final Exception errorException) {
                throw new IllegalArgumentException(message, errorException);
            }
        }
    }

    /**
     * Used as a shorthand to check that method argument is not false, throws an exception of the specified type on error.
     *
     * @param <E> the exception to throw if incoming value is false
     * @param value the value to check if false
     * @param exceptionClass the class of exception to return an instance of
     * @param message the error message to issue
     * @throws E an instance of the passed Exception Class
     */
    public static <E extends Exception> void argumentNotFalse(final boolean value, final Class<E> exceptionClass, final String message) throws E {
        if (!value) {
            // Instantiate the exception and throw it
            try {
                throw exceptionClass.getConstructor(String.class).newInstance(message);
            }
            catch (final Exception errorException) {
                throw new IllegalArgumentException(message, errorException);
            }
        }
    }

    /**
     * Used as a shorthand to check that an object is an instance of a given class, throws IllegalArgumentException on error.
     *
     * @param <T> the generic type of the argument to check
     * @param objectInstance the object instance for which to check the class
     * @param requiredClass the class that the object should be an instance of
     * @throws IllegalArgumentException if the incoming object is not an instance of requiredClass
     */
    public static <T> void instanceOf(final Object objectInstance, final Class<T> requiredClass) {
        if (!requiredClass.isAssignableFrom(objectInstance.getClass())) {
            throw new IllegalArgumentException(objectInstance.getClass().getCanonicalName() + " is not an instance of " + requiredClass.getCanonicalName());
        }
    }

    /**
     * Used as a shorthand to check that an instance of a class can be an instance of a given class, throws IllegalArgumentException on error.
     *
     * @param <T> the generic type of the argument to check
     * @param checkClass the class to check
     * @param requiredClass the class that the object should be an instance of
     * @throws IllegalArgumentException if the incoming object is not an instance of requiredClass
     */
    public static <T> void assignableFrom(final Class<?> checkClass, final Class<T> requiredClass) {
        if (!requiredClass.isAssignableFrom(checkClass)) {
            throw new IllegalArgumentException(checkClass.getCanonicalName() + " is not an instance of " + requiredClass.getCanonicalName());
        }
    }
}
