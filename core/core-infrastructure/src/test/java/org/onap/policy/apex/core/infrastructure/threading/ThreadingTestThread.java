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

package org.onap.policy.apex.core.infrastructure.threading;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ThreadingTestThread.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ThreadingTestThread implements Runnable {

    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(ThreadingTestThread.class);

    private boolean interrupted = false;

    private long counter = -1;

    private Thread thread = null;

    /**
     * Sets the thread.
     *
     * @param thread the new thread
     */
    public void setThread(final Thread thread) {
        this.thread = thread;
    }

    /**
     * Gets the thread.
     *
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("starting threading test thread \"" + thread.getName() + "\" . . .");
        }

        while (!interrupted) {
            counter++;
            if (logger.isDebugEnabled()) {
                logger.debug("in threading test thread \"" + thread.getName() + "\", counter=" + counter + " . . .");
            }

            if (!ThreadUtilities.sleep(100)) {
                interrupted = true;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("stopped threading test thread \"" + thread.getName() + "\"");
        }
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return thread.getName();
    }

    /**
     * Interrupt.
     */
    public void interrupt() {
        interrupted = true;
    }

    /**
     * Gets the counter.
     *
     * @return the counter
     */
    public Long getCounter() {
        return counter;
    }
}
