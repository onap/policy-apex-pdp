/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation
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

package org.onap.policy.apex.testsuites.integration.common.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test the sample domain model saver.
 */
class SampleDomainModelSaverTest {

    @Test
    void testSampleDomainModelSaver() throws IOException, ApexException {
        assertThatThrownBy(() -> SampleDomainModelSaver.main(null)).isInstanceOf(NullPointerException.class);

        String[] args0 =
            { "two", "arguments" };

        SampleDomainModelSaver.main(args0);

        Path tempDirectory = Files.createTempDirectory("ApexModelTempDir");
        String[] args1 =
            { tempDirectory.toString() };

        SampleDomainModelSaver.main(args1);

        File tempDir = new File(tempDirectory.toString());
        assertTrue(tempDir.isDirectory());
        assertEquals(5, Objects.requireNonNull(tempDir.listFiles()).length);

        Files.walk(tempDirectory).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
}
