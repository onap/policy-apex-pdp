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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.apex.model.basicmodel.concepts.AxModel;
import org.onap.apex.model.basicmodel.test.TestApexModel;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestBasicModelTest {

    @Test
    public void testNormalModelCreator() throws ApexException {
        TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexBasicModelCreator());

        testApexModel.testApexModelValid();
        try {
            testApexModel.testApexModelVaidateObservation();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertEquals("model should have observations", e.getMessage());
        }
        testApexModel.testApexModelVaidateWarning();
        testApexModel.testApexModelVaidateInvalidModel();
        testApexModel.testApexModelVaidateMalstructured();

        testApexModel.testApexModelWriteReadJSON();
        testApexModel.testApexModelWriteReadXML();
    }

    @Test
    public void testModelCreator0() throws ApexException {
        TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexTestModelCreator0());

        testApexModel.testApexModelValid();
        try {
            testApexModel.testApexModelVaidateObservation();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertEquals("model should have observations", e.getMessage());
        }
        try {
            testApexModel.testApexModelVaidateWarning();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertEquals("model should have warnings", e.getMessage());
        }
        try {
            testApexModel.testApexModelVaidateInvalidModel();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertEquals("should not be valid ***validation of model successful***", e.getMessage());
        }
        try {
            testApexModel.testApexModelVaidateMalstructured();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertEquals("should not be valid ***validation of model successful***", e.getMessage());
        }
    }

    @Test
    public void testModelCreator1() throws ApexException {
        TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexTestModelCreator1());

        try {
            testApexModel.testApexModelValid();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("model is invalid"));
        }
        try {
            testApexModel.testApexModelVaidateObservation();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("model is invalid"));
        }
        try {
            testApexModel.testApexModelVaidateWarning();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("model is invalid"));
        }
        testApexModel.testApexModelVaidateInvalidModel();
        testApexModel.testApexModelVaidateMalstructured();
    }
    
    @Test
    public void testModelCreator2() throws ApexException {
        TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexTestModelCreator2());

        testApexModel.testApexModelValid();
        testApexModel.testApexModelVaidateObservation();
        try {
            testApexModel.testApexModelVaidateWarning();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertEquals("model should have warnings", e.getMessage());
        }
    }
    
    @Test
    public void testModelCreator1XMLJSON() throws ApexException {
        TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexTestModelCreator1());

        try {
            testApexModel.testApexModelWriteReadJSON();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("error processing file"));
        }

        try {
            testApexModel.testApexModelWriteReadXML();
            fail("Test should throw an exception");
        }
        catch (Exception e) {
            assertTrue(e.getMessage().startsWith("error processing file"));
        }
    }
}
