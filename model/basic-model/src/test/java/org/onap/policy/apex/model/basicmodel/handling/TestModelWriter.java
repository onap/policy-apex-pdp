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

package org.onap.policy.apex.model.basicmodel.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelWriter;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestModelWriter {

    @Test
    public void testModelWriter() throws IOException, ApexException {
        AxModel model = new TestApexBasicModelCreator().getModel();
        
        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<AxModel>(AxModel.class);
        
        modelWriter.setValidateFlag(true);
        assertTrue(modelWriter.getValidateFlag());
        assertEquals(0, modelWriter.getCDataFieldSet().size());
        
        assertFalse(modelWriter.isJsonOutput());
        modelWriter.setJsonOutput(true);
        assertTrue(modelWriter.isJsonOutput());
        modelWriter.setJsonOutput(false);
        assertFalse(modelWriter.isJsonOutput());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        modelWriter.write(model, baos);
        modelWriter.setJsonOutput(true);
        modelWriter.write(model, baos);
        modelWriter.setJsonOutput(false);
        
        modelWriter.setValidateFlag(false);
        modelWriter.write(model, baos);
        modelWriter.setJsonOutput(true);
        modelWriter.write(model, baos);
        modelWriter.setJsonOutput(false);
        
        modelWriter.setValidateFlag(true);
        model.getKeyInformation().getKeyInfoMap().clear();
        try {
            modelWriter.write(model, baos);
            fail("Test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("Apex concept xml (BasicModel:0.0.1) validation failed", e.getMessage());
        }
        model.getKeyInformation().generateKeyInfo(model);

        try {
            modelWriter.write(null, baos);
            fail("Test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("concept may not be null", e.getMessage());
        }
        
        try {
            ByteArrayOutputStream nullBaos = null;
            modelWriter.write(model, nullBaos);
            fail("Test should throw an exception here");
        }
        catch (Exception e) {
            assertEquals("concept stream may not be null", e.getMessage());
        }
    }
}
