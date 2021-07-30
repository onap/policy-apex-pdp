/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is a bean that holds a block of text read from an incoming text file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
public class TextBlock {
    private boolean endOfText = false;
    private String text;

    /**
     * Constructor to initiate the text block.
     *
     * @param endOfText the end of text
     * @param text the text
     */
    public TextBlock(final boolean endOfText, final String text) {
        this.endOfText = endOfText;
        this.text = text;
    }
}
