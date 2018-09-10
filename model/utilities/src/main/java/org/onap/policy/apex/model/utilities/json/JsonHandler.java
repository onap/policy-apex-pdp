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

package org.onap.policy.apex.model.utilities.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This class reads objects of the given class from an input stream.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <P> the generic type
 */
public class JsonHandler<P> {

    /**
     * This method reads objects of a given class from an input stream.
     *
     * @param inputClass The class to read
     * @param inputStream the input stream to read from
     * @return the object read
     */
    public P read(final Class<P> inputClass, final InputStream inputStream) {
        // Register the adapters for our carrier technologies and event protocols with GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();

        final Gson gson = gsonBuilder.serializeNulls().create();
        final Reader jsonResourceReader = new InputStreamReader(inputStream);
        return gson.fromJson(jsonResourceReader, inputClass);
    }
}
