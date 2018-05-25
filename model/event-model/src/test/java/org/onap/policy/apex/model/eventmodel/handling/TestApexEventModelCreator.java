/*-
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

package org.onap.policy.apex.model.eventmodel.handling;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEventModel;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;

public class TestApexEventModelCreator implements TestApexModelCreator<AxEventModel> {

    @Override
    public AxEventModel getModel() {
        final AxContextSchema axSchema0 =
                new AxContextSchema(new AxArtifactKey("BooleanType", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema axSchema1 =
                new AxContextSchema(new AxArtifactKey("IntType", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema axSchema2 =
                new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema axSchema3 = new AxContextSchema(new AxArtifactKey("KeyType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey");
        final AxContextSchema axSchema4 = new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation");
        final AxContextSchema axSchema5 =
                new AxContextSchema(new AxArtifactKey("BigIntType", "0.0.1"), "Java", "java.math.BigInteger");
        final AxContextSchema axSchema6 = new AxContextSchema(new AxArtifactKey("ModelType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.basicmodel.concepts.AxModel");

        final AxContextSchemas dataTypes = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        dataTypes.getSchemasMap().put(axSchema0.getKey(), axSchema0);
        dataTypes.getSchemasMap().put(axSchema1.getKey(), axSchema1);
        dataTypes.getSchemasMap().put(axSchema2.getKey(), axSchema2);
        dataTypes.getSchemasMap().put(axSchema3.getKey(), axSchema3);
        dataTypes.getSchemasMap().put(axSchema4.getKey(), axSchema4);
        dataTypes.getSchemasMap().put(axSchema5.getKey(), axSchema5);
        dataTypes.getSchemasMap().put(axSchema6.getKey(), axSchema6);

        final AxEvents eventMap = new AxEvents(new AxArtifactKey("smallEventMap", "0.0.1"));

        final AxEvent event0 = new AxEvent(new AxArtifactKey("event0", "0.0.1"),
                "org.onap.policy.apex.model.eventmodel.events", "Source", "Target");
        event0.getParameterMap().put("par0",
                new AxField(new AxReferenceKey(event0.getKey(), "par0"), axSchema0.getKey()));
        event0.getParameterMap().put("par1",
                new AxField(new AxReferenceKey(event0.getKey(), "par1"), axSchema1.getKey()));
        event0.getParameterMap().put("par2",
                new AxField(new AxReferenceKey(event0.getKey(), "par2"), axSchema2.getKey()));
        event0.getParameterMap().put("par3",
                new AxField(new AxReferenceKey(event0.getKey(), "par3"), axSchema6.getKey()));
        event0.getParameterMap().put("par4",
                new AxField(new AxReferenceKey(event0.getKey(), "par4"), axSchema4.getKey()));
        event0.getParameterMap().put("par5",
                new AxField(new AxReferenceKey(event0.getKey(), "par5"), axSchema5.getKey()));
        event0.getParameterMap().put("par6",
                new AxField(new AxReferenceKey(event0.getKey(), "par6"), axSchema5.getKey()));
        eventMap.getEventMap().put(event0.getKey(), event0);

        final AxEvent event1 = new AxEvent(new AxArtifactKey("event1", "0.0.1"),
                "org.onap.policy.apex.model.eventmodel.events", "Source", "Target");
        event1.getParameterMap().put("theOnlyPar",
                new AxField(new AxReferenceKey(event1.getKey(), "theOnlyPar"), axSchema3.getKey()));
        eventMap.getEventMap().put(event1.getKey(), event1);

        final AxEvent event2 = new AxEvent(new AxArtifactKey("event2", "0.0.1"),
                "org.onap.policy.apex.model.eventmodel.events", "Source", "Target");
        eventMap.getEventMap().put(event2.getKey(), event2);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));

        final AxEventModel eventModel =
                new AxEventModel(new AxArtifactKey("EventModel", "0.0.1"), dataTypes, keyInformation, eventMap);
        keyInformation.generateKeyInfo(eventModel);

        eventModel.validate(new AxValidationResult());
        return eventModel;
    }

    @Override
    public AxEventModel getInvalidModel() {
        final AxContextSchema axSchema0 =
                new AxContextSchema(new AxArtifactKey("BooleanType", "0.0.1"), "Java", "java.lang.Zoolean");
        final AxContextSchema axSchema1 =
                new AxContextSchema(new AxArtifactKey("IntType", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema axSchema2 =
                new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema axSchema3 =
                new AxContextSchema(new AxArtifactKey("SetType", "0.0.1"), "Java", "java.util.Set");
        final AxContextSchema axSchema4 =
                new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java", "java.util.Map");
        final AxContextSchema axSchema5 =
                new AxContextSchema(new AxArtifactKey("BigIntType", "0.0.1"), "Java", "java.math.BigInteger");

        final AxContextSchemas dataTypes = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        dataTypes.getSchemasMap().put(axSchema0.getKey(), axSchema0);
        dataTypes.getSchemasMap().put(axSchema1.getKey(), axSchema1);
        dataTypes.getSchemasMap().put(axSchema2.getKey(), axSchema2);
        dataTypes.getSchemasMap().put(axSchema3.getKey(), axSchema3);
        dataTypes.getSchemasMap().put(axSchema4.getKey(), axSchema4);
        dataTypes.getSchemasMap().put(axSchema5.getKey(), axSchema5);

        final AxEvents eventMap = new AxEvents(new AxArtifactKey("smallEventMap", "0.0.1"));

        final AxEvent event0 =
                new AxEvent(new AxArtifactKey("event0", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        event0.getParameterMap().put("par0",
                new AxField(new AxReferenceKey(event0.getKey(), "par0"), axSchema0.getKey()));
        event0.getParameterMap().put("par1",
                new AxField(new AxReferenceKey(event0.getKey(), "par1"), axSchema1.getKey()));
        event0.getParameterMap().put("par2",
                new AxField(new AxReferenceKey(event0.getKey(), "par2"), axSchema2.getKey()));
        event0.getParameterMap().put("par3",
                new AxField(new AxReferenceKey(event0.getKey(), "par3"), axSchema3.getKey()));
        event0.getParameterMap().put("par4",
                new AxField(new AxReferenceKey(event0.getKey(), "par4"), axSchema4.getKey()));
        event0.getParameterMap().put("par5",
                new AxField(new AxReferenceKey(event0.getKey(), "par5"), axSchema5.getKey()));
        event0.getParameterMap().put("par6",
                new AxField(new AxReferenceKey(event0.getKey(), "par6"), axSchema5.getKey()));
        eventMap.getEventMap().put(event0.getKey(), event0);

        final AxEvent event1 =
                new AxEvent(new AxArtifactKey("event1", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        event1.getParameterMap().put("theOnlyPar",
                new AxField(new AxReferenceKey(event0.getKey(), "theOnlyPar"), axSchema3.getKey()));
        eventMap.getEventMap().put(event1.getKey(), event1);

        final AxEvent event2 =
                new AxEvent(new AxArtifactKey("event2", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        eventMap.getEventMap().put(event2.getKey(), event1);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));

        final AxEventModel eventModel =
                new AxEventModel(new AxArtifactKey("smallEventModel", "0.0.1"), dataTypes, keyInformation, eventMap);

        return eventModel;
    }

    @Override
    public AxEventModel getMalstructuredModel() {
        final AxContextSchema axSchema3 =
                new AxContextSchema(new AxArtifactKey("SetType", "0.0.1"), "Java", "java.util.Set");
        final AxContextSchemas dataTypes = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        dataTypes.getSchemasMap().put(axSchema3.getKey(), axSchema3);

        final AxEvents eventMap = new AxEvents(new AxArtifactKey("smallEventMap", "0.0.1"));

        final AxEvent event1 =
                new AxEvent(new AxArtifactKey("event1", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        event1.getParameterMap().put("theOnlyPar",
                new AxField(new AxReferenceKey(event1.getKey(), "theOnlyPar"), axSchema3.getKey()));
        eventMap.getEventMap().put(event1.getKey(), event1);

        final AxEvent event2 =
                new AxEvent(new AxArtifactKey("event2", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        eventMap.getEventMap().put(event2.getKey(), event1);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));

        final AxEventModel eventModel =
                new AxEventModel(new AxArtifactKey("smallEventModel", "0.0.1"), dataTypes, keyInformation, eventMap);

        eventModel.validate(new AxValidationResult());

        return eventModel;
    }

    @Override
    public AxEventModel getWarningModel() {
        final AxContextSchema axSchema0 =
                new AxContextSchema(new AxArtifactKey("BooleanType", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema axSchema1 =
                new AxContextSchema(new AxArtifactKey("IntType", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema axSchema2 =
                new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema axSchema3 =
                new AxContextSchema(new AxArtifactKey("SetType", "0.0.1"), "Java", "java.util.Set");
        final AxContextSchema axSchema4 =
                new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java", "java.util.Map");
        final AxContextSchema axSchema5 =
                new AxContextSchema(new AxArtifactKey("BigIntType", "0.0.1"), "Java", "java.math.BigInteger");
        final AxContextSchemas dataTypes = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        dataTypes.getSchemasMap().put(axSchema0.getKey(), axSchema0);
        dataTypes.getSchemasMap().put(axSchema1.getKey(), axSchema1);
        dataTypes.getSchemasMap().put(axSchema2.getKey(), axSchema2);
        dataTypes.getSchemasMap().put(axSchema3.getKey(), axSchema3);
        dataTypes.getSchemasMap().put(axSchema4.getKey(), axSchema4);
        dataTypes.getSchemasMap().put(axSchema5.getKey(), axSchema5);

        final AxEvents eventMap = new AxEvents(new AxArtifactKey("smallEventMap", "0.0.1"));

        final AxEvent event0 = new AxEvent(new AxArtifactKey("event0", "0.0.1"), "");
        event0.getParameterMap().put("par0",
                new AxField(new AxReferenceKey(event0.getKey(), "par0"), axSchema0.getKey()));
        event0.getParameterMap().put("par1",
                new AxField(new AxReferenceKey(event0.getKey(), "par1"), axSchema1.getKey()));
        event0.getParameterMap().put("par2",
                new AxField(new AxReferenceKey(event0.getKey(), "par2"), axSchema2.getKey()));
        event0.getParameterMap().put("par3",
                new AxField(new AxReferenceKey(event0.getKey(), "par3"), axSchema3.getKey()));
        event0.getParameterMap().put("par4",
                new AxField(new AxReferenceKey(event0.getKey(), "par4"), axSchema4.getKey()));
        event0.getParameterMap().put("par5",
                new AxField(new AxReferenceKey(event0.getKey(), "par5"), axSchema5.getKey()));
        eventMap.getEventMap().put(event0.getKey(), event0);

        final AxEvent event1 = new AxEvent(new AxArtifactKey("event1", "0.0.1"), "");
        event1.getParameterMap().put("theOnlyPar",
                new AxField(new AxReferenceKey(event1.getKey(), "theOnlyPar"), axSchema3.getKey()));
        eventMap.getEventMap().put(event1.getKey(), event1);

        final AxEvent event2 = new AxEvent(new AxArtifactKey("event2", "0.0.1"), "");
        eventMap.getEventMap().put(event2.getKey(), event2);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));

        final AxEventModel eventModel =
                new AxEventModel(new AxArtifactKey("smallEventModel", "0.0.1"), dataTypes, keyInformation, eventMap);
        eventModel.getKeyInformation().generateKeyInfo(eventModel);
        eventModel.validate(new AxValidationResult());

        return eventModel;
    }

    @Override
    public AxEventModel getObservationModel() {
        final AxContextSchema axSchema0 =
                new AxContextSchema(new AxArtifactKey("BooleanType", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema axSchema1 =
                new AxContextSchema(new AxArtifactKey("IntType", "0.0.1"), "Java", "java.lang.Integer");
        final AxContextSchema axSchema2 =
                new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java", "java.lang.String");
        final AxContextSchema axSchema3 =
                new AxContextSchema(new AxArtifactKey("SetType", "0.0.1"), "Java", "java.util.Set");
        final AxContextSchema axSchema4 =
                new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java", "java.util.Map");
        final AxContextSchema axSchema5 =
                new AxContextSchema(new AxArtifactKey("BigIntType", "0.0.1"), "Java", "java.math.BigInteger");
        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(axSchema0.getKey(), axSchema0);
        schemas.getSchemasMap().put(axSchema1.getKey(), axSchema1);
        schemas.getSchemasMap().put(axSchema2.getKey(), axSchema2);
        schemas.getSchemasMap().put(axSchema3.getKey(), axSchema3);
        schemas.getSchemasMap().put(axSchema4.getKey(), axSchema4);
        schemas.getSchemasMap().put(axSchema5.getKey(), axSchema5);

        final AxEvents eventMap = new AxEvents(new AxArtifactKey("smallEventMap", "0.0.1"));

        final AxEvent event0 =
                new AxEvent(new AxArtifactKey("event0", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        event0.getParameterMap().put("par0",
                new AxField(new AxReferenceKey(event0.getKey(), "par0"), axSchema0.getKey()));
        event0.getParameterMap().put("par1",
                new AxField(new AxReferenceKey(event0.getKey(), "par1"), axSchema1.getKey()));
        event0.getParameterMap().put("par2",
                new AxField(new AxReferenceKey(event0.getKey(), "par2"), axSchema2.getKey()));
        event0.getParameterMap().put("par3",
                new AxField(new AxReferenceKey(event0.getKey(), "par3"), axSchema3.getKey()));
        event0.getParameterMap().put("par4",
                new AxField(new AxReferenceKey(event0.getKey(), "par4"), axSchema4.getKey()));
        event0.getParameterMap().put("par5",
                new AxField(new AxReferenceKey(event0.getKey(), "par5"), axSchema5.getKey()));
        eventMap.getEventMap().put(event0.getKey(), event0);

        final AxEvent event1 =
                new AxEvent(new AxArtifactKey("event1", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        event1.getParameterMap().put("theOnlyPar",
                new AxField(new AxReferenceKey(event1.getKey(), "theOnlyPar"), axSchema3.getKey()));
        eventMap.getEventMap().put(event1.getKey(), event1);

        final AxEvent event2 =
                new AxEvent(new AxArtifactKey("event2", "0.0.1"), "org.onap.policy.apex.model.eventmodel.events");
        eventMap.getEventMap().put(event2.getKey(), event2);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));

        final AxEventModel eventModel =
                new AxEventModel(new AxArtifactKey("smallEventModel", "0.0.1"), schemas, keyInformation, eventMap);
        eventModel.getKeyInformation().generateKeyInfo(eventModel);
        eventModel.validate(new AxValidationResult());

        return eventModel;
    }
}
