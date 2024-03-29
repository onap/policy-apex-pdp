/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.core.infrastructure.threading;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class is a helper class for carrying out common threading tasks.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtilities {

    /**
     * Sleeps for the specified number of milliseconds, hiding interrupt handling.
     *
     * @param milliseconds the milliseconds
     * @return true, if successful
     */
    public static boolean sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            // restore the interrupt status
            Thread.currentThread().interrupt();
            return false;
        }

        return true;
    }
}
