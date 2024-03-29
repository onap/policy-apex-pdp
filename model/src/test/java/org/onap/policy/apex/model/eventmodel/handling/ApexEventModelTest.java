/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020,2022 Nordix Foundation.
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

package org.onap.policy.apex.model.eventmodel.handling;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.eventmodel.concepts.AxEventModel;

public class ApexEventModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String OBSERVATION_MODEL_STRING = "\n"
                    + "***observations noted during validation of model***\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event2,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event2,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "********************************";

    private static final String WARNING_MODEL_STRING = "\n" + "***warnings issued during validation of model***\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:WARNING:nameSpace on event is blank\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:WARNING:nameSpace on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event2,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:WARNING:nameSpace on event is blank\n"
                    + "AxArtifactKey:(name=event2,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event2,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "********************************";

    private static final String INVALID_MODEL_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):"
                    + "org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation:INVALID:"
                    + "keyInfoMap may not be empty\n" + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=smallEventModel,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=Schemas,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=BigIntType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=BooleanType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=IntType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=MapType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=SetType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=StringType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=smallEventMap,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=event0,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par0)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par2)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par3)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par4)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par5)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=par6)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=event1,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=theOnlyPar)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "duplicate key AxArtifactKey:(name=event1,version=0.0.1) found\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=event1,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "duplicate key AxReferenceKey:(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,"
                    + "localName=theOnlyPar) found\n" + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event0,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=theOnlyPar)\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event0,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:INVALID:"
                    + "parent key on parameter field AxReferenceKey:(parentKeyName=event0,parentKeyVersion=0.0.1,"
                    + "parentLocalName=NULL,localName=theOnlyPar) does not equal event key\n"
                    + "AxArtifactKey:(name=smallEventMap,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvents:INVALID:"
                    + "key on event entry key AxArtifactKey:(name=event2,version=0.0.1) does not equal event value key "
                    + "AxArtifactKey:(name=event1,version=0.0.1)\n" + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:INVALID:"
                    + "parent key on parameter field AxReferenceKey:(parentKeyName=event0,parentKeyVersion=0.0.1,"
                    + "parentLocalName=NULL,localName=theOnlyPar) does not equal event key\n"
                    + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):"
                    + "org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation:INVALID:"
                    + "keyInfoMap may not be empty\n" + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=smallEventModel,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=Schemas,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=SetType,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=smallEventMap,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=event1,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event1,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=theOnlyPar)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "duplicate key AxArtifactKey:(name=event1,version=0.0.1) found\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=event1,version=0.0.1)\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "duplicate key AxReferenceKey:(parentKeyName=event1,parentKeyVersion=0.0.1,"
                    + "parentLocalName=NULL,localName=theOnlyPar) found\n"
                    + "AxArtifactKey:(name=smallEventModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEventModel:INVALID:"
                    + "key information not found for parent key of key AxReferenceKey:"
                    + "(parentKeyName=event1,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=theOnlyPar)\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "AxArtifactKey:(name=smallEventMap,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvents:INVALID:"
                    + "key on event entry key AxArtifactKey:(name=event2,version=0.0.1) does not equal event value key "
                    + "AxArtifactKey:(name=event1,version=0.0.1)\n" + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:source on event is blank\n"
                    + "AxArtifactKey:(name=event1,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvent:OBSERVATION:target on event is blank\n"
                    + "********************************";

    TestApexModel<AxEventModel> testApexModel;

    /**
     * Set up the test.
     *
     * @throws Exception exceptions from the test
     */
    @Before
    public void setup() throws Exception {
        testApexModel = new TestApexModel<AxEventModel>(AxEventModel.class, new DummyTestApexEventModelCreator());
    }

    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testApexModelVaidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateObservation();
        assertEquals(OBSERVATION_MODEL_STRING, result.toString());
    }

    @Test
    public void testApexModelVaidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateWarning();
        assertEquals(WARNING_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateInvalidModel();
        assertEquals(INVALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateMalstructured();
        assertEquals(INVALID_MODEL_MALSTRUCTURED_STRING, result.toString());
    }

    @Test
    public void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
