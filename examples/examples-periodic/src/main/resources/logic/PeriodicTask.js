/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var eventList = executor.subject.getOutFieldSchemaHelper("EventList").createNewInstance();

var eventType = org.onap.policy.apex.service.engine.event.ApexEvent;

for (i = 0; i < 5; i++) {
    var event = new eventType("InputEvent", "0.0.1", "org.onap.policy.apex.periodic", "APEX", "APEX");

    var par0 = "Hello: " + i;
    var par1 = "Goodbye: " + i;

    event.put("Par0", par0);
    event.put("Par1", par1);

    eventList.add(event);
}

executor.outFields.put("EventList", eventList);

executor.logger.info(executor.outFields);

true;