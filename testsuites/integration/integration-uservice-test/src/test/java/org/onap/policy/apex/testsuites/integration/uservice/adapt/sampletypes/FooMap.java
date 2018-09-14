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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.sampletypes;

import java.util.LinkedHashMap;
import java.util.Map;

public class FooMap extends LinkedHashMap<String, String> {
    private static final long serialVersionUID = -7125986379378753022L;

    public FooMap() {
        super();
    }

    public FooMap(final int initialCapacity, final float loadFactor, final boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public FooMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public FooMap(final int initialCapacity) {
        super(initialCapacity);
    }

    public FooMap(final Map<? extends String, ? extends String> map) {
        super(map);
    }
}
