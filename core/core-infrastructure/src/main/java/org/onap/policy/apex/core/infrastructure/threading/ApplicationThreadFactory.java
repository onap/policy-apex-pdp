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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides a thread factory for use by classes that require thread factories to handle concurrent operation.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public class ApplicationThreadFactory implements ThreadFactory {
    private static final String HYPHEN = "-";
    private static final String APPLICATION_NAME = "Apex-";
    private static final AtomicInteger NEXT_POOL_NUMBER = new AtomicInteger();
    private final ThreadGroup group;
    private final AtomicInteger nextThreadNumber = new AtomicInteger();
    private final String name;
    private final long stackSize;
    private final int threadPriority;

    /**
     * Instantiates a new application thread factory with a default stack size and normal thread priority.
     *
     * @param nameLocal the name local
     */
    public ApplicationThreadFactory(final String nameLocal) {
        this(nameLocal, 0);
    }

    /**
     * Instantiates a new application thread factory with a default normal thread priority.
     *
     * @param nameLocal the name local
     * @param stackSize the stack size
     */
    public ApplicationThreadFactory(final String nameLocal, final long stackSize) {
        this(nameLocal, stackSize, Thread.NORM_PRIORITY);
    }

    /**
     * Instantiates a new application thread factory with a specified thread priority.
     *
     * @param nameLocal the name local
     * @param stackSize the stack size
     * @param threadPriority the thread priority
     */
    public ApplicationThreadFactory(final String nameLocal, final long stackSize, final int threadPriority) {
        final SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        name = APPLICATION_NAME + nameLocal + HYPHEN + NEXT_POOL_NUMBER.getAndIncrement();
        this.stackSize = stackSize;
        this.threadPriority = threadPriority;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thisThread;
        if (stackSize > 0) {
            thisThread = new Thread(group, runnable, name + ':' + nextThreadNumber.getAndIncrement(), stackSize);
        } else {
            thisThread = new Thread(group, runnable, name + ':' + nextThreadNumber.getAndIncrement());
        }
        if (thisThread.isDaemon()) {
            thisThread.setDaemon(false);
        }
        thisThread.setPriority(threadPriority);

        return thisThread;
    }

    /**
     * Stop group threads.
     */
    public void stopGroupThreads() {
        group.interrupt();
        group.list();

    }

    /**
     * Gets the name of the thread factory.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the stack size of the threads created by this thread factory.
     *
     * @return the stack size
     */
    public long getStackSize() {
        return stackSize;
    }

    /**
     * Gets the thread priority of the threads created by this thread factory.
     *
     * @return the thread priority
     */
    public int getThreadPriority() {
        return threadPriority;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ApplicationThreadFactory [nextPollNumber=" + NEXT_POOL_NUMBER + ",nextThreadNumber=" + nextThreadNumber
                + ", name=" + name + ", stackSize=" + stackSize + ", threadPriority=" + threadPriority + "]";
    }
}
