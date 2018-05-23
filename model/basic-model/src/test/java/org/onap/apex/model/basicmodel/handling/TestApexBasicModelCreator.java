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

import java.util.UUID;

import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.apex.model.basicmodel.concepts.AxModel;
import org.onap.apex.model.basicmodel.test.TestApexModelCreator;

public class TestApexBasicModelCreator implements TestApexModelCreator<AxModel> {

    @Override
    public AxModel getModel() {
        AxModel basicModel = new AxModel();

        basicModel.setKey(new AxArtifactKey("BasicModel", "0.0.1"));
        basicModel.setKeyInformation(new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1")));

        basicModel.getKeyInformation().getKeyInfoMap().put(basicModel.getKey(), new AxKeyInfo(basicModel.getKey()));
        basicModel.getKeyInformation().getKeyInfoMap().put(basicModel.getKeyInformation().getKey(), new AxKeyInfo(basicModel.getKeyInformation().getKey()));

        AxKeyInfo intKI = new AxKeyInfo(new AxArtifactKey("IntegerKIKey", "0.0.1"), UUID.randomUUID(), "IntegerKIKey description");
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI.getKey(), new AxKeyInfo(intKI.getKey()));

        AxKeyInfo floatKI = new AxKeyInfo(new AxArtifactKey("FloatKIKey", "0.0.1"), UUID.randomUUID(), "FloatKIKey description");
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI.getKey(), new AxKeyInfo(floatKI.getKey()));

        return basicModel;
    }

    @Override
    public final AxModel getMalstructuredModel() {
        AxModel basicModel = new AxModel();

        // Note: No Data types
        basicModel.setKey(new AxArtifactKey("BasicModelKey", "0.0.1"));
        basicModel.setKeyInformation(new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1")));

        basicModel.getKeyInformation().getKeyInfoMap().put(
                basicModel.getKey(),
                new AxKeyInfo(
                        basicModel.getKey(),
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        "\nbasic model description\nThis is a multi line description\nwith another line of text."));

        return basicModel;
    }

    @Override
    public final AxModel getObservationModel() {
        AxModel basicModel = getModel();

        // Set key information as blank
        basicModel.getKeyInformation().getKeyInfoMap().get(basicModel.getKey()).setDescription("");

        return basicModel;
    }

    @Override
    public final AxModel getWarningModel() {
        AxModel basicModel = getModel();

        // Add unreferenced key information
        AxKeyInfo unreferencedKeyInfo0 = new AxKeyInfo(new AxArtifactKey("Unref0", "0.0.1"));
        AxKeyInfo unreferencedKeyInfo1 = new AxKeyInfo(new AxArtifactKey("Unref1", "0.0.1"));

        basicModel.getKeyInformation().getKeyInfoMap().put(unreferencedKeyInfo0.getKey(), unreferencedKeyInfo0);
        basicModel.getKeyInformation().getKeyInfoMap().put(unreferencedKeyInfo1.getKey(), unreferencedKeyInfo1);

        return basicModel;
    }

    @Override
    public final AxModel getInvalidModel() {
        AxModel basicModel = new AxModel();

        basicModel.setKey(new AxArtifactKey("BasicModelKey", "0.0.1"));
        basicModel.setKeyInformation(new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1")));

        basicModel.getKeyInformation().getKeyInfoMap().put(
                basicModel.getKey(),
                new AxKeyInfo(
                        basicModel.getKey(),
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        "nbasic model description\nThis is a multi line description\nwith another line of text."));
        basicModel.getKeyInformation().getKeyInfoMap().put(
                basicModel.getKeyInformation().getKey(),
                new AxKeyInfo(
                        basicModel.getKeyInformation().getKey(),
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        ""));

        return basicModel;
    }
    
    public final AxModelWithReferences getModelWithReferences() {
        AxModel model = getModel();
        
        AxModelWithReferences modelWithReferences = new AxModelWithReferences(model.getKey());
        modelWithReferences.setKeyInformation(model.getKeyInformation());
        modelWithReferences.setReferenceKeyList();
        
        return modelWithReferences;
    }
}
