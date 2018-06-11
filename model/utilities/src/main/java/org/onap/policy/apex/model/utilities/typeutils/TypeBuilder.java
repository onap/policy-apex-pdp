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

package org.onap.policy.apex.model.utilities.typeutils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

/**
 * This class builds a type from a grammar using ANTLR.
 */
public final class TypeBuilder {
    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private TypeBuilder() {
    }

    /**
     * Builds the Type of the Type string that was input.
     *
     * @param type the java Type as a string
     * @return the Type of the string that was input
     */
    public static Type build(final String type) {
        if (type == null || type.length() == 0) {
            throw new IllegalArgumentException("Blank type string passed to " + TypeBuilder.class.getCanonicalName() + ".build(String type)");
        }

        try {
            final CharStream stream = CharStreams.fromString(type);
            final TokenStream tokenStream = new CommonTokenStream(new ParametrizedTypeLexer(stream));

            final ParametrizedTypeParser parser = new ParametrizedTypeParser(tokenStream);
            parser.removeErrorListeners();
            parser.setErrorHandler(new BailErrorStrategy());
            parser.setBuildParseTree(true);
            return parser.type().value.build();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Failed to build type '" + type + "': " + e, e);
        }
    }

    /**
     * Gets the class of Java Type.
     *
     * @param type the java Type as a string
     * @return the java Type
     */
    public static Class<?> getJavaTypeClass(final String type) {
        return getJavaTypeClass(build(type));
    }

    /**
     * Gets the class of Java Type.
     *
     * @param type the java Type as a Type
     * @return the java Type
     */
    public static Class<?> getJavaTypeClass(final Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        else if (type instanceof ParameterizedType) {
            final Type raw = ((ParameterizedType) type).getRawType();
            if (!(raw instanceof Class<?>)) {
                throw new IllegalArgumentException(
                        "The Parameterised javatype " + type + " with base type " + raw + "  is not a Java 'Class' that can be instantiated");
            }
            return (Class<?>) raw;
        }
        throw new IllegalArgumentException("The Parameterised javatype " + type + " is not a Java 'Type' that has a 'Class'");
    }

    /**
     * Gets the parameters of a Java Type.
     *
     * @param type the Java Type
     * @return the parameters of the java Type
     */
    public static Type[] getJavaTypeParameters(final Type type) {
        if (type instanceof Class<?>) {
            return new Type[0];
        }
        else if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        throw new IllegalArgumentException("\"The Parameterised javatype \" + type + \" is not a Java 'Type' that has parameter types");
    }
}
