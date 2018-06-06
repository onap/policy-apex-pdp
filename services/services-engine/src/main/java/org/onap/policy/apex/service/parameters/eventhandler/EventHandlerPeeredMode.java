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

package org.onap.policy.apex.service.parameters.eventhandler;

/**
 * This enum specifies the peered mode that an event handler may be in.
 * 
 * <p>
 * The following values are defined:
 * <ol>
 * <li>SYNCHRONOUS: The event handler is tied to another event handler for event handling in APEX,
 * used for request-response calls where APEX is the receiver.
 * <li>REQUESTOR: The event handler is tied another event handler for event handling in APEX, used
 * for request-response calls where APEX is the sender.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * 
 * @author liam.fallon@ericsson.com
 *
 */
public enum EventHandlerPeeredMode {
    SYNCHRONOUS, REQUESTOR
}
