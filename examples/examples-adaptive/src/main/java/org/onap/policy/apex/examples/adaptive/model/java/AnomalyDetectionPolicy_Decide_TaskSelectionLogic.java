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

package org.onap.policy.apex.examples.adaptive.model.java;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.FastMath;
import org.onap.policy.apex.core.engine.executor.context.TaskSelectionExecutionContext;
import org.onap.policy.apex.examples.adaptive.concepts.AnomalyDetection;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.slf4j.Logger;

/**
 * The Class AnomalyDetectionPolicy_Decide_TaskSelectionLogic.
 */
// CHECKSTYLE:OFF: checkstyle:className
public class AnomalyDetectionPolicy_Decide_TaskSelectionLogic {
    // CHECKSTYLE:ON: checkstyle:className

    private Logger logger;
    // configuration
    private static final double ANOMALY_SENSITIVITY = 0.05;
    private static final int FREQUENCY = 360;

    /**
     * A map to hold the Anomaly degree/levels/probabilities required for each task.<br>
     * If there is no task defined for a calculated anomaly-degree, then the default task is
     * used.<br>
     * The map use (LinkedHashMap) is an insertion-ordered map, so the first interval matching a
     * query is used.
     */
    // CHECKSTYLE:OFF: checkstyle:magicNumber
    private static final Map<double[], String> TASK_INTERVALS = new LinkedHashMap<>();

    static {
        TASK_INTERVALS.put(new double[] {0.0, 0.1}, null); // null will mean default task
        TASK_INTERVALS.put(new double[] {0.25, 0.5}, "AnomalyDetectionDecideTask1");
        TASK_INTERVALS.put(new double[] {0.5, 1.01}, "AnomalyDetectionDecideTask2");
    }
    // CHECKSTYLE:ON: checkstyle:magicNumber

    private volatile TaskSelectionExecutionContext executionContext;

    /**
     * Gets the task.
     *
     * @param executor the executor
     * @return the task
     */
    public boolean getTask(final TaskSelectionExecutionContext executor) {
        executionContext = executor;
        logger = executionContext.logger;
        logger.debug(executor.subject.getId());
        logger.debug(executor.inFields.toString());
        final double now = (Double) (executor.inFields.get("MonitoredValue"));
        final Integer iteration = (Integer) (executor.inFields.get("Iteration"));
        // get the double[forecastedValue, AnomalyScore, AnomalyProbability]
        final double[] vals = this.forecastingAndAnomaly(now);
        final double anomalyness = vals[2];
        String task = null;
        for (final Map.Entry<double[], String> i : TASK_INTERVALS.entrySet()) {
            if (checkInterval(anomalyness, i.getKey())) {
                task = i.getValue();
                break;
            }
        }
        if (task == null) {
            executionContext.subject.getDefaultTaskKey().copyTo(executionContext.selectedTask);
        } else {
            executionContext.subject.getTaskKey(task).copyTo(executionContext.selectedTask);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "TestAnomalyDetectionTSLPolicy0000DecideStateTaskSelectionLogic.getTask():\t************\t\t\t\t"
                            + "Iteration:\t" + iteration + "\tValue:\t" + now + "\tForecast:\t" + vals[0]
                            + "\tAnomalyScore:\t" + vals[1] + "\tAnomalyProbability:\t" + vals[2] + "\tInvoking Task:\t"
                            + executionContext.selectedTask);
        }
        return true;
    }

    /**
     * Anomaly detection and forecast.
     *
     * @param value The current value
     * @return Null if the function can not be executed correctly, otherwise double[forecastedValue,
     *         AnomalyScore, AnomalyProbability]
     */
    public double[] forecastingAndAnomaly(final double value) {
        try {
            executionContext.getContextAlbum("AnomalyDetectionAlbum").lockForWriting("AnomalyDetection");
        } catch (final ApexException e) {
            logger.error("Failed to acquire write lock on \"AnomalyDetection\" context", e);
            return null;
        }

        // Get the context object
        AnomalyDetection anomalyDetection =
                (AnomalyDetection) executionContext.getContextAlbum("AnomalyDetectionAlbum").get("AnomalyDetection");
        if (anomalyDetection == null) {
            anomalyDetection = new AnomalyDetection();
            executionContext.getContextAlbum("AnomalyDetectionAlbum").put("AnomalyDetection", anomalyDetection);
        }

        // Check the lists are initialized
        if (!anomalyDetection.isInitialized()) {
            anomalyDetection.init(FREQUENCY);
        }

        boolean unsetfirstround = false;

        int frequency = anomalyDetection.getFrequency();
        frequency = frequency + 1;

        // reset frequency counter
        if (frequency >= FREQUENCY) {
            unsetfirstround = true;
            frequency = 0;
        }
        anomalyDetection.setFrequency(frequency);

        if (unsetfirstround && anomalyDetection.getFirstRound()) {
            anomalyDetection.setFirstRound(false);
        }

        // --------- calculate the forecasted value - simple version
        final Double lastForecast = anomalyDetection.getFrequencyForecasted().get(frequency);

        // get forecast for current value
        final double forecastedValue = lastForecast == null ? value : expMovingAverage(value, lastForecast);

        // --------- calculate the anomalyScore
        final double anomalyScore = lastForecast == null ? 0.0 : FastMath.abs(lastForecast - value);

        anomalyDetection.getFrequencyForecasted().set(frequency, forecastedValue);

        // anomaly score is ignored in the first frequency period
        if (!anomalyDetection.getFirstRound()) {
            ((LinkedList<Double>) anomalyDetection.getAnomalyScores()).addLast(anomalyScore);
        }

        // CHECKSTYLE:OFF: checkstyle:magicNumber
        // max FREQUENCY*4 anomaly scores history
        listSizeControl(anomalyDetection.getAnomalyScores(), FREQUENCY * 4);

        // ---------- calculate the anomaly probability
        double anomalyProbability = 0.0;
        if (anomalyDetection.getAnomalyScores().size() > 30) {
            // 0.5
            anomalyProbability = getStatsTest(anomalyDetection.getAnomalyScores(), ANOMALY_SENSITIVITY);
        }
        // CHECKSTYLE:ON: checkstyle:magicNumber

        try {
            executionContext.getContextAlbum("AnomalyDetectionAlbum").unlockForWriting("AnomalyDetection");
        } catch (final ApexException e) {
            logger.error("Failed to release write lock on \"AnomalyDetection\" context", e);
            return null;
        }

        return new double[] {forecastedValue, anomalyScore, anomalyProbability};
    }

    /**
     * Is the passed value inside the interval, i.e. (value < interval[1] && value>=interval[0]).
     *
     * @param value The value to check
     * @param interval A 2 element double array describing an interval
     * @return true if the value is between interval[0] (inclusive) and interval[1] (exclusive),
     *         i.e. (value < interval[1] && value>=interval[0]). Otherwise false;
     */
    private static boolean checkInterval(final double value, final double[] interval) {
        if (interval == null || interval.length != 2) {
            throw new IllegalArgumentException("something other than an interval passed to checkInterval");
        }
        final double min = interval[0];
        final double max = interval[1];
        return (value < max && value >= min);
    }

    /**
     * calculate the anomaly probability using statistical test.
     *
     * @param values the values
     * @param significanceLevel the significance level
     * @return the anomaly probability
     */
    private static double getStatsTest(final List<Double> values, final double significanceLevel) {
        if (isAllEqual(values)) {
            return 0.0;
        }
        // the targeted value or the last value
        final double currentV = values.get(values.size() - 1);
        Double[] lvaluesCopy = values.toArray(new Double[values.size()]);
        Arrays.sort(lvaluesCopy); // takes ~40% of method time
        // get mean
        double mean = getMean(lvaluesCopy);
        // get the test value: val
        double val = getV(lvaluesCopy, mean, true);
        // get the p value for the test value
        double pvalue = getPValue(lvaluesCopy, val, mean); // takes approx 25% of method time

        // check the critical level
        while (pvalue < significanceLevel) { // takes approx 20% of method time
            // the score value as the anomaly probability
            final double score = (significanceLevel - pvalue) / significanceLevel;
            if (Double.compare(val, currentV) == 0) {
                return score;
            }
            // do the critical check again for the left values
            lvaluesCopy = removevalue(lvaluesCopy, val);
            if (isAllEqual(lvaluesCopy)) {
                return 0.0;
            }

            mean = getMean(lvaluesCopy);
            val = getV(lvaluesCopy, mean, true);
            pvalue = getPValue(lvaluesCopy, val, mean);
        }
        return 0.0;
    }

    /**
     * Get the test value based on mean from sorted values.
     *
     * @param lvalues the l values
     * @param mean the mean
     * @param maxValueOnly : only the max extreme value will be tested
     * @return the value to be tested
     */
    private static double getV(final Double[] lvalues, final double mean, final boolean maxValueOnly) {
        double val = lvalues[lvalues.length - 1];
        // max value as the extreme value
        if (maxValueOnly) {
            return val;
        }
        // check the extreme side
        if ((val - mean) < (mean - lvalues[0])) {
            val = lvalues[0];
        }
        return val;
    }

    /**
     * calculate the P value for the t distribution.
     *
     * @param lvalues the l values
     * @param val the value
     * @param mean the mean
     * @return the p value
     */
    private static double getPValue(final Double[] lvalues, final double val, final double mean) {
        // calculate z value
        final double z = FastMath.abs(val - mean) / getStdDev(lvalues, mean);
        // calculate T
        final double n = lvalues.length;
        final double s = (z * z * n * (2.0 - n)) / (z * z * n - (n - 1.0) * (n - 1.0));
        final double t = FastMath.sqrt(s);
        // default p value = 0
        double pvalue = 0.0;
        if (!Double.isNaN(t)) {
            // t distribution with n-2 degrees of freedom
            final TDistribution tDist = new TDistribution(n - 2);
            pvalue = n * (1.0 - tDist.cumulativeProbability(t));
            // set max pvalue = 1
            pvalue = pvalue > 1.0 ? 1.0 : pvalue;
        }
        return pvalue;
    }

    /*
     * Some utility methods
     */
    // exponential = 2(n+1)
    private static final double EMA_EXPONENT = 2.0 / (7.0 + 1.0);
    private static final double EMA_EXPONENT_1 = (1.0 - EMA_EXPONENT);

    /**
     * exponential moving average.
     *
     * @param value the value
     * @param lastForecast the last forecast
     * @return the double
     */
    private static double expMovingAverage(final double value, final double lastForecast) {
        return (value * EMA_EXPONENT) + (lastForecast * EMA_EXPONENT_1);
    }

    /**
     * Remove the first occurrence of the value val from the array.
     *
     * @param lvalues the l values
     * @param val the value
     * @return the double[]
     */
    private static Double[] removevalue(final Double[] lvalues, final double val) {
        for (int i = 0; i < lvalues.length; i++) {
            if (Double.compare(lvalues[i], val) == 0) {
                final Double[] ret = new Double[lvalues.length - 1];
                System.arraycopy(lvalues, 0, ret, 0, i);
                System.arraycopy(lvalues, i + 1, ret, i, lvalues.length - i - 1);
                return ret;
            }
        }
        return lvalues;
    }

    /**
     * get mean value of double list.
     *
     * @param lvalues the l values
     * @return the mean
     */
    private static double getMean(final Double[] lvalues) {
        double sum = 0.0;
        for (final double d : lvalues) {

            sum += d;
        }
        return sum / lvalues.length;
    }

    /**
     * get standard deviation of double list.
     *
     * @param lvalues the l values
     * @param mean the mean
     * @return stddev
     */
    private static double getStdDev(final Double[] lvalues, final double mean) {
        double temp = 0.0;
        for (final double d : lvalues) {
            temp += (mean - d) * (mean - d);
        }
        return FastMath.sqrt(temp / lvalues.length);
    }

    /**
     * Chop head off list to make it length max .
     *
     * @param list the list to chop
     * @param max the max size
     */
    private static void listSizeControl(final List<?> list, final int max) {
        final int k = list.size();
        if (k > max) {
            // Chop the head off the list.
            list.subList(0, k - max).clear();
        }
    }

    /**
     * return true if all values are equal.
     *
     * @param lvalues the l values
     * @return true, if checks if is all equal
     */
    private static boolean isAllEqual(final List<Double> lvalues) {
        final double first = lvalues.get(0);
        for (final Double d : lvalues) {
            if (Double.compare(d, first) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * return true if all values are equal.
     *
     * @param lvalues the l values
     * @return true, if checks if is all equal
     */
    private static boolean isAllEqual(final Double[] lvalues) {
        final double first = lvalues[0];
        for (final Double d : lvalues) {
            if (Double.compare(d, first) != 0) {
                return false;
            }
        }
        return true;
    }
}
