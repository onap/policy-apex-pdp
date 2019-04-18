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

package org.onap.policy.apex.plugins.event.carrier.cds;

import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events using CDS. In fact, no events reception is
 * initiated by CDS so this class is just a dummy implementation.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class ApexCdsConsumer implements ApexEventConsumer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexCdsConsumer.class);

    // Recurring string constants
    private static final String CDS_CONSUMER_ERROR_MESSAGE =
            "a CDS consumer may not be specified, CDS may only accept events";

    // The name for this consumer
    private String name = null;

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#init(java.lang.String,
     * org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters,
     * org.onap.policy.apex.service.engine.event.ApexEventReceiver)
     */
    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {

        String errorMessage = CDS_CONSUMER_ERROR_MESSAGE;
        LOGGER.warn(errorMessage);
        throw new ApexEventException(errorMessage);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#start()
     */
    @Override
    public void start() {
        String errorMessage = CDS_CONSUMER_ERROR_MESSAGE;
        LOGGER.warn(errorMessage);
        throw new ApexEventRuntimeException(errorMessage);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.policy.apex.service.
     * parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        String errorMessage = CDS_CONSUMER_ERROR_MESSAGE;
        LOGGER.warn(errorMessage);
        throw new ApexEventRuntimeException(errorMessage);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.policy.apex.service.
     * parameters.eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        String errorMessage = CDS_CONSUMER_ERROR_MESSAGE;
        LOGGER.warn(errorMessage);
        throw new ApexEventRuntimeException(errorMessage);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#stop()
     */
    @Override
    public void stop() {
        String errorMessage = CDS_CONSUMER_ERROR_MESSAGE;
        LOGGER.warn(errorMessage);
        throw new ApexEventRuntimeException(errorMessage);
    }
}
