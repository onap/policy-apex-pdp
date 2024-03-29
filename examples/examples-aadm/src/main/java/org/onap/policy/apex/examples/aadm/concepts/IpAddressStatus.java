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
import lombok.Getter;
import lombok.Setter;

/**
 * The Class IPAddressStatus holds the status of an IP address in the AADM domain.
 */
@Getter
@Setter
public class IpAddressStatus implements Serializable {
    private static final long serialVersionUID = -7402022458317593252L;

    private final String ipAddress;

    private String imsi;

    /**
     * The Constructor sets up the IP address status instance.
     *
     * @param ipAddress the ip address
     */
    public IpAddressStatus(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Check set IMSI.
     *
     * @return true, if check set IMSI
     */
    public boolean checkSetImsi() {
        return (imsi != null);
    }
}
