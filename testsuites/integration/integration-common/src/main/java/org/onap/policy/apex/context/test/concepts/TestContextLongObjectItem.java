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
 * The Class TestContextLongObjectItem.
 */
@Data
public class TestContextLongObjectItem implements Serializable {
    private static final long serialVersionUID = -1029406737866392421L;

    private Long longValue = 0L;

    /**
     * The Constructor.
     */
    public TestContextLongObjectItem() {
        // Default constructor
    }

    /**
     * The Constructor.
     *
     * @param longValue the long value
     */
    public TestContextLongObjectItem(final Long longValue) {
        this.longValue = longValue;
    }

    public Long getIncrementedLongValue() {
        return longValue + 1;
    }
}
