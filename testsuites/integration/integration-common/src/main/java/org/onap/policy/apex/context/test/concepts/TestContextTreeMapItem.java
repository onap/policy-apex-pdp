/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
import java.util.Map;
import java.util.TreeMap;
import lombok.Data;

/**
 * The Class TestContextTreeMapItem.
 */
@Data
public class TestContextTreeMapItem implements Serializable {
    private static final long serialVersionUID = -7497746259264651884L;

    private Map<String, String> mapValue = new TreeMap<>();

    /**
     * The Constructor.
     */
    public TestContextTreeMapItem() {
        // Default constructor
    }

    /**
     * The Constructor.
     *
     * @param mapValue the map value
     */
    public TestContextTreeMapItem(final Map<String, String> mapValue) {
        this.mapValue = mapValue;
    }
}
