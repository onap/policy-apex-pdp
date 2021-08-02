/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class holds a record of a REST request for the REST requestor plugin.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@ToString
public class ApexRestRequest {
    private long executionId;
    private String eventName;
    private Object event;
    @ToString.Exclude
    private Properties executionProperties;
    @Setter
    private long timestamp;

    /**
     * Instantiates a new apex rest request.
     *
     * @param executionId the execution id
     * @param eventName the event name
     * @param event the event
     */
    public ApexRestRequest(final long executionId, final Properties executionProperties,
            final String eventName, final Object event) {
        this.executionId = executionId;
        this.executionProperties = executionProperties;
        this.eventName = eventName;
        this.event = event;
    }
}
