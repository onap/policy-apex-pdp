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

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to save Apex models to file in XML or JSON format.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <M> the type of Apex model to save to file, must be a sub class of {@link AxModel}
 */
public class ApexModelSaver<M extends AxModel> {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexModelSaver.class);

    // The class of the model and the model to write to disk
    private final Class<M> rootModelClass;
    private final M model;

    // The path into which to write the models
    private final String writePath;

    /**
     * Constructor, specifies the type of the Apex model (a sub class of {@link AxModel}), the model to write, and the
     * path of a directory to which to write the model.
     *
     * @param rootModelClass the class of the model, a sub class of {@link AxModel}
     * @param model the model to write, an instance of a sub class of {@link AxModel}
     * @param writePath the directory to which models will be written. The name of the written model will be the Model
     *        Name for its key with the suffix {@code .xml} or {@code .json}.
     */
    public ApexModelSaver(final Class<M> rootModelClass, final M model, final String writePath) {
        Assertions.argumentNotNull(rootModelClass, "argument rootModelClass may not be null");
        Assertions.argumentNotNull(model, "argument model may not be null");
        Assertions.argumentNotNull(writePath, "writePath rootModelClass may not be null");

        this.rootModelClass = rootModelClass;
        this.model = model;
        this.writePath = writePath;
    }

    /**
     * Write an Apex model to a file in XML format. The model will be written to {@code <writePath/modelKeyName.xml>}
     *
     * @throws ApexException on errors writing the Apex model
     */
    public void apexModelWriteXml() throws ApexException {
        LOGGER.debug("running apexModelWriteXML . . .");

        // Write the file to disk
        final File xmlFile = new File(writePath + File.separatorChar + model.getKey().getName() + ".xml");
        new ApexModelFileWriter<M>(true).apexModelWriteXmlFile(model, rootModelClass, xmlFile.getPath());

        LOGGER.debug("ran apexModelWriteXML");
    }

    /**
     * Write an Apex model to a file in JSON format. The model will be written to {@code <writePath/modelKeyName.json>}
     *
     * @throws ApexException on errors writing the Apex model
     */
    public void apexModelWriteJson() throws ApexException {
        LOGGER.debug("running apexModelWriteJSON . . .");

        // Write the file to disk
        final File jsonFile = new File(writePath + File.separatorChar + model.getKey().getName() + ".json");
        new ApexModelFileWriter<M>(true).apexModelWriteJsonFile(model, rootModelClass, jsonFile.getPath());

        LOGGER.debug("ran apexModelWriteJSON");
    }
}
