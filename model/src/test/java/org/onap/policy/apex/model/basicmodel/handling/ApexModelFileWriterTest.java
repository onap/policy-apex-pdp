/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

class ApexModelFileWriterTest {

    @Test
    void testModelFileWriter() throws IOException, ApexException {
        ApexModelFileWriter<AxModel> modelFileWriter = new ApexModelFileWriter<>(true);

        modelFileWriter.setValidate(true);
        assertTrue(modelFileWriter.isValidate());

        File tempFile = File.createTempFile("ApexFileWriterTest", "test");
        File tempDir = tempFile.getParentFile();

        File jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/ApexFileWriterTest.json");

        AxModel model = new DummyApexBasicModelCreator().getModel();

        modelFileWriter.apexModelWriteJsonFile(model, AxModel.class, jsonTempFile.getAbsolutePath());

        assertFileDeleted(jsonTempFile);
        assertFileDeleted(new File(tempDir.getAbsolutePath() + "/aaa"));

        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.json");

        modelFileWriter.apexModelWriteJsonFile(model, AxModel.class, jsonTempFile.getAbsolutePath());

        assertFileDeleted(jsonTempFile);

        assertFileDeleted(new File(tempDir.getAbsolutePath() + "/aaa/bbb"));
        assertFileDeleted(new File(tempDir.getAbsolutePath() + "/aaa"));

        File dirA = new File(tempDir.getAbsolutePath() + "/aaa");
        assertTrue(dirA.createNewFile());

        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.json");
        final File jsonTempFile01 = jsonTempFile;
        assertThatThrownBy(
            () -> modelFileWriter.apexModelWriteJsonFile(model, AxModel.class, jsonTempFile01.getAbsolutePath()))
                .hasMessageContaining("could not create directory");

        assertFileDeleted(dirA);

        dirA = new File(tempDir.getAbsolutePath() + "/aaa");
        File fileB = new File(tempDir.getAbsolutePath() + "/aaa/bbb");
        assertTrue(dirA.mkdir());
        assertTrue(fileB.createNewFile());

        jsonTempFile = new File(tempDir.getAbsolutePath() + "/aaa/bbb/ApexFileWriterTest.json");

        File jsonTempFile02 = jsonTempFile;
        assertThatThrownBy(
            () -> modelFileWriter.apexModelWriteJsonFile(model, AxModel.class, jsonTempFile02.getAbsolutePath()))
                .hasMessageContaining("error processing file");

        assertFileDeleted(fileB);
        assertFileDeleted(dirA);
    }

    private void assertFileDeleted(File file) {
        assertTrue(file.delete());
    }
}
