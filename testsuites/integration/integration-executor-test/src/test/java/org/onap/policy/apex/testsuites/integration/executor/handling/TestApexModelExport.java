/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelSplitter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestApexModelReader tests Apex model reading.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class TestApexModelExport {
    private static final XLogger logger = XLoggerFactory.getXLogger(TestApexModelExport.class);

    private AxPolicyModel model = null;

    @BeforeEach
    void initApexModelSmall() {
        model = new TestApexSamplePolicyModelCreator("MVEL").getModel();
    }

    @Test
    void testApexModelExport() throws Exception {
        logger.info("Starting test: testApexModelExport");

        final List<AxArtifactKey> exportPolicyList = new ArrayList<>(model.getPolicies().getPolicyMap().keySet());

        final AxPolicyModel exportedModel0 = PolicyModelSplitter.getSubPolicyModel(model, exportPolicyList);

        // Remove unused schemas and their keys
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem000", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem001", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem002", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem003", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem004", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem005", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem006", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem007", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem008", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem009", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem00A", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem00B", "0.0.1"));
        model.getSchemas().getSchemasMap().remove(new AxArtifactKey("TestContextItem00C", "0.0.1"));

        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem000", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem001", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem002", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem003", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem004", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem005", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem006", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem007", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem008", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem009", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem00A", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem00B", "0.0.1"));
        model.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("TestContextItem00C", "0.0.1"));

        assertEquals(model, exportedModel0);

        exportPolicyList.remove(0);

        final AxPolicyModel exportedModel1 = PolicyModelSplitter.getSubPolicyModel(model, exportPolicyList);
        assertNotEquals(model, exportedModel1);
        assertEquals(model.getPolicies().get("Policy1"), exportedModel1.getPolicies().get("Policy1"));

        exportPolicyList.clear();
        exportPolicyList.add(new AxArtifactKey("NonExistentPolicy", "0.0.1"));

        final AxPolicyModel emptyExportedModel = PolicyModelSplitter.getSubPolicyModel(model, exportPolicyList);
        assertNotNull(emptyExportedModel);

    }
}
