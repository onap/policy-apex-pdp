/*-
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

package org.onap.policy.apex.examples.aadm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.core.engine.engine.EnEventListener;
import org.onap.policy.apex.core.engine.event.EnEvent;
import static org.awaitility.Awaitility.await;

/**
 * The listener interface for receiving testApexAction events. The class that is interested in processing a
 * testApexAction event implements this interface, and the object created with that class is registered with a component
 * using the component's <code>addTestApexActionListener</code> method. When the testApexAction event occurs, that
 * object's appropriate method is invoked.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApexActionListener implements EnEventListener {
    List<EnEvent> resultEvents = new ArrayList<EnEvent>();

    private final String id;

    /**
     * Instantiates a new test apex action listener.
     *
     * @param id the id
     */
    public TestApexActionListener(final String id) {
        this.id = id;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public EnEvent getResult() {
        await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> !resultEvents.isEmpty());
        return resultEvents.remove(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onEnEvent(final EnEvent actionEvent) {
        await().atLeast(100, TimeUnit.MILLISECONDS).until(() -> actionEvent != null);
        System.out.println("Action event from engine:" + actionEvent.getName());
        resultEvents.add(actionEvent);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }
}
