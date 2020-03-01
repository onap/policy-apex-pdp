/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.grpc;

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * This class implements an Apex gRPC consumer. It is not expected to receive events using gRPC.
 * So, initializing a gRPC consumer will result in error.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexGrpcConsumer extends ApexPluginsEventConsumer {

    private static final String GRPC_CONSUMER_ERROR_MSG =
        "A gRPC Consumer may not be specified. Only sending events is possible using gRPC";

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
        final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        throw new ApexEventException(GRPC_CONSUMER_ERROR_MSG);
    }

    @Override
    public void run() {
        throw new ApexEventRuntimeException(GRPC_CONSUMER_ERROR_MSG);
    }

    @Override
    public void start() {
        throw new ApexEventRuntimeException(GRPC_CONSUMER_ERROR_MSG);
    }

    @Override
    public void stop() {
        throw new ApexEventRuntimeException(GRPC_CONSUMER_ERROR_MSG);
    }

    @Override
    public PeeredReference getPeeredReference(EventHandlerPeeredMode peeredMode) {
        throw new ApexEventRuntimeException(GRPC_CONSUMER_ERROR_MSG);
    }

    @Override
    public void setPeeredReference(EventHandlerPeeredMode peeredMode, PeeredReference peeredReference) {
        throw new ApexEventRuntimeException(GRPC_CONSUMER_ERROR_MSG);
    }
}
