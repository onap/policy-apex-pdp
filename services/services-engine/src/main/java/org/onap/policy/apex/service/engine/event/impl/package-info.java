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
 * Contains implementations for conversion between externally facing
 * {@link org.onap.policy.apex.service.engine.event.ApexEvent} instances and internal APEX engine
 * {@link org.onap.policy.apex.core.engine.event.EnEvent} instances. It also contains the
 * implementation of the default APEX File carrier technology plugin as well as the protocol plugins
 * for XML and JSON event protocols.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
package org.onap.policy.apex.service.engine.event.impl;
