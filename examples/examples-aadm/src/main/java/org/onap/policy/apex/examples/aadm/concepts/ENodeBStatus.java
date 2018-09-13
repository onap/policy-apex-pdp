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

package org.onap.policy.apex.examples.aadm.concepts;

import java.io.Serializable;

/**
 * The Class ENodeBStatus holds the status of an eNodeB in the AADM domain.
 */
public class ENodeBStatus implements Serializable {
    private static final long serialVersionUID = 2852523814242234172L;

    private final String enodeB;

    private long dosCount = 0;
    private boolean beingProbed = false;

    /**
     * The Constructor initiates the status of the eNodeB.
     *
     * @param enodeB the eNodeB
     */
    public ENodeBStatus(final String enodeB) {
        this.enodeB = enodeB;
    }

    /**
     * Gets the eNodeB name.
     *
     * @return the eNodeB name
     */
    public String getENodeB() {
        return enodeB;
    }

    /**
     * Gets the number of Denial Of Service incidents on the eNodeB.
     *
     * @return the number of Denial Of Service incidents on the eNodeB
     */
    public long getDosCount() {
        return dosCount;
    }

    /**
     * Sets the number of Denial Of Service incidents on the eNodeB.
     *
     * @param incomingDosCount the number of Denial Of Service incidents on the eNodeB
     */
    public void setDosCount(final long incomingDosCount) {
        this.dosCount = incomingDosCount;
    }

    /**
     * Increment DOS count.
     *
     * @return the long
     */
    public long incrementDosCount() {
        return ++dosCount;
    }

    /**
     * Decrement DOS count.
     *
     * @return the long
     */
    public long decrementDosCount() {
        return --dosCount;
    }

    /**
     * Gets the being probed.
     *
     * @return the being probed
     */
    public boolean getBeingProbed() {
        return beingProbed;
    }

    /**
     * Sets the being probed.
     *
     * @param beingProbed the being probed
     */
    public void setBeingProbed(final boolean beingProbed) {
        this.beingProbed = beingProbed;
    }
}
