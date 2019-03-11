/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung Electronics Co., Ltd. All rights reserved.
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

package org.onap.policy.apex.auth.clicodegen;

public class PolicyStateTaskBuilder {

    private String policyName;
    private String version;
    private String stateName;
    private String taskLocalName;
    private String taskName;
    private String taskVersion;
    private String outputType;
    private String outputName;

    public String getPolicyName() {
        return policyName;
    }

    public String getVersion() {
        return version;
    }

    public String getStateName() {
        return stateName;
    }

    public String getTaskLocalName() {
        return taskLocalName;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskVersion() {
        return taskVersion;
    }

    public String getOutputType() {
        return outputType;
    }

    public String getOutputName() {
        return outputName;
    }

    public PolicyStateTaskBuilder setPolicyName(String policyName) {
        this.policyName = policyName;
        return this;
    }

    public PolicyStateTaskBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public PolicyStateTaskBuilder setStateName(String stateName) {
        this.stateName = stateName;
        return this;
    }

    public PolicyStateTaskBuilder setTaskLocalName(String taskLocalName) {
        this.taskLocalName = taskLocalName;
        return this;
    }

    public PolicyStateTaskBuilder setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public PolicyStateTaskBuilder setTaskVersion(String taskVersion) {
        this.taskVersion = taskVersion;
        return this;
    }

    public PolicyStateTaskBuilder setOutputType(String outputType) {
        this.outputType = outputType;
        return this;
    }

    public PolicyStateTaskBuilder setOutputName(String outputName) {
        this.outputName = outputName;
        return this;
    }

}
