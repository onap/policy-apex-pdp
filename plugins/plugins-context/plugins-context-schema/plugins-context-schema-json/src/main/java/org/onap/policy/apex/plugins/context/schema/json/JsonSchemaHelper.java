/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.plugins.context.schema.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.worldturner.medeia.api.SchemaSource;
import com.worldturner.medeia.api.StringInputSource;
import com.worldturner.medeia.api.StringSchemaSource;
import com.worldturner.medeia.api.gson.MedeiaGsonApi;
import com.worldturner.medeia.schema.validation.SchemaValidator;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.impl.schema.AbstractSchemaHelper;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;

/**
 * This class is the implementation of the {@link org.onap.policy.apex.context.SchemaHelper} interface for JSON schema.
 */
public class JsonSchemaHelper extends AbstractSchemaHelper {

    private static final Gson gson = new Gson();

    private MedeiaGsonApi api = new MedeiaGsonApi();
    private SchemaValidator validator;

    @Override
    public void init(final AxKey userKey, final AxContextSchema schema) {
        super.init(userKey, schema);

        try {
            SchemaSource source = new StringSchemaSource(schema.getSchema());
            validator = api.loadSchema(source);
        } catch (final Exception e) {
            final String resultSting = userKey.getId() + ": json context schema \"" + schema.getId()
                + "\" schema is invalid, schema: " + schema.getSchema();
            throw new ContextRuntimeException(resultSting, e);
        }
    }

    @Override
    public Object createNewInstance(final String stringValue) {
        return unmarshal(stringValue);
    }

    @Override
    public Object createNewInstance(final Object incomingObject) {
        if (incomingObject instanceof JsonElement) {
            final var elementJsonString = gson.toJson((JsonElement) incomingObject);

            return createNewInstance(elementJsonString);
        } else {
            final var returnString =
                getUserKey().getId() + ": the object \"" + incomingObject + "\" is not an instance of JsonObject";
            throw new ContextRuntimeException(returnString);
        }
    }

    @Override
    public Object unmarshal(Object object) {
        // If an object is already in the correct format, just carry on
        if (passThroughObject(object)) {
            return object;
        }
        var objectString = (String) object;
        JsonReader reader = api.createJsonReader(validator, new StringInputSource(objectString));
        return gson.fromJson(reader, Object.class);
    }

    @Override
    public String marshal2String(Object schemaObject) {
        StringWriter stringWriter = new StringWriter();
        validateAndDecode(schemaObject, stringWriter);
        return stringWriter.toString();
    }

    @Override
    public Object marshal2Object(Object schemaObject) {
        return validateAndDecode(schemaObject, new StringWriter());
    }

    private JsonElement validateAndDecode(Object schemaObject, StringWriter stringWriter) {
        JsonWriter jsonWriter = api.createJsonWriter(validator, stringWriter);
        jsonWriter.setIndent("  "); // to enable pretty print
        JsonElement jsonObj = gson.toJsonTree(schemaObject);
        gson.toJson(jsonObj, jsonWriter);
        return jsonObj;
    }

    /**
     * Check if we can pass this object straight through encoding or decoding.
     *
     * @param object the object to check
     * @return true if it's a straight pass through
     */
    private boolean passThroughObject(final Object object) {
        return (object instanceof Map || object instanceof List);
    }
}
