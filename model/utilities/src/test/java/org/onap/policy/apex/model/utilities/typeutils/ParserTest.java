/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Java parsing.
 */
public class ParserTest {
    // TODO: Fix this test based on the decision around prameterized vs generic types
    @Ignore
    @Test
    public void testParser() {
        final CharStream stream = CharStreams
                .fromString("java.util.Map<java.util.List<java.lang.Integer>,java.util.Set<java.lang.String>>");
        final TokenStream tokenStream = new CommonTokenStream(new ParametrizedTypeLexer(stream));

        final ParametrizedTypeParser parser = new ParametrizedTypeParser(tokenStream);
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        parser.setBuildParseTree(true);
        assertEquals("java.util.Map<java.util.List<java.lang.Integer>, java.util.Set<java.lang.String>>",
                parser.type().value.build().getTypeName());

    }

    // TODO: Fix this test based on the decision around prameterized vs generic types
    @Ignore
    @Test
    public void testBuilder() throws IllegalArgumentException {
        String typeString = "java.lang.Integer";
        Type ret = TypeBuilder.build(typeString);
        assertEquals(java.lang.Integer.class, TypeBuilder.getJavaTypeClass(ret));

        typeString = "java.util.Map<java.util.List<java.lang.Integer>,java.util.Set<java.lang.String>>";
        ret = TypeBuilder.build(typeString);
        assertEquals("java.util.Map<java.util.List<java.lang.Integer>, java.util.Set<java.lang.String>>",
                ret.getTypeName());
        assertEquals(java.util.Map.class, TypeBuilder.getJavaTypeClass(ret));

        final Type[] args = TypeBuilder.getJavaTypeParameters(ret);
        assertEquals("java.util.List<java.lang.Integer>", args[0].getTypeName());
        assertEquals("java.util.Set<java.lang.String>", args[1].getTypeName());
    }

    @Test
    public void testBoundaryConditions() {
        try {
            TypeBuilder.build(null);
            fail("Test should throw exception");
        } catch (final IllegalArgumentException e) {
            assertEquals(
                    "Blank type string passed to "
                            + "org.onap.policy.apex.model.utilities.typeutils.TypeBuilder.build(String type)",
                    e.getMessage());
        }

        try {
            TypeBuilder.build("org.zooby.Wooby");
            fail("Test should throw exception");
        } catch (final IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                    "Failed to build type 'org.zooby.Wooby': java.lang.IllegalArgumentException: "
                            + "Class 'org.zooby.Wooby' not found. "
                            + "Also looked for a class called 'java.lang.org.zooby.Wooby'");
        }

        assertEquals(TypeBuilder.getJavaTypeClass("java.lang.String"), String.class);
    }
}
