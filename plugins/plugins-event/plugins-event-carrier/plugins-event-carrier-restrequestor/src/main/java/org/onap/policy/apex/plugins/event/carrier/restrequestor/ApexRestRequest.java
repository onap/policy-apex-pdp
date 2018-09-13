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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

/**
 * This class holds a record of a REST request for the REST requestor plugin.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRestRequest {
    private long executionId;
    private String eventName;
    private Object event;
    private long timestamp;

    /**
     * Instantiates a new apex rest request.
     *
     * @param executionId the execution id
     * @param eventName the event name
     * @param event the event
     */
    public ApexRestRequest(final long executionId, final String eventName, final Object event) {
        this.executionId = executionId;
        this.eventName = eventName;
        this.event = event;
    }

    /**
     * Gets the execution id.
     *
     * @return the execution id
     */
    public long getExecutionId() {
        return executionId;
    }

    /**
     * Gets the event name.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets the event.
     *
     * @return the event
     */
    public Object getEvent() {
        return event;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ApexRestRequest [executionId=" + executionId + ", eventName=" + eventName + ", event=" + event
                + ", timestamp=" + timestamp + "]";
    }
}
