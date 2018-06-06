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

package org.onap.policy.apex.service.engine.event;

import java.util.List;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * The Interface ApexEventConverter is used for applications that want to convert arbitrary event
 * types to and from Apex events. Application implement this interface to convert their events to
 * and from Apex events.The Apex service can then use this interface to transparently transfer
 * events into and out of an Apex system.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ApexEventConverter {

    /**
     * Convert an event of arbitrary type into an Apex event.
     *
     * @param name the name of the incoming event
     * @param eventOfOtherType the event of some other type to convert
     * @return the apex event
     * @throws ApexException thrown on conversion errors
     */
    List<ApexEvent> toApexEvent(String name, Object eventOfOtherType) throws ApexException;

    /**
     * Convert an Apex event into an event of arbitrary type {@code OTHER_EVENT_TYPE}.
     *
     * @param apexEvent the apex event to convert
     * @return the event converted into the other type
     * @throws ApexException thrown on conversion errors
     */
    Object fromApexEvent(ApexEvent apexEvent) throws ApexException;
}
