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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.PropertyUtils;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PropertyUtilsTest {

    @Test
    public void test() {
        System.setProperty("boolean.true", "true");
        System.setProperty("boolean.false", "false");
        System.setProperty("boolean.blank", " ");
        
        assertNotNull(PropertyUtils.getAllProperties());
        
        assertEquals(false, PropertyUtils.propertySetOrTrue(null));
        assertEquals(false, PropertyUtils.propertySetOrTrue("ZOOBY"));
        assertEquals(true,  PropertyUtils.propertySetOrTrue("boolean.true"));
        assertEquals(true,  PropertyUtils.propertySetOrTrue("boolean.blank"));
        assertEquals(false, PropertyUtils.propertySetOrTrue("boolean.false"));
    }
}
