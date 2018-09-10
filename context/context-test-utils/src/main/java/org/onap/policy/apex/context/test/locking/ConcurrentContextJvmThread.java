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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;



/**
 * The Class TestConcurrentContextThread tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ConcurrentContextJvmThread implements Runnable, Closeable {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextJvmThread.class);

    private final int jvm;
    private final ConfigrationProvider configrationProvider;
    private Process process = null;

    public ConcurrentContextJvmThread(final int jvm, final ConfigrationProvider configrationProvider) {
        this.jvm = jvm;
        this.configrationProvider = configrationProvider;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final List<String> commandList = new ArrayList<>();
        commandList.add(System.getProperty("java.home") + System.getProperty("file.separator") + "bin"
                + System.getProperty("file.separator") + "java");
        commandList.add("-Xms512m");
        commandList.add("-Xmx512m");
        commandList.add("-cp");
        commandList.add(System.getProperty("java.class.path"));
        commandList.add(ConcurrentContextJvm.class.getCanonicalName());
        commandList.add(configrationProvider.getTestName());
        commandList.add(new Integer(jvm).toString());
        commandList.add(new Integer(configrationProvider.getThreadCount()).toString());
        commandList.add(new Integer(configrationProvider.getLoopSize()).toString());
        commandList.add(new Integer(configrationProvider.getAlbumSize()).toString());
        commandList.add(new Integer(configrationProvider.getLockType().getValue()).toString());
        commandList.add(System.getProperty("hazelcast.config", ""));

        for (final Entry<String, ParameterGroup> parameterServiceEntry : ParameterService.getAll()) {
            commandList.add(parameterServiceEntry.getValue().getClass().getCanonicalName());
            commandList.add(new Gson().toJson(parameterServiceEntry.getValue()));
        }

        LOGGER.info("starting JVM " + jvm);

        // Run the JVM
        final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.redirectErrorStream(true);

        try {
            process = processBuilder.start();

            final InputStream is = process.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            String line;
            LOGGER.info("JVM Output for command " + commandList + "\n");
            while ((line = br.readLine()) != null) {
                LOGGER.info(line);
            }

            // Wait to get exit value
            try {
                final int exitValue = process.waitFor();
                LOGGER.info("\n\nJVM " + jvm + " finished, exit value is " + exitValue);
            } catch (final InterruptedException e) {
                LOGGER.warn("Thread was interrupted");
                Thread.currentThread().interrupt();
            }
        } catch (final Exception ioException) {
            LOGGER.error("Error occured while writing JVM Output for command ", ioException);
        }
    }


    @Override
    public void close() {
        LOGGER.info("Shutting down {} thread ...", Thread.currentThread().getName());
        if (process != null) {
            LOGGER.info("Destroying process ...");
            process.destroy();
        }
    }
}
