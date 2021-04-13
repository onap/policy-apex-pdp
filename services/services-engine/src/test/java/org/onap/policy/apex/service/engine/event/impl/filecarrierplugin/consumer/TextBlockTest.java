/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021  Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class TextBlockTest {
    private final Random random = new Random();

    @Test
    public void isEndOfText() {
        final boolean endOfText = random.nextBoolean();
        final TextBlock textBlock = new TextBlock(endOfText, null);

        assertEquals(endOfText, textBlock.isEndOfText());
    }

    @Test
    public void getText() {
        final boolean endOfText = random.nextBoolean();
        final String text = RandomStringUtils.randomAlphanumeric(8);
        final TextBlock textBlock = new TextBlock(endOfText, text);

        assertEquals(text, textBlock.getText());
    }

    @Test
    public void setText() {
        final boolean endOfText = random.nextBoolean();
        final String text = RandomStringUtils.randomAlphanumeric(8);
        final TextBlock textBlock = new TextBlock(endOfText, null);

        textBlock.setText(text);
        assertEquals(text, textBlock.getText());
    }
}