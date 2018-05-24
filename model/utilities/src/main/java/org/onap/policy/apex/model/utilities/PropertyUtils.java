/*
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

package org.onap.policy.apex.model.utilities;

import java.util.Map.Entry;

/**
 * Convenience methods for handling Java properties class instances.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class PropertyUtils {
    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private PropertyUtils() {
    }

    /**
     * Return all properties as a string.
     *
     * @return a string containing all the property values
     */
    public static String getAllProperties() {
        final StringBuilder builder = new StringBuilder();

        for (final Entry<Object, Object> property : System.getProperties().entrySet()) {
            builder.append(property.getKey().toString());
            builder.append('=');
            builder.append(property.getValue().toString());
            builder.append('\n');
        }

        return builder.toString();
    }

    /**
     * Checks if a property is set. If the property is set with no value or with a value of "true", this method returns true. It returns "false" if the property
     * is not set or is set to false
     *
     * @param propertyName The property to check
     * @return true if the property is set to true, false otherwise
     */
    public static boolean propertySetOrTrue(final String propertyName) {
        if (propertyName == null) {
            return false;
        }

        final String propertyValue = System.getProperty(propertyName);
        if (propertyValue == null) {
            return false;
        }

        if (propertyValue.trim().length() == 0) {
            return true;
        }

        return new Boolean(propertyValue);
    }
}
