/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

/**
 * This class contains the definitions of Apex model properties.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
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
        final Properties properties = new Properties();
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

    /**
     * Gets the default concept version.
     *
     * @return the default concept version
     */
    public String getDefaultConceptVersion() {
        return defaultConceptVersion;
    }

    /**
     * Sets the default concept version.
     *
     * @param defaultConceptVersion the default concept version
     */
    public void setDefaultConceptVersion(final String defaultConceptVersion) {
        this.defaultConceptVersion = defaultConceptVersion;
    }

    /**
     * Gets the default event namespace.
     *
     * @return the default event namespace
     */
    public String getDefaultEventNamespace() {
        return defaultEventNamespace;
    }

    /**
     * Sets the default event namespace.
     *
     * @param defaultEventNamespace the default event namespace
     */
    public void setDefaultEventNamespace(final String defaultEventNamespace) {
        this.defaultEventNamespace = defaultEventNamespace;
    }

    /**
     * Gets the default event source.
     *
     * @return the default event source
     */
    public String getDefaultEventSource() {
        return defaultEventSource;
    }

    /**
     * Sets the default event source.
     *
     * @param defaultEventSource the default event source
     */
    public void setDefaultEventSource(final String defaultEventSource) {
        this.defaultEventSource = defaultEventSource;
    }

    /**
     * Gets the default event target.
     *
     * @return the default event target
     */
    public String getDefaultEventTarget() {
        return defaultEventTarget;
    }

    /**
     * Sets the default event target.
     *
     * @param defaultEventTarget the default event target
     */
    public void setDefaultEventTarget(final String defaultEventTarget) {
        this.defaultEventTarget = defaultEventTarget;
    }

    /**
     * Gets the default logic block start tag.
     *
     * @return the default logic block start tag
     */
    public String getDefaultLogicBlockStartTag() {
        return defaultLogicBlockStartTag;
    }

    /**
     * Gets the default logic block end tag.
     *
     * @return the default logic block end tag
     */
    public String getDefaultLogicBlockEndTag() {
        return defaultLogicBlockEndTag;
    }

    /**
     * Gets the default policy template type.
     *
     * @return the default policy template
     */
    public String getDefaultPolicyTemplate() {
        return defaultPolicyTemplate;
    }

    /**
     * Sets the default policy template type.
     *
     * @param defaultPolicyTemplate the new default policy template
     */
    public void setDefaultPolicyTemplate(final String defaultPolicyTemplate) {
        this.defaultPolicyTemplate = defaultPolicyTemplate;
    }

    /**
     * Gets the default macro file tag.
     *
     * @return the default macro file end tag
     */
    public String getDefaultMacroFileTag() {
        return defaultMacroFileTag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ApexModelProperties [defaultConceptVersion=" + defaultConceptVersion + ", defaultEventNamespace="
                + defaultEventNamespace + ", defaultEventSource=" + defaultEventSource + ", defaultEventTarget="
                + defaultEventTarget + ", defaultLogicBlockStartTag=" + defaultLogicBlockStartTag
                + ", defaultLogicBlockEndTag=" + defaultLogicBlockEndTag + ", defaultPolicyTemplate="
                + defaultPolicyTemplate + ", defaultMacroFileTag=" + defaultMacroFileTag + "]";
    }
}
