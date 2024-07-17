/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation. All rights reserved.
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

package org.onap.policy.apex.context.impl.schema.java;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.impl.distribution.DistributorFlushTimerTask;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.common.parameters.ParameterService;


class DistributorTest {

    @BeforeAll
    public static void prepareForTest() {
        final var contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters()
                .setPluginClass("org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager");
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());
    }

    /**
     * Clear down the test data.
     */
    @AfterAll
    public static void cleanUpAfterTest() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testModelRegister() throws ContextException {
        var schemas = new AxContextSchemas();
        var simpleIntSchema = new AxContextSchema(new AxArtifactKey("SimpleIntSchema", "0.0.1"), "JAVA",
                "java.lang.Integer");
        schemas.getSchemasMap().put(simpleIntSchema.getKey(), simpleIntSchema);

        var axContextAlbum = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
                true, AxArtifactKey.getNullKey());

        axContextAlbum.setItemSchema(simpleIntSchema.getKey());
        var model = new AxContextModel(new AxArtifactKey("TestArtifact", "0.0.1"));
        var albums = new AxContextAlbums();
        albums.getAlbumsMap().put(axContextAlbum.getKey(), axContextAlbum);
        model.setAlbums(albums);
        model.setSchemas(schemas);
        var distributor = new JvmLocalDistributor();
        assertDoesNotThrow(() -> distributor.registerModel(model));

        distributor.flush();
        ModelService.clear();
    }

    @Test
    void testDistributorFlushTimer() throws ContextException {
        var distributor = new JvmLocalDistributor();
        distributor.init(new AxArtifactKey("test", "0.0.1"));
        var timerTask = new DistributorFlushTimerTask(distributor);
        assertDoesNotThrow(timerTask::run);
        assertDoesNotThrow(timerTask::toString);
        assertTrue(timerTask.cancel());
    }

    @Test
    void testDistributorFactory() {
        var dfactory = new DistributorFactory();
        var axArtifactKey = new AxArtifactKey("testKey", "1.0.1");
        assertDoesNotThrow(() -> dfactory.getDistributor(axArtifactKey));
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        var params = new DistributorParameters();
        params.setPluginClass("invalid.class");
        ParameterService.register(params);
        assertThrows(ContextException.class, () -> dfactory.getDistributor(axArtifactKey));
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        params.setPluginClass("org.onap.policy.apex.context.impl.persistence.ephemeral.EphemeralPersistor");
        ParameterService.register(params);
        assertThrows(ContextException.class, () -> dfactory.getDistributor(axArtifactKey));
    }

    @Test
    void testAbstractDistributor() throws ContextException {
        var distributor = new JvmLocalDistributor();
        assertThrows(ContextException.class, () -> distributor.removeContextAlbum(new AxArtifactKey()));
        assertDoesNotThrow(distributor::flush);
        distributor.init(new AxArtifactKey());
        assertDoesNotThrow(distributor::clear);

    }
}
