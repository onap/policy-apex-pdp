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

package org.onap.policy.apex.service.parameters.eventprotocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * An event protocol parameter class for token delimited textual event protocols that may be specialized by event
 * protocol plugins that require plugin specific parameters.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>startDelimiterToken: the token string that delimits the start of text blocks that contain events.
 * <li>endDelimiterToken: the token string that delimits the end of text blocks that contain events, this parameter is
 * optional and defaults to null.
 * <li>delimiterAtStart: indicates if the first text block should have a delimiter at the start (true), or whether
 * processing of the first block should begin at the start of the text (false). The parameter is optional and defaults
 * to true.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class EventProtocolTextTokenDelimitedParameters extends EventProtocolParameters {
    // The delimiter token for text blocks
    private @NotNull @NotBlank String startDelimiterToken = null;
    private String endDelimiterToken = null;
    private boolean delimiterAtStart = true;
}
