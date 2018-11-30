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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test Java schema helper parameters.
 */
public class JavaSchemaHelperParametersTest {

    @Test
    public void testJavaSchemaHelperParameters() {
        JavaSchemaHelperParameters pars = new JavaSchemaHelperParameters();

        assertEquals("org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelper",
                        pars.getSchemaHelperPluginClass());

        assertEquals(0, pars.getJsonAdapters().size());

        JavaSchemaHelperJsonAdapterParameters jsonPars = new JavaSchemaHelperJsonAdapterParameters();
        pars.getJsonAdapters().put("JsonAdapter0", jsonPars);

        assertFalse(pars.validate().isValid());

        jsonPars.setAdaptedClass("AAA");
        jsonPars.setAdaptorClass("AAA");
        assertFalse(pars.validate().isValid());

        jsonPars.setAdaptedClass("java.lang.String");
        jsonPars.setAdaptorClass("AAA");
        assertFalse(pars.validate().isValid());

        jsonPars.setAdaptedClass("AAA");
        jsonPars.setAdaptorClass("java.lang.String");
        assertFalse(pars.validate().isValid());

        jsonPars.setAdaptedClass("java.lang.String");
        jsonPars.setAdaptorClass("org.onap.policy.apex.context.impl.schema.java.SupportJsonDeserializer");
        assertFalse(pars.validate().isValid());

        jsonPars.setAdaptedClass("java.lang.String");
        jsonPars.setAdaptorClass("org.onap.policy.apex.context.impl.schema.java.SupportJsonSerializer");
        assertFalse(pars.validate().isValid());

        jsonPars.setAdaptedClass("java.lang.String");
        jsonPars.setAdaptorClass("org.onap.policy.apex.context.impl.schema.java.SupportJsonAdapter");
        assertTrue(pars.validate().isValid());

        Map<String, JavaSchemaHelperJsonAdapterParameters> adapterMap = new LinkedHashMap<>();

        pars.setJsonAdapters(adapterMap);
        assertTrue(pars.validate().isValid());
    }

    @Test
    public void testJavaSchemaHelperJsonAdapterParameters() {
        JavaSchemaHelperJsonAdapterParameters pars = new JavaSchemaHelperJsonAdapterParameters();

        assertNull(pars.getName());
        assertNull(pars.getAdaptedClass());
        assertNull(pars.getAdaptedClazz());
        assertNull(pars.getAdaptorClass());
        assertNull(pars.getAdaptorClazz());

        pars.setName("Zooby");
        assertEquals("Zooby", pars.getAdaptedClass());
        assertEquals("Zooby", pars.getName());
        assertNull(pars.getAdaptedClazz());

        pars.setAdaptorClass("Zooby");
        assertEquals("Zooby", pars.getAdaptorClass());
        assertNull(pars.getAdaptorClazz());
    }
}
