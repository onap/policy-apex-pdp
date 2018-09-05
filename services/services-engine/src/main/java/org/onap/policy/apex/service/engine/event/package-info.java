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
 * Provides a generic externally-facing ApexEvent class that can be sent into an APEX engine
 * and processed by an APEX engine. It provides the producer ApexEventProducer producer and
 * ApexEventConsumer consumer interfaces that APEX uses to send events to and receive events
 * from other systems. It also provides the ApexEventConverter interface that can be
 * implemented by plugins that wish to convert some external event format into the APEX event
 * format. It also provides a periodic event generator that can be used to send periodic events into
 * an APEX engine for triggering of policies to carry out housekeeping tasks.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
package org.onap.policy.apex.service.engine.event;
