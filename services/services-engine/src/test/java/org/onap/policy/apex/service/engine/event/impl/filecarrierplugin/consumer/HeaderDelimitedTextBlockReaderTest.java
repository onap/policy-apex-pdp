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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;


class HeaderDelimitedTextBlockReaderTest {

    @Test
    void readTextBlockWithDelimiter() throws IOException {
        final String startToken = RandomStringUtils.randomAlphabetic(5);
        final String endToken = RandomStringUtils.randomAlphabetic(6);
        final boolean delimiter = true;
        final String text = RandomStringUtils.randomAlphanumeric(20);
        final String expected = startToken + text;
        // Prepare the stream
        final InputStream stream = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));

        final HeaderDelimitedTextBlockReader reader =
            new HeaderDelimitedTextBlockReader(startToken, endToken, delimiter);
        reader.init(stream);

        final TextBlock textBlock = reader.readTextBlock();
        assertThat(textBlock.getText()).isEqualTo(expected);
    }

    @Test
    void readTextBlockWithEndTokenDelimiter() throws IOException {
        final String startToken = RandomStringUtils.randomAlphabetic(5);
        final String endToken = RandomStringUtils.randomAlphabetic(6);
        final boolean delimiter = true;
        final String text = RandomStringUtils.randomAlphanumeric(20);
        final String input = startToken + "\n" + text + "\n" + endToken + "\n" + text;
        final String expected = startToken + "\n" + text + "\n" + endToken;
        // Prepare the stream
        final InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        final HeaderDelimitedTextBlockReader reader =
            new HeaderDelimitedTextBlockReader(startToken, endToken, delimiter);
        reader.init(stream);

        final TextBlock textBlock = reader.readTextBlock();
        assertThat(textBlock.getText()).isEqualTo(expected);
    }

    @Test
    void readTextBlockWithoutDelimiter() throws IOException {
        final String startToken = RandomStringUtils.randomAlphabetic(5);
        final String endToken = RandomStringUtils.randomAlphabetic(6);
        final boolean delimiter = false;
        final String text = RandomStringUtils.randomAlphanumeric(20);
        // Prepare the stream
        final InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        final HeaderDelimitedTextBlockReader reader =
            new HeaderDelimitedTextBlockReader(startToken, endToken, delimiter);
        reader.init(stream);

        final TextBlock textBlock = reader.readTextBlock();
        assertThat(textBlock.getText()).isEqualTo(text);
    }

    @Test
    void readTextBlockWithEndTokenWithoutDelimiter() throws IOException {
        final String startToken = RandomStringUtils.randomAlphabetic(5);
        final String endToken = RandomStringUtils.randomAlphabetic(6);
        final boolean delimiter = false;
        final String text = RandomStringUtils.randomAlphanumeric(20);
        final String input = startToken + "\n" + text + "\n" + endToken + "\n" + text;
        final String expected = startToken + "\n" + text + "\n" + endToken;
        // Prepare the stream
        final InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        final HeaderDelimitedTextBlockReader reader =
            new HeaderDelimitedTextBlockReader(startToken, endToken, delimiter);
        reader.init(stream);

        final TextBlock textBlock = reader.readTextBlock();
        assertThat(textBlock.getText()).isEqualTo(expected);
    }
}
