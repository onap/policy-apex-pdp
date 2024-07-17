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

package org.onap.policy.apex.context.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.TreeSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.persistence.PersistorFactory;
import org.onap.policy.apex.context.impl.persistence.ephemeral.EphemeralPersistor;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.common.parameters.ParameterService;


class PersistorTest {

    @AfterAll
    public static void cleanUpAfterTest() {
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testContextItemRead() throws ContextException {
        var persistor = new EphemeralPersistor();
        assertNull(persistor.readContextItem(new AxReferenceKey(), null));
        assertThat(persistor.readContextItems(null, null)).isInstanceOf(TreeSet.class);
        persistor.init(new AxArtifactKey("testkey", "1.0.0"));
        assertEquals("testkey", persistor.getKey().getName());
    }

    @Test
    void testWriteContextItem() {
        var persistor = new EphemeralPersistor();
        assertThat(persistor.writeContextItem(new Object())).isInstanceOf(Object.class);
    }

    @Test
    void testPersistorFactory() {
        var factory = new PersistorFactory();
        PersistorParameters params = new PersistorParameters();
        params.setPluginClass("invalid.class");
        ParameterService.register(params);
        assertThrows(ContextException.class, () -> factory.createPersistor(new AxArtifactKey()));
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        params.setPluginClass(JvmLocalDistributor.class.getName());
        ParameterService.register(params);
        assertThrows(ContextException.class, () -> factory.createPersistor(new AxArtifactKey()));
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        params.setPluginClass(PersistorParameters.DEFAULT_PERSISTOR_PLUGIN_CLASS);
        ParameterService.register(params);
        assertDoesNotThrow(() -> factory.createPersistor(new AxArtifactKey()));
    }
}
