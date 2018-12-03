/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.impl.schema.java;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * This class serialises and deserialises various type of event protocol parameters to and from JSON.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SupportJsonAdapter implements JsonSerializer<String>, JsonDeserializer<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(final String src, final Type typeOfSrc, final JsonSerializationContext context) {
        return new Gson().toJsonTree(src, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {

        return new Gson().fromJson(json, String.class);
    }
}
