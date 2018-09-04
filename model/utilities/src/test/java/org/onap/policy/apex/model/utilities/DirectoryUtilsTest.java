/*
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

package org.onap.policy.apex.model.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.DirectoryUtils;
import org.onap.policy.apex.model.utilities.TextFileUtils;

public class DirectoryUtilsTest {

    @Test
    public void test() throws IOException {
        DirectoryUtils.emptyDirectory(new File("/i/dont/exist"));
        
        File tempDir = Files.createTempDirectory("test").toFile();

        Files.createTempDirectory(tempDir.toPath(), "testsubprefix");

        TextFileUtils.putStringAsTextFile("Temp File 0 contents", tempDir.getAbsolutePath() + "/tempFile0.tmp");
        TextFileUtils.putStringAsTextFile("Temp File 1 contents", tempDir.getAbsolutePath() + "/tempFile1.tmp");
        
        DirectoryUtils.emptyDirectory(tempDir);
        
        DirectoryUtils.getLocalTempDirectory(null);
        
        byte[] byteArray = new byte[] {0, 0, 0};
        DirectoryUtils.getLocalTempDirectory(Arrays.toString(byteArray));
    }

}
