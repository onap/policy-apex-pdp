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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JSONEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextTokenDelimitedParameters;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SuperTokenDelimitedEventProtocolParameters extends EventProtocolTextTokenDelimitedParameters {
    /** The label of this carrier technology. */
    public static final String SUPER_TOKEN_EVENT_PROTOCOL_LABEL = "SUPER_TOK_DEL";

    // Constants for text delimiter tokeb
    private static final String SUPER_TOKEN_DELIMITER = "SuperToken";

    /**
     * Constructor to create a JSON event protocol parameter instance and register the instance with
     * the parameter service.
     */
    public SuperTokenDelimitedEventProtocolParameters() {
        super(JSONEventProtocolParameters.class.getCanonicalName());
        ParameterService.registerParameters(SuperTokenDelimitedEventProtocolParameters.class, this);

        // Set the event protocol properties for the JSON carrier technology
        this.setLabel(SUPER_TOKEN_EVENT_PROTOCOL_LABEL);

        // Set the starting and ending delimiters for text blocks of JSON events
        this.setDelimiterToken(SUPER_TOKEN_DELIMITER);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(SuperTokenDelimitedEventConverter.class.getCanonicalName());
    }
}
