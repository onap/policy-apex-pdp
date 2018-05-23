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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF: checkstyle:IllegalImport
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
//CHECKSTYLE:ON: checkstyle:IllegalImport

/**
 * This class is a utility class that builds a class with a set of user defined fields. It is used to get the Type of fields in Java schemas<br>
 * For more information see:<br>
 * <a href="http://stackoverflow.com/questions/39401083/class-forname-equivalent-for-creating-parameterizedtypes-from-string">
 * http://stackoverflow.com/questions/39401083/class-forname-equivalent-for-creating-parameterizedtypes-from-string</a><br>
 * <a href="https://github.com/KetothXupack/stackoverflow-answers/tree/master/q39401083">
 * https://github.com/KetothXupack/stackoverflow-answers/tree/master/q39401083</a><br>
 */
@SuppressWarnings("restriction")
public class ClassBuilder {
    private final Class<?> clazz;
    private final List<ClassBuilder> parameters = new ArrayList<>();

    /**
     * Constructor that sets the class for the class builder.
     *
     * @param clazz the class of the class builder
     */
    public ClassBuilder(final Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Creates a {@link ClassBuilder} instance for a class with the given class name.
     *
     * @param className the class name of the class builder to create
     * @return the class builder that is created
     */
    public static ClassBuilder parse(final String className) {
        try {
            return new ClassBuilder(Class.forName(className));
        }
        catch (ClassNotFoundException e) {
            try {
                return new ClassBuilder(Class.forName("java.lang." + className));
            }
            catch (Exception ignore) {
                throw new IllegalArgumentException("Class '" + className + "' not found. Also looked for a class called 'java.lang." + className + "'", e);
            }
        }
    }

    /**
     * Adds a field to the class builder. Each field is itself a class builder.
     *
     * @param fieldBuilder the class builder for the field
     */
    public void add(final ClassBuilder fieldBuilder) {
        parameters.add(fieldBuilder);
    }

    /**
     * Builds the {@link Type} of the class.
     *
     * @return the {@link Type} of the class
     */
    public Type build() {
        // class is not parameterized
        if (parameters.isEmpty()) {
            return clazz;
        }
        Type[] paramtypes = new Type[parameters.size()];
        int i = 0;
        for (ClassBuilder classBuilder : parameters) {
            paramtypes[i++] = classBuilder.build();
        }
        return ParameterizedTypeImpl.make(clazz, paramtypes, null);
    }
}
