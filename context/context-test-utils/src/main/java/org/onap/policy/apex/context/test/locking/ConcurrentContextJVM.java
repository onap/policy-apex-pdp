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

package org.onap.policy.apex.context.test.locking;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.apex.context.test.utils.ConfigrationProviderImpl;
import org.onap.policy.apex.context.test.utils.Constants;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.gson.Gson;

/**
 * The Class ConcurrentContextJVM tests concurrent use of context in a single JVM.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class ConcurrentContextJVM {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextJVM.class);

    private static final int IPV4_ADDRESS_LENGTH = 4;

    private final int jvmNo;

    private final ExecutorService executorService;

    private final ConfigrationProvider configrationProvider;

    private ConcurrentContextJVM(final int jvmNo, final ConfigrationProvider configrationProvider) {
        this.jvmNo = jvmNo;
        this.configrationProvider = configrationProvider;
        final String name = configrationProvider.getTestName() + ":ConcurrentContextThread_" + jvmNo;
        this.executorService = configrationProvider.getExecutorService(name, configrationProvider.getThreadCount());
    }

    public void execute() throws ApexException {
        LOGGER.debug("starting JVMs and threads . . .");

        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor" + jvmNo, "0.0.1");
        final Distributor distributor = configrationProvider.getDistributor(distributorKey);
        final ContextAlbum contextAlbum = configrationProvider.getContextAlbum(distributor);
        assert (contextAlbum != null);

        final List<Future<?>> tasks = new ArrayList<>(configrationProvider.getThreadCount());

        for (int t = 0; t < configrationProvider.getThreadCount(); t++) {
            tasks.add(executorService.submit(new ConcurrentContextThread(jvmNo, t, configrationProvider)));
        }

        try {
            executorService.shutdown();
            // wait for threads to finish, if not Timeout
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException interruptedException) {
            LOGGER.error("Exception while waiting for threads to finish", interruptedException);
            // restore the interrupt status
            Thread.currentThread().interrupt();
        }

        LOGGER.debug("threads finished, end value is {}", contextAlbum.get(Constants.TEST_VALUE));
        distributor.clear();
        LOGGER.info("Shutting down now ... ");
        executorService.shutdownNow();
    }



    /**
     * The main method.
     *
     * @param args the args
     * @throws Exception Any exception thrown by the test code
     */
    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {
        configure();

        System.out.println("JVM Arguments: " + Arrays.toString(args));
        // CHECKSTYLE:OFF: checkstyle:magicNumber

        // An even number of arguments greater than 3
        if (args.length < 9) {
            LOGGER.error("invalid arguments: " + Arrays.toString(args));
            LOGGER.error("usage: TestConcurrentContextJVM testType jvmNo threadCount threadLoops albumSize "
                    + "lockType [parameterKey parameterJson].... ");
            return;
        }


        final String testName = getStringValue("testType", args, 0);
        final int jvmNo = getIntValue("jvmNo", args, 1);
        final int threadCount = getIntValue("threadCount", args, 2);
        final int threadLoops = getIntValue("threadLoops", args, 3);
        final int albumSize = getIntValue("albumSize", args, 4);
        final int lockType = getIntValue("lockType", args, 5);
        final String hazelCastfileLocation = getStringValue("hazelcast file location", args, 6);;

        System.setProperty("hazelcast.config", hazelCastfileLocation);

        for (int p = 7; p < args.length - 1; p += 2) {
            @SuppressWarnings("rawtypes")
            final Class parametersClass = Class.forName(args[p]);
            final AbstractParameters parameters =
                    (AbstractParameters) new Gson().fromJson(args[p + 1], parametersClass);
            ParameterService.registerParameters(parametersClass, parameters);
        }

        for (final Entry<Class<?>, AbstractParameters> parameterEntry : ParameterService.getAll()) {
            LOGGER.info("Parameter class " + parameterEntry.getKey().getCanonicalName() + "="
                    + parameterEntry.getValue().toString());
        }

        try {
            final ConfigrationProvider configrationProvider =
                    new ConfigrationProviderImpl(testName, 1, threadCount, threadLoops, albumSize, lockType);
            final ConcurrentContextJVM concurrentContextJVM = new ConcurrentContextJVM(jvmNo, configrationProvider);
            concurrentContextJVM.execute();

        } catch (final Exception e) {
            LOGGER.error("error running test in JVM", e);
            return;
        }
        // CHECKSTYLE:ON: checkstyle:magicNumber

    }

    private static String getStringValue(final String key, final String[] args, final int position) {
        try {
            return args[position];
        } catch (final Exception e) {
            final String msg = "invalid argument " + key;
            LOGGER.error(msg, e);
            throw new ApexRuntimeException(msg, e);
        }
    }

    private static int getIntValue(final String key, final String[] args, final int position) {
        final String value = getStringValue(key, args, position);
        try {
            return Integer.parseInt(value);
        } catch (final Exception e) {
            final String msg = "Expects number found " + value;
            LOGGER.error(msg, e);
            throw new ApexRuntimeException(msg, e);
        }
    }


    /**
     * This method setus up any static configuration required by the JVM.
     *
     * @throws Exception on configuration errors
     */
    public static void configure() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        // The JGroups IP address must be set to a real (not loopback) IP address for Infinispan to
        // work. IN order to
        // ensure that all
        // the JVMs in a test pick up the same IP address, this function sets te address to be the
        // first non-loopback
        // IPv4 address
        // on a host
        final TreeSet<String> ipAddressSet = new TreeSet<String>();

        final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (final NetworkInterface netint : Collections.list(nets)) {
            final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (final InetAddress inetAddress : Collections.list(inetAddresses)) {
                // Look for real IPv4 Internet addresses
                if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == IPV4_ADDRESS_LENGTH) {
                    ipAddressSet.add(inetAddress.getHostAddress());
                }
            }
        }

        if (ipAddressSet.size() == 0) {
            throw new Exception("cound not find real IP address for test");
        }
        System.out.println("Setting jgroups.tcp.address to: " + ipAddressSet.first());
        System.setProperty("jgroups.tcp.address", ipAddressSet.first());
    }
}
