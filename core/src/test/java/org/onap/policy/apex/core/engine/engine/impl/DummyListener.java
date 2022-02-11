/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.core.engine.engine.impl;

import org.onap.policy.apex.core.engine.engine.EnEventListener;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Dummy engine event listener for unit test.
 *
 */
public class DummyListener implements EnEventListener {

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onEnEvent(EnEvent enEvent) throws ApexException {
        // Do nothing
    }
}
