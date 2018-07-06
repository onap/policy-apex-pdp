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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class IntegrationThreadFactory implements ThreadFactory {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(IntegrationThreadFactory.class);


    private final String threadFactoryName;

    private final AtomicInteger counter = new AtomicInteger();

    public IntegrationThreadFactory(final String threadFactoryName) {
        this.threadFactoryName = threadFactoryName;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable);
        thread.setName(threadFactoryName + "_" + counter.getAndIncrement());
        LOGGER.debug("started thread " + thread.getName());
        return thread;
    }

}
