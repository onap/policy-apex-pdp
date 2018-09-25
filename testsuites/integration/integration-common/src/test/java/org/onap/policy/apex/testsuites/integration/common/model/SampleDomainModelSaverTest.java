/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.Test;

/**
 * Test the sample domain model saver.
 */
public class SampleDomainModelSaverTest {

    @Test
    public void testSampleDomainModelSaver() throws IOException {
        try {
            SampleDomainModelSaver.main(null);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("java.lang.NullPointerException", exc.getClass().getCanonicalName());
        }

        String[] args0 =
            { "two", "arguments" };

        try {
            SampleDomainModelSaver.main(args0);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }

        Path tempDirectory = Files.createTempDirectory("ApexModelTempDir");
        String[] args1 =
            { tempDirectory.toString() };

        try {
            SampleDomainModelSaver.main(args1);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }

        File tempDir = new File(tempDirectory.toString());
        assertTrue(tempDir.isDirectory());
        assertEquals(10, tempDir.listFiles().length);

        Files.walk(tempDirectory).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }
}
