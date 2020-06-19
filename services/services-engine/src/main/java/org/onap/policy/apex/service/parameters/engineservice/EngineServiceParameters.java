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

package org.onap.policy.apex.service.parameters.engineservice;

import java.io.File;
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
    private static final String POLICY_MODEL_FILE_NAME = "policyModelFileName";

    // Apex engine service parameters
    private String name                = DEFAULT_NAME;
    private String version             = DEFAULT_VERSION;
    private int    id                  = DEFAULT_ID;
    private int    instanceCount       = DEFAULT_INSTANCE_COUNT;
    private int    deploymentPort      = DEFAULT_DEPLOYMENT_PORT;
    private String policyModelFileName = null;
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
     * Gets the name of the engine service.
     *
     * @return the name of the engine service
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the engine service.
     *
     * @param name the name of the engine service
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the version of the engine service.
     *
     * @return the version of the engine service
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the engine service.
     *
     * @param version the version of the engine service
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Gets the id of the engine service.
     *
     * @return the id of the engine service
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the engine service.
     *
     * @param id the id of the engine service
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Gets the instance count of the engine service.
     *
     * @return the instance count of the engine service
     */
    public int getInstanceCount() {
        return instanceCount;
    }

    /**
     * Sets the instance count of the engine service.
     *
     * @param instanceCount the instance count of the engine service
     */
    public void setInstanceCount(final int instanceCount) {
        this.instanceCount = instanceCount;
    }

    /**
     * Gets the deployment port of the engine service.
     *
     * @return the deployment port of the engine service
     */
    public int getDeploymentPort() {
        return deploymentPort;
    }

    /**
     * Sets the deployment port of the engine service.
     *
     * @param deploymentPort the deployment port of the engine service
     */
    public void setDeploymentPort(final int deploymentPort) {
        this.deploymentPort = deploymentPort;
    }

    /**
     * Gets the file name of the policy engine for deployment on the engine service.
     *
     * @return the file name of the policy engine for deployment on the engine service
     */
    public String getPolicyModelFileName() {
        return policyModelFileName;
    }

    /**
     * Sets the file name of the policy engine for deployment on the engine service.
     *
     * @param policyModelFileName the file name of the policy engine for deployment on the engine service
     */
    public void setPolicyModelFileName(final String policyModelFileName) {
        this.policyModelFileName = policyModelFileName;
    }

    /**
     * Get the period in milliseconds at which periodic events are sent, zero means no periodic events are being sent.
     * 
     * @return the periodic period
     */
    public long getPeriodicEventPeriod() {
        return periodicEventPeriod;
    }

    /**
     * Set the period in milliseconds at which periodic events are sent, zero means no periodic events are to be sent,
     * negative values are illegal.
     * 
     * @param periodicEventPeriod the periodic period
     */
    public void setPeriodicEventPeriod(final long periodicEventPeriod) {
        this.periodicEventPeriod = periodicEventPeriod;
    }

    /**
     * Gets the engine parameters for engines in the engine service.
     *
     * @return the engine parameters for engines in the engine service
     */
    public EngineParameters getEngineParameters() {
        return engineParameters;
    }

    /**
     * Sets the engine parameters for engines in the engine service.
     *
     * @param engineParameters the engine parameters for engines in the engine service
     */
    public void setEngineParameters(final EngineParameters engineParameters) {
        this.engineParameters = engineParameters;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);

        validateStringParameters(result);

        validateNumericParameters(result);

        if (policyModelFileName != null) {
            validatePolicyModelFileName(result);
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

    /**
     * Validate the policy model file name parameter.
     * 
     * @param result the variable in which to store the result of the validation
     */
    private void validatePolicyModelFileName(final GroupValidationResult result) {
        if (policyModelFileName.trim().length() == 0) {
            result.setResult(POLICY_MODEL_FILE_NAME, ValidationStatus.INVALID,
                            "\"" + policyModelFileName + "\" invalid, must be specified as a non-empty string");
            return;
        }

        String absolutePolicyFileName = null;

        // Resolve the file name if it is a relative file name
        File policyModelFile = new File(policyModelFileName);
        if (policyModelFile.isAbsolute()) {
            absolutePolicyFileName = policyModelFileName;
        } else {
            absolutePolicyFileName = System.getProperty("APEX_RELATIVE_FILE_ROOT") + File.separator
                            + policyModelFileName;
            policyModelFile = new File(absolutePolicyFileName);
        }

        // Check that the file exists
        if (!policyModelFile.exists()) {
            result.setResult(POLICY_MODEL_FILE_NAME, ValidationStatus.INVALID, "not found");
        } else if (!policyModelFile.isFile()) {
            // Check that the file is a regular file
            result.setResult(POLICY_MODEL_FILE_NAME, ValidationStatus.INVALID, "is not a plain file");
        } else {
            // OK, we found the file and it's OK, so reset the file name
            policyModelFileName = absolutePolicyFileName;

            // Check that the file is readable
            if (!policyModelFile.canRead()) {
                result.setResult(POLICY_MODEL_FILE_NAME, ValidationStatus.INVALID, "is not readable");
            }
        }
    }
}
