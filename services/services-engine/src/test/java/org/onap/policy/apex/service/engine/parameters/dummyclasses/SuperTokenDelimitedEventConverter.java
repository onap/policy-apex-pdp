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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import java.util.List;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

public final class SuperTokenDelimitedEventConverter implements ApexEventProtocolConverter {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventConverter#toApexEvent(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public List<ApexEvent> toApexEvent(final String eventName, final Object eventOfOtherType) throws ApexException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.event.ApexEventConverter#fromApexEvent(org.onap.policy.
     * apex.service.engine.event.ApexEvent)
     */
    @Override
    public String fromApexEvent(final ApexEvent apexEvent) throws ApexException {
        return null;
    }

    @Override
    public void init(final EventProtocolParameters parameters) {}
}
