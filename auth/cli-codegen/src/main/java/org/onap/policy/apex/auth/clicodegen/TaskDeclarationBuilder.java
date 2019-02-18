/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
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

public class TaskDeclarationBuilder {

    private String name;
    private String version;
    private String uuid;
    private String description;
    private List<ST> infields;
    private List<ST> outfields;
    private ST logic;
    private List<ST> parameters;
    private List<ST> contextRefs;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public List<ST> getInfields() {
        return infields;
    }

    public List<ST> getOutfields() {
        return outfields;
    }

    public ST getLogic() {
        return logic;
    }

    public List<ST> getParameters() {
        return parameters;
    }

    public List<ST> getContextRefs() {
        return contextRefs;
    }

    public TaskDeclarationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TaskDeclarationBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public TaskDeclarationBuilder setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public TaskDeclarationBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskDeclarationBuilder setInfields(List<ST> infields) {
        this.infields = infields;
        return this;
    }

    public TaskDeclarationBuilder setOutfields(List<ST> outfields) {
        this.outfields = outfields;
        return this;
    }

    public TaskDeclarationBuilder setLogic(ST logic) {
        this.logic = logic;
        return this;
    }

    public TaskDeclarationBuilder setParameters(List<ST> parameters) {
        this.parameters = parameters;
        return this;
    }

    public TaskDeclarationBuilder setContextRefs(List<ST> contextRefs) {
        this.contextRefs = contextRefs;
        return this;
    }
}
