/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import lombok.Getter;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class SuperDooperEventSubscriber implements ApexEventConsumer {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(SuperDooperEventSubscriber.class);

    @Getter
    private String name;

    @Override
    public void init(final String name, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver apexEventReceiver) throws ApexEventException {
        this.name = name;
        LOGGER.info("Initialising Apex Consumer: " + this.getClass().getName() + ":" + this.name);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
    }

    @Override
    public void start() {
        // Not used

    }

    @Override
    public void stop() {
        // Not used
    }

}
