/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.executor.handling;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineImpl;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.apex.testsuites.integration.common.model.SampleDomainModelFactory;
import org.onap.policy.apex.testsuites.integration.executor.engine.TestApexActionListener;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestApexEngine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContextUpdateDifferentModels {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestContextUpdateDifferentModels.class);

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;
    private EngineParameters engineParameters;

    /**
     * Before test.
     */
    @Before
    public void beforeTest() {
        schemaParameters = new SchemaParameters();

        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);

        contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());

        engineParameters = new EngineParameters();
        engineParameters.getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        ParameterService.register(engineParameters);
    }

    /**
     * After test.
     */
    @After
    public void afterTest() {
        ParameterService.deregister(engineParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);

        ParameterService.deregister(schemaParameters);
    }

    /**
     * Test context update different models.
     *
     * @throws ApexException the apex exception
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testContextUpdateDifferentModels() throws ApexException, InterruptedException, IOException {
        logger.debug("Running test testContextUpdateDifferentModels . . .");

        final AxPolicyModel apexModelSample = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(apexModelSample);

        final ApexEngineImpl apexEngine =
                (ApexEngineImpl) new ApexEngineFactory().createApexEngine(new AxArtifactKey("TestApexEngine", "0.0.1"));
        final TestApexActionListener listener = new TestApexActionListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexModelSample, false);
        apexEngine.start();

        apexEngine.stop();

        final AxPolicyModel someSpuriousModel = new AxPolicyModel(new AxArtifactKey("SomeSpuriousModel", "0.0.1"));
        assertNotNull(someSpuriousModel);

        assertThatThrownBy(() -> apexEngine.updateModel(null, false))
            .hasMessage("updateModel()<-TestApexEngine:0.0.1, Apex model is not defined, it has a null value");
        assertEquals(apexEngine.getInternalContext().getContextAlbums().size(),
                apexModelSample.getAlbums().getAlbumsMap().size());
        for (final ContextAlbum contextAlbum : apexEngine.getInternalContext().getContextAlbums().values()) {
            assertEquals(
                    contextAlbum.getAlbumDefinition(), apexModelSample.getAlbums().get(contextAlbum.getKey()));
        }

        apexEngine.updateModel(someSpuriousModel, false);
        assertEquals(apexEngine.getInternalContext().getContextAlbums().size(),
                someSpuriousModel.getAlbums().getAlbumsMap().size());
        for (final ContextAlbum contextAlbum : apexEngine.getInternalContext().getContextAlbums().values()) {
            assertEquals(
                    contextAlbum.getAlbumDefinition(), someSpuriousModel.getAlbums().get(contextAlbum.getKey()));
        }

        apexEngine.clear();

        logger.debug("Ran test testContextUpdateDifferentModels");
    }

}
