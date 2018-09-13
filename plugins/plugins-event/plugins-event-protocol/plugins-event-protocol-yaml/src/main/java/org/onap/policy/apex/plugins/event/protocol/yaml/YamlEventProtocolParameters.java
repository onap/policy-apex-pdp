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

package org.onap.policy.apex.plugins.event.protocol.yaml;

import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextTokenDelimitedParameters;

/**
 * Event protocol parameters for YAML as an event protocol.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>nameAlias: The field in a YAML event to use as an alias for the event name. This parameter is
 * optional.
 * <li>versionAlias: The field in a YAML event to use as an alias for the event version. This
 * parameter is optional.
 * <li>nameSpaceAlias: The field in a YAML event to use as an alias for the event name space. This
 * parameter is optional.
 * <li>sourceAlias: The field in a YAML event to use as an alias for the event source. This
 * parameter is optional.
 * <li>targetAlias: The field in a YAML event to use as an alias for the event target. This
 * parameter is optional.
 * <li>yamlFieldName: The name of the field in the APEX event that will contain the unmarshaled YAML object. The
 * parameter is optional and defaults to the value "yaml_field".
 * </ol>
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class YamlEventProtocolParameters extends EventProtocolTextTokenDelimitedParameters {
    /** The label of this event protocol. */
    public static final String YAML_EVENT_PROTOCOL_LABEL = "YAML";

    // Constants for text block delimiters
    private static final String YAML_START_TEXT_DELIMITER_TOKEN = "---";
    private static final String YAML_END_TEXT_DELIMITER_TOKEN = "...";

    // Default parameter values
    private static final String DEFAULT_YAML_FIELD_NAME = "yaml_field";

    // Aliases for Apex event header fields
    // @formatter:off
    private String nameAlias      = null;
    private String versionAlias   = null;
    private String nameSpaceAlias = null;
    private String sourceAlias    = null;
    private String targetAlias    = null;
    private String yamlFieldName  = DEFAULT_YAML_FIELD_NAME;
    // @formatter:on

    /**
     * Constructor to create a YAML event protocol parameter instance and register the instance with
     * the parameter service.
     */
    public YamlEventProtocolParameters() {
        this(YamlEventProtocolParameters.class.getCanonicalName(), YAML_EVENT_PROTOCOL_LABEL);
    }

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of
     * this class.
     *
     * @param parameterClassName the class name of a sub class of this class
     * @param eventProtocolLabel the name of the event protocol for this plugin
     */
    public YamlEventProtocolParameters(final String parameterClassName, final String eventProtocolLabel) {
        super(parameterClassName);

        // Set the event protocol properties for the YAML event protocol
        this.setLabel(eventProtocolLabel);

        // Set the delimiter token for text blocks of YAML events
        this.setStartDelimiterToken(YAML_START_TEXT_DELIMITER_TOKEN);
        this.setEndDelimiterToken(YAML_END_TEXT_DELIMITER_TOKEN);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(Apex2YamlEventConverter.class.getCanonicalName());
    }

    /**
     * Gets the name alias.
     *
     * @return the name alias
     */
    public String getNameAlias() {
        return nameAlias;
    }

    /**
     * Gets the version alias.
     *
     * @return the version alias
     */
    public String getVersionAlias() {
        return versionAlias;
    }

    /**
     * Gets the name space alias.
     *
     * @return the name space alias
     */
    public String getNameSpaceAlias() {
        return nameSpaceAlias;
    }

    /**
     * Gets the source alias.
     *
     * @return the source alias
     */
    public String getSourceAlias() {
        return sourceAlias;
    }

    /**
     * Gets the target alias.
     *
     * @return the target alias
     */
    public String getTargetAlias() {
        return targetAlias;
    }
    
    /**
     * Gets the YAML field name.
     *
     * @return the YAML field name
     */
    public String getYamlFieldName() {
        return yamlFieldName;
    }

    /**
     * Sets the name alias.
     *
     * @param nameAlias the new name alias
     */
    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    /**
     * Sets the version alias.
     *
     * @param versionAlias the new version alias
     */
    public void setVersionAlias(String versionAlias) {
        this.versionAlias = versionAlias;
    }

    /**
     * Sets the name space alias.
     *
     * @param nameSpaceAlias the new name space alias
     */
    public void setNameSpaceAlias(String nameSpaceAlias) {
        this.nameSpaceAlias = nameSpaceAlias;
    }

    /**
     * Sets the source alias.
     *
     * @param sourceAlias the new source alias
     */
    public void setSourceAlias(String sourceAlias) {
        this.sourceAlias = sourceAlias;
    }

    /**
     * Sets the target alias.
     *
     * @param targetAlias the new target alias
     */
    public void setTargetAlias(String targetAlias) {
        this.targetAlias = targetAlias;
    }

    /**
     * Sets the encapsulating object name.
     *
     * @param yamlFieldName
     *        the new YAML field name
     */
    public void setYamlFieldName(String yamlFieldName) {
        this.yamlFieldName = yamlFieldName;
    }
}
