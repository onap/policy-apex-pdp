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

package org.onap.policy.apex.plugins.context.distribution.infinispan;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.common.parameters.ParameterService;


class InfinispanDistributorTest {

    private static InfinispanDistributorParameters distributorParams;

    @BeforeAll
    static void prepareForTest() {
        final ContextParameters contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());

        distributorParams = new InfinispanDistributorParameters();
        ParameterService.register(distributorParams);
    }

    /**
     * Clear down the test data.
     */
    @AfterAll
    static void cleanUpAfterTest() {
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testInvalidParameters() throws ContextException {
        var infiniSpanDistributor = new InfinispanContextDistributor();
        var key = new AxArtifactKey("test", "1.1.1");
        assertThatThrownBy(() -> infiniSpanDistributor.init(key)).isInstanceOf(NoClassDefFoundError.class)
                .hasMessageContaining("TransactionManager");
        assertThatThrownBy(() -> infiniSpanDistributor.getContextAlbumMap(key))
                .hasMessageContaining("infinispanManager\" is null");
        assertDoesNotThrow(infiniSpanDistributor::shutdown);
    }

}
