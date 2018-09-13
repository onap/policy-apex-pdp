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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.jms;

import java.io.Serializable;

import org.onap.policy.apex.service.engine.event.ApexEventException;

/**
 * The Class TestPing.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestPing implements Serializable {
    private static final long serialVersionUID = -3400711508992955886L;

    private String name = "Rose";
    private String description = "A rose by any other name would smell as sweet";
    private long pingTime = System.currentTimeMillis();
    private long pongTime = -1;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the ping time.
     *
     * @return the ping time
     */
    public long getPingTime() {
        return pingTime;
    }

    /**
     * Sets the ping time.
     *
     * @param pingTime the new ping time
     */
    public void setPingTime(final long pingTime) {
        this.pingTime = pingTime;
    }

    /**
     * Gets the pong time.
     *
     * @return the pong time
     */
    public long getPongTime() {
        return pongTime;
    }

    /**
     * Sets the pong time.
     *
     * @param pongTime the new pong time
     */
    public void setPongTime(final long pongTime) {
        this.pongTime = pongTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TestPing [name=" + name + ", description=" + description + ", pingTime=" + pingTime + ", pongTime="
                + pongTime + "]";
    }

    /**
     * Verify.
     *
     * @throws ApexEventException the apex event exception
     */
    public void verify() throws ApexEventException {
        if (!name.startsWith("Rose")) {
            throw new ApexEventException("TestPing is not valid");
        }

        if (name.length() <= 4) {
            throw new ApexEventException("TestPing is not valid");
        }

        if (!description.startsWith("A rose by any other name would smell as sweet")) {
            throw new ApexEventException("TestPing is not valid");
        }

        if (description.length() <= 44) {
            throw new ApexEventException("TestPing is not valid");
        }

        if (pongTime <= pingTime) {
            throw new ApexEventException("TestPing is not valid");
        }
    }
}
