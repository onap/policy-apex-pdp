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

package org.onap.apex.model.basicmodel.handling;

import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.apex.model.basicmodel.concepts.AxModel;
import org.onap.apex.model.basicmodel.test.TestApexModelCreator;

public class TestApexTestModelCreator2 implements TestApexModelCreator<AxModel> {

    @Override
    public AxModel getModel() {
        AxModel basicModel = new AxModel();

        basicModel.setKey(new AxArtifactKey("BasicModel", "0.0.1"));
        basicModel.setKeyInformation(new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1")));

        basicModel.getKeyInformation().getKeyInfoMap().put(basicModel.getKey(), new AxKeyInfo(basicModel.getKey()));
        basicModel.getKeyInformation().getKeyInfoMap().put(basicModel.getKeyInformation().getKey(), new AxKeyInfo(basicModel.getKeyInformation().getKey()));
        basicModel.getKeyInformation().get("BasicModel").setDescription("");
        return basicModel;
    }

    @Override
    public final AxModel getMalstructuredModel() {
        return getModel();
    }

    @Override
    public final AxModel getObservationModel() {
        return getModel();
    }

    @Override
    public final AxModel getWarningModel() {
        return getModel();
    }

    @Override
    public final AxModel getInvalidModel() {
        return getModel();
    }
}
