/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 - 2019 Bell Canada.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.plugins.event.carrier.cds;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

public class BasicAuthClientInterceptor implements ClientInterceptor {

    private CdsCarrierTechnologyParameters parameters;

    public BasicAuthClientInterceptor(final CdsCarrierTechnologyParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public <I, O> ClientCall<I, O> interceptCall(MethodDescriptor<I, O> method,
            CallOptions callOptions, Channel channel) {

        Key<String> authHeader = Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

        return new ForwardingClientCall.SimpleForwardingClientCall<I, O>(channel.newCall(method, callOptions)) {
            @Override
            public void start(Listener<O> responseListener, Metadata headers) {
                headers.put(authHeader, parameters.getBasicAuth());
                super.start(responseListener, headers);
            }
        };
    }
}
