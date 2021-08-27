/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.grpc;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;

// @formatter:off
/**
 * Apex parameters for gRPC as an event carrier technology.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>host: The host on which CDS is running. This parameter is mandatory
 * <li>port: The port on the CDS host to connect to for CDS. This parameter is mandatory.
 * <li>username: The username for basic authentication to connect to CDS. This parameter is mandatory.
 * <li>password: The password for basic authentication to connect to CDS. This parameter is mandatory.
 * <li>timeout: The timeout in seconds for CDS requests. This parameter is mandatory.
 * </ol>
 *
 * @author Ajith Sreekumar(ajith.sreekumar@est.tech)
 */
//@formatter:on
@Getter
@Setter
public class GrpcCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    private static final int MIN_USER_PORT =  1024;
    private static final int MAX_USER_PORT = 65535;

    /** The label of this carrier technology. */
    public static final String GRPC_CARRIER_TECHNOLOGY_LABEL = "GRPC";

    /** The producer plugin class for the grpc carrier technology. */
    public static final String GRPC_EVENT_PRODUCER_PLUGIN_CLASS = ApexGrpcProducer.class.getName();

    /** The consumer plugin class for the gRPC carrier technology. */
    public static final String GRPC_EVENT_CONSUMER_PLUGIN_CLASS = ApexGrpcConsumer.class.getName();

    private int timeout;
    private int port;
    private String host;
    private String username;
    private String password;


    /**
     * Constructor to create a gRPC carrier technology parameters instance and register the instance with the
     * parameter service.
     */
    public GrpcCarrierTechnologyParameters() {
        super();
        // Set the carrier technology properties for the gRPC carrier technology
        this.setLabel(GRPC_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(GRPC_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(GRPC_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /**
     * The method validates the gRPC parameters. Host details are specified as parameters only for a gRPC producer.
     *
     * @param isProducer if the parameters specified are for the gRPC producer or consumer
     * @throws ApexEventException exception thrown when invalid parameters are provided
     */
    public void validateGrpcParameters(boolean isProducer) throws ApexEventException {
        var errorMessage = new StringBuilder();
        if (isProducer) {
            if (timeout < 1) {
                errorMessage.append("timeout should have a positive value.\n");
            }
            if (MIN_USER_PORT > port || MAX_USER_PORT < port) {
                errorMessage.append("port range should be between ").append(MIN_USER_PORT).append(" and ")
                .append(MAX_USER_PORT).append("\n");
            }
            if (StringUtils.isEmpty(host)) {
                errorMessage.append("host should be specified.\n");
            }
            if (StringUtils.isEmpty(username)) {
                errorMessage.append("username should be specified.\n");
            }
            if (StringUtils.isEmpty(password)) {
                errorMessage.append("password should be specified.\n");
            }
        }
        if (errorMessage.length() > 0) {
            errorMessage.insert(0, "Issues in specifying gRPC Producer parameters:\n");
            throw new ApexEventException(errorMessage.toString());
        }
    }
}
