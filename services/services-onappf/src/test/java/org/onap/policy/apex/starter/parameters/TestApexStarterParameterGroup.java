/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.starter.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

/**
 * Class to perform unit test of {@link ApexStarterParameterGroup}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestApexStarterParameterGroup {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    public void testApexStarterParameterGroup_Named() {
        final ApexStarterParameterGroup apexStarterParameters = new ApexStarterParameterGroup("my-name");
        assertEquals("my-name", apexStarterParameters.getName());
    }

    @Test
    public void testApexStarterParameterGroup() {
        final ApexStarterParameterGroup apexStarterParameters = commonTestData.toObject(
                commonTestData.getApexStarterParameterGroupMap(CommonTestData.APEX_STARTER_GROUP_NAME),
                ApexStarterParameterGroup.class);
        final PdpStatusParameters pdpStatusParameters = apexStarterParameters.getPdpStatusParameters();
        final GroupValidationResult validationResult = apexStarterParameters.validate();
        assertTrue(validationResult.isValid());
        assertEquals(CommonTestData.APEX_STARTER_GROUP_NAME, apexStarterParameters.getName());
        assertEquals(CommonTestData.TIME_INTERVAL, pdpStatusParameters.getTimeInterval());
        assertEquals(CommonTestData.PDP_NAME, pdpStatusParameters.getPdpName());
        assertEquals(CommonTestData.PDP_TYPE, pdpStatusParameters.getPdpType());
        assertEquals(CommonTestData.VERSION, pdpStatusParameters.getVersion());
        assertEquals(CommonTestData.DESCRIPTION, pdpStatusParameters.getDescription());
        assertEquals(CommonTestData.SUPPORTED_POLICY_TYPES, pdpStatusParameters.getSupportedPolicyTypes());
    }

    @Test
    public void testApexStarterParameterGroup_NullName() {
        final ApexStarterParameterGroup apexStarterParameters = commonTestData
                .toObject(commonTestData.getApexStarterParameterGroupMap(null), ApexStarterParameterGroup.class);
        final GroupValidationResult validationResult = apexStarterParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals(null, apexStarterParameters.getName());
        assertTrue(validationResult.getResult().contains("is null"));
    }

    @Test
    public void testApexStarterParameterGroup_EmptyName() {
        final ApexStarterParameterGroup apexStarterParameters = commonTestData
                .toObject(commonTestData.getApexStarterParameterGroupMap(""), ApexStarterParameterGroup.class);
        final GroupValidationResult validationResult = apexStarterParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals("", apexStarterParameters.getName());
        assertTrue(validationResult.getResult().contains(
                "field \"name\" type \"java.lang.String\" value \"\" INVALID, " + "must be a non-blank string"));
    }

    @Test
    public void testApexStarterParameterGroup_SetName() {
        final ApexStarterParameterGroup apexStarterParameters = commonTestData.toObject(
                commonTestData.getApexStarterParameterGroupMap(CommonTestData.APEX_STARTER_GROUP_NAME),
                ApexStarterParameterGroup.class);
        apexStarterParameters.setName("ApexStarterNewGroup");
        final GroupValidationResult validationResult = apexStarterParameters.validate();
        assertTrue(validationResult.isValid());
        assertEquals("ApexStarterNewGroup", apexStarterParameters.getName());
    }

    @Test
    public void testApexStarterParameterGroup_EmptyPdpStatusParameters() {
        final Map<String, Object> map =
                commonTestData.getApexStarterParameterGroupMap(CommonTestData.APEX_STARTER_GROUP_NAME);
        map.put("pdpStatusParameters", commonTestData.getPdpStatusParametersMap(true));
        final ApexStarterParameterGroup apexStarterParameters =
                commonTestData.toObject(map, ApexStarterParameterGroup.class);
        final GroupValidationResult validationResult = apexStarterParameters.validate();
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("\"org.onap.policy.apex.starter.parameters.ApexStarterParameterGroup\" INVALID, "
                        + "parameter group has status INVALID"));
    }
}
