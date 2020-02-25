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

package org.onap.policy.apex.service.parameters.carriertechnology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @formatter:off
/**
 * Apex plugin parameters for REST as an event carrier technology with Apex.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>url: The URL that the Apex Rest client will connect to over REST for event reception or event sending. This
 * parameter is mandatory.
 * <li>httpMethod: The HTTP method to use when sending events over REST, legal values are POST (default) and PUT. When
 * receiving events, the REST client plugin always uses the HTTP GET method.
 * <li>httpHeaders, the HTTP headers to send on REST requests, optional parameter, defaults to none.
 * <li>httpCodeFilter: a regular expression filter for returned HTTP codes, if the returned HTTP code passes this
 * filter, then the request is assumed to have succeeded by the plugin, optional, defaults to allowing 2xx codes
 * through, that is a regular expression of "[2][0-9][0-9]"
 * </ol>
 *
 * @author Ning Xi(ning.xi@ericsson.com)
 */
//@formatter:on
@Setter
@Getter
public class RestPluginCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RestPluginCarrierTechnologyParameters.class);

    /** The supported HTTP methods. */
    public enum HttpMethod {
        GET,
        PUT,
        POST,
        DELETE
    }

    /** The default HTTP code filter, allows 2xx HTTP codes through. */
    public static final String DEFAULT_HTTP_CODE_FILTER = "[2][0-9][0-9]";

    // Commonly occurring strings
    private static final String HTTP_HEADERS = "httpHeaders";
    private static final String HTTP_CODE_FILTER = "httpCodeFilter";

    // Regular expression patterns for finding and checking keys in URLs
    private static final Pattern patternProperKey = Pattern.compile("(?<=\\{)[^}]*(?=\\})");
    protected static final Pattern patternErrorKey =
            Pattern.compile("(\\{[^\\{}]*.?\\{)|(\\{[^\\{}]*$)|(\\}[^\\{}]*.?\\})|(^[^\\{}]*.?\\})|\\{\\s*\\}");

    // variable
    protected String url = null;
    protected HttpMethod httpMethod = null;
    protected String[][] httpHeaders = null;
    protected String httpCodeFilter = DEFAULT_HTTP_CODE_FILTER;

    /**
     * Constructor to create a REST carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public RestPluginCarrierTechnologyParameters() {
        super();
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
        Matcher matcher = patternProperKey.matcher(getUrl());
        Set<String> key = new HashSet<>();
        while (matcher.find()) {
            key.add(matcher.group());
        }
        return key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        GroupValidationResult result = super.validate();

        validateUrl(result);
        validateHttpHeaders(result);

        return validateHttpCodeFilter(result);
    }

    // @formatter:off
    /**
     * Validate the URL.
     *
     * <p>Checks:
     * http://www.blah.com/{par1/somethingelse (Missing end tag) use  {[^\\{}]*$
     * http://www.blah.com/{par1/{some}thingelse (Nested tag) use {[^}]*{
     * http://www.blah.com/{par1}/some}thingelse (Missing start tag1) use }[^{}]*.}
     * http://www.blah.com/par1}/somethingelse (Missing start tag2) use }[^{}]*}
     * http://www.blah.com/{}/somethingelse (Empty tag) use {[\s]*}
     * @param result the result of the validation
     */
    // @formatter:on
    public GroupValidationResult validateUrl(final GroupValidationResult result) {
        // Check if the URL has been set for event output
        String urlErrorMessage = "no URL has been set for event sending on " + getLabel();
        if (getUrl() == null) {
            result.setResult("url", ValidationStatus.INVALID, urlErrorMessage);
            return result;
        }

        Matcher matcher = patternErrorKey.matcher(getUrl());
        if (matcher.find()) {
            result.setResult("url", ValidationStatus.INVALID, urlErrorMessage);
        }

        return result;
    }

    /**
     * Validate the HTTP headers.
     *
     * @param result the result of the validation
     */
    private GroupValidationResult validateHttpHeaders(final GroupValidationResult result) {
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
     * Validate the HTTP code filter.
     *
     * @param result the result of the validation
     */
    public GroupValidationResult validateHttpCodeFilter(final GroupValidationResult result) {
        if (httpCodeFilter == null) {
            httpCodeFilter = DEFAULT_HTTP_CODE_FILTER;

        } else if (StringUtils.isBlank(httpCodeFilter)) {
            result.setResult(HTTP_CODE_FILTER, ValidationStatus.INVALID,
                    "HTTP code filter must be specified as a three digit regular expression");
        } else {
            try {
                Pattern.compile(httpCodeFilter);
            } catch (PatternSyntaxException pse) {
                String message =
                        "Invalid HTTP code filter, the filter must be specified as a three digit regular expression: "
                                + pse.getMessage();
                result.setResult(HTTP_CODE_FILTER, ValidationStatus.INVALID, message);
                LOGGER.debug(message, pse);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return getLabel() + "CarrierTechnologyParameters [url=" + url + ", httpMethod=" + httpMethod + ", httpHeaders="
                + Arrays.deepToString(httpHeaders) + ", httpCodeFilter=" + httpCodeFilter + "]";
    }
}
