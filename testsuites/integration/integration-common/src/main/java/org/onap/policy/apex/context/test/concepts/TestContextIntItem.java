/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.context.test.concepts;

import java.io.Serializable;
import lombok.Data;

/**
 * The Class TestContextIntItem.
 */
@Data
public class TestContextIntItem implements Serializable {
    private static final long serialVersionUID = -8978435658277900984L;

    private int intValue = 0;

    /**
     * The Constructor.
     */
    public TestContextIntItem() {
        // Default constructor
    }

    /**
     * The Constructor.
     *
     * @param intValue the int value
     */
    public TestContextIntItem(final Integer intValue) {
        this.intValue = intValue;
    }

    /**
     * The Constructor.
     *
     * @param original the original
     */
    public TestContextIntItem(final TestContextIntItem original) {
        this.intValue = original.intValue;
    }

    public Integer getIncrementedIntValue() {
        return intValue + 1;
    }
}
