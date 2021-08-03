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
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Data;

/**
 * The Class TestContextTreeSetItem.
 */
@Data
public class TestContextTreeSetItem implements Serializable {
    private static final long serialVersionUID = 1254589722957250388L;

    private SortedSet<String> setValue = new TreeSet<>();

    /**
     * The Constructor.
     */
    public TestContextTreeSetItem() {
        // Default constructor
    }

    /**
     * The Constructor.
     *
     * @param setArray the set array
     */
    public TestContextTreeSetItem(final String[] setArray) {
        this.setValue = new TreeSet<>(Arrays.asList(setArray));
    }

    /**
     * The Constructor.
     *
     * @param setValue the set value
     */
    public TestContextTreeSetItem(final SortedSet<String> setValue) {
        this.setValue = setValue;
    }
}
