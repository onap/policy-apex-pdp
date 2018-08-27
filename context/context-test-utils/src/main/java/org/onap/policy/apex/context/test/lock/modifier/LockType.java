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

import org.onap.policy.apex.context.ContextRuntimeException;

public enum LockType {

    NO_LOCK(0) {
        @Override
        public AlbumModifier getAlbumModifier() {
            return NO_LOCK_MODIFER;
        }
    },
    READ_LOCK(1) {
        @Override
        public AlbumModifier getAlbumModifier() {
            return READ_LOCK_MODIFER;
        }
    },
    WRITE_LOCK(2) {
        @Override
        public AlbumModifier getAlbumModifier() {
            return WRITE_LOCK_MODIFER;
        }
    },
    WRITE_LOCK_SINGLE_VALUE_UPDATE(3) {
        @Override
        public AlbumModifier getAlbumModifier() {
            return WRITE_LOCK_SINGLE_VALUE_MODIFER;
        }
    };

    private static final AlbumModifier NO_LOCK_MODIFER = new NoLockAlbumModifier();
    private static final AlbumModifier READ_LOCK_MODIFER = new ReadLockAlbumModifier();
    private static final AlbumModifier WRITE_LOCK_MODIFER = new WriteLockAlbumModifier();
    private static final AlbumModifier WRITE_LOCK_SINGLE_VALUE_MODIFER = new SingleValueWriteLockAlbumModifier();

    private final int value;

    private LockType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get the lock type given an int value.
     * @param value the value of lock type
     * @return the lock type
     */
    public static LockType getLockType(final int value) {
        for (final LockType lockType : LockType.values()) {
            if (lockType.getValue() == value) {
                return lockType;
            }
        }
        throw new ContextRuntimeException("Invalid Lock type value: " + value);
    }

    public abstract AlbumModifier getAlbumModifier();

}
