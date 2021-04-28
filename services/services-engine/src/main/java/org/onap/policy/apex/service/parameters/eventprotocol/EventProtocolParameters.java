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

import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.ParameterRuntimeException;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * A default event protocol parameter class that may be specialized by event protocol plugins that require plugin
 * specific parameters.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>label: The label of the event protocol technology.
 * <li>eventProducerPluginClass: The name of the plugin class that will be used by Apex to produce and emit output
 * events for this carrier technology
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NotNull
@NotBlank
public abstract class EventProtocolParameters extends ParameterGroupImpl {
    // The event protocol label
    private String label = null;

    // Event protocol converter plugin class for this event protocol
    private @ClassName String eventProtocolPluginClass;

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of this class and
     * register the instance with the parameter service.
     */
    protected EventProtocolParameters() {
        super();
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "CarrierTechnologyParameters [label=" + label + ", EventProtocolPluginClass=" + eventProtocolPluginClass
                        + "]";
    }

    @Override
    public String getName() {
        return this.getLabel();
    }

    @Override
    public void setName(final String name) {
        throw new ParameterRuntimeException("the name/label of this event protocol is always \"" + getLabel() + "\"");
    }
}
