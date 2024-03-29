/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.ParameterRuntimeException;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * The default carrier technology parameter class that may be specialized by carrier technology plugins that require
 * plugin specific parameters.
 *
 * <p>The following parameters are defined: <ol> <li>label: The label of the carrier technology.
 * <li>eventProducerPluginClass: The name of the plugin class that will be used by Apex to produce and emit output
 * events for this carrier technology <li>eventConsumerPluginClass: The name of the plugin class that will be used by
 * Apex to receive and process input events from this carrier technology carrier technology </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@NotNull
@NotBlank
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CarrierTechnologyParameters extends ParameterGroupImpl {

    // The carrier technology label
    private String label = null;

    // Producer and Consumer plugin classes for the event producer and consumer for this carrier
    // technology
    private @ClassName String eventProducerPluginClass = null;
    private @ClassName String eventConsumerPluginClass = null;

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

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "CarrierTechnologyParameters [label=" + label + ", eventProducerPluginClass=" + eventProducerPluginClass
                        + ", eventConsumerPluginClass=" + eventConsumerPluginClass + "]";
    }

    @Override
    public String getName() {
        return this.getLabel();
    }

    @Override
    public void setName(final String name) {
        throw new ParameterRuntimeException(
                        "the name/label of this carrier technology is always \"" + getLabel() + "\"");
    }

}
