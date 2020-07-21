/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

public class ApexModelStringWriterTest {

    @Test
    public void testModelStringWriter() throws IOException, ApexException {
        AxModel basicModel = new DummyApexBasicModelCreator().getModel();
        assertNotNull(basicModel);

        AxKeyInfo intKeyInfo   = basicModel.getKeyInformation().get("IntegerKIKey");
        AxKeyInfo floatKeyInfo = basicModel.getKeyInformation().get("FloatKIKey");

        // Ensure marshalling is OK
        ApexModelStringWriter<AxKeyInfo> stringWriter = new ApexModelStringWriter<AxKeyInfo>(true);

        assertNotNull(stringWriter.writeJsonString(intKeyInfo,   AxKeyInfo.class));
        assertNotNull(stringWriter.writeJsonString(floatKeyInfo, AxKeyInfo.class));

        assertNotNull(stringWriter.writeString(intKeyInfo,   AxKeyInfo.class, true));
        assertNotNull(stringWriter.writeString(floatKeyInfo, AxKeyInfo.class, true));

        assertNotNull(stringWriter.writeString(intKeyInfo,   AxKeyInfo.class, false));
        assertNotNull(stringWriter.writeString(floatKeyInfo, AxKeyInfo.class, false));

        assertNotNull(stringWriter.writeXmlString(intKeyInfo,   AxKeyInfo.class));
        assertNotNull(stringWriter.writeXmlString(floatKeyInfo, AxKeyInfo.class));

        assertThatThrownBy(() -> stringWriter.writeString(null, AxKeyInfo.class, true))
            .hasMessage("concept may not be null");
        assertThatThrownBy(() -> stringWriter.writeString(null, AxKeyInfo.class, false))
            .hasMessage("concept may not be null");
        assertThatThrownBy(() -> stringWriter.writeJsonString(null, AxKeyInfo.class))
            .hasMessage("error writing JSON string");
        assertThatThrownBy(() -> stringWriter.writeXmlString(null, AxKeyInfo.class))
            .hasMessage("error writing XML string");
        stringWriter.setValidateFlag(true);
        assertTrue(stringWriter.isValidateFlag());
    }
}
