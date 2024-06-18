/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (c) 2021, 2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.adaptive.model.java;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.context.TaskSelectionExecutionContext;
import org.onap.policy.apex.examples.adaptive.concepts.AutoLearn;

/**
 * The Class AutoLearnPolicyDecideTaskSelectionLogic.
 */
public class AutoLearnPolicyDecideTaskSelectionLogic {
    // Recurring string constants
    private static final String AUTO_LEARN_ALBUM = "AutoLearnAlbum";
    private static final String AUTO_LEARN = "AutoLearn";

    /*
     * This is not used for encryption/security, thus disabling sonar.
     */
    private static final Random RAND = new Random(System.currentTimeMillis());  // NOSONAR

    private static final double WANT = 50.0;
    private int size;

    /**
     * Gets the task.
     *
     * @param executor the executor
     * @return the task
     */
    public boolean getTask(final TaskSelectionExecutionContext executor) {
        var returnValue = true;
        var idString = executor.subject.getId();
        TaskSelectionExecutionContext.logger.debug(idString);

        var inFieldsString = executor.inFields.toString();
        TaskSelectionExecutionContext.logger.debug(inFieldsString);

        final List<String> tasks = executor.subject.getTaskNames();
        size = tasks.size();

        try {
            executor.getContextAlbum(AUTO_LEARN_ALBUM).lockForWriting(AUTO_LEARN);
        } catch (final ContextException e) {
            TaskSelectionExecutionContext.logger.error("Failed to acquire write lock on \"autoLearn\" context", e);
            returnValue = false;
        }

        // Get the context object
        var autoLearn = (AutoLearn) executor.getContextAlbum(AUTO_LEARN_ALBUM).get(AUTO_LEARN);
        if (autoLearn == null) {
            autoLearn = new AutoLearn();
        }

        // Check the lists are initialized
        if (!autoLearn.isInitialized()) {
            autoLearn.init(size);
        }

        final double now = (Double) (executor.inFields.get("MonitoredValue"));
        final double diff = now - WANT;
        final int option = getOption(diff, autoLearn);
        learn(option, diff, autoLearn);

        executor.getContextAlbum(AUTO_LEARN_ALBUM).put(AUTO_LEARN_ALBUM, autoLearn);

        try {
            executor.getContextAlbum(AUTO_LEARN_ALBUM).unlockForWriting(AUTO_LEARN);
        } catch (final ContextException e) {
            TaskSelectionExecutionContext.logger.error("Failed to acquire write lock on \"autoLearn\" context", e);
            returnValue = false;
        }

        executor.subject.getTaskKey(tasks.get(option)).copyTo(executor.selectedTask);
        return returnValue;
    }

    /**
     * Gets the option.
     *
     * @param diff      the diff
     * @param autoLearn the auto learn
     * @return the option
     */
    private int getOption(final double diff, final AutoLearn autoLearn) {
        final Double[] avdiffs = autoLearn.getAvDiffs().toArray(new Double[0]);
        final var r = RAND.nextInt(size);
        int closestupi = -1;
        int closestdowni = -1;
        double closestup = Double.MAX_VALUE;
        double closestdown = Double.MIN_VALUE;
        for (var i = 0; i < size; i++) {
            if (Double.isNaN(avdiffs[i])) {
                return r;
            }
            if (avdiffs[i] >= diff && avdiffs[i] <= closestup) {
                closestup = avdiffs[i];
                closestupi = i;
            }
            if (avdiffs[i] <= diff && avdiffs[i] >= closestdown) {
                closestdown = avdiffs[i];
                closestdowni = i;
            }
        }
        return calculateReturnValue(diff, r, closestupi, closestdowni, closestup, closestdown);
    }

    /**
     * Learn.
     *
     * @param option    the option
     * @param diff      the diff
     * @param autoLearn the auto learn
     */
    private void learn(final int option, final double diff, final AutoLearn autoLearn) {
        final Double[] avdiffs = autoLearn.getAvDiffs().toArray(new Double[0]);
        final Long[] counts = autoLearn.getCounts().toArray(new Long[0]);
        if (option < 0 || option >= avdiffs.length) {
            throw new IllegalArgumentException("Error: option" + option);
        }
        counts[option]++;
        if (Double.isNaN(avdiffs[option])) {
            avdiffs[option] = diff;
        } else {
            avdiffs[option] = (avdiffs[option] * (counts[option] - 1) + diff) / counts[option];
        }
        autoLearn.setAvDiffs(Arrays.asList(avdiffs));
        autoLearn.setCounts(Arrays.asList(counts));
    }

    /**
     * Calculate the return value of the learning.
     *
     * @param diff         the difference
     * @param random       the random value
     * @param closestupi   closest to i upwards
     * @param closestdowni closest to i downwards
     * @param closestup    closest up value
     * @param closestdown  closest down value
     * @return the return value
     */
    private int calculateReturnValue(final double diff, final int random, int closestupi, int closestdowni,
                                     double closestup, double closestdown) {
        if (closestupi == -1 || closestdowni == -1) {
            return random;
        }
        if (closestupi == closestdowni) {
            return closestupi;
        }
        if (Math.abs(closestdown - diff) > Math.abs(closestup - diff)) {
            return closestupi;
        } else {
            return closestdowni;
        }
    }
}
