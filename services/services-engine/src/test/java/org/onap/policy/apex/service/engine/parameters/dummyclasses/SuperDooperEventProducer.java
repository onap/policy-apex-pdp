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

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class SuperDooperEventProducer implements ApexEventProducer {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(SuperDooperEventProducer.class);

    private String name;

    public SuperDooperEventProducer() {}

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.apex.service.engine.event.ApexEventProducer#init(java.lang.String,
     * com.ericsson.apex.service.parameters.producer.ProducerParameters)
     */
    @Override
    public void init(final String name, final EventHandlerParameters producerParameters) throws ApexEventException {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.apex.service.engine.event.ApexEventProducer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.apex.service.engine.event.ApexEventProducer#getPeeredReference(com.ericsson.apex
     * .service.parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.apex.service.engine.event.ApexEventProducer#setPeeredReference(com.ericsson.apex
     * .service.parameters.eventhandler.EventHandlerPeeredMode,
     * com.ericsson.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {}


    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.apex.service.engine.event.ApexEventProducer#sendEvent(long,
     * java.lang.String, java.lang.Object)
     */
    @Override
    public void sendEvent(final long executionId, final String eventName, final Object event) {
        LOGGER.info("Sending Event: " + this.getClass().getCanonicalName() + ":" + this.name + " ... event ("
                + eventName + ") : " + event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.apex.service.engine.event.ApexEventProducer#stop()
     */
    @Override
    public void stop() {}
}
