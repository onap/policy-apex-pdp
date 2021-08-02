/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.w3c.dom.Document;

@RunWith(MockitoJUnitRunner.class)
public class ApexModelWriterTest {
    @Mock
    private Marshaller marshallerMock;

    @Test
    public void testModelWriter() throws IOException, ApexException {
        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<AxModel>(AxModel.class);

        modelWriter.setValidateFlag(true);
        assertTrue(modelWriter.isValidateFlag());
        assertEquals(0, modelWriter.getCDataFieldSet().size());

        assertFalse(modelWriter.isJsonOutput());
        modelWriter.setJsonOutput(true);
        assertTrue(modelWriter.isJsonOutput());
        modelWriter.setJsonOutput(false);
        assertFalse(modelWriter.isJsonOutput());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        AxModel model = new DummyApexBasicModelCreator().getModel();

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
        assertThatThrownBy(() -> modelWriter.write(model, baos))
            .hasMessageContaining("Apex concept xml (BasicModel:0.0.1) validation failed");
        model.getKeyInformation().generateKeyInfo(model);

        assertThatThrownBy(() -> modelWriter.write(null, baos))
            .hasMessage("concept may not be null");

        ByteArrayOutputStream nullBaos = null;
        assertThatThrownBy(() -> modelWriter.write(model, nullBaos))
            .hasMessage("concept stream may not be null");
    }

    @Test
    public void testSetOutputTypeError() throws ApexModelException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, PropertyException {
        MockitoAnnotations.initMocks(this);

        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<AxModel>(AxModel.class);

        Field marshallerField = modelWriter.getClass().getDeclaredField("marshaller");
        marshallerField.setAccessible(true);
        marshallerField.set(modelWriter, marshallerMock);
        marshallerField.setAccessible(false);
        Mockito.doThrow(new PropertyException("Exception setting JAXB property")).when(marshallerMock)
            .setProperty(Mockito.anyString(), Mockito.anyString());
        assertThatThrownBy(() -> modelWriter.setJsonOutput(true))
            .hasMessage("JAXB error setting marshaller for JSON output");
        Mockito.doThrow(new PropertyException("Exception setting JAXB property")).when(marshallerMock)
            .setProperty(Mockito.anyString(), Mockito.anyString());
        assertThatThrownBy(() -> modelWriter.setJsonOutput(false))
            .hasMessage("JAXB error setting marshaller for XML output");
    }

    @Test
    public void testOutputJsonError() throws ApexModelException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, JAXBException {
        MockitoAnnotations.initMocks(this);

        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<AxModel>(AxModel.class);

        Field marshallerField = modelWriter.getClass().getDeclaredField("marshaller");
        marshallerField.setAccessible(true);
        marshallerField.set(modelWriter, marshallerMock);
        marshallerField.setAccessible(false);

        modelWriter.setValidateFlag(false);
        modelWriter.setJsonOutput(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AxModel model = new DummyApexBasicModelCreator().getModel();
        Mockito.doThrow(new JAXBException("Exception marshalling to JSON")).when(marshallerMock)
            .marshal((AxModel) Mockito.anyObject(), (Writer) Mockito.anyObject());
        assertThatThrownBy(() -> modelWriter.write(model, baos)).hasMessage("Unable to marshal Apex concept to JSON");
    }

    @Test
    public void testOutputXmlError() throws ApexModelException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, JAXBException {
        MockitoAnnotations.initMocks(this);

        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<AxModel>(AxModel.class);
        modelWriter.setJsonOutput(false);

        Field marshallerField = modelWriter.getClass().getDeclaredField("marshaller");
        marshallerField.setAccessible(true);
        marshallerField.set(modelWriter, marshallerMock);
        marshallerField.setAccessible(false);

        modelWriter.setValidateFlag(false);
        modelWriter.setJsonOutput(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AxModel model = new DummyApexBasicModelCreator().getModel();

        Mockito.doThrow(new JAXBException("Exception marshalling to JSON")).when(marshallerMock)
            .marshal((AxModel) Mockito.anyObject(), (Document) Mockito.anyObject());

        assertThatThrownBy(() -> modelWriter.write(model, baos))
            .hasMessage("Unable to marshal Apex concept to XML");
    }
}
