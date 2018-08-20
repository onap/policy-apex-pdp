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


package org.onap.policy.apex.model.basicmodel.handling;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class generates the XML model schema from the given Apex concept classes.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexSchemaGenerator {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexSchemaGenerator.class);

    /**
     * A Main method to allow schema generation from the command line or from maven or scripts.
     *
     * @param args the command line arguments, usage is
     *        {@code ApexSchemaGenerator apex-root-class [schema-file-name]}
     */
    public static void main(final String[] args) {
        PrintStream printStream = null;

        if (args.length == 1) {
            printStream = System.out;
        } else if (args.length == 2) {
            final File schemaFile = new File(args[1]);

            try {
                schemaFile.getParentFile().mkdirs();
                printStream = new PrintStream(schemaFile);
            } catch (final Exception e) {
                LOGGER.error("error on Apex schema output", e);
                return;
            }
        } else {
            LOGGER.error("usage: ApexSchemaGenerator apex-root-class [schema-file-name]");
            return;
        }

        // Get the schema
        final String schema = new ApexSchemaGenerator().generate(args[0]);

        // Output the schema
        printStream.println(schema);

        printStream.close();
    }

    /**
     * Generates the XML schema (XSD) for the Apex model described using JAXB annotations.
     *
     * @param rootClassName the name of the root class for schema generation
     * @return The schema
     */
    public String generate(final String rootClassName) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Class.forName(rootClassName));
        } catch (final ClassNotFoundException e) {
            LOGGER.error("could not create JAXB context, root class " + rootClassName + " not found", e);
            return null;
        } catch (final JAXBException e) {
            LOGGER.error("could not create JAXB context", e);
            return null;
        }

        final ApexSchemaOutputResolver sor = new ApexSchemaOutputResolver();
        try {
            jaxbContext.generateSchema(sor);
        } catch (final IOException e) {
            LOGGER.error("error generating the Apex schema (XSD) file", e);
            return null;
        }

        String schemaString = sor.getSchema();
        schemaString = fixForUnqualifiedBug(schemaString);

        return schemaString;
    }

    /**
     * There is a bug in schema generation that does not specify the elements from Java Maps as
     * being unqualified. This method "hacks" those elements in the schema to fix this, the elements
     * being {@code entry}, {@code key}, and {@code value}
     *
     * @param schemaString The schema in which elements should be fixed
     * @return the string
     */
    private String fixForUnqualifiedBug(final String schemaString) {
        // Fix the "entry" element
        String newSchemaString =
                schemaString.replaceAll("<xs:element name=\"entry\" minOccurs=\"0\" maxOccurs=\"unbounded\">",
                        "<xs:element name=\"entry\" minOccurs=\"0\" maxOccurs=\"unbounded\" form=\"unqualified\">");

        // Fix the "key" element
        newSchemaString =
                newSchemaString.replaceAll("<xs:element name=\"key\"", "<xs:element name=\"key\" form=\"unqualified\"");

        // Fix the "value" element
        newSchemaString = newSchemaString.replaceAll("<xs:element name=\"value\"",
                "<xs:element name=\"value\" form=\"unqualified\"");

        return newSchemaString;
    }

    /**
     * This inner class is used to receive the output of schema generation from the JAXB schema
     * generator.
     */
    private class ApexSchemaOutputResolver extends SchemaOutputResolver {
        private final StringWriter stringWriter = new StringWriter();

        /*
         * (non-Javadoc)
         *
         * @see javax.xml.bind.SchemaOutputResolver#createOutput(java.lang.String, java.lang.String)
         */
        @Override
        public Result createOutput(final String namespaceUri, final String suggestedFileName) throws IOException {
            final StreamResult result = new StreamResult(stringWriter);
            result.setSystemId(suggestedFileName);
            return result;
        }

        /**
         * Get the schema from the string writer created in the {@link createOutput} method.
         *
         * @return the schema generated by JAXB
         */
        public String getSchema() {
            return stringWriter.toString();
        }
    }
}
