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

package org.onap.policy.apex.core.protocols;

import org.onap.policy.apex.core.protocols.Action;
import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * A dummy message class for testing.
 *
 */
public class DummyMessage extends Message {
    private static final long serialVersionUID = 8671295165136561708L;

    public DummyMessage(final Action action, final AxArtifactKey targetKey) {
        super(action, targetKey);
    }
    
    public DummyMessage(final Action action, final AxArtifactKey targetKey, final String messageData) {
        super(action, targetKey, messageData);
    }
}
