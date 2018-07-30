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

package org.onap.policy.apex.examples.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is a listener that is called when the servlet is started and stopped. It brings up the Apex engine on
 * servlet start and shuts it down on servlet stop.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@WebListener
public class ApexServletListener implements ServletContextListener {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexServletListener.class);

    // The Apex engine reference
    private ApexMain apexMain;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        // The web.xml file contains the context parameters for the Apex engine
        final String configFileName = servletContextEvent.getServletContext().getInitParameter("config-file");
        final String modelFileName = servletContextEvent.getServletContext().getInitParameter("model-file");

        LOGGER.info("Apex Servliet has been started, config-file= " + configFileName + ", model-file=" + modelFileName);

        // Check that a configuration file have been specified
        if (servletContextEvent.getServletContext().getInitParameter("config-file") == null) {
            final String errorMessage =
                    "Apex servlet start failed, servlet parameter \"config-file\" has not been specified";
            LOGGER.error("Apex servlet start failed, servlet parameter \"config-file\" has not been specified");
            throw new ApexRuntimeException(errorMessage);
        }

        // Construct the Apex command line arguments
        final List<String> argsList = new ArrayList<String>();
        argsList.add("-config-file");
        argsList.add(configFileName);

        // Model file name is an optional parameter
        if (modelFileName != null) {
            argsList.add("-model-file");
            argsList.add(modelFileName);
        }

        // Initialize apex
        apexMain = new ApexMain(argsList.toArray(new String[argsList.size()]));
    }


    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        // Shut Apex down
        try {
            apexMain.shutdown();
            apexMain = null;
        } catch (final ApexException e) {
            final String errorMessage = "Apex servlet stop did not execute normally";
            LOGGER.error(errorMessage, e);
        }

        LOGGER.info("Apex Servliet has been stopped");
    }
}
