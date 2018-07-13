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

package org.onap.policy.apex.client.editor.rest.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The base class for Beans.
 */
public abstract class BeanBase {

    /**
     * Gets a named field from the bean.
     *
     * @param field the field name
     * @return the value for the field
     */
    public String get(final String field) {
        // CHECKSTYLE:OFF: MagicNumber
        // use getter preferably
        for (final Method method : this.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.toLowerCase())) {
                    try {
                        return (String) method.invoke(this);
                    } catch (final Exception e) {
                        throw new IllegalArgumentException(
                                "Problem retrieving field called ('" + field + "') from JSON bean " + this, e);
                    }
                }
            }
        }
        // Use field approach
        if (field != null) {
            try {
                final Field f = this.getClass().getDeclaredField(field);
                if (f != null) {
                    f.setAccessible(true);
                    return (String) (f.get(this));
                }
            } catch (final Exception e) {
                throw new IllegalArgumentException(
                        "Problem retrieving field called ('" + field + "') from JSON bean " + this, e);
            }
        }
        throw new IllegalArgumentException("Problem retrieving field called ('" + field + "') from JSON bean " + this);
    }
}
