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

package org.onap.policy.apex.service.parameters.eventprotocol;

import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.service.parameters.ApexParameterValidator;

/**
 * A default event protocol parameter class that may be specialized by event protocol plugins that
 * require plugin specific parameters.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>label: The label of the event protocol technology.
 * <li>eventProducerPluginClass: The name of the plugin class that will be used by Apex to produce
 * and emit output events for this carrier technology
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class EventProtocolParameters extends AbstractParameters implements ApexParameterValidator {
    // The event protocol label
    private String label = null;

    // Event protocol converter plugin class for this event protocol
    private String eventProtocolPluginClass;

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of
     * this class and register the instance with the parameter service.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public EventProtocolParameters(final String parameterClassName) {
        super(parameterClassName);
    }

    /**
     * Gets the label of the event protocol.
     *
     * @return the label of the event protocol
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of the event protocol.
     *
     * @param label the label of the event protocol
     */
    public void setLabel(final String label) {
        this.label = label.replaceAll("\\s+", "");
    }

    /**
     * Gets the event event protocol plugin class.
     *
     * @return the event event protocol plugin class
     */
    public String getEventProtocolPluginClass() {
        return eventProtocolPluginClass;
    }

    /**
     * Sets the event event protocol plugin class.
     *
     * @param eventProtocolPluginClass the event event protocol plugin class
     */
    public void setEventProtocolPluginClass(final String eventProtocolPluginClass) {
        this.eventProtocolPluginClass = eventProtocolPluginClass.replaceAll("\\s+", "");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.service.AbstractParameters#toString()
     */
    @Override
    public String toString() {
        return "CarrierTechnologyParameters [label=" + label + ", EventProtocolPluginClass=" + eventProtocolPluginClass
                + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.parameters.ApexParameterValidator#validate()
     */
    @Override
    public String validate() {
        final StringBuilder errorMessageBuilder = new StringBuilder();

        if (label == null || label.length() == 0) {
            errorMessageBuilder.append("  event protocol label not specified or is blank\n");
        }

        if (eventProtocolPluginClass == null || eventProtocolPluginClass.length() == 0) {
            errorMessageBuilder.append("  event protocol eventProtocolPluginClass not specified or is blank\n");
        }

        return errorMessageBuilder.toString();
    }
}
