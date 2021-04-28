/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.annotations.Max;
import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

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

    @NotNull @NotBlank
    private String name                = DEFAULT_NAME;
    @NotNull @NotBlank
    private String host                = DEFAULT_HOST;
    @Min(1024)
    @Max(65535)
    private int    port                = DEFAULT_PORT;
    @Min(0)
    private int    batchCount          = DEFAULT_BATCH_COUNT;
    @Min(1)
    private int    batchSize           = DEFAULT_BATCH_SIZE;
    @Min(0)
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
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        return new BeanValidator().validateTop(getClass().getSimpleName(), this);
    }

}
