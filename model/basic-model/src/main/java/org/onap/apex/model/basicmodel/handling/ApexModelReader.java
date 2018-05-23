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

package org.onap.apex.model.basicmodel.handling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.onap.apex.model.basicmodel.concepts.AxConcept;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;
import org.onap.policy.apex.model.utilities.ResourceUtils;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class reads an Apex concept from an XML file into a Java Apex Concept {@link AxConcept}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <C> the type of Apex concept to read, must be a sub class of {@link AxConcept}
 */
public class ApexModelReader<C extends AxConcept> {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelReader.class);

    // Regular expressions for checking input types
    private static final String XML_INPUT_TYPE_REGEXP = "^\\s*<\\?xml.*>\\s*"; // (starts with <?xml...>
    private static final String JSON_INPUT_TYPE_REGEXP = "^\\s*[\\(\\{\\[][\\s+\\S]*[\\)\\}\\]]"; // starts with some kind of bracket [ or (
    // or {, then has something, then has
    // and has a close bracket

    //  The root class of the concept we are reading
    private final Class<C> rootConceptClass;

    // The unmarshaller for the Apex concepts
    private Unmarshaller unmarshaller = null;

    // All read concepts are validated after reading if this flag is set
    private boolean validateFlag = true;

    /**
     * Constructor, initiates the reader with validation on.
     *
     * @param rootConceptClass the root concept class for concept reading
     * @throws ApexModelException the apex concept reader exception
     */
    public ApexModelReader(final Class<C> rootConceptClass) throws ApexModelException {
        // Save the root concept class
        this.rootConceptClass = rootConceptClass;

        try {
            final JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[] {rootConceptClass}, null);

            // Set up the unmarshaller to carry out validation
            unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        }
        catch (final JAXBException e) {
            LOGGER.error("Unable to set JAXB context", e);
            throw new ApexModelException("Unable to set JAXB context", e);
        }
    }

    /**
     * Constructor, initiates the reader.
     *
     * @param rootConceptClass the root concept class for concept reading
     * @param validate whether to perform validation by default
     * @throws ApexModelException the apex concept reader exception
     */
    public ApexModelReader(final Class<C> rootConceptClass, final boolean validate) throws ApexModelException {
        this(rootConceptClass);
        this.validateFlag = validate;
    }

    /**
     * Set the schema to use for reading XML files.
     *
     * @param schemaFileName the schema file to use
     * @throws ApexModelException if the schema cannot be set
     */
    public void setSchema(final String schemaFileName) throws ApexModelException {
        // Has a schema been set
        if (schemaFileName != null) {
            try {
                // Set the concept schema
                final URL schemaURL = ResourceUtils.getURLResource(schemaFileName);
                final Schema apexConceptSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaURL);
                unmarshaller.setSchema(apexConceptSchema);
            }
            catch (final Exception e) {
                LOGGER.error("Unable to load schema ", e);
                throw new ApexModelException("Unable to load schema", e);
            }
        }
        else {
            // Clear the schema
            unmarshaller.setSchema(null);
        }
    }

    /**
     * This method checks the specified Apex concept XML file and reads it into an Apex concept.
     *
     * @param apexConceptStream the apex concept stream
     * @return the Apex concept
     * @throws ApexModelException on reading exceptions
     */
    public C read(final InputStream apexConceptStream) throws ApexModelException {
        Assertions.argumentNotNull(apexConceptStream, "concept stream may not be null");

        return read(new BufferedReader(new InputStreamReader(apexConceptStream)));
    }

    /**
     * This method reads the specified Apex reader into an Apex concept.
     *
     * @param apexConceptReader the apex concept reader
     * @return the Apex concept
     * @throws ApexModelException on reading exceptions
     */
    public C read(final BufferedReader apexConceptReader) throws ApexModelException {
        Assertions.argumentNotNull(apexConceptReader, "concept reader may not be null");

        LOGGER.entry("reading Apex concept into a String . . .");

        // Get the Apex concept as a string
        String apexConceptString = null;
        try {
            apexConceptString = TextFileUtils.getReaderAsString(apexConceptReader).trim();
        }
        catch (final IOException e) {
            throw new ApexModelException("Unable to read Apex concept ", e);
        }

        return read(apexConceptString);
    }

    /**
     * This method reads the specified Apex string into an Apex concept.
     *
     * @param apexConceptString the apex concept as a string
     * @return the Apex concept
     * @throws ApexModelException on reading exceptions
     */
    public C read(final String apexConceptString) throws ApexModelException {
        Assertions.argumentNotNull(apexConceptString, "concept string may not be null");

        LOGGER.entry("reading Apex concept from string . . .");

        final String apexString = apexConceptString.trim();

        // Set the type of input for this stream
        setInputType(apexString);

        // The Apex Concept
        C apexConcept = null;

        // Use JAXB to read and verify the Apex concept XML file
        try {
            // Load the configuration file
            final StreamSource source = new StreamSource(new StringReader(apexString));
            final JAXBElement<C> rootElement = unmarshaller.unmarshal(source, rootConceptClass);
            apexConcept = rootElement.getValue();
        }
        catch (final JAXBException e) {
            throw new ApexModelException("Unable to unmarshal Apex concept ", e);
        }

        LOGGER.debug("reading of Apex concept {} completed");

        // Check if the concept should be validated
        if (validateFlag) {
            // Validate the configuration file
            final AxValidationResult validationResult = apexConcept.validate(new AxValidationResult());
            LOGGER.debug(validationResult.toString());
            if (validationResult.isValid()) {
                return apexConcept;
            }
            else {
                LOGGER.error("Apex concept validation failed" + validationResult.toString());
                throw new ApexModelException("Apex concept validation failed" + validationResult.toString());
            }
        }
        else {
            // No validation check
            return apexConcept;
        }
    }

    /**
     * Gets the value of the validation flag.
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

    /**
     * Set the type of input for the concept reader.
     *
     * @param apexConceptString The stream with
     * @throws ApexModelException on errors setting input type
     */
    private void setInputType(final String apexConceptString) throws ApexModelException {
        // Check the input type
        if (Pattern.compile(JSON_INPUT_TYPE_REGEXP).matcher(apexConceptString).find()) {
            //is json
            try {
                unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
                unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
            }
            catch (final Exception e) {
                LOGGER.warn("JAXB error setting marshaller for JSON Input", e);
                throw new ApexModelException("JAXB error setting marshaller for JSON Input", e);
            }
        }
        else if (Pattern.compile(XML_INPUT_TYPE_REGEXP).matcher(apexConceptString).find()) {
            //is xml
            try {
                unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML);
            }
            catch (final Exception e) {
                LOGGER.warn("JAXB error setting marshaller for XML Input", e);
                throw new ApexModelException("JAXB error setting marshaller for XML Input", e);
            }
        }
        else {
            LOGGER.warn("format of input for Apex concept is neither JSON nor XML");
            throw new ApexModelException("format of input for Apex concept is neither JSON nor XML");
        }
    }

}
