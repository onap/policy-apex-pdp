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
package org.onap.policy.apex.context.test.utils;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.test.lock.modifier.AlbumModifier;
import org.onap.policy.apex.context.test.lock.modifier.LockType;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

public interface ConfigrationProvider {

    String getTestName();

    int getLoopSize();

    int getThreadCount();

    int getJvmCount();

    int getAlbumSize();

    ExecutorService getExecutorService();

    ExecutorService getExecutorService(final String threadFactoryName, final int threadPoolSize);

    Distributor getDistributor(final AxArtifactKey key);

    Distributor getDistributor();

    ContextAlbum getContextAlbum(final Distributor distributor);

    ContextAlbum getContextAlbum(final Distributor distributor, AxArtifactKey axContextAlbumKey,
            AxArtifactKey[] artifactKeys) throws ContextException;

    Map<String, Object> getContextAlbumInitValues();

    AlbumModifier getAlbumModifier();

    LockType getLockType();

}
