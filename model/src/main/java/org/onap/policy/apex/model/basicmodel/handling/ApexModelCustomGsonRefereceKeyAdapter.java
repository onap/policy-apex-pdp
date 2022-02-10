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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

public class ApexModelCustomGsonRefereceKeyAdapter implements JsonDeserializer<AxReferenceKey> {

    @Override
    public AxReferenceKey deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

        if (jsonElement instanceof JsonObject) {
            return new AxReferenceKey(
                jsonElement.getAsJsonObject().get("parentKeyName").getAsString(),
                jsonElement.getAsJsonObject().get("parentKeyVersion").getAsString(),
                jsonElement.getAsJsonObject().get("parentLocalName").getAsString(),
                jsonElement.getAsJsonObject().get("localName").getAsString()
            );
        } else {
            AxReferenceKey returnKey = new AxReferenceKey();
            returnKey.setLocalName(jsonElement.getAsString());
            return returnKey;
        }
    }
}
