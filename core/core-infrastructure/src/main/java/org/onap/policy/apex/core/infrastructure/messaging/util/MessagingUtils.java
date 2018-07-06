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

package org.onap.policy.apex.core.infrastructure.messaging.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class MessagingUtils is a class with static methods used in IPC messaging for finding free
 * ports, translating host names to addresses, serializing objects and flushing object streams.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public final class MessagingUtils {
    // The port number of the lowest user port, ports 0-1023 are system ports
    private static final int LOWEST_USER_PORT = 1024;

    /**
     * Port number is an unsigned 16-bit integer, so maximum port is 65535
     */
    private static final int MAX_PORT_RANGE = 65535;

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(MessagingUtils.class);

    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private MessagingUtils() {}

    /**
     * This method searches the availability of the port, if the requested port not available, this
     * method will throw an exception.
     *
     * @param port the port to check
     * @return the port verified as being free
     * @throws RuntimeException on port allocation errors
     */
    public static int checkPort(final int port) {
        LOGGER.entry("Checking availability of  port {}", port);

        if (isPortAvailable(port)) {
            LOGGER.debug("Port {} is available ", port);
            return port;
        }
        LOGGER.debug("Port {} is not available", port);
        throw new RuntimeException("could not allocate requested port: " + port);
    }

    /**
     * This method searches the availability of the port, if the requested port not available,this
     * method will increment the port number and check the availability of that port, this process
     * will continue until it reaches max port range which is {@link MAX_PORT_RANGE}.
     *
     * @param port the first port to check
     * @return the port that was found
     * @throws RuntimeException on port allocation errors
     */
    public static int findPort(final int port) {
        LOGGER.entry("Checking availability of  port {}", port);

        int availablePort = port;

        while (availablePort <= MAX_PORT_RANGE) {
            if (isPortAvailable(availablePort)) {
                LOGGER.debug("Port {} is available ", availablePort);
                return availablePort;
            }
            LOGGER.debug("Port {} is not available", availablePort);
            availablePort++;
        }
        throw new RuntimeException("could not find free available");
    }

    /**
     * Check if port is available or not.
     * 
     * @param port the port to test
     * @return true if port is available
     */
    public static boolean isPortAvailable(final int port) {
        try (final Socket socket = new Socket("localhost", port)) {
            return false;
        } catch (final IOException ignoredException) {
            LOGGER.trace("Port {} is available", port, ignoredException);
            return true;
        }
    }

    /**
     * Returns the local host address.
     *
     * @return the local host address
     */
    public static InetAddress getHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * This method searches the availability of the port, if the requested port not available,this
     * method will increment the port number and check the availability, this process will continue
     * until it find port available.
     *
     * @param port the first port to check
     * @return the port that was found
     * @throws RuntimeException on port allocation errors
     */
    public static int allocateAddress(final int port) {
        if (port < LOWEST_USER_PORT) {
            throw new IllegalArgumentException("The port " + port + "  is already in use");
        }
        return MessagingUtils.findPort(port);
    }

    /**
     * Get an Internet Address for the local host.
     *
     * @return an Internet address
     * @throws UnknownHostException if the address of the local host cannot be found
     */
    public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                    .hasMoreElements();) {
                final NetworkInterface iface = ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (final Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs
                        .hasMoreElements();) {
                    final InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it
                            // immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not
                            // necessarily site-local.
                            // Store it as a candidate to be returned if
                            // site-local address is not subsequently
                            // found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign
                            // non-loopback non-site-local addresses as
                            // candidates,
                            // only the first. For subsequent iterations,
                            // candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other
                // non-loopback address.
                // Server might have a non-site-local address assigned to its
                // NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost()
            // returns...
            final InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (final Exception e) {
            final UnknownHostException unknownHostException =
                    new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    /**
     * This method serializes the message holder objects.
     *
     * @param object the object
     * @return byte[]
     */
    public static byte[] serializeObject(final Object object) {
        LOGGER.entry(object.getClass().getName());
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bytesOut);
            oos.writeObject(object);
        } catch (final IOException e) {
            LOGGER.warn("error on object serialization", e);
        } finally {
            flushAndClose(oos, bytesOut);
        }
        final byte[] bytes = bytesOut.toByteArray();
        return bytes;
    }

    /**
     * Flush and close an object stream and a byte array output stream.
     *
     * @param oos the object output stream
     * @param bytesOut the byte array output stream
     */
    private static void flushAndClose(final ObjectOutputStream oos, final ByteArrayOutputStream bytesOut) {
        try {
            if (oos != null) {
                oos.flush();
                oos.close();
            }
            if (bytesOut != null) {
                bytesOut.close();
            }

        } catch (final IOException e) {
            LOGGER.error("Failed to close the Srialization operation");
            LOGGER.catching(e);
        }
    }
}
