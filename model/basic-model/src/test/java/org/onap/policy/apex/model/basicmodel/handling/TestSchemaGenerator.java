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

package org.onap.policy.apex.model.basicmodel.handling;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestSchemaGenerator {

    @Test
    public void test() throws IOException {
        final ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos0));

        final String[] args0 = {};
        ApexSchemaGenerator.main(args0);
        assertTrue(baos0.toString().contains("usage: ApexSchemaGenerator apex-root-class [schema-file-name]"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos1));

        final String[] args1 = { "hello", "goodbye", "here" };
        ApexSchemaGenerator.main(args1);
        assertTrue(baos1.toString().contains("usage: ApexSchemaGenerator apex-root-class [schema-file-name]"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos2));

        final String[] args2 = { "hello", "goodbye" };
        ApexSchemaGenerator.main(args2);
        assertTrue(baos2.toString().contains("error on Apex schema output"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        final ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos3));

        final String[] args3 = { "hello" };
        ApexSchemaGenerator.main(args3);
        assertTrue(baos3.toString().contains("could not create JAXB context, root class hello not found"));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        final ByteArrayOutputStream baos4 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos4));

        final String[] args4 = { "org.onap.policy.apex.model.basicmodel.concepts.AxModel" };
        ApexSchemaGenerator.main(args4);
        assertTrue(baos4.toString().contains("targetNamespace=\"http://www.onap.org/policy/apex-pdp\""));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        final ByteArrayOutputStream baos5 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos5));

        final File tempFile = File.createTempFile("ApexSchemaGeneratorTest", "xsd");
        final String[] args5 =
                { "org.onap.policy.apex.model.basicmodel.concepts.AxModel", tempFile.getCanonicalPath() };

        ApexSchemaGenerator.main(args5);
        assertTrue(tempFile.length() > 100);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        tempFile.delete();
    }
}
