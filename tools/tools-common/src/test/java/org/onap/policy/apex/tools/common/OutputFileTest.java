/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.tools.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutputFileTest {

    final String testFileName = "testing.txt";
    final Path fp = FileSystems.getDefault().getPath(testFileName);
    File file = fp.toFile();

    @BeforeEach
    void beforeSetUp() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testToWriter() {
        OutputFile testFile = new OutputFile(testFileName, false);
        testFile.validate();
        file.setReadable(false);
        file.setWritable(false);
        assertNull(testFile.toWriter());
        file.setWritable(true);
        assertNotNull(testFile.toWriter());
    }

    @Test
    void testValidate() {
        OutputFile testFile = new OutputFile(testFileName, true);
        assertNull(testFile.validate());
        file.setReadable(false);
        file.setWritable(false);
        assertNotNull(testFile.validate());
        OutputFile testFile2 = new OutputFile(testFileName);
        assertNotNull(testFile2.validate());
        assertEquals("file already exists", testFile2.validate());
    }

    @Test
    void testToOutputStream() {
        OutputFile testFile = new OutputFile(testFileName, true);
        assertNotNull(testFile.toOutputStream());
        file.setReadable(false);
        file.setWritable(false);
        assertNull(testFile.toOutputStream());
    }

    @AfterEach
    void testDown() {
        if (file.exists()) {
            file.delete();
        }
    }
}
