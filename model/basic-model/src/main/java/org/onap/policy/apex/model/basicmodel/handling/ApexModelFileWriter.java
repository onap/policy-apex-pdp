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

package org.onap.policy.apex.model.basicmodel.handling;

import java.io.File;
import java.io.FileOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class writes an Apex model to a file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <M> the type of Apex model to write to file, must be a sub class of {@link AxModel}
 */
@AllArgsConstructor
public class ApexModelFileWriter<M extends AxModel> {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelFileWriter.class);

    // Should models being written to files be valid
    @Getter
    @Setter
    private boolean validateFlag;

    /**
     * Write a model to an XML file.
     *
     * @param model The model to write
     * @param rootModelClass The concept class
     * @param modelFileName The name of the file to write to
     * @throws ApexException thrown on errors
     */
    public void apexModelWriteXmlFile(final M model, final Class<M> rootModelClass, final String modelFileName)
                    throws ApexException {
        LOGGER.debug("running apexModelWriteXMLFile . . .");

        final ApexModelWriter<M> modelWriter = new ApexModelWriter<>(rootModelClass);
        modelWriter.setValidateFlag(validateFlag);
        modelWriter.getCDataFieldSet().add("description");
        modelWriter.getCDataFieldSet().add("logic");
        modelWriter.getCDataFieldSet().add("uiLogic");

        writeModelFile(model, modelWriter, modelFileName);

        LOGGER.debug("ran apexModelWriteXMLFile");
    }

    /**
     * Write a model to an JSON file.
     *
     * @param model The model to write
     * @param rootModelClass The concept class
     * @param modelFileName The name of the file to write to
     * @throws ApexException thrown on errors
     */
    public void apexModelWriteJsonFile(final M model, final Class<M> rootModelClass, final String modelFileName)
                    throws ApexException {
        LOGGER.debug("running apexModelWriteJSONFile . . .");

        final ApexModelWriter<M> modelWriter = new ApexModelWriter<>(rootModelClass);
        modelWriter.setJsonOutput(true);
        modelWriter.setValidateFlag(validateFlag);

        writeModelFile(model, modelWriter, modelFileName);

        LOGGER.debug("ran apexModelWriteJSONFile");
    }

    /**
     * Write a model to a file using a model writer.
     *
     * @param model The model to write
     * @param modelWriter the model writer to use to write the model to the file
     * @param modelFileName the file name of the file to write to
     * @throws ApexException on exceptions writing the model
     */
    private void writeModelFile(final M model, final ApexModelWriter<M> modelWriter, final String modelFileName)
                    throws ApexException {
        final var modelFile = new File(modelFileName);
        if (!modelFile.getParentFile().exists() && !modelFile.getParentFile().mkdirs()) {
            LOGGER.warn("could not create directory  " + modelFile.getParentFile());
            throw new ApexException("could not create directory  " + modelFile.getParentFile());
        }

        try (final var fileOutputStream = new FileOutputStream(modelFile)) {
            modelWriter.write(model, fileOutputStream);
        } catch (final Exception e) {
            LOGGER.warn("error processing file " + modelFile.getAbsolutePath(), e);
            throw new ApexException("error processing file " + modelFile.getAbsolutePath(), e);
        }
    }
}
