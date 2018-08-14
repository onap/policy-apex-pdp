/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.test.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TreeSet;

public class NetworkUtils {

    private NetworkUtils() {}

    /**
     * The JGroups IP address must be set to a real (not loopback) IP address for Infinispan to
     * work. In order to ensure that all the JVMs in a test pick up the same IP address, this
     * function sets the address to be the first non-loopback IPv4 address on a host
     * 
     * @return Set of IPv4 addresses
     * @throws SocketException throw socket exception if error occurs
     */
    public static TreeSet<String> getIPv4NonLoopAddresses() throws SocketException {
        final TreeSet<String> ipAddressSet = new TreeSet<String>();

        final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (final NetworkInterface netint : Collections.list(nets)) {
            final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (final InetAddress inetAddress : Collections.list(inetAddresses)) {
                // Look for real IPv4 internet addresses
                if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                    ipAddressSet.add(inetAddress.getHostAddress());
                }
            }
        }
        return ipAddressSet;
    }


}
