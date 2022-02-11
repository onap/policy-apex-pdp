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
 * Provides the core engine implementation for Apex. It builds a state machine for execution for each policy in its
 * policy model. It provides the infrastructure for running policies and their states, for running executors provided by
 * executor plugins, for supplying events and context to running policies, states, and tasks, and for handling event
 * transmission into and out of policies and between states in policies.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

package org.onap.policy.apex.core.engine;
