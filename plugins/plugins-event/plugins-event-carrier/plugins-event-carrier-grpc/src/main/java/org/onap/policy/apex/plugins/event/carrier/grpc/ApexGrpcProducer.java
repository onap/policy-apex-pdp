/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.onap.ccsdk.cds.controllerblueprints.common.api.EventType;
import org.onap.ccsdk.cds.controllerblueprints.common.api.Status;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceInput;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceInput.Builder;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceOutput;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventProducer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.cds.api.CdsProcessorListener;
import org.onap.policy.cds.client.CdsProcessorGrpcClient;
import org.onap.policy.cds.properties.CdsServerProperties;
import org.onap.policy.controlloop.actor.cds.constants.CdsActorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex gRPC plugin that manages to send a GRPC request.
 *
 * @author Ajith Sreekumar(ajith.sreekumar@est.tech)
 *
 */
public class ApexGrpcProducer extends ApexPluginsEventProducer implements CdsProcessorListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexGrpcProducer.class);

    private CdsServerProperties props;
    // The gRPC client
    private CdsProcessorGrpcClient client;

    private AtomicReference<ExecutionServiceOutput> cdsResponse = new AtomicReference<>();

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
        throws ApexEventException {
        this.name = producerName;

        // Check and get the gRPC Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof GrpcCarrierTechnologyParameters)) {
            final String errorMessage =
                "Specified producer properties are not applicable to gRPC producer (" + this.name + ")";
            throw new ApexEventException(errorMessage);
        }
        GrpcCarrierTechnologyParameters grpcProducerProperties =
            (GrpcCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        client = makeGrpcClient(grpcProducerProperties);
    }

    private CdsProcessorGrpcClient makeGrpcClient(GrpcCarrierTechnologyParameters grpcProducerProperties) {
        props = new CdsServerProperties();
        props.setHost(grpcProducerProperties.getHost());
        props.setPort(grpcProducerProperties.getPort());
        props.setUsername(grpcProducerProperties.getUsername());
        props.setPassword(grpcProducerProperties.getPassword());
        props.setTimeout(grpcProducerProperties.getTimeout());

        return new CdsProcessorGrpcClient(this, props);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void sendEvent(final long executionId, final Properties executionProperties, final String eventName,
        final Object event) {

        ExecutionServiceInput executionServiceInput;
        Builder builder = ExecutionServiceInput.newBuilder();
        try {
            JsonFormat.parser().ignoringUnknownFields().merge((String) event, builder);
            executionServiceInput = builder.build();
        } catch (InvalidProtocolBufferException e) {
            throw new ApexEventRuntimeException(
                "Incoming Event cannot be converted to ExecutionServiceInput type for gRPC request." + e.getMessage());
        }
        try {
            CountDownLatch countDownLatch = client.sendRequest(executionServiceInput);
            if (!countDownLatch.await(props.getTimeout(), TimeUnit.SECONDS)) {
                cdsResponse.set(ExecutionServiceOutput.newBuilder().setStatus(Status.newBuilder()
                    .setErrorMessage(CdsActorConstants.TIMED_OUT).setEventType(EventType.EVENT_COMPONENT_FAILURE))
                    .build());
                LOGGER.error("gRPC Request timed out.");
            }
        } catch (InterruptedException e) {
            cdsResponse.set(ExecutionServiceOutput.newBuilder().setStatus(Status.newBuilder()
                .setErrorMessage(CdsActorConstants.INTERRUPTED).setEventType(EventType.EVENT_COMPONENT_FAILURE))
                .build());
            Thread.currentThread().interrupt();
        }

        if (!EventType.EVENT_COMPONENT_EXECUTED.equals(cdsResponse.get().getStatus().getEventType())) {
            String errorMessage = "Sending event \"" + eventName + "\" by " + this.name + " to CDS failed, "
                + "response from CDS:\n" + cdsResponse.get();
            throw new ApexEventRuntimeException(errorMessage);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        client.close();
    }

    @Override
    public void onMessage(ExecutionServiceOutput message) {
        LOGGER.info("Received notification from CDS: {}", message);
        cdsResponse.set(message);
    }

    @Override
    public void onError(Throwable throwable) {
        String errorMsg = throwable.getLocalizedMessage();
        cdsResponse.set(ExecutionServiceOutput.newBuilder()
            .setStatus(Status.newBuilder().setErrorMessage(errorMsg).setEventType(EventType.EVENT_COMPONENT_FAILURE))
            .build());
        LOGGER.error("Failed processing blueprint {} {}", errorMsg, throwable);
    }
}
