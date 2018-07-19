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
 * The Class IMSIStatus holds the status of an IMSI in the AADM domain.
 */
public class IMSIStatus implements Serializable {
    private static final long serialVersionUID = 2852523814242234172L;

    private static final long TIME_NOT_SET = 0;

    private final String imsi;

    private boolean anomalous = false;
    private long anomalousTime = TIME_NOT_SET;
    private String eNodeBID;
    private long blackListedTime = TIME_NOT_SET;
    private long blockingCount = 0;

    /**
     * Initiate an IMSI status instance with an IMSI value.
     *
     * @param imsi the IMSI value
     */
    public IMSIStatus(final String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the IMSI value.
     *
     * @return the IMSI value
     */
    public String getIMSI() {
        return imsi;
    }

    /**
     * Gets the anomalous flag.
     *
     * @return the anomalous flag
     */
    public boolean getAnomalous() {
        return anomalous;
    }

    /**
     * Sets the anomalous flag.
     *
     * @param anomalous the anomalous flag
     */
    public void setAnomalous(final boolean anomalous) {
        this.anomalous = anomalous;
    }

    /**
     * Gets the time of the most recent anomolous event.
     *
     * @return the time of the most recent anomolous event
     */
    public long getAnomolousTime() {
        return anomalousTime;
    }

    /**
     * Sets the time of the most recent anomolous event.
     *
     * @param incomingAnomalousTime the time of the most recent anomolous event
     */
    public void setAnomolousTime(final long incomingAnomalousTime) {
        this.anomalousTime = incomingAnomalousTime;
    }

    /**
     * Gets the eNodeB ID to which the IMSI is attached.
     *
     * @return theeNodeB ID to which the IMSI is attached
     */
    public String getENodeBID() {
        return eNodeBID;
    }

    /**
     * Sets the eNodeB ID to which the IMSI is attached.
     *
     * @param incomingENodeBID the eNodeB ID to which the IMSI is attached
     */
    public void setENodeBID(final String incomingENodeBID) {
        this.eNodeBID = incomingENodeBID;
    }

    /**
     * Checks if the eNodeB ID to which the IMSI is attached is set.
     *
     * @return true, if eNodeB ID to which the IMSI is attached is set
     */
    public boolean checkSetENodeBID() {
        return (eNodeBID != null);
    }

    /**
     * Gets the time at which the IMSI was blacklisted.
     *
     * @return the time at which the IMSI was blacklisted
     */
    public long getBlacklistedTime() {
        return blackListedTime;
    }

    /**
     * Sets the time at which the IMSI was blacklisted.
     *
     * @param incomingBlackListedTime the time at which the IMSI was blacklisted
     */
    public void setBlacklistedTime(final long incomingBlackListedTime) {
        this.blackListedTime = incomingBlackListedTime;
    }

    /**
     * Gets the number of times this IMSI was blocked.
     *
     * @return the number of times this IMSI was blocked
     */
    public long getBlockingCount() {
        return blockingCount;
    }

    /**
     * Sets the number of times this IMSI was blocked.
     *
     * @param blockingCount the number of times this IMSI was blocked
     */
    public void setBlockingCount(final long blockingCount) {
        this.blockingCount = blockingCount;
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
