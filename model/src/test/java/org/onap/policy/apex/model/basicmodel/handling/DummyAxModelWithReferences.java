/*
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

package org.onap.policy.apex.model.basicmodel.handling;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

@Getter
public class DummyAxModelWithReferences extends AxModel {
    @Serial
    private static final long serialVersionUID = -8194956638511120008L;

    private final List<AxKey> extrakeyList = new ArrayList<>();
    
    public DummyAxModelWithReferences(final AxArtifactKey key) {
        super(key);
    }
    
    @Override
    public List<AxKey> getKeys() {
        List<AxKey> keys = super.getKeys();
        keys.addAll(extrakeyList);

        return keys;
    }

    /**
     * Set the reference key list.
     */
    public void setReferenceKeyList() {
        List<AxKey> keys = super.getKeys();
        
        for (AxKey key: keys) {
            AxArtifactKey akey = (AxArtifactKey) key;
            AxReferenceKey keyRef = new AxReferenceKey(akey, akey.getName());
            extrakeyList.add(keyRef);
        }
    }
    
    public void addKey(final AxKey akey) {
        extrakeyList.add(akey);
    }
    
    public void removeKey(final AxKey akey) {
        extrakeyList.remove(akey);
    }
}
