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

/**
 * Implements the RESTful editor for Apex. It implements a RESTful service towards the
 * {@link org.onap.policy.apex.model.modelapi.ApexEditorAPI} Java interface for use by clients over REST. It also
 * provides a web-based client written in Javascript.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

package org.onap.policy.apex.client.editor.rest;
