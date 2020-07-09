/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

public class SupportConceptGetterTester {

    @Test
    public void testConceptGetter() throws IOException, ApexException {
        AxModel basicModel = new DummyApexBasicModelCreator().getModel();
        assertNotNull(basicModel);

        AxKeyInfo intKI01 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey01", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey01 description");
        AxKeyInfo intKI11 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey11", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey11 description");
        AxKeyInfo intKI21 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey21", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey21 description");
        AxKeyInfo intKI22 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey22", "0.0.2"), UUID.randomUUID(),
                        "IntegerKIKey22 description");
        AxKeyInfo intKI23 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey23", "0.0.3"), UUID.randomUUID(),
                        "IntegerKIKey23 description");
        AxKeyInfo intKI24 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey24", "0.0.4"), UUID.randomUUID(),
                        "IntegerKIKey24 description");
        AxKeyInfo intKI25 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey25", "0.0.5"), UUID.randomUUID(),
                        "IntegerKIKey25 description");
        AxKeyInfo intKI26 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey26", "0.0.6"), UUID.randomUUID(),
                        "IntegerKIKey26 description");
        AxKeyInfo intKI31 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey31", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey31 description");
        AxKeyInfo intKI41 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey41", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey41 description");
        AxKeyInfo intKI51 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey51", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey51 description");
        AxKeyInfo intKI52 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey52", "0.0.2"), UUID.randomUUID(),
                        "IntegerKIKey52 description");
        AxKeyInfo intKI53 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey53", "0.0.3"), UUID.randomUUID(),
                        "IntegerKIKey53 description");
        AxKeyInfo intKI54 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey54", "0.0.4"), UUID.randomUUID(),
                        "IntegerKIKey54 description");
        AxKeyInfo intKI61 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey61", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey61 description");
        AxKeyInfo intKI62 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey62", "0.0.2"), UUID.randomUUID(),
                        "IntegerKIKey62 description");
        AxKeyInfo intKI63 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey63", "0.0.3"), UUID.randomUUID(),
                        "IntegerKIKey63 description");
        AxKeyInfo intKI64 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey64", "0.0.4"), UUID.randomUUID(),
                        "IntegerKIKey64 description");
        AxKeyInfo intKI71 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey71", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey71 description");
        AxKeyInfo intKI81 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey81", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey81 description");
        AxKeyInfo intKI91 = new AxKeyInfo(new AxArtifactKey("IntegerKIKey91", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey91 description");
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI31.getKey(), intKI31);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI24.getKey(), intKI24);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI11.getKey(), intKI11);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI64.getKey(), intKI64);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI41.getKey(), intKI41);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI51.getKey(), intKI51);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI23.getKey(), intKI23);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI81.getKey(), intKI81);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI71.getKey(), intKI71);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI01.getKey(), intKI01);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI91.getKey(), intKI91);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI52.getKey(), intKI52);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI53.getKey(), intKI53);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI62.getKey(), intKI62);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI54.getKey(), intKI54);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI26.getKey(), intKI26);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI22.getKey(), intKI22);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI25.getKey(), intKI25);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI21.getKey(), intKI21);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI61.getKey(), intKI61);
        basicModel.getKeyInformation().getKeyInfoMap().put(intKI63.getKey(), intKI63);

        AxKeyInfo floatKI01 = new AxKeyInfo(new AxArtifactKey("FloatKIKey01", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey01 description");
        AxKeyInfo floatKI11 = new AxKeyInfo(new AxArtifactKey("FloatKIKey11", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey11 description");
        AxKeyInfo floatKI21 = new AxKeyInfo(new AxArtifactKey("FloatKIKey21", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey21 description");
        AxKeyInfo floatKI31 = new AxKeyInfo(new AxArtifactKey("FloatKIKey31", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey31 description");
        AxKeyInfo floatKI41 = new AxKeyInfo(new AxArtifactKey("FloatKIKey41", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey41 description");
        AxKeyInfo floatKI51 = new AxKeyInfo(new AxArtifactKey("FloatKIKey51", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey51 description");
        AxKeyInfo floatKI61 = new AxKeyInfo(new AxArtifactKey("FloatKIKey61", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey61 description");
        AxKeyInfo floatKI71 = new AxKeyInfo(new AxArtifactKey("FloatKIKey71", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey71 description");
        AxKeyInfo floatKI81 = new AxKeyInfo(new AxArtifactKey("FloatKIKey81", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey81 description");
        AxKeyInfo floatKI82 = new AxKeyInfo(new AxArtifactKey("FloatKIKey82", "0.0.2"), UUID.randomUUID(),
                        "IntegerKIKey82 description");
        AxKeyInfo floatKI83 = new AxKeyInfo(new AxArtifactKey("FloatKIKey83", "0.0.3"), UUID.randomUUID(),
                        "IntegerKIKey83 description");
        AxKeyInfo floatKI91 = new AxKeyInfo(new AxArtifactKey("FloatKIKey91", "0.0.1"), UUID.randomUUID(),
                        "IntegerKIKey91 description");
        AxKeyInfo floatKI92 = new AxKeyInfo(new AxArtifactKey("FloatKIKey92", "0.0.2"), UUID.randomUUID(),
                        "IntegerKIKey92 description");
        AxKeyInfo floatKI93 = new AxKeyInfo(new AxArtifactKey("FloatKIKey93", "0.0.3"), UUID.randomUUID(),
                        "IntegerKIKey93 description");
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI11.getKey(), floatKI11);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI83.getKey(), floatKI83);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI51.getKey(), floatKI51);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI71.getKey(), floatKI71);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI21.getKey(), floatKI21);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI81.getKey(), floatKI81);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI92.getKey(), floatKI92);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI91.getKey(), floatKI91);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI01.getKey(), floatKI01);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI82.getKey(), floatKI82);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI61.getKey(), floatKI61);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI41.getKey(), floatKI41);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI31.getKey(), floatKI31);
        basicModel.getKeyInformation().getKeyInfoMap().put(floatKI93.getKey(), floatKI93);

        assertNull(basicModel.getKeyInformation().get("NonExistantKey", "0.0.6"));
        assertEquals(intKI26, basicModel.getKeyInformation().get("IntegerKIKey26", "0.0.6"));
        assertEquals(intKI62, basicModel.getKeyInformation().get("IntegerKIKey62", "0.0.2"));
        assertEquals(intKI21, basicModel.getKeyInformation().get("IntegerKIKey21", "0.0.1"));
        assertEquals(intKI61, basicModel.getKeyInformation().get("IntegerKIKey61", "0.0.1"));

        assertNull(basicModel.getKeyInformation().get("NonExistantKey"));

        assertEquals(intKI01, basicModel.getKeyInformation().get("IntegerKIKey01"));
        assertEquals(intKI11, basicModel.getKeyInformation().get("IntegerKIKey11"));
        assertEquals(intKI26, basicModel.getKeyInformation().get("IntegerKIKey26"));
        assertEquals(intKI31, basicModel.getKeyInformation().get("IntegerKIKey31"));
        assertEquals(intKI41, basicModel.getKeyInformation().get("IntegerKIKey41"));
        assertEquals(intKI54, basicModel.getKeyInformation().get("IntegerKIKey54"));
        assertEquals(intKI64, basicModel.getKeyInformation().get("IntegerKIKey64"));
        assertEquals(intKI71, basicModel.getKeyInformation().get("IntegerKIKey71"));
        assertEquals(intKI81, basicModel.getKeyInformation().get("IntegerKIKey81"));
        assertEquals(intKI91, basicModel.getKeyInformation().get("IntegerKIKey91"));

        assertEquals(floatKI01, basicModel.getKeyInformation().get("FloatKIKey01"));
        assertEquals(floatKI11, basicModel.getKeyInformation().get("FloatKIKey11"));
        assertEquals(floatKI21, basicModel.getKeyInformation().get("FloatKIKey21"));
        assertEquals(floatKI31, basicModel.getKeyInformation().get("FloatKIKey31"));
        assertEquals(floatKI41, basicModel.getKeyInformation().get("FloatKIKey41"));
        assertEquals(floatKI51, basicModel.getKeyInformation().get("FloatKIKey51"));
        assertEquals(floatKI61, basicModel.getKeyInformation().get("FloatKIKey61"));
        assertEquals(floatKI71, basicModel.getKeyInformation().get("FloatKIKey71"));
        assertEquals(floatKI83, basicModel.getKeyInformation().get("FloatKIKey83"));
        assertEquals(floatKI93, basicModel.getKeyInformation().get("FloatKIKey93"));

        // Ensure marshalling and unmarshalling is OK
        ApexModelReader<AxModel> modelReader = new ApexModelReader<AxModel>(AxModel.class);
        ApexModelFileWriter<AxModel> modelWriter = new ApexModelFileWriter<AxModel>(true);

        modelReader.setValidateFlag(false);
        modelWriter.setValidateFlag(false);

        File tempXmlFile = File.createTempFile("ApexModel", "xml");
        modelWriter.apexModelWriteJsonFile(basicModel, AxModel.class, tempXmlFile.getCanonicalPath());

        FileInputStream xmlFileInputStream = new FileInputStream(tempXmlFile);
        AxModel readXmlModel = modelReader.read(xmlFileInputStream);
        xmlFileInputStream.close();
        assertEquals(basicModel, readXmlModel);
        assertEquals(intKI91, readXmlModel.getKeyInformation().get("IntegerKIKey91"));
        assertNotNull(readXmlModel.getKeyInformation().get("FloatKIKey"));
        tempXmlFile.delete();
    }
}
