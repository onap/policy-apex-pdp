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

import org.stringtemplate.v4.ST;

import java.util.List;

public class EventDeclarationBuilder {
    private String name;
    private String version;
    private String uuid;
    private String description;
    private String nameSpace;
    private String source;
    private String target;
    private List<ST> fields;

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

    public String getNameSpace() {
        return nameSpace;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public List<ST> getFields() {
        return fields;
    }

    /**
     * Set Name.
     *
     * @param name the event name
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set Version.
     *
     * @param version the event version
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set Uuid.
     *
     * @param uuid a UUID for the definition
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Set description.
     *
     * @param description a description of the event
     * @return
     */
    public EventDeclarationBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set Name space.
     *
     * @param nameSpace the name space for the event
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    /**
     * Set source.
     *
     * @param source a source sending the event
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * Set target.
     *
     * @param target a target receiving the event
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setTarget(String target) {
        this.target = target;
        return this;
    }

    /**
     * Set fields.
     *
     * @param fields any event fields
     * @return EventDeclarationBuilder
     */
    public EventDeclarationBuilder setFields(List<ST> fields) {
        this.fields = fields;
        return this;
    }
}
