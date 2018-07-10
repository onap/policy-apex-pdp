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

package org.onap.policy.apex.plugins.executor.test.script.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineImpl;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MVELExecutorParameters;
import org.onap.policy.apex.plugins.executor.test.script.engine.TestApexActionListener;
import org.onap.policy.apex.test.common.model.SampleDomainModelFactory;
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

    @Test
    public void testContextUpdateDifferentModels() throws ApexException, InterruptedException, IOException {
        logger.debug("Running test testContextUpdateDifferentModels . . .");

        final AxPolicyModel apexModelSample = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(apexModelSample);

        final EngineParameters parameters = new EngineParameters();
        parameters.getExecutorParameterMap().put("MVEL", new MVELExecutorParameters());

        final ApexEngineImpl apexEngine =
                (ApexEngineImpl) new ApexEngineFactory().createApexEngine(new AxArtifactKey("TestApexEngine", "0.0.1"));
        final TestApexActionListener listener = new TestApexActionListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexModelSample);
        apexEngine.start();

        apexEngine.stop();

        final AxPolicyModel someSpuriousModel = new AxPolicyModel(new AxArtifactKey("SomeSpuriousModel", "0.0.1"));
        assertNotNull(someSpuriousModel);

        try {
            apexEngine.updateModel(null);
            fail("null model should throw an exception");
        } catch (final ApexException e) {
            assertEquals("updateModel()<-TestApexEngine:0.0.1, Apex model is not defined, it has a null value",
                    e.getMessage());
        }
        assertEquals(apexEngine.getInternalContext().getContextAlbums().size(),
                apexModelSample.getAlbums().getAlbumsMap().size());
        for (final ContextAlbum contextAlbum : apexEngine.getInternalContext().getContextAlbums().values()) {
            assertTrue(
                    contextAlbum.getAlbumDefinition().equals(apexModelSample.getAlbums().get(contextAlbum.getKey())));
        }

        apexEngine.updateModel(someSpuriousModel);
        assertEquals(apexEngine.getInternalContext().getContextAlbums().size(),
                someSpuriousModel.getAlbums().getAlbumsMap().size());
        for (final ContextAlbum contextAlbum : apexEngine.getInternalContext().getContextAlbums().values()) {
            assertTrue(
                    contextAlbum.getAlbumDefinition().equals(someSpuriousModel.getAlbums().get(contextAlbum.getKey())));
        }

        apexEngine.clear();

        logger.debug("Ran test testContextUpdateDifferentModels");
    }

}
