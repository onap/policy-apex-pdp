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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.apex.model.basicmodel.concepts.AxModel;
import org.onap.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.apex.model.basicmodel.handling.ApexModelWriter;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestModelReader {

    @Test
    public void testModelReader() throws IOException, ApexException {
        AxModel model = new TestApexBasicModelCreator().getModel();
        AxModel invalidModel = new TestApexBasicModelCreator().getInvalidModel();
        
        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<AxModel>(AxModel.class);
        modelWriter.setValidateFlag(true);
        modelWriter.setJsonOutput(true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        modelWriter.write(model, baos);
        
        ByteArrayOutputStream baosInvalid = new ByteArrayOutputStream();
        modelWriter.setValidateFlag(false);
        modelWriter.write(invalidModel, baosInvalid);
        
        ApexModelReader<AxModel> modelReader = new ApexModelReader<AxModel>(AxModel.class, true);
        
        modelReader.setValidateFlag(true);
        assertTrue(modelReader.getValidateFlag());
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        AxModel readModel = modelReader.read(bais);
        assertEquals(model, readModel);
        
        ByteArrayInputStream baisInvalid = new ByteArrayInputStream(baosInvalid.toByteArray());
        try {
            modelReader.read(baisInvalid);
            fail("test should throw an exceptino here");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Apex concept validation failed"));
        }
        
        modelReader.setValidateFlag(false);
        assertFalse(modelReader.getValidateFlag());
        
        ByteArrayInputStream bais2 = new ByteArrayInputStream(baos.toByteArray());
        AxModel readModel2 = modelReader.read(bais2);
        assertEquals(model, readModel2);
        
        modelWriter.setJsonOutput(false);
        
        ByteArrayOutputStream baosXML = new ByteArrayOutputStream();
        modelWriter.write(model, baosXML);
        
        ByteArrayInputStream baisXML = new ByteArrayInputStream(baosXML.toByteArray());
        AxModel readModelXML = modelReader.read(baisXML);
        assertEquals(model, readModelXML);
        
        String dummyString = "SomeDummyText";
        ByteArrayInputStream baisDummy = new ByteArrayInputStream(dummyString.getBytes());
        try {
            modelReader.read(baisDummy);
            fail("test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("format of input for Apex concept is neither JSON nor XML", e.getMessage());
        }
        
        try {
            ByteArrayInputStream nullBais = null;
            modelReader.read(nullBais);
            fail("test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("concept stream may not be null", e.getMessage());
        }
        
        try {
            FileInputStream fis = new FileInputStream(new File("somewhere/over/the/rainbow"));
            modelReader.read(fis);
            fail("test should throw an exception here");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().contains("rainbow"));
        }
        
        File tempFile = File.createTempFile("Apex", "Dummy");
        try {
            BufferedReader br = new BufferedReader(new FileReader(tempFile));
            br.close();
            modelReader.read(br);
            fail("test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("Unable to read Apex concept ", e.getMessage());
        }
        finally {
            tempFile.delete();
        }
        
        modelReader.setSchema(null);
        
        tempFile = File.createTempFile("Apex", "Dummy");
        try {
            modelReader.setSchema(tempFile.getCanonicalPath());
            fail("test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("Unable to load schema", e.getMessage());
        }
        finally {
            tempFile.delete();
        }
        
        modelReader.setSchema("xml/example.xsd");
    }
}
