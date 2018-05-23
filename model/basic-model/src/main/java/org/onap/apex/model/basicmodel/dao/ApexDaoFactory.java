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

package org.onap.apex.model.basicmodel.dao;

import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This factory class returns an Apex DAO for the configured persistence mechanism. The factory uses the plugin class specified in {@link DAOParameters} to
 * instantiate a DAO instance.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexDaoFactory {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexDaoFactory.class);

    /**
     * Return an Apex DAO for the required APEX DAO plugin class.
     *
     * @param daoParameters parameters to use to read the database configuration information
     * @return the Apex DAO
     * @throws ApexException on invalid JPA plugins
     */
    public ApexDao createApexDao(final DAOParameters daoParameters) throws ApexException {
        Assertions.argumentNotNull(daoParameters, ApexException.class, "Parameter \"daoParameters\" may not be null");

        // Get the class for the DAO using reflection
        Object apexDaoObject = null;
        try {
            apexDaoObject = Class.forName(daoParameters.getPluginClass()).newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.error("Apex DAO class not found for DAO plugin \"" + daoParameters.getPluginClass() + "\"", e);
            throw new ApexException("Apex DAO class not found for DAO plugin \"" + daoParameters.getPluginClass() + "\"", e);
        }

        // Check the class is an Apex DAO
        if (!(apexDaoObject instanceof ApexDao)) {
            LOGGER.error("Specified Apex DAO plugin class \"" + daoParameters.getPluginClass() + "\" does not implement the ApexDao interface");
            throw new ApexException("Specified Apex DAO plugin class \"" + daoParameters.getPluginClass() + "\" does not implement the ApexDao interface");
        }

        return (ApexDao) apexDaoObject;
    }
}
