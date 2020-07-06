/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.service.parameters.carriertechnology.RestPluginCarrierTechnologyParameters;

// @formatter:off
/**
 * Apex parameters for REST as an event carrier technology with Apex issuing a REST request and receiving a REST
 * response.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>url: The URL that the Apex Rest Requestor will connect to over REST for REST request sending.
 * This parameter is mandatory.
 * <li>httpMethod: The HTTP method to use when making requests over REST, legal values are GET (default),
 *  POST, PUT, and DELETE.
 * <li>httpHeaders, the HTTP headers to send on REST requests, optional parameter, defaults to none.
 * <li>httpCodeFilter: a regular expression filter for returned HTTP codes, if the returned HTTP code passes this
 * filter, then the request is assumed to have succeeded by the plugin, optional, defaults to allowing 2xx codes
 * through, that is a regular expression of "[2][0-9][0-9]"
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
//@formatter:on
@Getter
@Setter
public class RestRequestorCarrierTechnologyParameters extends RestPluginCarrierTechnologyParameters {

    /** The default HTTP method for request events. */
    public static final HttpMethod DEFAULT_REQUESTOR_HTTP_METHOD = HttpMethod.GET;

    /** The default timeout for REST requests. */
    public static final long DEFAULT_REST_REQUEST_TIMEOUT = 500;

    /**
     * Constructor to create a REST carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public RestRequestorCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the REST requestor carrier technology
        this.setLabel("RESTREQUESTOR");
        this.setEventProducerPluginClass(ApexRestRequestorProducer.class.getName());
        this.setEventConsumerPluginClass(ApexRestRequestorConsumer.class.getName());
    }
}
