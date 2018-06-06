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

package org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin;

import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

/**
 * Event protocol parameters for JSON as an event protocol, there are no user defined parameters.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEventProtocolParameters extends EventProtocolParameters {
    /** The label of this event protocol. */
    public static final String APEX_EVENT_PROTOCOL_LABEL = "APEX";

    /**
     * Constructor to create a JSON event protocol parameter instance and register the instance with
     * the parameter service.
     */
    public ApexEventProtocolParameters() {
        this(ApexEventProtocolParameters.class.getCanonicalName(), APEX_EVENT_PROTOCOL_LABEL);
    }

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of
     * this class.
     *
     * @param parameterClassName the class name of a sub class of this class
     * @param eventProtocolLabel the name of the event protocol for this plugin
     */
    public ApexEventProtocolParameters(final String parameterClassName, final String eventProtocolLabel) {
        super(parameterClassName);

        // Set the event protocol properties for the JSON event protocol
        this.setLabel(eventProtocolLabel);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(Apex2ApexEventConverter.class.getCanonicalName());
    }
}
