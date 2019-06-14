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
package org.onap.policy.apex.model.policymodel.handling;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * This class makes the albums field optional in marshaled Policy Models.
 * Empty albums are not marshaled to JSON/XML.
 * When unmarshaled, if no albums value is present then a new empty albums entry is created.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class EmptyAlbumsAdapter extends XmlAdapter<AxContextAlbums, AxContextAlbums> {


    /**
     * Decide whether to marshall a context albums entry. Non-empty context albums are always marshalled.
     * Empty albums are filtered.
     *
     * @param v the albums entry
     * @return the albums entry, or null if empty
     * @throws Exception
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(Object)
     */
    @Override
    public AxContextAlbums marshal(AxContextAlbums albums) throws Exception {
        if ((albums == null) || (albums.getAlbumsMap() == null) || (albums.getAlbumsMap().isEmpty())) {
            return null;
        } else {
            return albums;
        }
    }

    /**
     * Decide whether to unmarshall a context albums entry - Always.
     *
     * @param v the albums entry
     * @return the albums entry
     * @throws Exception
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(Object)
     */
    @Override
    public AxContextAlbums unmarshal(AxContextAlbums v) throws Exception {
        return v;
    }

    /**
     * After unmarshalling has completed the model's context albums entry may be null/empty or default.
     * If so the key for the albums entry should updated to a sensible value and additional keyinfo
     * information should then be added for that key
     *
     * @param policyModel the policy model containing the possibly empty context albums entry
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(Object)
     */
    public void doAfterUnmarshal(AxPolicyModel policyModel){
        AxArtifactKey nullkey = new AxArtifactKey();
        AxArtifactKey blanknullalbumskey =
            new AxArtifactKey(nullkey.getKey().getName() + "_Albums", nullkey.getKey().getVersion());
        AxArtifactKey thisalbumskey = policyModel.getAlbums().getKey();
        AxArtifactKey thismodelkey = policyModel.getKey();
        AxContextAlbums thismodelalbums = policyModel.getAlbums();

        if (nullkey.equals(thisalbumskey) || blanknullalbumskey.equals(thisalbumskey)) {
            thismodelalbums.setKey(new AxArtifactKey(thismodelkey.getName() + "_Albums", thismodelkey.getVersion()));
            policyModel.getKeyInformation().generateKeyInfo(thismodelalbums);
        }
    }

}
