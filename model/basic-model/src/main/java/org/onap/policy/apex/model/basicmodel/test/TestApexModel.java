/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import lombok.AllArgsConstructor;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.dao.ApexDaoFactory;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelFileWriter;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelWriter;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class tests reading and writing of Apex models to file and to a database using JPA. It also tests validation of
 * Apex models. This class is designed for use in unit tests in modules that define Apex models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <M> the generic type
 */
@AllArgsConstructor
public class TestApexModel<M extends AxModel> {
    private static final String MODEL_IS_INVALID = "model is invalid ";
    private static final String ERROR_PROCESSING_FILE = "error processing file ";
    private static final String TEST_MODEL_UNEQUAL_STR = "test model does not equal model read from XML file ";
    private static final String TEMP_FILE_CREATE_ERR_STR = "error creating temporary file for Apex model";

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestApexModel.class);

    // The root model class that specifies the root to import and export from
    private final Class<M> rootModelClass;

    // The class that provides the model
    private TestApexModelCreator<M> modelCreator = null;

    /**
     * Get a test Apex model using the model creator.
     *
     * @return the test Apex model
     */
    public final M getModel() {
        return modelCreator.getModel();
    }

    /**
     * Test write and read in XML format.
     *
     * @throws ApexException on write/read errors
     */
    public final void testApexModelWriteReadXml() throws ApexException {
        LOGGER.debug("running testApexModelWriteReadXML . . .");

        final var model = modelCreator.getModel();

        // Write the file to disk
        File xmlFile;

        try {
            xmlFile = File.createTempFile("ApexModel", ".xml");
            xmlFile.deleteOnExit();
        } catch (final Exception e) {
            LOGGER.warn(TEMP_FILE_CREATE_ERR_STR, e);
            throw new ApexException(TEMP_FILE_CREATE_ERR_STR, e);
        }
        new ApexModelFileWriter<M>(true).apexModelWriteXmlFile(model, rootModelClass, xmlFile.getPath());

        // Read the file from disk
        final ApexModelReader<M> modelReader = new ApexModelReader<>(rootModelClass);

        try {
            final var apexModelUrl = ResourceUtils.getLocalFile(xmlFile.getAbsolutePath());
            final var fileModel = modelReader.read(apexModelUrl.openStream());
            checkModelEquality(model, fileModel, TEST_MODEL_UNEQUAL_STR + xmlFile.getAbsolutePath());
        } catch (final Exception e) {
            LOGGER.warn(ERROR_PROCESSING_FILE + xmlFile.getAbsolutePath(), e);
            throw new ApexException(ERROR_PROCESSING_FILE + xmlFile.getAbsolutePath(), e);
        }

        final ApexModelWriter<M> modelWriter = new ApexModelWriter<>(rootModelClass);
        modelWriter.getCDataFieldSet().add("description");
        modelWriter.getCDataFieldSet().add("logic");
        modelWriter.getCDataFieldSet().add("uiLogic");

        final var baOutputStream = new ByteArrayOutputStream();
        modelWriter.write(model, baOutputStream);
        final var baInputStream = new ByteArrayInputStream(baOutputStream.toByteArray());
        final var byteArrayModel = modelReader.read(baInputStream);

        checkModelEquality(model, byteArrayModel, "test model does not equal XML marshalled and unmarshalled model");

        LOGGER.debug("ran testApexModelWriteReadXML");
    }

    /**
     * Test write and read in JSON format.
     *
     * @throws ApexException on write/read errors
     */
    public final void testApexModelWriteReadJson() throws ApexException {
        LOGGER.debug("running testApexModelWriteReadJSON . . .");

        final var model = modelCreator.getModel();

        // Write the file to disk
        File jsonFile;
        try {
            jsonFile = File.createTempFile("ApexModel", ".xml");
            jsonFile.deleteOnExit();
        } catch (final Exception e) {
            LOGGER.warn(TEMP_FILE_CREATE_ERR_STR, e);
            throw new ApexException(TEMP_FILE_CREATE_ERR_STR, e);
        }
        new ApexModelFileWriter<M>(true).apexModelWriteJsonFile(model, rootModelClass, jsonFile.getPath());

        // Read the file from disk
        final ApexModelReader<M> modelReader = new ApexModelReader<>(rootModelClass);

        try {
            final var apexModelUrl = ResourceUtils.getLocalFile(jsonFile.getAbsolutePath());
            final var fileModel = modelReader.read(apexModelUrl.openStream());
            checkModelEquality(model, fileModel, TEST_MODEL_UNEQUAL_STR + jsonFile.getAbsolutePath());
        } catch (final Exception e) {
            LOGGER.warn(ERROR_PROCESSING_FILE + jsonFile.getAbsolutePath(), e);
            throw new ApexException(ERROR_PROCESSING_FILE + jsonFile.getAbsolutePath(), e);
        }

        final ApexModelWriter<M> modelWriter = new ApexModelWriter<>(rootModelClass);
        modelWriter.setJsonOutput(true);

        final var baOutputStream = new ByteArrayOutputStream();
        modelWriter.write(model, baOutputStream);
        final var baInputStream = new ByteArrayInputStream(baOutputStream.toByteArray());
        final var byteArrayModel = modelReader.read(baInputStream);

        checkModelEquality(model, byteArrayModel, "test model does not equal JSON marshalled and unmarshalled model");

        LOGGER.debug("ran testApexModelWriteReadJSON");
    }

    /**
     * Test write and read of an Apex model to database using JPA.
     *
     * @param daoParameters the DAO parameters to use for JPA/JDBC
     * @throws ApexException thrown on errors writing or reading the model to database
     */
    public final void testApexModelWriteReadJpa(final DaoParameters daoParameters) throws ApexException {
        LOGGER.debug("running testApexModelWriteReadJPA . . .");

        final var model = modelCreator.getModel();

        final var apexDao = new ApexDaoFactory().createApexDao(daoParameters);
        apexDao.init(daoParameters);

        apexDao.create(model);
        final var dbJpaModel = apexDao.get(rootModelClass, model.getKey());
        apexDao.close();

        checkModelEquality(model, dbJpaModel, "test model does not equal model written and read using generic JPA");

        LOGGER.debug("ran testApexModelWriteReadJPA");
    }

    /**
     * Test that an Apex model is valid.
     *
     * @return the result of the validation
     * @throws ApexException thrown on errors validating the Apex model
     */
    public final AxValidationResult testApexModelValid() throws ApexException {
        LOGGER.debug("running testApexModelVaid . . .");

        final var model = modelCreator.getModel();
        final AxValidationResult result = model.validate(new AxValidationResult());

        if (!result.isValid()) {
            String message = MODEL_IS_INVALID + result.toString();
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        LOGGER.debug("ran testApexModelVaid");
        return result;
    }

    /**
     * Test that an Apex model is structured incorrectly.
     *
     * @return the result of the validation
     * @throws ApexException thrown on errors validating the Apex model
     */
    public final AxValidationResult testApexModelVaidateMalstructured() throws ApexException {
        LOGGER.debug("running testApexModelVaidateMalstructured . . .");

        final var model = modelCreator.getMalstructuredModel();
        final AxValidationResult result = model.validate(new AxValidationResult());

        if (result.isValid()) {
            String message = "model should not be valid " + result.toString();
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        LOGGER.debug("ran testApexModelVaidateMalstructured");
        return result;
    }

    /**
     * Test that an Apex model has observations.
     *
     * @return the result of the validation
     * @throws ApexException thrown on errors validating the Apex model
     */
    public final AxValidationResult testApexModelVaidateObservation() throws ApexException {
        LOGGER.debug("running testApexModelVaidateObservation . . .");

        final var model = modelCreator.getObservationModel();
        final AxValidationResult result = model.validate(new AxValidationResult());

        if (!result.isValid()) {
            String message = MODEL_IS_INVALID + result.toString();
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.OBSERVATION)) {
            LOGGER.warn("model should have observations");
            throw new ApexException("model should have observations");
        }

        LOGGER.debug("ran testApexModelVaidateObservation");
        return result;
    }

    /**
     * Test that an Apex model has warnings.
     *
     * @return the result of the validation
     * @throws ApexException thrown on errors validating the Apex model
     */
    public final AxValidationResult testApexModelVaidateWarning() throws ApexException {
        LOGGER.debug("running testApexModelVaidateWarning . . .");

        final var model = modelCreator.getWarningModel();
        final AxValidationResult result = model.validate(new AxValidationResult());

        if (!result.isValid()) {
            String message = MODEL_IS_INVALID + result.toString();
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.WARNING)) {
            LOGGER.warn("model should have warnings");
            throw new ApexException("model should have warnings");
        }

        LOGGER.debug("ran testApexModelVaidateWarning");
        return result;
    }

    /**
     * Test that an Apex model is invalid.
     *
     * @return the result of the validation
     * @throws ApexException thrown on errors validating the Apex model
     */
    public final AxValidationResult testApexModelVaidateInvalidModel() throws ApexException {
        LOGGER.debug("running testApexModelVaidateInvalidModel . . .");

        final var model = modelCreator.getInvalidModel();
        final AxValidationResult result = model.validate(new AxValidationResult());

        if (result.isValid()) {
            String message = "model should not be valid " + result.toString();
            LOGGER.warn(message);
            throw new ApexException(message);
        }

        LOGGER.debug("ran testApexModelVaidateInvalidModel");
        return result;
    }

    /**
     * Check if two models are equal.
     *
     * @param leftModel the left model
     * @param rightModel the right model
     * @param errorMessage the error message to output on inequality
     * @throws ApexException the exception to throw on inequality
     */
    public void checkModelEquality(final M leftModel, final M rightModel, final String errorMessage)
        throws ApexException {
        if (!leftModel.equals(rightModel)) {
            LOGGER.warn(errorMessage);
            throw new ApexException(errorMessage);
        }
    }
}
