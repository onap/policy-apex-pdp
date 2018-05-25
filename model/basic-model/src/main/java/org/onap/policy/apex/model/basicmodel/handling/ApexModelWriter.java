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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.w3c.dom.Document;

/**
 * This class writes an Apex concept to an XML file or JSON file from a Java Apex Concept.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 * @param <C> the type of Apex concept to write, must be a sub class of {@link AxConcept}
 */
public class ApexModelWriter<C extends AxConcept> {
	private static final String CONCEPT_MAY_NOT_BE_NULL = "concept may not be null";
    private static final String CONCEPT_WRITER_MAY_NOT_BE_NULL = "concept writer may not be null";
	private static final String CONCEPT_STREAM_MAY_NOT_BE_NULL = "concept stream may not be null";

	// Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelWriter.class);

    // Writing as JSON or XML
    private boolean jsonOutput = false;

    // The list of fields to output as CDATA
    private final Set<String> cDataFieldSet = new TreeSet<>();

    // The Marshaller for the Apex concepts
    private Marshaller marshaller = null;

    // All written concepts are validated before writing if this flag is set
    private boolean validateFlag = true;

    /**
     * Constructor, initiates the writer.
     *
     * @param rootConceptClass the root concept class for concept reading
     * @throws ApexModelException the apex concept writer exception
     */
    public ApexModelWriter(final Class<C> rootConceptClass) throws ApexModelException {
        // Set up Eclipselink for XML and JSON output
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

        try {
            final JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[] {rootConceptClass}, null);

            // Set up the unmarshaller to carry out validation
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        }
        catch (final JAXBException e) {
            LOGGER.error("JAXB marshaller creation exception", e);
            throw new ApexModelException("JAXB marshaller creation exception", e);
        }
    }

    /**
     * The set of fields to be output as CDATA.
     *
     * @return the set of fields
     */
    public Set<String> getCDataFieldSet() {
        return cDataFieldSet;
    }

    /**
     * Return true if JSON output enabled, XML output if false.
     *
     * @return true for JSON output
     */
    public boolean isJsonOutput() {
        return jsonOutput;
    }

    /**
     * Set the value of JSON output, true for JSON output, false for XML output.
     *
     * @param jsonOutput true for JSON output
     * @throws ApexModelException on errors setting output type
     */
    public void setJsonOutput(final boolean jsonOutput) throws ApexModelException {
        this.jsonOutput = jsonOutput;

        // Set up output specific parameters
        if (this.jsonOutput) {
            try {
                marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
                marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
            }
            catch (final Exception e) {
                LOGGER.warn("JAXB error setting marshaller for JSON output", e);
                throw new ApexModelException("JAXB error setting marshaller for JSON output", e);
            }
        }
        else {
            try {
                marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML);
            }
            catch (final Exception e) {
                LOGGER.warn("JAXB error setting marshaller for XML output", e);
                throw new ApexModelException("JAXB error setting marshaller for XML output", e);
            }
        }
    }

    /**
     * This method validates the Apex concept then writes it into a stream.
     *
     * @param concept the concept to write
     * @param apexConceptStream the stream to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    public void write(final C concept, final OutputStream apexConceptStream) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);
        Assertions.argumentNotNull(apexConceptStream, CONCEPT_STREAM_MAY_NOT_BE_NULL);
        
        this.write(concept, new OutputStreamWriter(apexConceptStream));
    }

    /**
     * This method validates the Apex concept then writes it into a writer.
     *
     * @param concept the concept to write
     * @param apexConceptWriter the writer to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    public void write(final C concept, final Writer apexConceptWriter) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);
        Assertions.argumentNotNull(apexConceptWriter, CONCEPT_WRITER_MAY_NOT_BE_NULL);

        // Check if we should validate the concept
        if (validateFlag) {
            // Validate the concept first
            final AxValidationResult validationResult = concept.validate(new AxValidationResult());
            LOGGER.debug(validationResult.toString());
            if (!validationResult.isValid()) {
                LOGGER.warn(validationResult.toString());
                throw new ApexModelException("Apex concept xml (" + concept.getKey().getID() + ") validation failed");
            }
        }

        if (jsonOutput) {
            writeJSON(concept, apexConceptWriter);
        }
        else {
            writeXML(concept, apexConceptWriter);
        }
    }

    /**
     * This method writes the Apex concept into a writer in XML format.
     *
     * @param concept the concept to write
     * @param apexConceptWriter the writer to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    private void writeXML(final C concept, final Writer apexConceptWriter) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);

        LOGGER.debug("writing Apex concept XML . . .");

        try {
            // Write the concept into a DOM document, then transform to add CDATA fields and pretty print, then write out the result
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final Document document = docBuilderFactory.newDocumentBuilder().newDocument();

            // Marshal the concept into the empty document.
            marshaller.marshal(concept, document);

            // Transform the DOM to the output stream
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer domTransformer = transformerFactory.newTransformer();

            // Pretty print
            try {
                domTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                // May fail if not using XALAN XSLT engine. But not in any way vital
                domTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            }
            catch (final Exception ignore) {
            		// We ignore exceptions here and catch errors below
            }

            // Convert the cDataFieldSet into a space delimited string
            domTransformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, cDataFieldSet.toString().replaceAll("[\\[\\]\\,]", " "));
            domTransformer.transform(new DOMSource(document), new StreamResult(apexConceptWriter));
        }
        catch (JAXBException | TransformerException | ParserConfigurationException e) {
            LOGGER.warn("Unable to marshal Apex concept XML", e);
            throw new ApexModelException("Unable to marshal Apex concept XML", e);
        }
        LOGGER.debug("wrote Apex concept XML");
    }

    /**
     * This method writes the Apex concept into a writer in JSON format.
     *
     * @param concept the concept to write
     * @param apexConceptWriter the writer to write to
     * @throws ApexModelException on validation or writing exceptions
     */
    private void writeJSON(final C concept, final Writer apexConceptWriter) throws ApexModelException {
        Assertions.argumentNotNull(concept, CONCEPT_MAY_NOT_BE_NULL);

        LOGGER.debug("writing Apex concept JSON . . .");

        try {
            marshaller.marshal(concept, apexConceptWriter);
        }
        catch (final JAXBException e) {
            LOGGER.warn("Unable to marshal Apex concept JSON", e);
            throw new ApexModelException("Unable to marshal Apex concept JSON", e);
        }
        LOGGER.debug("wrote Apex concept JSON");
    }

    /**
     * Gets the validation flag value.
     *
     * @return the validation flag value
     */
    public boolean getValidateFlag() {
        return validateFlag;
    }

    /**
     * Sets the validation flag.
     *
     * @param validateFlag the validation flag value
     */
    public void setValidateFlag(final boolean validateFlag) {
        this.validateFlag = validateFlag;
    }
}
