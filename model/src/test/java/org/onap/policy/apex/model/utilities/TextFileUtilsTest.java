/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.model.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Test text file utilities.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class TextFileUtilsTest {

    private static final String FILE_CONTENT = "This is the contents of a text file";

    @Test
    void test() throws IOException {
        final File tempTextFile = File.createTempFile("Test", "txt");

        TextFileUtils.putStringAsTextFile(FILE_CONTENT, tempTextFile.getAbsolutePath());

        final String textFileString0 = TextFileUtils.getTextFileAsString(tempTextFile.getAbsolutePath());
        assertEquals(FILE_CONTENT, textFileString0);

        final FileInputStream fis = new FileInputStream(tempTextFile);
        final String textFileString1 = TextFileUtils.getStreamAsString(fis);
        assertEquals(textFileString0, textFileString1);

    }

}
