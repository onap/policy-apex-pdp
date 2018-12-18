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

import java.util.Arrays;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;

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
    /** The supported HTTP methods. */
    public enum HttpMethod {
        GET, PUT, POST, DELETE
    }

    /** The label of this carrier technology. */
    public static final String RESTCLIENT_CARRIER_TECHNOLOGY_LABEL = "RESTCLIENT";

    /** The producer plugin class for the REST carrier technology. */
    public static final String RESTCLIENT_EVENT_PRODUCER_PLUGIN_CLASS = ApexRestClientProducer.class.getCanonicalName();

    /** The consumer plugin class for the REST carrier technology. */
    public static final String RESTCLIENT_EVENT_CONSUMER_PLUGIN_CLASS = ApexRestClientConsumer.class.getCanonicalName();

    // Commonly occurring strings
    private static final String HTTP_HEADERS = "httpHeaders";

    private String url = null;
    private HttpMethod httpMethod = null;
    private String[][] httpHeaders = null;

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
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * Sets the HTTP method to use for the REST request.
     *
     * @param httpMethod the HTTP method
     */
    public void setHttpMethod(final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Check if http headers have been set for the REST request.
     *
     * @return true if headers have been set
     */
    public boolean checkHttpHeadersSet() {
        return httpHeaders != null && httpHeaders.length > 0;
    }

    /**
     * Gets the http headers for the REST request.
     *
     * @return the headers
     */
    public String[][] getHttpHeaders() {
        return httpHeaders;
    }

    /**
     * Gets the http headers for the REST request as a multivalued map.
     *
     * @return the headers
     */
    public MultivaluedMap<String, Object> getHttpHeadersAsMultivaluedMap() {
        if (httpHeaders == null) {
            return null;
        }

        // Load the HTTP headers into the map
        MultivaluedMap<String, Object> httpHeaderMap = new MultivaluedHashMap<>();

        for (String[] httpHeader : httpHeaders) {
            httpHeaderMap.putSingle(httpHeader[0], httpHeader[1]);
        }

        return httpHeaderMap;
    }

    /**
     * Sets the header for the REST request.
     *
     * @param httpHeaders the incoming HTTP headers
     */
    public void setHttpHeaders(final String[][] httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        // Check if the URL has been set for event output
        if (getUrl() == null) {
            result.setResult("url", ValidationStatus.INVALID, "no URL has been set for event sending on REST client");
        }

        if (httpHeaders == null) {
            return result;
        }

        for (String[] httpHeader : httpHeaders) {
            if (httpHeader == null) {
                result.setResult(HTTP_HEADERS, ValidationStatus.INVALID, "HTTP header array entry is null");
            } else if (httpHeader.length != 2) {
                result.setResult(HTTP_HEADERS, ValidationStatus.INVALID,
                                "HTTP header array entries must have one key and one value: "
                                                + Arrays.deepToString(httpHeader));
            } else if (!ParameterValidationUtils.validateStringParameter(httpHeader[0])) {
                result.setResult(HTTP_HEADERS, ValidationStatus.INVALID,
                                "HTTP header key is null or blank: " + Arrays.deepToString(httpHeader));
            } else if (!ParameterValidationUtils.validateStringParameter(httpHeader[1])) {
                result.setResult(HTTP_HEADERS, ValidationStatus.INVALID,
                                "HTTP header value is null or blank: " + Arrays.deepToString(httpHeader));
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RestClientCarrierTechnologyParameters [url=" + url + ", httpMethod=" + httpMethod + ", httpHeaders="
                        + Arrays.deepToString(httpHeaders) + "]";
    }
}
