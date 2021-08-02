/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
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

package org.onap.policy.apex.model.policymodel.concepts;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

@Getter
@Builder
public class AxStateParams {
    private AxReferenceKey key;
    private AxArtifactKey trigger;
    private Map<String, AxStateOutput> stateOutputs;
    private Set<AxArtifactKey> contextAlbumReferenceSet;
    private AxTaskSelectionLogic taskSelectionLogic;
    private Map<String, AxStateFinalizerLogic> stateFinalizerLogicMap;
    private AxArtifactKey defaultTask;
    private Map<AxArtifactKey, AxStateTaskReference> taskReferenceMap;
}
