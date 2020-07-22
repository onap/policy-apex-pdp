/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

public class ApexModelFileWriterTest {

    @Test
    public void testModelFileWriter() throws IOException, ApexException {
        ApexModelFileWriter<AxModel> modelFileWriter = new ApexModelFileWriter<>(true);

        modelFileWriter.setValidateFlag(true);
        assertTrue(modelFileWriter.isValidateFlag());

        File tempFile = File.createTempFile("ApexFileWriterTest", "test");
        File tempDir = tempFile.getParentFile();

        File jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/ApexFileWriterTest.json");
        File xmlTempFile = new File(tempDir.getAbsolutePath() + "/ccc/ApexFileWriterTest.xml");

        AxModel model = new DummyApexBasicModelCreator().getModel();

        modelFileWriter.apexModelWriteJsonFile(model, AxModel.class, jsonTempFile.getAbsolutePath());
        modelFileWriter.apexModelWriteXmlFile(model, AxModel.class, xmlTempFile.getAbsolutePath());

        jsonTempFile.delete();
        xmlTempFile.delete();
        new File(tempDir.getAbsolutePath() + "/aaa").delete();
        new File(tempDir.getAbsolutePath() + "/ccc").delete();

        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.json");
        xmlTempFile = new File(tempDir.getAbsolutePath() + "/ccc/ddd/ApexFileWriterTest.xml");

        modelFileWriter.apexModelWriteJsonFile(model, AxModel.class, jsonTempFile.getAbsolutePath());
        modelFileWriter.apexModelWriteXmlFile(model, AxModel.class, xmlTempFile.getAbsolutePath());

        jsonTempFile.delete();
        xmlTempFile.delete();

        new File(tempDir.getAbsolutePath() + "/aaa/bbb").delete();
        new File(tempDir.getAbsolutePath() + "/aaa").delete();
        new File(tempDir.getAbsolutePath() + "/ccc/ddd").delete();
        new File(tempDir.getAbsolutePath() + "/ccc").delete();

        File dirA = new File(tempDir.getAbsolutePath() + "/aaa");
        //File dirB = new File(tempDir.getAbsolutePath() + "/aaa/bbb");
        dirA.createNewFile();
        //dirB.createNewFile();

        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.json");
        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.xml");
        final File jsonTempFile01 = jsonTempFile;
        /*assertThatThrownBy(() -> modelFileWriter.apexModelWriteJsonFile(model, AxModel.class,
                jsonTempFile01.getAbsolutePath()))
                .hasMessageContaining("could not create directory");

        assertThatThrownBy(() -> modelFileWriter.apexModelWriteXmlFile(model, AxModel.class,
                jsonTempFile01.getAbsolutePath()))
                .hasMessageContaining("could not create directory");*/

        dirA.delete();

        dirA = new File(tempDir.getAbsolutePath() + "/aaa");
        File fileB = new File(tempDir.getAbsolutePath() + "/aaa/bbb");
        dirA.mkdir();
        fileB.createNewFile();

        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.json");
        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.xml");

        File jsonTempFile02 = jsonTempFile;
        /*assertThatThrownBy(() -> modelFileWriter.apexModelWriteJsonFile(model, AxModel.class,
                jsonTempFile02.getAbsolutePath()))
                .hasMessageContaining("error processing file");

        assertThatThrownBy(() -> modelFileWriter.apexModelWriteXmlFile(model, AxModel.class,
                jsonTempFile02.getAbsolutePath()))
                .hasMessageContaining("error processing file");*/

        fileB.delete();
        dirA.delete();
    }
}
