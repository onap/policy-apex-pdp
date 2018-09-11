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

package org.onap.policy.apex.context.impl.locking.jvmlocal;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.locking.AbstractLockManager;

/**
 * A lock manager that returns locks that have a range of just the local JVM. The implementation uses a Jav
 * {@link ReentrantReadWriteLock} as the lock for context album items.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JvmLocalLockManager extends AbstractLockManager {
    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.context.impl.locking.AbstractLockManager#getReentrantReadWriteLock(java.lang.String)
     */
    @Override
    public ReadWriteLock getReentrantReadWriteLock(final String lockId) throws ContextException {
        return new ReentrantReadWriteLock();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#shutdown()
     */
    @Override
    public void shutdown() {
        // Nothing to do for this lock manager
    }
}
