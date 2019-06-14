/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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
package org.onap.policy.apex.model.basicmodel.handling;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;

/**
 * This class implements a filter to prevent some keyinfo information being marshalled when a model is serialised
 */
public class KeyInfoMarshalFilter extends XmlAdapter<AxKeyInformation, AxKeyInformation> {

    private List<AxKey> filterList = new LinkedList<>();

    /**
     * Adds a key to the list to be filtered.
     *
     * @param key the key to add to the filter list
     */
    public void addFilterKey(AxKey key) {
        filterList.add(key);
    }

    /**
     * Remove a key from the list to be filtered.
     *
     * @return true if the passed key was in the filter list and has been removed.
     * @param key the key to remove from the filter list
     */
    public boolean removeFilterKey(AxKey key) {
        return filterList.remove(key);
    }

    /**
     * Adds some keys to the list to be filtered.
     *
     * @param keys the keys to add to the filter list
     */
    public void addFilterKeys(Collection<? extends AxKey> keys) {
        filterList.addAll(keys);
    }

    /**
     * Decide whether to unmarshall some keyinfos - Always.
     *
     * @param v the keyinfo
     * @return the the keyinfo
     * @throws Exception
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(Object)
     */
    @Override
    public AxKeyInformation unmarshal(AxKeyInformation v) throws Exception {
        return v;
    }

    /**
     * Select which keyinfo entries will be marshalled - i.e. those not in the filter list.
     *
     * @param v the keyinfo
     * @return the keyinfo
     * @throws Exception
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(Object)
     */
    @Override
    public AxKeyInformation marshal(AxKeyInformation v) throws Exception {
        if(v == null || v.getKeyInfoMap() == null || v.getKeyInfoMap().isEmpty() || filterList.isEmpty() ) {
            return v;
        }
        //create a new keyinfo clone to avoid removing keyinfo entries from the original model
        AxKeyInformation ret = new AxKeyInformation(v);
        Map<AxArtifactKey, AxKeyInfo> retmap = new TreeMap<>(ret.getKeyInfoMap());
        for (AxKey key : filterList) {
            retmap.remove(key);
        }
        ret.setKeyInfoMap(retmap);
        return ret;
    }
}
