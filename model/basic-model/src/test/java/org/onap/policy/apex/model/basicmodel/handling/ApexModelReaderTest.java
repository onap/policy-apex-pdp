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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

@RunWith(MockitoJUnitRunner.class)
public class ApexModelReaderTest {
    @Mock
    private Unmarshaller unmarshallerMock;

    @Test
    public void testModelReader() throws IOException, ApexException {
        AxModel model = new DummyApexBasicModelCreator().getModel();
        AxModel invalidModel = new DummyApexBasicModelCreator().getInvalidModel();

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
        assertTrue(modelReader.isValidateFlag());

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        AxModel readModel = modelReader.read(bais);
        assertEquals(model, readModel);

        ByteArrayInputStream baisInvalid = new ByteArrayInputStream(baosInvalid.toByteArray());
        assertThatThrownBy(() -> modelReader.read(baisInvalid))
            .hasMessageStartingWith("Apex concept validation failed");
        modelReader.setValidateFlag(false);
        assertFalse(modelReader.isValidateFlag());

        ByteArrayInputStream bais2 = new ByteArrayInputStream(baos.toByteArray());
        AxModel readModel2 = modelReader.read(bais2);
        assertEquals(model, readModel2);

        modelWriter.setJsonOutput(false);

        ByteArrayOutputStream baosXml = new ByteArrayOutputStream();
        modelWriter.write(model, baosXml);

        ByteArrayInputStream baisXml = new ByteArrayInputStream(baosXml.toByteArray());
        AxModel readModelXml = modelReader.read(baisXml);
        assertEquals(model, readModelXml);

        String dummyString = "SomeDummyText";
        ByteArrayInputStream baisDummy = new ByteArrayInputStream(dummyString.getBytes());
        assertThatThrownBy(() -> modelReader.read(baisDummy))
            .hasMessage("format of input for Apex concept is neither JSON nor XML");
        ByteArrayInputStream nullBais = null;
        assertThatThrownBy(() -> modelReader.read(nullBais))
            .hasMessage("concept stream may not be null");

        assertThatThrownBy(() -> {
            FileInputStream fis = new FileInputStream(new File("somewhere/over/the/rainbow"));
            modelReader.read(fis);
        }).hasMessageContaining("rainbow");
        final File tempFile = File.createTempFile("Apex", "Dummy");
        BufferedReader br = new BufferedReader(new FileReader(tempFile));
        br.close();
        assertThatThrownBy(() -> modelReader.read(br))
             .hasMessage("Unable to read Apex concept ");
        tempFile.delete();
        modelReader.setSchema(null);

        final File tempFileA = File.createTempFile("Apex", "Dummy");
        assertThatThrownBy(() -> modelReader.setSchema(tempFileA.getCanonicalPath()))
            .hasMessage("Unable to load schema");
        tempFile.delete();
        modelReader.setSchema("xml/example.xsd");
    }

    @Test
    public void testSetInputTypeError() throws ApexModelException,
        NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        ApexModelReader<AxModel> modelReader = new ApexModelReader<AxModel>(AxModel.class, true);

        Field marshallerField = modelReader.getClass().getDeclaredField("unmarshaller");
        marshallerField.setAccessible(true);
        marshallerField.set(modelReader, unmarshallerMock);
        marshallerField.setAccessible(false);

        assertThatThrownBy(() -> {
            Mockito.doThrow(new JAXBException("Exception marshalling to JSON")).when(unmarshallerMock)
                .unmarshal((StreamSource) Mockito.anyObject(), Mockito.anyObject());

            modelReader.read("{Hello}");
        }).hasMessage("Unable to unmarshal Apex concept ");
        assertThatThrownBy(() -> {
            Mockito.doThrow(new PropertyException("Exception setting JAXB property")).when(unmarshallerMock)
                .setProperty(Mockito.anyString(), Mockito.anyString());
            modelReader.read("{Hello}");
        }).hasMessage("JAXB error setting unmarshaller for JSON input");
        assertThatThrownBy(() -> {
            Mockito.doThrow(new PropertyException("Exception setting JAXB property")).when(unmarshallerMock)
                .setProperty(Mockito.anyString(), Mockito.anyString());
            modelReader.read("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        }).hasMessage("JAXB error setting unmarshaller for XML input");
    }
}
