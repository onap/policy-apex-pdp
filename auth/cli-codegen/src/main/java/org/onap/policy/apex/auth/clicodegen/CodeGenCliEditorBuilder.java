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

public class CodeGenCliEditorBuilder {
    private String name;
    private String version;
    private String uuid;
    private String description;
    private String scope;
    private boolean writable;
    private String schemaName;
    private String schemaVersion;

    /**
     * Set name.
     * @param name the name of the context album
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set version.
     * @param version the version of the context album
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set uuid.
     * @param uuid a UUID for the declaration
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Set description.
     * @param description a description for the context album
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set scope.
     * @param scope the scope
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setScope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Set writable.
     * @param writable a flag for writable context
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setWritable(boolean writable) {
        this.writable = writable;
        return this;
    }

    /**
     * Set schemaname.
     * @param schemaName the name of the schema
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    /**
     * Set schema version.
     * @param schemaVersion the version of the declaration
     * @return CodeGenCliEditorBuilder
     */
    public CodeGenCliEditorBuilder setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }

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

    public String getScope() {
        return scope;
    }

    public boolean isWritable() {
        return writable;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }
}
