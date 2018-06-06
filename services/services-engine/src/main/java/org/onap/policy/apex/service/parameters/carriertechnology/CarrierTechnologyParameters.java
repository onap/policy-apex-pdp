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

package org.onap.policy.apex.service.parameters.carriertechnology;

import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.service.parameters.ApexParameterValidator;

/**
 * The default carrier technology parameter class that may be specialized by carrier technology
 * plugins that require plugin specific parameters.
 * <p>
 * The following parameters are defined:
 * <ol>
 * <li>label: The label of the carrier technology.
 * <li>eventProducerPluginClass: The name of the plugin class that will be used by Apex to produce
 * and emit output events for this carrier technology
 * <li>eventConsumerPluginClass: The name of the plugin class that will be used by Apex to receive
 * and process input events from this carrier technology carrier technology
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class CarrierTechnologyParameters extends AbstractParameters implements ApexParameterValidator {

    // The carrier technology label
    private String label = null;

    // Producer and Consumer plugin classes for the event producer and consumer for this carrier
    // technology
    private String eventProducerPluginClass = null;
    private String eventConsumerPluginClass = null;

    /**
     * Constructor to create a carrier technology parameters instance with the name of a sub class
     * of this class and register the instance with the parameter service.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public CarrierTechnologyParameters(final String parameterClassName) {
        super(parameterClassName);
    }

    /**
     * Gets the label of the carrier technology.
     *
     * @return the label of the carrier technology
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of the carrier technology.
     *
     * @param label the label of the carrier technology
     */
    public void setLabel(final String label) {
        if (label != null) {
            this.label = label.replaceAll("\\s+", "");
        } else {
            this.label = null;
        }
    }

    /**
     * Gets the event producer plugin class.
     *
     * @return the event producer plugin class
     */
    public String getEventProducerPluginClass() {
        return eventProducerPluginClass;
    }

    /**
     * Sets the event producer plugin class.
     *
     * @param eventProducerPluginClass the new event producer plugin class
     */
    public void setEventProducerPluginClass(final String eventProducerPluginClass) {
        if (eventProducerPluginClass != null) {
            this.eventProducerPluginClass = eventProducerPluginClass.replaceAll("\\s+", "");
        } else {
            this.eventProducerPluginClass = null;
        }
    }

    /**
     * Gets the event consumer plugin class.
     *
     * @return the event consumer plugin class
     */
    public String getEventConsumerPluginClass() {
        return eventConsumerPluginClass;
    }

    /**
     * Sets the event consumer plugin class.
     *
     * @param eventConsumerPluginClass the new event consumer plugin class
     */
    public void setEventConsumerPluginClass(final String eventConsumerPluginClass) {
        if (eventConsumerPluginClass != null) {
            this.eventConsumerPluginClass = eventConsumerPluginClass.replaceAll("\\s+", "");
        } else {
            this.eventConsumerPluginClass = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CarrierTechnologyParameters [label=" + label + ", eventProducerPluginClass=" + eventProducerPluginClass
                + ", eventConsumerPluginClass=" + eventConsumerPluginClass + "]";
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
            errorMessageBuilder.append("  carrier technology label not specified or is blank\n");
        }

        if (eventProducerPluginClass == null || eventProducerPluginClass.length() == 0) {
            errorMessageBuilder.append("  carrier technology eventProducerPluginClass not specified or is blank\n");
        }

        if (eventConsumerPluginClass == null || eventConsumerPluginClass.length() == 0) {
            errorMessageBuilder.append("  carrier technology eventConsumerPluginClass not specified or is blank\n");
        }

        return errorMessageBuilder.toString();
    }
}
