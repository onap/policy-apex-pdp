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

package org.onap.policy.apex.test.common.model.java;

import java.util.Date;
import java.util.Random;

import org.onap.policy.apex.core.engine.executor.context.TaskExecutionContext;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * The Class DefaultTask_Logic is default task logic in Java.
 */
//CHECKSTYLE:OFF: checkstyle:typeNames
public class DefaultTask_Logic {
// CHECKSTYLE:ON: checkstyle:typeNames
    private static final int BOUND_FOR_RANDOM_INT = 4;

   /**
     * Gets the event.
     *
     * @param executor the executor
     * @return the event
     * @throws ApexException the apex exception
     */
    public boolean getEvent(final TaskExecutionContext executor) throws ApexException {
        executor.logger.debug(executor.subject.getId());
        executor.logger.debug(executor.getContextAlbum("GlobalContextAlbum").getName());
        executor.logger.debug(executor.inFields.toString());

        final Date timeNow = new Date();
        final Random rand = new Random();

        if (executor.inFields.containsKey("TestDecideCaseSelected")) {
            executor.outFields.put("TestActCaseSelected", new Byte((byte) rand.nextInt(BOUND_FOR_RANDOM_INT)));
            executor.outFields.put("TestActStateTime", timeNow.getTime());
        }
        else if (executor.inFields.containsKey("TestEstablishCaseSelected")) {
            executor.outFields.put("TestDecideCaseSelected", new Byte((byte) rand.nextInt(BOUND_FOR_RANDOM_INT)));
            executor.outFields.put("TestDecideStateTime", timeNow.getTime());
        }
        else if (executor.inFields.containsKey("TestMatchCaseSelected")) {
            executor.outFields.put("TestEstablishCaseSelected", new Byte((byte) rand.nextInt(BOUND_FOR_RANDOM_INT)));
            executor.outFields.put("TestEstablishStateTime", timeNow.getTime());
        }
        else {
            executor.outFields.put("TestMatchCaseSelected", new Byte((byte) rand.nextInt(BOUND_FOR_RANDOM_INT)));
            executor.outFields.put("TestMatchStateTime", timeNow.getTime());
        }

        return true;
    }
}
