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

package org.onap.policy.apex.plugins.context.locking.curator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.context.parameters.LockManagerParameters;

/**
 * Bean class for Curator locking parameters.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CuratorLockManagerParameters extends LockManagerParameters {
    // @formatter:off
    /** The default address used to connect to the Zookeeper server. */
    public static final String DEFAULT_ZOOKEEPER_ADDRESS = "localhost:2181";

    /** The default sleep time to use when connecting to the Zookeeper server. */
    public static final int DEFAULT_ZOOKEEPER_CONNECT_SLEEP_TIME = 1000;

    /** The default number of times to retry failed connections to the Zookeeper server. */
    public static final int DEFAULT_ZOOKEEPER_CONNECT_RETRIES = 3;

    // Curator parameters
    private String zookeeperAddress       = DEFAULT_ZOOKEEPER_ADDRESS;
    private int zookeeperConnectSleepTime = DEFAULT_ZOOKEEPER_CONNECT_SLEEP_TIME;
    private int zookeeperContextRetries   = DEFAULT_ZOOKEEPER_CONNECT_RETRIES;
}
