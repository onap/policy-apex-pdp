/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.context.lock.modifier;

import java.util.Random;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class NoLockAlbumModifier implements a non lock context album.
 */
public class NoLockAlbumModifier implements AlbumModifier {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(NoLockAlbumModifier.class);

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.test.lock.modifier.AlbumModifier#modifyAlbum(org.onap.policy.apex.context.
     * ContextAlbum, int, int)
     */
    @Override
    public void modifyAlbum(final ContextAlbum contextAlbum, final int loopSize, final int arraySize) {
        final Random rand = new Random();
        for (int i = 0; i < loopSize; i++) {
            final String nextLongKey = Integer.toString(rand.nextInt(arraySize));
            final TestContextLongItem item = (TestContextLongItem) contextAlbum.get(nextLongKey);
            final long value = item.getLongValue();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("lock type={}, value={}", LockType.NO_LOCK, value);
            }
        }
    }

}
