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

package org.onap.policy.apex.client.editor.rest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.StringReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.onap.policy.apex.client.editor.rest.bean.BeanBase;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;

/**
 * Utilities for handling RESTful communication for Apex.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class RestUtils {

    /**
     * Constructor, block inheritance.
     */
    private RestUtils() {}

    /**
     * HTTP POST requests can't send nulls so we interpret blanks as nulls.
     *
     * @param parameter the parameter to convert from blank to null
     * @return null if the parameter us blank, otherwise the original parameter
     */
    private static String blank2null(final String parameter) {
        return (parameter.length() == 0 ? null : parameter);
    }

    /**
     * HTTP POST requests can't send nulls so we interpret blanks as nulls.
     *
     * @param val the val
     * @return null if the parameter us blank, otherwise the original parameter
     */
    private static JsonElement blank2null(final JsonElement val) {
        if (val == null) {
            return JsonNull.INSTANCE;
        }
        if (val.isJsonPrimitive() && ((JsonPrimitive) val).isString()) {
            final String v = ((JsonPrimitive) val).getAsString();
            if (v == null || v.equals("")) {
                return JsonNull.INSTANCE;
            }
        }
        if (val.isJsonArray()) {
            final JsonArray arr = val.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, blank2null(arr.get(i)));
            }
        }
        if (val.isJsonObject()) {
            final JsonObject o = val.getAsJsonObject();
            for (final Entry<String, JsonElement> e : o.entrySet()) {
                e.setValue(blank2null(e.getValue()));
            }
        }
        return val;
    }

    /**
     * Apex HTTP PUT requests send simple single level JSON strings, this method reads those strings into a map.
     *
     * @param jsonString the incoming JSON string
     * @return a map of the JSON strings
     */
    public static Map<String, String> getJSONParameters(final String jsonString) {
        final GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls().enableComplexMapKeySerialization();
        final JsonObject jsonObject = gb.create().fromJson(jsonString, JsonObject.class);

        final Map<String, String> jsonMap = new TreeMap<>();
        for (final Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
            jsonMap.put(jsonEntry.getKey(), (jsonEntry.getValue() == JsonNull.INSTANCE ? null
                    : blank2null(jsonEntry.getValue().getAsString())));
        }
        return jsonMap;
    }

    /**
     * Apex HTTP PUT requests send simple single level JSON strings, this method reads those strings into a map.
     *
     * @param <CLZ> the generic type
     * @param jsonString the incoming JSON string
     * @param clz the clz
     * @return a map of the JSON strings
     */
    public static <CLZ extends BeanBase> CLZ getJSONParameters(final String jsonString, final Class<CLZ> clz) {
        final GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls().enableComplexMapKeySerialization();
        final JsonObject jsonObject = gb.create().fromJson(jsonString, JsonObject.class);

        for (final Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
            final JsonElement val = jsonEntry.getValue();
            jsonEntry.setValue(blank2null(val));
        }
        final CLZ ret = gb.create().fromJson(jsonObject, clz);
        return ret;
    }

    // Regular expressions for checking input types
    private static final String XML_INPUT_TYPE_REGEXP = "^\\s*<\\?xml.*>\\s*"; // (starts with <?xml...>
    private static final String JSON_INPUT_TYPE_REGEXP = "^\\s*[\\(\\{\\[][\\s+\\S]*[\\)\\}\\]]"; // starts with 
    // some kind of bracket [ or ( or {, then has something, then has bracket

    /**
     * Gets the concept from JSON.
     *
     * @param <CLZ> the generic type
     * @param jsonString the json string
     * @param clz the clz
     * @return the concept from JSON
     * @throws JAXBException the JAXB exception
     */
    public static <CLZ extends AxConcept> CLZ getConceptFromJSON(final String jsonString, final Class<CLZ> clz)
            throws JAXBException {
        Unmarshaller unmarshaller = null;
        final JAXBContext jaxbContext = JAXBContext.newInstance(clz);
        unmarshaller = jaxbContext.createUnmarshaller();
        if (jsonString.matches(JSON_INPUT_TYPE_REGEXP)) {
            unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        } else if (jsonString.matches(XML_INPUT_TYPE_REGEXP)) {
            unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML);
        } else {
            return null;
        }
        final StreamSource source = new StreamSource(new StringReader(jsonString));
        final JAXBElement<CLZ> rootElement = unmarshaller.unmarshal(source, clz);
        final CLZ apexConcept = rootElement.getValue();
        return apexConcept;

    }

    /**
     * Gets the JSO nfrom concept.
     *
     * @param object the object
     * @return the JSO nfrom concept
     */
    public static String getJSONfromConcept(final Object object) {
        final GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls().enableComplexMapKeySerialization();
        final String jsonObject = gb.create().toJson(object);
        return jsonObject;
    }

}
