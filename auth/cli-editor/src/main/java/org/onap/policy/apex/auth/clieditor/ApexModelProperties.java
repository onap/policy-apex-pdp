/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.apex.auth.clieditor;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class contains the definitions of Apex model properties.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString
public class ApexModelProperties {
    /** The default version that will be used for concepts. */
    public static final String DEFAULT_CONCEPT_VERSION = "0.0.1";

    /** The default name space that will be used for concepts. */
    public static final String DEFAULT_EVENT_NAMESPACE = "org.onap.policy.apex";

    /** The default source that will be used for events. */
    public static final String DEFAULT_EVENT_SOURCE = "eventSource";

    /** The default target that will be used for events. */
    public static final String DEFAULT_EVENT_TARGET = "eventTarget";

    /** The default logic block start token. */
    public static final String DEFAULT_LOGIC_BLOCK_START_TAG = "LB{";

    /** The default logic block end token. */
    public static final String DEFAULT_LOGIC_BLOCK_END_TAG = "}LB";

    /** The default logic block end token. */
    public static final String DEFAULT_POLICY_TEMPLATE = "FREEFORM";

    /** The default macro file token. */
    public static final String DEFAULT_MACRO_FILE_TAG = "#MACROFILE:";

    // @formatter:off
    private String defaultConceptVersion     = DEFAULT_CONCEPT_VERSION;
    private String defaultEventNamespace     = DEFAULT_EVENT_NAMESPACE;
    private String defaultEventSource        = DEFAULT_EVENT_SOURCE;
    private String defaultEventTarget        = DEFAULT_EVENT_TARGET;
    private String defaultLogicBlockStartTag = DEFAULT_LOGIC_BLOCK_START_TAG;
    private String defaultLogicBlockEndTag   = DEFAULT_LOGIC_BLOCK_END_TAG;
    private String defaultPolicyTemplate     = DEFAULT_POLICY_TEMPLATE;
    private String defaultMacroFileTag       = DEFAULT_MACRO_FILE_TAG;
    // @formatter:on

    /**
     * Gets the default property values for the Apex CLI editor.
     *
     * @return the default properties
     */
    public Properties getProperties() {
        final var properties = new Properties();
        // @formatter:off
        properties.setProperty("DEFAULT_CONCEPT_VERSION", defaultConceptVersion);
        properties.setProperty("DEFAULT_EVENT_NAMESPACE", defaultEventNamespace);
        properties.setProperty("DEFAULT_EVENT_SOURCE", defaultEventSource);
        properties.setProperty("DEFAULT_EVENT_TARGET", defaultEventTarget);
        properties.setProperty("DEFAULT_LOGIC_BLOCK_START_TAG", defaultLogicBlockStartTag);
        properties.setProperty("DEFAULT_LOGIC_BLOCK_END_TAG", defaultLogicBlockEndTag);
        properties.setProperty("DEFAULT_MACRO_FILE_TAG", defaultMacroFileTag);
        // @formatter:on
        return properties;
    }
}
