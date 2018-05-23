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

package org.onap.apex.model.basicmodel.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.apex.model.basicmodel.concepts.AxModel;
import org.onap.apex.model.basicmodel.handling.ApexModelStringWriter;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestModelStringWriter {

    @Test
    public void testModelStringWriter() throws IOException, ApexException {
        AxModel basicModel = new TestApexBasicModelCreator().getModel();
        assertNotNull(basicModel);
       
        AxKeyInfo intKI   = basicModel.getKeyInformation().get("IntegerKIKey");
        AxKeyInfo floatKI = basicModel.getKeyInformation().get("FloatKIKey");

        // Ensure marshalling is OK
        ApexModelStringWriter<AxKeyInfo> stringWriter = new ApexModelStringWriter<AxKeyInfo>(true);
        
        assertNotNull(stringWriter.writeJSONString(intKI,   AxKeyInfo.class));
        assertNotNull(stringWriter.writeJSONString(floatKI, AxKeyInfo.class));
       
        assertNotNull(stringWriter.writeString(intKI,   AxKeyInfo.class, true));
        assertNotNull(stringWriter.writeString(floatKI, AxKeyInfo.class, true));
       
        assertNotNull(stringWriter.writeString(intKI,   AxKeyInfo.class, false));
        assertNotNull(stringWriter.writeString(floatKI, AxKeyInfo.class, false));
        
        assertNotNull(stringWriter.writeXMLString(intKI,   AxKeyInfo.class));
        assertNotNull(stringWriter.writeXMLString(floatKI, AxKeyInfo.class));
        
        try {
            stringWriter.writeString(null, AxKeyInfo.class, true);
            fail("test should thrown an exception here");
        }
        catch (Exception e) {
            assertEquals("concept may not be null", e.getMessage());
        }
        
        try {
            stringWriter.writeString(null, AxKeyInfo.class, false);
            fail("test should thrown an exception here");
        }
        catch (Exception e) {
            assertEquals("concept may not be null", e.getMessage());
        }
        
        try {
            stringWriter.writeJSONString(null, AxKeyInfo.class);
            fail("test should thrown an exception here");
        }
        catch (Exception e) {
            assertEquals("error writing JSON string", e.getMessage());
        }
        
        try {
            stringWriter.writeXMLString(null, AxKeyInfo.class);
            fail("test should thrown an exception here");
        }
        catch (Exception e) {
            assertEquals("error writing XML string", e.getMessage());
        }
        
        stringWriter.setValidateFlag(true);
        assertTrue(stringWriter.isValidateFlag());
    }
}
