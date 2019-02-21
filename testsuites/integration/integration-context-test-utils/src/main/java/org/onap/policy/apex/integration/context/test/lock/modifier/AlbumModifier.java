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

package org.onap.policy.apex.integration.context.test.lock.modifier;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;

/**
 * The Interface AlbumModifier defines the interface to allow context albums be modified during context tests.
 */
@FunctionalInterface
public interface AlbumModifier {

    /**
     * Modify album.
     *
     * @param contextAlbum the context album
     * @param loopSize the loop size
     * @param arraySize the array size
     * @throws ContextException the context exception
     */
    void modifyAlbum(final ContextAlbum contextAlbum, final int loopSize, final int arraySize) throws ContextException;

}
