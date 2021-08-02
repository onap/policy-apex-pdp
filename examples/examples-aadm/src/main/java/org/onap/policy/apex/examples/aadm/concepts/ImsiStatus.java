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
 * The Class IMSIStatus holds the status of an IMSI in the AADM domain.
 */
@Getter
@Setter
public class ImsiStatus implements Serializable {
    private static final long serialVersionUID = 2852523814242234172L;

    private static final long TIME_NOT_SET = 0;

    private final String imsi;

    private boolean anomalous = false;
    private long anomalousTime = TIME_NOT_SET;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String enodeBId;
    private long blacklistedTime = TIME_NOT_SET;
    private long blockingCount = 0;

    /**
     * Initiate an IMSI status instance with an IMSI value.
     *
     * @param imsi the IMSI value
     */
    public ImsiStatus(final String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the eNodeB ID to which the IMSI is attached.
     *
     * @return theeNodeB ID to which the IMSI is attached
     */
    public String getENodeBId() {
        return enodeBId;
    }

    /**
     * Sets the eNodeB ID to which the IMSI is attached.
     *
     * @param incomingENodeBId the eNodeB ID to which the IMSI is attached
     */
    public void setENodeBId(final String incomingENodeBId) {
        this.enodeBId = incomingENodeBId;
    }

    /**
     * Checks if the eNodeB ID to which the IMSI is attached is set.
     *
     * @return true, if eNodeB ID to which the IMSI is attached is set
     */
    public boolean checkSetENodeBId() {
        return (enodeBId != null);
    }

    /**
     * Increment the number of times this IMSI was blocked.
     *
     * @return the incremented number of times this IMSI was blocked
     */
    public long incrementBlockingCount() {
        return ++blockingCount;
    }

    /**
     * Decrement the number of times this IMSI was blocked.
     *
     * @return the decremented number of times this IMSI was blocked
     */
    public long decrementBlockingCount() {
        return --blockingCount;
    }
}
