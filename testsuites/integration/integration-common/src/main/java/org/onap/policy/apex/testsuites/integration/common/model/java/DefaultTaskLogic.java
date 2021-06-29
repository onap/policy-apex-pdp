/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.common.model.java;

import java.util.Random;
import org.onap.policy.apex.core.engine.executor.context.TaskExecutionContext;

/**
 * The Class DefaultTask_Logic is default task logic in Java.
 */
public class DefaultTaskLogic {
    private static final int BOUND_FOR_RANDOM_INT = 4;

    /*
     * This is not used for encryption/security, thus disabling sonar.
     */
    private static final Random rand = new Random();    // NOSONAR

    /**
     * Gets the event.
     *
     * @param executor the executor
     * @return the event
     */
    public boolean getEvent(final TaskExecutionContext executor) {
        var idString = executor.subject.getId();
        executor.logger.debug(idString);

        var albumNameString = executor.getContextAlbum("GlobalContextAlbum").getName();
        executor.logger.debug(albumNameString);

        var inFieldsString = executor.inFields.toString();
        executor.logger.debug(inFieldsString);
        if (executor.inFields.containsKey("TestDecideCaseSelected")) {
            executor.outFields.put("TestActCaseSelected", (byte) rand.nextInt(BOUND_FOR_RANDOM_INT));
            executor.outFields.put("TestActStateTime", System.nanoTime());
        } else if (executor.inFields.containsKey("TestEstablishCaseSelected")) {
            executor.outFields.put("TestDecideCaseSelected", (byte) rand.nextInt(BOUND_FOR_RANDOM_INT));
            executor.outFields.put("TestDecideStateTime", System.nanoTime());
        } else if (executor.inFields.containsKey("TestMatchCaseSelected")) {
            executor.outFields.put("TestEstablishCaseSelected", (byte) rand.nextInt(BOUND_FOR_RANDOM_INT));
            executor.outFields.put("TestEstablishStateTime", System.nanoTime());
        } else {
            executor.outFields.put("TestMatchCaseSelected", (byte) rand.nextInt(BOUND_FOR_RANDOM_INT));
            executor.outFields.put("TestMatchStateTime", System.nanoTime());
        }
        return true;
    }
}
