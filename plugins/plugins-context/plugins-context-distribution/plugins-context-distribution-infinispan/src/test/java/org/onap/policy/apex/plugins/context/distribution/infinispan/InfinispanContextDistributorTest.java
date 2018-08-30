/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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
package org.onap.policy.apex.plugins.context.distribution.infinispan;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.context.test.distribution.ContextAlbumUpdate;
import org.onap.policy.apex.context.test.distribution.ContextInstantiation;
import org.onap.policy.apex.context.test.distribution.ContextUpdate;
import org.onap.policy.apex.context.test.distribution.SequentialContextInstantiation;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class InfinispanContextDistributorTest {
    private static final XLogger logger = XLoggerFactory.getXLogger(InfinispanContextDistributorTest.class);

    private static final String PLUGIN_CLASS = InfinispanContextDistributor.class.getCanonicalName();

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;

    @Before
    public void beforeTest() {
        contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        InfinispanDistributorParameters inifinispanDistributorParameters = new InfinispanDistributorParameters();
        inifinispanDistributorParameters.setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        inifinispanDistributorParameters.setPluginClass(PLUGIN_CLASS);
        contextParameters.setDistributorParameters(inifinispanDistributorParameters);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());
        
        schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
    }

    @After
    public void afterTest() {
        ParameterService.deregister(schemaParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);
    }

    @Test
    public void testContextAlbumUpdateInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextAlbumUpdateInfinispan test . . .");

        new ContextAlbumUpdate().testContextAlbumUpdate();

        logger.debug("Ran testContextAlbumUpdateInfinispan test");
    }

    @Test
    public void testContextInstantiationInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextInstantiationInfinispan test . . .");

        new ContextInstantiation().testContextInstantiation();

        logger.debug("Ran testContextInstantiationInfinispan test");
    }

    @Test
    public void testContextUpdateInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateInfinispan test . . .");

        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateInfinispan test");
    }

    @Test
    public void testSequentialContextInstantiationInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testSequentialContextInstantiationInfinispan test . . .");

        new SequentialContextInstantiation().testSequentialContextInstantiation();

        logger.debug("Ran testSequentialContextInstantiationInfinispan test");
    }
}
