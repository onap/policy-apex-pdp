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

package org.onap.policy.apex.auth.clieditor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;


class CommandLineParametersTest {

    @Test
    void testMetadataFile() {
        var clParameters = new CommandLineParameters();
        assertNotNull(clParameters.getMetadataLocation());
        assertFalse(clParameters.checkSetMetadataFileName());
        clParameters.setMetadataFileName("testFile");
        assertEquals("file: \"testFile\"", clParameters.getMetadataLocation());
        assertTrue(clParameters.checkSetMetadataFileName());
    }

    @Test
    void testApexPropertiesFile() throws IOException {
        var clParameters = new CommandLineParameters();
        assertNotNull(clParameters.getApexPropertiesLocation());
        assertNotNull(clParameters.getApexPropertiesStream());
        assertFalse(clParameters.checkSetApexPropertiesFileName());
        clParameters.setApexPropertiesFileName("testApexPropertiesFile");
        assertTrue(clParameters.checkSetApexPropertiesFileName());
    }

    @Test
    void testInputModelFile() {
        var clParameters = new CommandLineParameters();
        assertFalse(clParameters.checkSetInputModelFileName());
        assertFalse(clParameters.checkSetLogFileName());
        clParameters.validate();
    }
}
