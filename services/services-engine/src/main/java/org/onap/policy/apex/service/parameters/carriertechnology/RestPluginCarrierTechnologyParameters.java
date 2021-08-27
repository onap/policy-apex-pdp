/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

package org.onap.policy.apex.service.parameters.carriertechnology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ObjectValidationResult;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.models.base.Validated;
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
@NoArgsConstructor
public class RestPluginCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RestPluginCarrierTechnologyParameters.class);

    /** The supported HTTP methods. */
    // @formatter:off
    public enum HttpMethod {
        GET,
        PUT,
        POST,
        DELETE
    }
    // @formatter:on

    /** The default HTTP code filter, allows 2xx HTTP codes through. */
    public static final String DEFAULT_HTTP_CODE_FILTER = "[2][0-9][0-9]";

    // Commonly occurring strings
    private static final String HTTP_HEADERS = "httpHeaders";
    private static final String HTTP_CODE_FILTER = "httpCodeFilter";

    // Regular expression patterns for finding and checking keys in URLs
    private static final Pattern patternProperKey = Pattern.compile("(?<=\\{)[^}]*(?=\\})");
    protected static final Pattern patternErrorKey = Pattern
        .compile("(\\{[^\\{}]*.?\\{)|(\\{[^\\{}]*$)|(\\}[^\\{}]*.?\\})|(^[^\\{}]*.?\\})|\\{\\s*\\}");

    // variable
    protected String url = null;
    protected HttpMethod httpMethod = null;
    protected String[][] httpHeaders = null;
    protected String httpCodeFilter = DEFAULT_HTTP_CODE_FILTER;

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
    public BeanValidationResult validate() {
        BeanValidationResult result = super.validate();

        result.addResult(validateUrl());
        result.addResult(validateHttpHeaders());
        result.addResult(validateHttpCodeFilter());

        return result;
    }

    // @formatter:off
    /**
     * Validate the URL.
     *
     * <p/>Checks:
     * <br/>http://www.blah.com/{par1/somethingelse (Missing end tag) use  {[^\\{}]*$
     * <br/>http://www.blah.com/{par1/{some}thingelse (Nested tag) use {[^}]*{
     * <br/>http://www.blah.com/{par1}/some}thingelse (Missing start tag1) use }[^{}]*.}
     * <br/>http://www.blah.com/par1}/somethingelse (Missing start tag2) use }[^{}]*}
     * <br/>http://www.blah.com/{}/somethingelse (Empty tag) use {[\s]*}
     */
    // @formatter:on
    public ValidationResult validateUrl() {
        // The URL may be optional so existence must be checked in the plugin code
        String url2 = getUrl();
        if (url2 == null) {
            return null;
        }

        var matcher = patternErrorKey.matcher(url2);
        if (matcher.find()) {
            final String urlInvalidMessage = "invalid URL has been set for event sending on " + getLabel();
            return new ObjectValidationResult("url", url2, ValidationStatus.INVALID, urlInvalidMessage);
        }

        return null;
    }

    /**
     * Validate the HTTP headers.
     *
     * @return the result of the validation
     */
    private ValidationResult validateHttpHeaders() {
        if (httpHeaders == null) {
            return null;
        }

        var result = new BeanValidationResult(HTTP_HEADERS, httpHeaders);

        var item = 0;
        for (String[] httpHeader : httpHeaders) {
            final String label = "entry " + (item++);
            final List<String> value = (httpHeader == null ? null : Arrays.asList(httpHeader));
            var result2 = new BeanValidationResult(label, value);

            if (httpHeader == null) {
                // note: add to result, not result2
                result.addResult(label, null, ValidationStatus.INVALID, Validated.IS_NULL);

            } else if (httpHeader.length != 2) {
                // note: add to result, not result2
                result.addResult(label, value, ValidationStatus.INVALID, "must have one key and one value");

            } else if (!ParameterValidationUtils.validateStringParameter(httpHeader[0])) {
                result2.addResult("key", httpHeader[0], ValidationStatus.INVALID, Validated.IS_BLANK);

            } else if (!ParameterValidationUtils.validateStringParameter(httpHeader[1])) {
                result2.addResult("value", httpHeader[1], ValidationStatus.INVALID, Validated.IS_BLANK);
            }

            result.addResult(result2);
        }

        return result;
    }

    /**
     * Validate the HTTP code filter.
     */
    public ValidationResult validateHttpCodeFilter() {
        if (httpCodeFilter == null) {
            httpCodeFilter = DEFAULT_HTTP_CODE_FILTER;

        } else if (StringUtils.isBlank(httpCodeFilter)) {
            return new ObjectValidationResult(HTTP_CODE_FILTER, httpCodeFilter, ValidationStatus.INVALID,
                "must be a three digit regular expression");
        } else {
            try {
                Pattern.compile(httpCodeFilter);
            } catch (PatternSyntaxException pse) {
                LOGGER.debug("Invalid HTTP code filter", pse);
                String message = "Invalid HTTP code filter, the filter must be specified as a three digit "
                    + "regular expression: " + pse.getMessage();
                return new ObjectValidationResult(HTTP_CODE_FILTER, httpCodeFilter, ValidationStatus.INVALID,
                                message);
            }
        }

        return null;
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
