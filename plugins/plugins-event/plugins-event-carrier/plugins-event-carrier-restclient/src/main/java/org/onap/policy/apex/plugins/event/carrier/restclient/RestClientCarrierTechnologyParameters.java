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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Apex parameters for REST as an event carrier technology with Apex as a REST client.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>url: The URL that the Apex Rest client will connect to over REST for event reception or event sending. This
 * parameter is mandatory.
 * <li>httpMethod: The HTTP method to use when sending events over REST, legal values are POST (default) and PUT. When
 * receiving events, the REST client plugin always uses the HTTP GET method.
 * </ol>
 *
 * @author Joss Armstrong (joss.armstrong@ericsson.com)
 */
public class RestClientCarrierTechnologyParameters extends CarrierTechnologyParameters {

    /** The label of this carrier technology. */
    public static final String RESTCLIENT_CARRIER_TECHNOLOGY_LABEL = "RESTCLIENT";

    /** The producer plugin class for the REST carrier technology. */
    public static final String RESTCLIENT_EVENT_PRODUCER_PLUGIN_CLASS = ApexRestClientProducer.class.getCanonicalName();

    /** The consumer plugin class for the REST carrier technology. */
    public static final String RESTCLIENT_EVENT_CONSUMER_PLUGIN_CLASS = ApexRestClientConsumer.class.getCanonicalName();

    /** The default HTTP method for output of events. */
    public static final String DEFAULT_PRODUCER_HTTP_METHOD = "POST";

    /** The HTTP method for input of events. */
    public static final String CONSUMER_HTTP_METHOD = "GET";

    private String url = null;
    private String httpMethod = null;

    /**
     * Constructor to create a REST carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public RestClientCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the web socket carrier technology
        this.setLabel(RESTCLIENT_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(RESTCLIENT_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(RESTCLIENT_EVENT_CONSUMER_PLUGIN_CLASS);

    }

    /**
     * Gets the URL for the REST request.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL for the REST request.
     *
     * @param incomingUrl the URL
     */
    public void setUrl(final String incomingUrl) {
        this.url = incomingUrl;
    }

    /**
     * Gets the HTTP method to use for the REST request.
     *
     * @return the HTTP method
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * Sets the HTTP method to use for the REST request.
     *
     * @param httpMethod the HTTP method
     */
    public void setHttpMethod(final String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RESTClientCarrierTechnologyParameters [url=" + url + ", httpMethod=" + httpMethod + "]";
    }

    /*
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        // Check if the URL has been set for event output
        if (getUrl() == null) {
            result.setResult("url", ValidationStatus.INVALID, "no URL has been set for event sending on REST client");
        }

        return result;
    }
}
