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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * This class defines the parameters for event generation.
 */
public class EventGeneratorParameters implements ParameterGroup {
    // @formatter:off
    private static final String DEFAULT_NAME                  = EventGeneratorParameters.class.getSimpleName();
    private static final String DEFAULT_HOST                  = "localhost";
    private static final int    DEFAULT_PORT                  = 32801;
    private static final int    DEFAULT_BATCH_COUNT           = 1;
    private static final int    DEFAULT_BATCH_SIZE            = 1;
    private static final long   DEFAULT_DELAY_BETWEEN_BATCHES = 2000;

    private String name                = DEFAULT_NAME;
    private String host                = DEFAULT_HOST;
    private int    port                = DEFAULT_PORT;
    private int    batchCount          = DEFAULT_BATCH_COUNT;
    private int    batchSize           = DEFAULT_BATCH_SIZE;
    private long   delayBetweenBatches = DEFAULT_DELAY_BETWEEN_BATCHES;
    private String outFile             = null;
    // @formatter:on

    /**
     * Create default parameters.
     */
    public EventGeneratorParameters() {
        // Default parameters are generated
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(int batchCount) {
        this.batchCount = batchCount;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public long getDelayBetweenBatches() {
        return delayBetweenBatches;
    }

    public void setDelayBetweenBatches(long delayBetweenBatches) {
        this.delayBetweenBatches = delayBetweenBatches;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupValidationResult validate() {
        GroupValidationResult validationResult = new GroupValidationResult(this);

        if (isNullOrBlank(name)) {
            validationResult.setResult("name", ValidationStatus.INVALID, "name must be a non-blank string");
        }

        if (isNullOrBlank(host)) {
            validationResult.setResult("host", ValidationStatus.INVALID, "host must be a non-blank string");
        }

        if (port < 1024 || port > 65535) {
            validationResult.setResult("port", ValidationStatus.INVALID,
                            "port must be an integer between 1024 and 65535 inclusive");
        }

        if (batchCount < 0) {
            validationResult.setResult("batchCount", ValidationStatus.INVALID,
                            "batchCount must be an integer with a value of zero or more, "
                                            + "zero means generate batches forever");
        }

        if (batchSize < 1) {
            validationResult.setResult("batchSize", ValidationStatus.INVALID,
                            "batchSize must be an integer greater than zero");
        }

        if (delayBetweenBatches < 0) {
            validationResult.setResult("batchSize", ValidationStatus.INVALID,
                            "batchSize must be an integer with a value of zero or more");
        }

        return validationResult;
    }

    private boolean isNullOrBlank(final String stringValue) {
        return stringValue == null || stringValue.trim().length() == 0;
    }

}
