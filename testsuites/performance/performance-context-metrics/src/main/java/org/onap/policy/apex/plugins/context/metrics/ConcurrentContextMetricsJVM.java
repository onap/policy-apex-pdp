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

package org.onap.policy.apex.plugins.context.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The Class ConcurrentContextMetricsJVM rins in its own JVM to test concurrent context updates and
 * lockings across JVMs.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class ConcurrentContextMetricsJVM {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextMetricsJVM.class);

    private static final int NUM_ARGS = 6;
    private static final int ARG_JVM_NO = 1;
    private static final int ARG_THREAD_COUNT = 2;
    private static final int ARG_ITERATIONS = 3;
    private static final int ARG_ARRAY_SIZE = 4;
    private static final int ARG_LOCK_TYPE = 5;

    private static final int WAIT_10_MS = 10;

    /**
     * The Constructor for this class.
     *
     * @param testType the test type
     * @param jvmNo the jvm no
     * @param threadCount the thread count
     * @param threadLoops the thread loops
     * @param longArraySize the long array size
     * @param lockType the lock type
     * @throws ApexException the apex exception
     * @throws IOException the IO exception
     */
    private ConcurrentContextMetricsJVM(final String testType, final int jvmNo, final int threadCount,
            final int threadLoops, final int longArraySize, final int lockType) throws ApexException, IOException {
        LOGGER.debug("starting JVMs and threads . . .");

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        // Set up the distributor for this JVM
        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1");
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        final AxArtifactKey[] usedArtifactStackArray = {new AxArtifactKey("testC-top_" + jvmNo, "0.0.1"),
                new AxArtifactKey("testC-next_" + jvmNo, "0.0.1"), new AxArtifactKey("testC-bot_" + jvmNo, "0.0.1")};

        final AxContextModel testAxContextModel = TestContextAlbumFactory.createLongContextModel();
        contextDistributor.registerModel(testAxContextModel);
        final ContextAlbum testContextAlbum =
                contextDistributor.createContextAlbum(new AxArtifactKey("LongSameTypeContextAlbum", "0.0.1"));
        Assertions.argumentNotNull(testContextAlbum, "testContextAlbum may not be null");
        testContextAlbum.setUserArtifactStack(usedArtifactStackArray);

        final Thread[] threadArray = new Thread[threadCount];

        for (int t = 0; t < threadCount; t++) {
            threadArray[t] =
                    new Thread(new ConcurrentContextMetricsThread(jvmNo, t, threadLoops, longArraySize, lockType));
            threadArray[t].setName(testType + ":ConcurrentContextMetricsThread_" + jvmNo + "_" + t);
            threadArray[t].start();
            LOGGER.debug("started thread " + threadArray[t].getName());
        }

        System.out.println("ReadyToGo");
        while (true) {
            final String goLine = bufferedReader.readLine();
            if (!goLine.trim().equals("OffYouGo")) {
                throw new IOException("Expected OffYouGo");
            }
            break;
        }

        boolean allFinished;
        do {
            allFinished = true;
            for (int t = 0; t < threadCount; t++) {
                if (threadArray[t].isAlive()) {
                    allFinished = false;
                    ThreadUtilities.sleep(WAIT_10_MS);
                    break;
                }
            }
        } while (!allFinished);

        System.out.println("AllFinished");
        while (true) {
            final String goLine = bufferedReader.readLine();
            if (!goLine.trim().equals("FinishItOut")) {
                throw new IOException("Expected FinishItOut");
            }
            break;
        }

        LOGGER.debug("threads finished");
        contextDistributor.clear();
    }

    /**
     * The main method.
     *
     * @param args the args
     * @throws JsonSyntaxException the json syntax exception
     * @throws ClassNotFoundException the class not found exception
     */
    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws JsonSyntaxException, ClassNotFoundException {
        if (args.length < NUM_ARGS || (args.length % 2 != 0)) {
            LOGGER.error("invalid arguments: " + Arrays.toString(args));
            LOGGER.error(
                    "usage: ConcurrentContextMetricsJVM testLabel jvmNo threadCount threadLoops longArraySize lockType [parameterKey parameterJson].... ");
            return;
        }

        int jvmNo = -1;
        int threadCount = -1;
        int threadLoops = -1;
        int longArraySize = -1;
        int lockType = -1;

        try {
            jvmNo = Integer.parseInt(args[ARG_JVM_NO]);
        } catch (final Exception e) {
            LOGGER.error("invalid argument jvmNo", e);
            return;
        }

        try {
            threadCount = Integer.parseInt(args[ARG_THREAD_COUNT]);
        } catch (final Exception e) {
            LOGGER.error("invalid argument threadCount", e);
            return;
        }

        try {
            threadLoops = Integer.parseInt(args[ARG_ITERATIONS]);
        } catch (final Exception e) {
            LOGGER.error("invalid argument threadLoops", e);
            return;
        }

        try {
            longArraySize = Integer.parseInt(args[ARG_ARRAY_SIZE]);
        } catch (final Exception e) {
            LOGGER.error("invalid argument longArraySize", e);
            return;
        }

        try {
            lockType = Integer.parseInt(args[ARG_LOCK_TYPE]);
        } catch (final Exception e) {
            LOGGER.error("invalid argument lockType", e);
            return;
        }

        for (int p = NUM_ARGS; p < args.length - 1; p += 2) {
            @SuppressWarnings("rawtypes")
            final Class parametersClass = Class.forName(args[p]);
            final AbstractParameters parameters =
                    (AbstractParameters) new Gson().fromJson(args[p + 1], parametersClass);
            ParameterService.registerParameters(parametersClass, parameters);
        }

        try {
            new ConcurrentContextMetricsJVM(args[0], jvmNo, threadCount, threadLoops, longArraySize, lockType);
        } catch (final Exception e) {
            LOGGER.error("error running test in JVM", e);
            return;
        }
    }
}
