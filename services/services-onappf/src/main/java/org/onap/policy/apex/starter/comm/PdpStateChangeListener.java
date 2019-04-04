/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.starter.comm;

import org.onap.policy.apex.starter.handler.PdpStateChangeMessageHandler;
import org.onap.policy.common.endpoints.event.comm.Topic.CommInfrastructure;
import org.onap.policy.common.endpoints.listeners.TypedMessageListener;
import org.onap.policy.models.pdp.concepts.PdpStateChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for Pdp state change messages sent by PAP.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpStateChangeListener implements TypedMessageListener<PdpStateChange> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpStateChangeListener.class);

    @Override
    public void onTopicEvent(final CommInfrastructure infra, final String topic,
            final PdpStateChange pdpStateChangeMsg) {
        LOGGER.debug("Pdp state change message received from PAP. - {}", pdpStateChangeMsg);
        new PdpStateChangeMessageHandler().handlePdpStateChangeEvent(pdpStateChangeMsg);
    }
}
