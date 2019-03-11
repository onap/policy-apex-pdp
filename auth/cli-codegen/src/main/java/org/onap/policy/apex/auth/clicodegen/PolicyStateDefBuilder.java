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

import java.util.List;
import org.stringtemplate.v4.ST;

public class PolicyStateDefBuilder {

    private String policyName;
    private String version;
    private String stateName;
    private String triggerName;
    private String triggerVersion;
    private String defaultTask;
    private String defaultTaskVersion;
    private List<ST> outputs;
    private List<ST> tasks;
    private List<ST> tsLogic;
    private List<ST> finalizerLogics;
    private List<ST> ctxRefs;


    public String getPolicyName() {
        return policyName;
    }

    public String getVersion() {
        return version;
    }

    public String getStateName() {
        return stateName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public String getTriggerVersion() {
        return triggerVersion;
    }

    public String getDefaultTask() {
        return defaultTask;
    }

    public String getDefaultTaskVersion() {
        return defaultTaskVersion;
    }

    public List<ST> getOutputs() {
        return outputs;
    }

    public List<ST> getTasks() {
        return tasks;
    }

    public List<ST> getTsLogic() {
        return tsLogic;
    }

    public List<ST> getFinalizerLogics() {
        return finalizerLogics;
    }

    public List<ST> getCtxRefs() {
        return ctxRefs;
    }

    public PolicyStateDefBuilder setPolicyName(String policyName) {
        this.policyName = policyName;
        return this;
    }

    public PolicyStateDefBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public PolicyStateDefBuilder setStateName(String stateName) {
        this.stateName = stateName;
        return this;
    }

    public PolicyStateDefBuilder setTriggerName(String triggerName) {
        this.triggerName = triggerName;
        return this;
    }

    public PolicyStateDefBuilder setTriggerVersion(String triggerVersion) {
        this.triggerVersion = triggerVersion;
        return this;
    }

    public PolicyStateDefBuilder setDefaultTask(String defaultTask) {
        this.defaultTask = defaultTask;
        return this;
    }

    public PolicyStateDefBuilder setDefaultTaskVersion(String defaultTaskVersion) {
        this.defaultTaskVersion = defaultTaskVersion;
        return this;
    }

    public PolicyStateDefBuilder setOutputs(List<ST> outputs) {
        this.outputs = outputs;
        return this;
    }

    public PolicyStateDefBuilder setTasks(List<ST> tasks) {
        this.tasks = tasks;
        return this;
    }

    public PolicyStateDefBuilder setTsLogic(List<ST> tsLogic) {
        this.tsLogic = tsLogic;
        return this;
    }

    public PolicyStateDefBuilder setFinalizerLogics(List<ST> finalizerLogics) {
        this.finalizerLogics = finalizerLogics;
        return this;
    }

    public PolicyStateDefBuilder setCtxRefs(List<ST> ctxRefs) {
        this.ctxRefs = ctxRefs;
        return this;
    }
}
