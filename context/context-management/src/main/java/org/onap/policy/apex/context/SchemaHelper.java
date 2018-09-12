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

package org.onap.policy.apex.context;

import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;

/**
 * This interface is implemented by plugin classes that use a particular schema to convert Apex context objects to an
 * understandable form.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface SchemaHelper {

    /**
     * Initialize the schema helper with its properties.
     *
     * @param userKey The key that identifies the user of the schema helper
     * @param schema the schema
     * @throws ContextRuntimeException the context runtime exception
     */
    void init(AxKey userKey, AxContextSchema schema);

    /**
     * Get the user key of the schema helper.
     *
     * @return the user key
     */
    AxKey getUserKey();

    /**
     * Get the schema of the schema helper.
     *
     * @return the schema
     */
    AxContextSchema getSchema();

    /**
     * The Java class that this schema produces on the Java side.
     *
     * @return the schema class
     */
    Class<?> getSchemaClass();

    /**
     * The Java class that handles the schema for the schema technology in use.
     *
     * @return the schema object
     */
    Object getSchemaObject();

    /**
     * Create a new instance of the schema class using whatever schema technology is being used.
     *
     * @return the new instance
     */
    Object createNewInstance();

    /**
     * Create a new instance of the schema class using whatever schema technology is being used.
     *
     * @param stringValue the string represents the value the new instance should have
     * @return the new instance
     */
    Object createNewInstance(String stringValue);

    /**
     * Create a new instance of the schema class from an object using whatever schema technology is being used.
     *
     * @param incomingObject the incoming object that holds the raw representation of the object to be created
     * @return the new instance
     */
    Object createNewInstance(Object incomingObject);

    /**
     * Unmarshal an object in schema format into a Java object.
     *
     * @param object the object as a Java object
     * @return the object in schema format
     */
    Object unmarshal(Object object);

    /**
     * Marshal a Java object into Json format.
     *
     * @param schemaObject the object in schema format
     * @return the object as a Json string
     */
    String marshal2String(Object schemaObject);

    /**
     * Marshal a Java object into an output object of an arbitrary type.
     *
     * @param schemaObject the object in schema format
     * @return the object as output object of an arbitrary type
     */
    Object marshal2Object(Object schemaObject);
}
