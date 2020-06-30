/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.core.infrastructure.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import javax.xml.xpath.XPathConstants;
import org.junit.Test;

public class XPathReaderTest {

    final URL testResource = getClass().getClassLoader().getResource("xmlTestFile.xml");

    @Test
    public void canConstructWithFromFile() {
        final XPathReader objectUnderTest = new XPathReader(testResource.getFile());
        assertNotNull(objectUnderTest);
    }

    @Test
    public void canConstructWithStream() throws IOException {
        final XPathReader objectUnderTest = new XPathReader(testResource.openStream());
        assertNotNull(objectUnderTest);
    }

    @Test
    public void canConstructWithNull() {
        final XPathReader objectUnderTest = new XPathReader((String) null);
        assertNotNull(objectUnderTest);
    }

    @Test
    public void canConstructWithInvalidFileName() {
        final XPathReader objectUnderTest = new XPathReader("");
        assertNotNull(objectUnderTest);
    }

    @Test
    public void readReturnsCorrectValueWhenUsingCorrectXpath() throws IOException {
        final XPathReader objectUnderTest = new XPathReader(testResource.openStream());
        final String validXPathExpression = "/example/name";
        final Object result = objectUnderTest.read(validXPathExpression, XPathConstants.STRING);
        assertEquals("This is my name", result);
    }

    @Test
    public void readReturnsNullWhenUsingInvalidXpath() throws IOException {
        final XPathReader objectUnderTest = new XPathReader(testResource.openStream());
        final String invalidXPathExpression = "";
        assertNull(objectUnderTest.read(invalidXPathExpression, XPathConstants.STRING));
    }

}
