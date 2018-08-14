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

package org.onap.policy.apex.context.test.lock.modifier;

import static org.onap.policy.apex.context.test.utils.Constants.TEST_VALUE;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class SingleValueWriteLockAlbumModifier implements AlbumModifier {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(SingleValueWriteLockAlbumModifier.class);


    @Override
    public void modifyAlbum(final ContextAlbum contextAlbum, final int loopSize, final int arraySize)
            throws ContextException {
        for (int i = 0; i < loopSize; i++) {
            try {
                contextAlbum.lockForWriting(TEST_VALUE);
                TestContextLongItem item = (TestContextLongItem) contextAlbum.get(TEST_VALUE);
                if (item != null) {
                    long value = item.getLongValue();
                    item.setLongValue(++value);
                } else {
                    item = new TestContextLongItem(0L);
                }
                contextAlbum.put(TEST_VALUE, item);
            } finally {
                contextAlbum.unlockForWriting(TEST_VALUE);
            }
        }

        try {
            contextAlbum.lockForWriting(TEST_VALUE);
            final TestContextLongItem item = (TestContextLongItem) contextAlbum.get(TEST_VALUE);
            final long value = item.getLongValue();
            LOGGER.info("Value after modification: ", value);
        } catch (final Exception e) {
            LOGGER.error("could not read the value in the test context album", e);
        } finally {
            contextAlbum.unlockForWriting(TEST_VALUE);
        }
    }

}
