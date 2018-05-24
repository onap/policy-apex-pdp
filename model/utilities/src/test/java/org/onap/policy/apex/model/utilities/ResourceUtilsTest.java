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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.utilities.ResourceUtils;

/**
 * The Class ResourceUtilsTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ResourceUtilsTest {
    private File tmpDir = null;
    private File tmpEmptyFile = null;
    private File tmpUsedFile = null;

    private String jarDirResource = null;
    private String jarFileResource = null;

    private final String pathDirResource = "testdir";
    private final String pathFileResource = "testdir/testfile.xml";

    private final String nonExistantResource = "somewhere/over/the/rainbow";
    private final String invalidResource = "@%%%\\\\_:::DESD";

    /**
     * Setup resource utils test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void setupResourceUtilsTest() throws IOException {
        tmpDir = new File(System.getProperty("java.io.tmpdir"));
        tmpEmptyFile = File.createTempFile(this.getClass().getName(), ".tmp");
        tmpUsedFile = File.createTempFile(this.getClass().getName(), ".tmp");

        jarDirResource = "META-INF";
        jarFileResource = "META-INF/MANIFEST.MF";

        final FileWriter fileWriter = new FileWriter(tmpUsedFile);
        fileWriter.write("Bluebirds fly over the rainbow");
        fileWriter.close();
    }

    /**
     * Test get url resource.
     */
    @Test
    public void testGetURLResource() {
        URL theURL = ResourceUtils.getURLResource(tmpDir.getAbsolutePath());
        assertNull(theURL);

        theURL = ResourceUtils.getURLResource(tmpEmptyFile.getAbsolutePath());
        assertNull(theURL);

        theURL = ResourceUtils.getURLResource(tmpUsedFile.getAbsolutePath());
        assertNull(theURL);

        theURL = ResourceUtils.getURLResource(jarDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURLResource(jarFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURLResource(pathDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURLResource(pathFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURLResource("file:///" + pathDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile("src/test/resources/" + pathDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile("src/test/resources/" + pathFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURLResource(nonExistantResource);
        assertNull(theURL);

        theURL = ResourceUtils.getURLResource(invalidResource);
        assertNull(theURL);

        theURL = ResourceUtils.getURLResource(null);
        assertNull(theURL);
    }

    /**
     * Test get local file.
     */
    @Test
    public void testGetLocalFile() {
        URL theURL = ResourceUtils.getLocalFile(tmpDir.getAbsolutePath());
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile(tmpEmptyFile.getAbsolutePath());
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile(tmpUsedFile.getAbsolutePath());
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile(jarDirResource);
        assertNull(theURL);

        theURL = ResourceUtils.getLocalFile(jarFileResource);
        assertNull(theURL);

        theURL = ResourceUtils.getLocalFile(pathDirResource);
        assertNull(theURL);

        theURL = ResourceUtils.getLocalFile(pathFileResource);
        assertNull(theURL);

        theURL = ResourceUtils.getLocalFile("src/test/resources/" + pathDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile("src/test/resources/" + pathFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getLocalFile(nonExistantResource);
        assertNull(theURL);

        theURL = ResourceUtils.getLocalFile(invalidResource);
        assertNull(theURL);

        theURL = ResourceUtils.getLocalFile("file:///");
        assertNotNull(theURL);
        
        theURL = ResourceUtils.getLocalFile("file:///testdir/testfile.xml");
        assertNull(theURL);
        
        theURL = ResourceUtils.getLocalFile(null);
        assertNull(theURL);
    }

    /**
     * Test get resource as stream.
     */
    @Test
    public void testGetResourceAsStream() {
        InputStream theStream = ResourceUtils.getResourceAsStream(tmpDir.getAbsolutePath());
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(tmpEmptyFile.getAbsolutePath());
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(tmpUsedFile.getAbsolutePath());
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(jarDirResource);
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(jarFileResource);
        assertNotNull(theStream);
        
        theStream = ResourceUtils.getResourceAsStream(pathDirResource);
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(pathFileResource);
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream("src/test/resources/" + pathDirResource);
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream("src/test/resources/" + pathFileResource);
        assertNotNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(nonExistantResource);
        assertNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(invalidResource);
        assertNull(theStream);

        theStream = ResourceUtils.getResourceAsStream(null);
        assertNull(null);

        theStream = ResourceUtils.getResourceAsStream("");
        assertNull(null);
    }

    /**
     * Test get resource as string.
     */
    @Test
    public void testGetResourceAsString() {
        String theString = ResourceUtils.getResourceAsString(tmpDir.getAbsolutePath());
        assertNotNull(theString);

        theString = ResourceUtils.getResourceAsString(tmpEmptyFile.getAbsolutePath());
        assertTrue(theString.equals(""));

        theString = ResourceUtils.getResourceAsString(tmpUsedFile.getAbsolutePath());
        assertTrue(theString.equals("Bluebirds fly over the rainbow"));

        theString = ResourceUtils.getResourceAsString(jarFileResource);
        assertNotNull(theString);

        theString = ResourceUtils.getResourceAsString(pathDirResource);
        assertNotNull(theString);

        theString = ResourceUtils.getResourceAsString(pathFileResource);
        assertNotNull(theString);

        theString = ResourceUtils.getResourceAsString("src/test/resources/" + pathDirResource);
        assertNotNull(theString);

        theString = ResourceUtils.getResourceAsString("src/test/resources/" + pathFileResource);
        assertNotNull(theString);

        theString = ResourceUtils.getResourceAsString(nonExistantResource);
        assertNull(theString);

        theString = ResourceUtils.getResourceAsString(invalidResource);
        assertNull(theString);

        theString = ResourceUtils.getResourceAsString(null);
        assertNull(theString);

        theString = ResourceUtils.getResourceAsString("");
        assertEquals("org\ntestdir\n", theString);
    }

    @Test
    public void testGetURL4Resource() {
        URL theURL = ResourceUtils.getURL4Resource(tmpDir.getAbsolutePath());
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(tmpEmptyFile.getAbsolutePath());
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(tmpUsedFile.getAbsolutePath());
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(jarDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(jarFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(pathDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(pathFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource("src/test/resources/" + pathDirResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource("src/test/resources/" + pathFileResource);
        assertNotNull(theURL);

        theURL = ResourceUtils.getURL4Resource(nonExistantResource);
        assertNull(theURL);

        theURL = ResourceUtils.getURL4Resource(invalidResource);
        assertNull(theURL);
    }

    @Test
    public void testGetFilePath4Resource() {
        assertNull(ResourceUtils.getFilePath4Resource(null));
        assertEquals("/something/else", ResourceUtils.getFilePath4Resource("/something/else"));
        assertTrue(ResourceUtils.getFilePath4Resource("xml/example.xml").endsWith("xml/example.xml"));
    }
    
    /**
     * Cleandown resource utils test.
     */
    @After
    public void cleandownResourceUtilsTest() {
        tmpEmptyFile.delete();
        tmpUsedFile.delete();
    }
}
