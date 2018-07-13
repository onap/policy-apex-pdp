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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestBeans {

    @Test
    public void testBeans() {
        assertNotNull(new BeanEvent().toString());
        assertNotNull(new BeanState().toString());
        assertNotNull(new BeanContextAlbum().toString());
        assertNotNull(new BeanPolicy().toString());
        assertNotNull(new BeanContextSchema().toString());
        assertNotNull(new BeanField().toString());
        assertNotNull(new BeanModel().toString());
        assertNotNull(new BeanLogic().toString());
        assertNotNull(new BeanStateOutput().toString());
        assertNotNull(new BeanTaskParameter().toString());
        assertNotNull(new BeanKeyRef().toString());
        assertNotNull(new BeanStateTaskRef().toString());
        assertNotNull(new BeanTask().toString());

        final BeanState beanState = new BeanState();
        assertNull(beanState.getName());
        beanState.setDefaultTask(new BeanKeyRef());
        assertNotNull(beanState.getDefaultTask());

        final BeanEvent beanEvent = new BeanEvent();
        assertNull(beanEvent.get("name"));

        final BeanFake beanFake = new BeanFake();
        assertNull(beanFake.get("name"));
        assertNull(beanFake.get("field1"));

        try {
            beanFake.get("iDontExist");
            fail("test should throw an exception here");
        } catch (final IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            beanFake.get("nome");
            fail("test should throw an exception here");
        } catch (final IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            beanFake.get("field2");
            fail("test should throw an exception here");
        } catch (final IllegalArgumentException e) {
            assertNotNull(e);
        }
        try {
            beanFake.get("field3");
            fail("test should throw an exception here");
        } catch (final IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}
