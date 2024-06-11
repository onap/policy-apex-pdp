/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;


class AxKeyInfoTest {

    @Test
    void testNullAxKeyInfo() {
        assertNotNull(new AxKeyInfo());
        assertNotNull(new AxKeyInfo(new AxArtifactKey()));
        assertNotNull(new AxKeyInfo(new AxArtifactKey(), UUID.randomUUID(), "Key description"));
    }

    @Test
    void testAxKeyInfo() {
        AxKeyInfo testKeyInfo = new AxKeyInfo();
        testKeyInfo.setKey((new AxArtifactKey("PN", "0.0.1")));
        assertEquals("PN:0.0.1", testKeyInfo.getKey().getId());
        assertTrue(testKeyInfo.matchesId("PN:0.0.1"));

        AxArtifactKey key = new AxArtifactKey("key", "0.0.1");
        testKeyInfo.setKey(key);
        assertEquals(key, testKeyInfo.getKey());

        UUID uuid = UUID.randomUUID();
        testKeyInfo.setUuid(uuid);
        assertEquals(uuid, testKeyInfo.getUuid());
        testKeyInfo.setDescription("Key Description");
        assertEquals("Key Description", testKeyInfo.getDescription());

        AxKeyInfo clonedReferenceKey = new AxKeyInfo(testKeyInfo);
        assertTrue(clonedReferenceKey.toString()
                        .startsWith("AxKeyInfo:(artifactId=AxArtifactKey:(name=key,version=0.0.1),uuid="));

        assertNotEquals(0, testKeyInfo.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(testKeyInfo, testKeyInfo); // NOSONAR
        assertEquals(testKeyInfo, clonedReferenceKey);
        assertNotNull(testKeyInfo);
        Object differentKeyType = new AxArtifactKey();
        assertNotEquals(testKeyInfo, differentKeyType);
        assertNotEquals(testKeyInfo, new AxKeyInfo(new AxArtifactKey()));
        assertNotEquals(testKeyInfo, new AxKeyInfo(key, UUID.randomUUID(), "Some Description"));
        assertEquals(testKeyInfo, new AxKeyInfo(key, uuid, "Some Other Description"));
        assertEquals(testKeyInfo, new AxKeyInfo(key, uuid, "Key Description"));

        assertEquals(0, testKeyInfo.compareTo(clonedReferenceKey));
    }

    @Test
    void testAxKeyValidation() {
        AxKeyInfo testKeyInfo = new AxKeyInfo();
        testKeyInfo.setKey((new AxArtifactKey("PN", "0.0.1")));

        AxValidationResult result = new AxValidationResult();
        result = testKeyInfo.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        testKeyInfo.setDescription("");
        result = testKeyInfo.validate(result);
        assertEquals(AxValidationResult.ValidationResult.OBSERVATION, result.getValidationResult());

        testKeyInfo.setUuid(new UUID(0, 0));
        result = testKeyInfo.validate(result);
        assertEquals(AxValidationResult.ValidationResult.WARNING, result.getValidationResult());

        testKeyInfo.setKey(AxArtifactKey.getNullKey());
        result = testKeyInfo.validate(result);
        assertEquals(AxValidationResult.ValidationResult.INVALID, result.getValidationResult());

        assertNotNull(AxKeyInfo.generateReproducibleUuid(null));
        assertNotNull(AxKeyInfo.generateReproducibleUuid("SeedString"));

        testKeyInfo.clean();
        assertNotNull(testKeyInfo);
    }
}
