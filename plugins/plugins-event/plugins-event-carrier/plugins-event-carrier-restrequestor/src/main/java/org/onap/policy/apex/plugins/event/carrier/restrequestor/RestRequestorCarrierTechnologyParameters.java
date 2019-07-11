/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;

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
 * <li>restRequestTimeout: The time in milliseconds to wait for a REST request to complete.
 * <li>restRequestHeader: The necessary header needed
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
//@formatter:on
public class RestRequestorCarrierTechnologyParameters extends CarrierTechnologyParameters {
    /** The supported HTTP methods. */
    public enum HttpMethod {
        GET, PUT, POST, DELETE
    }

    /** The label of this carrier technology. */
    public static final String RESTREQUESTOR_CARRIER_TECHNOLOGY_LABEL = "RESTREQUESTOR";

    /** The producer plugin class for the REST carrier technology. */
    public static final String RESTREQUSTOR_EVENT_PRODUCER_PLUGIN_CLASS = ApexRestRequestorProducer.class
                    .getCanonicalName();

    /** The consumer plugin class for the REST carrier technology. */
    public static final String RESTREQUSTOR_EVENT_CONSUMER_PLUGIN_CLASS = ApexRestRequestorConsumer.class
                    .getCanonicalName();

    /** The default HTTP method for request events. */
    public static final HttpMethod DEFAULT_REQUESTOR_HTTP_METHOD = HttpMethod.GET;

    /** The default timeout for REST requests. */
    public static final long DEFAULT_REST_REQUEST_TIMEOUT = 500;

    // Commonly occurring strings
    private static final String HTTP_HEADERS = "httpHeaders";

    private String url = null;
    private HttpMethod httpMethod = null;
    private String[][] httpHeaders = null;

    private static final Pattern patternProperKey = Pattern.compile("(?<=\\{)[^}]*(?=\\})");
    private static final Pattern patternErrorKey = Pattern.compile(
        "(\\{[^\\{}]*.?\\{)|(\\{[^\\{}]*$)|(\\}[^\\{}]*.?\\})|(^[^\\{}]*.?\\})|\\{\\s*\\}");

    /**
     * Constructor to create a REST carrier technology parameters instance and register the instance with the
     * parameter service.
     */
    public RestRequestorCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the web socket carrier technology
        this.setLabel(RESTREQUESTOR_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(RESTREQUSTOR_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(RESTREQUSTOR_EVENT_CONSUMER_PLUGIN_CLASS);
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
     * @return true if headers have beenset
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
     * Get the tag for the REST Producer Properties.
     *
     * @return set of the tags
     */
    public Set<String> getKeysFromUrl() {
        Matcher matcher = patternProperKey.matcher(this.url);
        Set<String> key = new HashSet<>();
        while (matcher.find()) {
            key.add(matcher.group());
        }
        return key;
    }

    /**
     * Validate tags in url.
     * http://www.blah.com/{par1/somethingelse (Missing end tag) use  {[^\\{}]*$
     * http://www.blah.com/{par1/{some}thingelse (Missing end tag2) use {[^}]*{
     * http://www.blah.com/{par1}/some}thingelse (Missing start tag1) use }[^{}]*.}
     * http://www.blah.com/par1}/somethingelse (Missing start tag2) use }[^{}]*}
     * http://www.blah.com/{}/somethingelse (Empty tag) use {[\s]*}
     *
     * @return if url is legal
     */
    public boolean validateTagInUrl() {
        // Check url tag syntax error
        Matcher matcher = patternErrorKey.matcher(this.url);
        return (!matcher.find());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

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

        if (!validateTagInUrl()) {
            result.setResult("url", ValidationStatus.INVALID,
                "no proper URL has been set for event sending on REST client");
        }


        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "RESTRequestorCarrierTechnologyParameters [url=" + url + ", httpMethod=" + httpMethod + ", httpHeaders="
                        + Arrays.deepToString(httpHeaders) + "]";
    }
}
