/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020 Nordix Foundation.
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
package org.onap.policy.apex.core.infrastructure.java.classes;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import org.junit.Test;
import org.mockito.Mockito;

public class ClassUtilsTest {

    @Test
    public void testGetClassNames() throws IOException {
        InputStream input = null;
        ClassUtils.getClassNames();
        assertEquals(new TreeSet<>(), ClassUtils.processJar(input));
    }

    @Test
    public void testProcessFileName() {
        assertEquals("testing.txt",ClassUtils.processFileName("testing.txt"));
        assertNull(ClassUtils.processFileName(null));
        assertEquals("",ClassUtils.processFileName("/classes/"));
    }

    @Test
    public void testProcessDir() throws Exception {
        File mockFile = Mockito.mock(File.class);
        File mockChildFile = Mockito.mock(File.class);
        Mockito.when(mockFile.isDirectory()).thenReturn(false);
        assertEquals(new TreeSet<>(),ClassUtils.processDir(mockFile, "Here"));
        assertEquals(new TreeSet<>(),ClassUtils.processDir(null, "Test"));
        Mockito.when(mockFile.isDirectory()).thenReturn(true);
        File[] files = {mockChildFile};
        Mockito.when(mockFile.listFiles()).thenReturn(files);
        Mockito.when(mockChildFile.getName()).thenReturn("test.class");
        Mockito.when(mockChildFile.getAbsolutePath()).thenReturn("/test/");
        final TreeSet<String> testSet = new TreeSet<>();
        testSet.add(".test.");
        assertEquals(testSet,ClassUtils.processDir(mockFile, "Here"));
        Mockito.when(mockChildFile.getName()).thenReturn("test.class");
        assertEquals(testSet,ClassUtils.processDir(mockFile, "Here"));
        Mockito.when(mockChildFile.getName()).thenReturn("$test.class");
        assertEquals(new TreeSet<>(),ClassUtils.processDir(mockFile, "Here"));
    }

}
