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

package org.onap.policy.apex.model.policymodel.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyComparer;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelComparer;
import org.onap.policy.apex.model.utilities.TextFileUtils;

public class TestPolicyModelComparer {

    @Test
    public void testPolicyComparer() throws IOException {
        final AxPolicyModel leftApexModel = new TestApexPolicyModelCreator().getModel();
        final AxPolicyModel rightApexModel = new AxPolicyModel(leftApexModel);

        PolicyModelComparer policyModelComparer = new PolicyModelComparer(leftApexModel, rightApexModel);

        String resultString = policyModelComparer.asString(false, false);
        String checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonIdenticalVerboseValues.txt");
        assertEquals(resultString.trim().replaceAll("[\\r?\\n]+", " "),
                checkString.trim().replaceAll("[\\r?\\n]+", " "));

        resultString = policyModelComparer.asString(false, true);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonIdenticalVerboseKeys.txt");
        assertTrue(resultString.trim().replaceAll("[\\r?\\n]+", " ")
                .equals(checkString.trim().replaceAll("[\\r?\\n]+", " ")));

        resultString = policyModelComparer.asString(true, false);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonIdenticalTerse.txt");
        assertTrue(resultString.trim().replaceAll("[\\r?\\n]+", " ")
                .equals(checkString.trim().replaceAll("[\\r?\\n]+", " ")));

        resultString = policyModelComparer.asString(true, true);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonIdenticalTerse.txt");
        assertTrue(resultString.trim().replaceAll("[\\r?\\n]+", " ")
                .equals(checkString.trim().replaceAll("[\\r?\\n]+", " ")));

        final AxKeyInfo leftOnlyKeyInfo = new AxKeyInfo(new AxArtifactKey("LeftOnlyKeyInfo", "0.0.1"),
                UUID.fromString("ce9168c-e6df-414f-9646-6da464b6f000"), "Left only key info");
        final AxKeyInfo rightOnlyKeyInfo = new AxKeyInfo(new AxArtifactKey("RightOnlyKeyInfo", "0.0.1"),
                UUID.fromString("ce9168c-e6df-414f-9646-6da464b6f001"), "Right only key info");

        leftApexModel.getKeyInformation().getKeyInfoMap().put(leftOnlyKeyInfo.getKey(), leftOnlyKeyInfo);
        rightApexModel.getKeyInformation().getKeyInfoMap().put(rightOnlyKeyInfo.getKey(), rightOnlyKeyInfo);

        leftApexModel.getKeyInformation().getKeyInfoMap().get(new AxArtifactKey("inEvent", "0.0.1"))
                .setDescription("Left InEvent Description");
        rightApexModel.getKeyInformation().getKeyInfoMap().get(new AxArtifactKey("inEvent", "0.0.1"))
                .setDescription("Right InEvent Description");

        policyModelComparer = new PolicyModelComparer(leftApexModel, rightApexModel);

        resultString = policyModelComparer.asString(false, false);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonDifferentVerboseValues.txt");
        assertEquals(resultString.trim().replaceAll("[\\r?\\n]+", " "),
                checkString.trim().replaceAll("[\\r?\\n]+", " "));

        resultString = policyModelComparer.asString(false, true);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonDifferentVerboseKeys.txt");
        assertTrue(resultString.trim().replaceAll("[\\r?\\n]+", " ")
                .equals(checkString.trim().replaceAll("[\\r?\\n]+", " ")));

        resultString = policyModelComparer.asString(true, false);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonDifferentTerseValues.txt");
        assertTrue(resultString.trim().replaceAll("[\\r?\\n]+", " ")
                .equals(checkString.trim().replaceAll("[\\r?\\n]+", " ")));

        resultString = policyModelComparer.asString(true, true);
        checkString = TextFileUtils
                .getTextFileAsString("src/test/resources/checkFiles/PolicyModelComparisonDifferentTerseKeys.txt");
        assertTrue(resultString.trim().replaceAll("[\\r?\\n]+", " ")
                .equals(checkString.trim().replaceAll("[\\r?\\n]+", " ")));

        assertNotNull(policyModelComparer.getContextAlbumComparisonResult());
        assertNotNull(policyModelComparer.getContextAlbumKeyDifference());
        assertNotNull(policyModelComparer.getContextSchemaComparisonResult());
        assertNotNull(policyModelComparer.getContextSchemaKeyDifference());
        assertNotNull(policyModelComparer.getEventComparisonResult());
        assertNotNull(policyModelComparer.getEventKeyDifference());
        assertNotNull(policyModelComparer.getKeyInfoComparisonResult());
        assertNotNull(policyModelComparer.getKeyInformationKeyDifference());
        assertNotNull(policyModelComparer.getPolicyComparisonResult());
        assertNotNull(policyModelComparer.getPolicykeyDifference());
        assertNotNull(policyModelComparer.getPolicyModelsKeyDifference());
        assertNotNull(policyModelComparer.getTaskComparisonResult());
        assertNotNull(policyModelComparer.getTaskKeyDifference());

        assertNotNull(new PolicyComparer().compare(leftApexModel.getPolicies(), rightApexModel.getPolicies()));

        assertEquals("****** policy map differences ******\n*** context s",
                policyModelComparer.toString().substring(0, 50));
    }
}
