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

package org.onap.policy.apex.service.engine.event.testpojos;

/**
 * A test Pojo for pojo decoding and encoding in Apex.
 */
public class TestSubSubPojo {
    private int anInt;
    private Integer anInteger;
    private String someString;

    /**
     * Gets the an int.
     *
     * @return the an int
     */
    public int getAnInt() {
        return anInt;
    }

    /**
     * Gets the an integer.
     *
     * @return the an integer
     */
    public Integer getAnInteger() {
        return anInteger;
    }

    /**
     * Gets the a string.
     *
     * @return the a string
     */
    public String getSomeString() {
        return someString;
    }
}
