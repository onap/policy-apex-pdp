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
 * Provides context and facades for executing tasks, task selection logic, and state finalizer
 * logic. The public fields and methods of {@link TaskExecutionContext},
 * {@link TaskSelectionExecutionContext} and {@link StateFinalizerExecutionContext} are available to
 * task logic, task selection logic, and state finalizer logic respectively when that logic is
 * executing in an executor plugin under the control of an APEX engine.
 *
 * The {@link AxStateFacade} and {@link AxTaskFacade} classes provide facades and convenience
 * methods for state and task definition information for logic at execution time.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

package org.onap.policy.apex.core.engine.executor.context;
