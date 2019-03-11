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
package org.onap.policy.apex.model.modelapi.impl;

public class ContextAlbumBuilder {
  private String name;
  private String version;
  private String scope;
  private String writable;
  private String contextSchemaName;
  private String contextSchemaVersion;
  private String uuid;
  private String description;

  public String getName() {
    return name;
  }

  public ContextAlbumBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public ContextAlbumBuilder setVersion(String version) {
    this.version = version;
    return this;
  }

  public String getScope() {
    return scope;
  }

  public ContextAlbumBuilder setScope(String scope) {
    this.scope = scope;
    return this;
  }

  public String getWritable() {
    return writable;
  }

  public ContextAlbumBuilder setWritable(String writable) {
    this.writable = writable;
    return this;
  }

  public String getContextSchemaName() {
    return contextSchemaName;
  }

  public ContextAlbumBuilder setContextSchemaName(String contextSchemaName) {
    this.contextSchemaName = contextSchemaName;
    return this;
  }

  public String getContextSchemaVersion() {
    return contextSchemaVersion;
  }

  public ContextAlbumBuilder setContextSchemaVersion(String contextSchemaVersion) {
    this.contextSchemaVersion = contextSchemaVersion;
    return this;
  }

  public String getUuid() {
    return uuid;
  }

  public ContextAlbumBuilder setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public ContextAlbumBuilder setDescription(String description) {
    this.description = description;
    return this;
  }
}
