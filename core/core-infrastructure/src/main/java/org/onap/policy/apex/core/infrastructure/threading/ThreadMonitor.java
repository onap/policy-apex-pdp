/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

/**
 * Provides interface for different situation for testing the statue of a service.
 */
package org.onap.policy.apex.core.infrastructure.threading;

public interface ThreadMonitor<T> {
    /**
     * A method to check the statue of a service.
     * @param t is an instance of T
     * @return true if the service meet our requirement
     */
    boolean check(T t);

    /**
     * A method is designed for situation when the waiting time is too long.
     * @param timeOut is the max wait time for the method
     * @param t is an instance of T
     */
    void waitUntil(long timeOut, T t);
}
