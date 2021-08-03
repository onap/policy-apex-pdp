/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.core.engine.executor.context;

import java.util.Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;

/**
 * Abstract class for the execution context for logic executions in logic being executed in an Apex engine. The
 * logic must have easy access to its subject definition, the incoming and outgoing field contexts, as well as the
 * policy, global, and external context.
 */
@Getter
@RequiredArgsConstructor
public class AbstractExecutionContext {
    /** A constant <code>boolean true</code> value available for reuse e.g., for the return value */
    public static final Boolean IS_TRUE = true;

    /**
     * A constant <code>boolean false</code> value available for reuse e.g., for the return value
     */
    public static final Boolean IS_FALSE = false;

    /** the execution ID for the current APEX policy execution instance. */
    public final Long executionId;

    // Standard coder for JSON converts
    private static final StandardCoder STANDARD_CODER = new StandardCoder();

    // A message specified in the logic
    @Setter
    private String message;

    // Execution properties for a policy execution
    private final Properties executionProperties;

    /**
     * Get a JSON representation of an object.
     *
     * @param theObject the object to get a JSON representation of
     * @return the JSON version of the object
     * @throws CoderException on JSON coding errors
     */
    public String stringify2Json(final Object theObject) throws CoderException {
        return stringify2Json(theObject, null);
    }

    /**
     * Get a JSON representation of an object.
     *
     * @param theObject the object to get a JSON representation of
     * @param schemaHelper a schema helper to use for the JSON conversion, if null, a standard conversion is done
     * @return the JSON version of the object
     * @throws CoderException on JSON coding errors
     */
    public String stringify2Json(final Object theObject, final SchemaHelper schemaHelper) throws CoderException {
        if (schemaHelper == null) {
            return STANDARD_CODER.encode(theObject);
        } else {
            return schemaHelper.marshal2String(theObject);
        }
    }
}
