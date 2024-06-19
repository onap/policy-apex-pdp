/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.services.onappf.parameters.dummyclasses;

import java.util.Map;
import java.util.Properties;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;

/**
 * Dummy state finalizer executor for testing.
 */
public class DummyStateFinalizerExecutor extends StateFinalizerExecutor {

    @Override
    public String execute(final long executionId, final Properties executorProperties,
                          final Map<String, Object> newIncomingFields) {

        return "stateOutput0";
    }
}
