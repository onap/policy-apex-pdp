/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.handling;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("rawtypes")
public class ApexModelCustomGsonMapAdapter implements JsonSerializer<Map>, JsonDeserializer<Map> {
    private static final String MAP_ENTRY_KEY = "entry";

    @SuppressWarnings("unchecked")
    @Override
    public Map deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

        if (!(jsonElement instanceof JsonObject)) {
            throw new JsonParseException("could not parse JSON map, map is not a JsonObject");
        }

        JsonObject jsonObject = (JsonObject) jsonElement;

        if (jsonObject.size() != 1) {
            throw new JsonParseException("could not parse JSON map, map must be in a JsonObject with a single member");
        }

        JsonArray mapEntryArray = (JsonArray) jsonObject.get(MAP_ENTRY_KEY);
        if (mapEntryArray == null) {
            throw new JsonParseException("could not parse JSON map, map \"entry\" in JsonObject not found");
        }

        return new TreeMap(
            StreamSupport
                .stream(mapEntryArray.spliterator(), true)
                .map(element -> deserializeMapEntry(element, typeOfT, context))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))
        );
    }

    @Override
    public JsonElement serialize(Map sourceMap, Type typeOfSrc, JsonSerializationContext context) {

        // A map is stored in a JsonArray
        JsonArray mapEntryArray = new JsonArray();

        for (Object mapEntryObject : sourceMap.entrySet()) {
            Entry mapEntry = (Entry) mapEntryObject;
            mapEntryArray.add(serializeMapEntry(mapEntry, typeOfSrc, context));
        }

        JsonObject returnObject = new JsonObject();
        returnObject.add(MAP_ENTRY_KEY, mapEntryArray);

        return returnObject;
    }

    @SuppressWarnings("unchecked")
    private static Entry deserializeMapEntry(JsonElement element, Type typeOfT, JsonDeserializationContext context) {
        // Get the types of the map
        ParameterizedType pt = (ParameterizedType) typeOfT;

        // Type of the key and value of the map
        Type keyType = pt.getActualTypeArguments()[0];
        Type valueType = pt.getActualTypeArguments()[1];

        // Deserialize the key and value
        return new AbstractMap.SimpleEntry(
            context.deserialize(element.getAsJsonObject().get("key"), keyType),
            context.deserialize(element.getAsJsonObject().get("value"), valueType));
    }

    private static JsonElement serializeMapEntry(Entry sourceEntry, Type typeOfSrc, JsonSerializationContext context) {
        // Get the types of the map
        ParameterizedType pt = (ParameterizedType) typeOfSrc;

        // Type of the key and value of the map
        Type keyType = pt.getActualTypeArguments()[0];
        Type valueType = pt.getActualTypeArguments()[1];

        JsonObject entryObject = new JsonObject();
        entryObject.add("key", context.serialize(sourceEntry.getKey(), keyType));
        entryObject.add("value", context.serialize(sourceEntry.getValue(), valueType));

        return entryObject;
    }
}
