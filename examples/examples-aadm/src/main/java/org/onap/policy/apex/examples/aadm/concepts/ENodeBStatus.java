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

package org.onap.policy.apex.examples.aadm.concepts;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class ENodeBStatus holds the status of an eNodeB in the AADM domain.
 */
@Getter
@Setter
public class ENodeBStatus implements Serializable {
    private static final long serialVersionUID = 2852523814242234172L;

    @Getter(AccessLevel.NONE)
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
}
