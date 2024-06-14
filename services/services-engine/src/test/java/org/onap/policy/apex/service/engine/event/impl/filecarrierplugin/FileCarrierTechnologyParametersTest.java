/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterRuntimeException;
import org.onap.policy.common.parameters.ValidationResult;

class FileCarrierTechnologyParametersTest {

    private final Random random = new Random();
    private static final String APEX_RELATIVE_FILE_ROOT = "APEX_RELATIVE_FILE_ROOT";
    private final String defaultApesRelativeFileRoot = System.getProperty(APEX_RELATIVE_FILE_ROOT);
    private FileCarrierTechnologyParameters parameters;
    private File tempFile;

    @BeforeEach
    void setUp() {
        parameters = new FileCarrierTechnologyParameters();
    }

    /**
     * Cleaning after testing.
     */
    @AfterEach
    void tearDown() {
        if (tempFile != null) {
            assertTrue(tempFile.delete());
        }
        if (defaultApesRelativeFileRoot != null) {
            System.setProperty(APEX_RELATIVE_FILE_ROOT, defaultApesRelativeFileRoot);
        } else {
            System.clearProperty(APEX_RELATIVE_FILE_ROOT);
        }
    }

    @Test
    void getSetFileName() {
        final String fileName = RandomStringUtils.random(10);
        parameters.setFileName(fileName);
        assertThat(parameters.getFileName())
            .isEqualTo(fileName);
    }

    @Test
    void isStandardIo() {
        assertThat(parameters.isStandardIo()).isFalse();
    }

    @Test
    void isStandardError() {
        assertThat(parameters.isStandardError()).isFalse();
    }

    @Test
    void isStreamingMode() {
        assertThat(parameters.isStreamingMode()).isFalse();
    }

    @Test
    void setStandardIo() {
        final boolean standardIo = random.nextBoolean();
        parameters.setStandardIo(standardIo);
        assertThat(parameters.isStandardIo()).isEqualTo(standardIo);
    }

    @Test
    void setStandardError() {
        final boolean standardError = random.nextBoolean();
        parameters.setStandardError(standardError);
        assertThat(parameters.isStandardError()).isEqualTo(standardError);
    }

    @Test
    void getStartDelay() {
        assertThat(parameters.getStartDelay()).isZero();
    }

    @Test
    void setStartDelay() {
        final long delay = random.nextInt();
        parameters.setStartDelay(delay);
        assertThat(parameters.getStartDelay()).isEqualTo(delay);
    }

    @Test
    void getLabel() {
        final String label = RandomStringUtils.random(10);
        parameters.setLabel(label);
        assertThat(parameters.getLabel()).isEqualTo(label);
    }

    @Test
    void setName() {
        final String name = RandomStringUtils.random(10);
        assertThatThrownBy(() -> parameters.setName(name)).isInstanceOf(ParameterRuntimeException.class);
    }

    @Test
    void getName() {
        final String label = RandomStringUtils.random(10);
        parameters.setLabel(label);
        assertThat(parameters.getName()).isEqualTo(label);
    }

    @Test
    void getStreamingMode() {
        assertThat(parameters.isStreamingMode()).isFalse();
    }

    @Test
    void setStreamingMode() {
        final boolean streamingMode = random.nextBoolean();
        parameters.setStreamingMode(streamingMode);
        assertThat(parameters.isStreamingMode()).isEqualTo(streamingMode);
    }

    @Test
    void validateFileNameNull() {
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validateFileNameAbsolutePath() throws IOException {
        tempFile = File.createTempFile("test_", ".tmp");
        parameters.setFileName(tempFile.getAbsolutePath());
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateFileNameAbsolutePathNotExisting() {
        parameters.setFileName(RandomStringUtils.randomAlphabetic(5) + ".tmp");
        System.setProperty(APEX_RELATIVE_FILE_ROOT, System.getProperty("user.home"));
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateDirectoryName() {
        parameters.setFileName(System.getProperty("user.dir"));
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validateParentNotDirectory() {
        final URL resource = FileCarrierTechnologyParameters.class
            .getResource("FileCarrierTechnologyParameters.class");
        assumeTrue(resource != null && "file".equalsIgnoreCase(resource.getProtocol()));
        final String fileParentPath = resource.getPath();
        final String fileName = RandomStringUtils.randomAlphabetic(5);
        final String absolutePath = new File(fileParentPath, fileName).getAbsolutePath();
        parameters.setFileName(absolutePath);
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validateParentDoesNOtExists() {
        final File fileParent = new File(System.getProperty("user.home"), RandomStringUtils.randomAlphabetic(6));
        final String fileName = RandomStringUtils.randomAlphabetic(5);
        final String absolutePath = new File(fileParent, fileName).getAbsolutePath();
        parameters.setFileName(absolutePath);
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validateStandardIo() {
        parameters.setStandardIo(true);
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateStandardError() {
        parameters.setStandardError(true);
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateNegativeDelay() {
        final long delay = random.nextInt() * -1;
        parameters.setStartDelay(delay);
        final ValidationResult result = parameters.validate();
        assertThat(result.isValid()).isFalse();
    }

}