/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.service.parameters.engineservice;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.service.parameters.ApexParameterConstants;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

// @formatter:off
/**
 * This class holds the parameters for an Apex Engine Service with multiple engine threads running multiple engines.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>name: The name of the Apex engine service, which can be set to any value that matches the regular expression
 * {@link org.onap.policy.apex.model.basicmodel.concepts.AxKey#NAME_REGEXP}.
 * <li>version: The name of the Apex engine service, which can be set to any value that matches the regular expression
 * {@link org.onap.policy.apex.model.basicmodel.concepts.AxKey#VERSION_REGEXP}.
 * <li>id: The ID of the Apex engine service, which can be set to any integer value by a user.
 * <li>instanceCount: The number of Apex engines to spawn in this engine service. Each engine executes in its own
 * thread.
 * <li>deploymentPort: The port that the Apex Engine Service will open so that it can be managed using the EngDep
 * protocol. The EngDep protocol allows the engine service to be monitored, to start and stop engines in the engine
 * service, and to update the policy model of the engine service.
 * <li>engineParameters: Parameters (a {@link EngineParameters} instance) that all of the engines in the engine service
 * will use. All engine threads use the same parameters and act as a pool of engines. Engine parameters specify the
 * executors and context management for the engines.
 * <li>policyModelFileName: The full path to the policy model file name to deploy on the engine service.
 * <li>periodicEventPeriod: The period in milliseconds at which the periodic event PERIOIC_EVENT will be generated by
 * APEX, 0 means no periodic event generation, negative values are illegal.
 * </ol>
 */
// @formatter:on
@Getter
@Setter
public class EngineServiceParameters implements ParameterGroup {
    private static final int MAX_PORT = 65535;

    // @formatter:off
    /** The default name of the Apex engine service. */
    public static final String DEFAULT_NAME = "ApexEngineService";

    /** The default version of the Apex engine service. */
    public static final String DEFAULT_VERSION = "1.0.0";

    /** The default ID of the Apex engine service. */
    public static final int DEFAULT_ID = -1;

    /** The default instance count for the Apex engine service. */
    public static final int DEFAULT_INSTANCE_COUNT  = 1;

    /** The default EngDep deployment port of the Apex engine service. */
    public static final int DEFAULT_DEPLOYMENT_PORT = 34421;

    // Constants for repeated strings

    // Apex engine service parameters
    private String name                = DEFAULT_NAME;
    private String version             = DEFAULT_VERSION;
    private int    id                  = DEFAULT_ID;
    private int    instanceCount       = DEFAULT_INSTANCE_COUNT;
    private int    deploymentPort      = DEFAULT_DEPLOYMENT_PORT;
    private String policyModel = null;
    private long   periodicEventPeriod = 0;
    // @formatter:on

    // Apex engine internal parameters
    private EngineParameters engineParameters = new EngineParameters();

    /**
     * Constructor to create an apex engine service parameters instance and register the instance with the parameter
     * service.
     */
    public EngineServiceParameters() {
        super();

        // Set the name for the parameters
        this.name = ApexParameterConstants.ENGINE_SERVICE_GROUP_NAME;
    }

    /**
     * Gets the key of the Apex engine service.
     *
     * @return the Apex engine service key
     */
    public AxArtifactKey getEngineKey() {
        return new AxArtifactKey(name, version);
    }

    /**
     * Sets the key of the Apex engine service.
     *
     * @param key the the Apex engine service key
     */
    public void setEngineKey(final AxArtifactKey key) {
        this.setName(key.getName());
        this.setVersion(key.getVersion());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);

        validateStringParameters(result);

        validateNumericParameters(result);

        if (StringUtils.isBlank(policyModel)) {
            result.setResult("policyModel", ValidationStatus.INVALID, "must be specified");
        }
        result.setResult("engineParameters", engineParameters.validate());

        return result;
    }

    /**
     * Validate string parameters.
     *
     * @param result the result of string parameter validation
     */
    private void validateStringParameters(final GroupValidationResult result) {
        if (name == null || !name.matches(AxKey.NAME_REGEXP)) {
            result.setResult("name", ValidationStatus.INVALID,
                            "name is invalid, it must match regular expression" + AxKey.NAME_REGEXP);
        }

        if (version == null || !version.matches(AxKey.VERSION_REGEXP)) {
            result.setResult("version", ValidationStatus.INVALID,
                            "version is invalid, it must match regular expression" + AxKey.VERSION_REGEXP);
        }
    }

    /**
     * Validate numeric parameters.
     *
     * @param result the result of numeric parameter validation
     */
    private void validateNumericParameters(final GroupValidationResult result) {
        if (id < 0) {
            result.setResult("id", ValidationStatus.INVALID,
                            "id not specified or specified value [" + id + "] invalid, must be specified as id >= 0");
        }

        if (instanceCount < 1) {
            result.setResult("instanceCount", ValidationStatus.INVALID,
                            "instanceCount [" + instanceCount + "] invalid, must be specified as instanceCount >= 1");
        }

        if (deploymentPort < 1 || deploymentPort > MAX_PORT) {
            result.setResult("deploymentPort", ValidationStatus.INVALID, "deploymentPort [" + deploymentPort
                            + "] invalid, must be specified as 1024 <= port <= 65535");
        }

        if (periodicEventPeriod < 0) {
            result.setResult("periodicEventPeriod", ValidationStatus.INVALID, "periodicEventPeriod ["
                            + periodicEventPeriod + "] invalid, must be specified in milliseconds as >=0");
        }
    }

}
