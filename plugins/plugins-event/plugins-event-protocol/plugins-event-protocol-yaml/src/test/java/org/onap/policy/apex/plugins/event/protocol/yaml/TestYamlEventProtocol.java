/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.plugins.event.protocol.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.HeaderDelimitedTextBlockReader;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlock;
import org.onap.policy.common.parameters.ParameterService;

/**
 * The Class TestYamlEventProtocol.
 */
public class TestYamlEventProtocol {

    /**
     * Register test events and schemas.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeClass
    public static void registerTestEventsAndSchemas() throws IOException {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());
        ParameterService.register(schemaParameters, true);

        AxContextSchemas schemas = new AxContextSchemas();

        AxContextSchema simpleIntSchema = new AxContextSchema(new AxArtifactKey("SimpleIntSchema", "0.0.1"), "JAVA",
                        "java.lang.Integer");
        schemas.getSchemasMap().put(simpleIntSchema.getKey(), simpleIntSchema);

        AxContextSchema simpleDoubleSchema = new AxContextSchema(new AxArtifactKey("SimpleDoubleSchema", "0.0.1"),
                        "JAVA", "java.lang.Double");
        schemas.getSchemasMap().put(simpleDoubleSchema.getKey(), simpleDoubleSchema);

        AxContextSchema simpleStringSchema = new AxContextSchema(new AxArtifactKey("SimpleStringSchema", "0.0.1"),
                        "JAVA", "java.lang.String");
        schemas.getSchemasMap().put(simpleStringSchema.getKey(), simpleStringSchema);

        AxContextSchema arrayListSchema = new AxContextSchema(new AxArtifactKey("ArrayListSchema", "0.0.1"), "JAVA",
                        "java.util.ArrayList");
        schemas.getSchemasMap().put(arrayListSchema.getKey(), arrayListSchema);

        AxContextSchema linkedHashMapSchema = new AxContextSchema(new AxArtifactKey("LinkedHashMapSchema", "0.0.1"),
                        "JAVA", "java.util.LinkedHashMap");
        schemas.getSchemasMap().put(linkedHashMapSchema.getKey(), linkedHashMapSchema);

        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxEvents events = new AxEvents();

        AxEvent testEvent0 = new AxEvent(new AxArtifactKey("TestEvent0", "0.0.1"));
        testEvent0.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        events.getEventMap().put(testEvent0.getKey(), testEvent0);

        AxEvent testEvent1 = new AxEvent(new AxArtifactKey("TestEvent1", "0.0.1"));
        testEvent1.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te1Field0 = new AxField(new AxReferenceKey(testEvent1.getKey(), "yaml_field"),
                        arrayListSchema.getKey());
        testEvent1.getParameterMap().put("yaml_field", te1Field0);
        events.getEventMap().put(testEvent1.getKey(), testEvent1);

        AxEvent testEvent2 = new AxEvent(new AxArtifactKey("TestEvent2", "0.0.1"));
        testEvent2.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te2Field0 = new AxField(new AxReferenceKey(testEvent2.getKey(), "hr"), simpleIntSchema.getKey());
        testEvent2.getParameterMap().put("hr", te2Field0);
        AxField te2Field1 = new AxField(new AxReferenceKey(testEvent2.getKey(), "avg"), simpleDoubleSchema.getKey());
        testEvent2.getParameterMap().put("avg", te2Field1);
        AxField te2Field2 = new AxField(new AxReferenceKey(testEvent2.getKey(), "rbi"), simpleIntSchema.getKey());
        testEvent2.getParameterMap().put("rbi", te2Field2);
        events.getEventMap().put(testEvent2.getKey(), testEvent2);

        AxEvent testEvent3 = new AxEvent(new AxArtifactKey("TestEvent3", "0.0.1"));
        testEvent3.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te3Field0 = new AxField(new AxReferenceKey(testEvent3.getKey(), "american"), arrayListSchema.getKey());
        testEvent3.getParameterMap().put("american", te3Field0);
        AxField te3Field1 = new AxField(new AxReferenceKey(testEvent3.getKey(), "national"), arrayListSchema.getKey());
        testEvent3.getParameterMap().put("national", te3Field1);
        events.getEventMap().put(testEvent3.getKey(), testEvent3);

        AxEvent testEvent4 = new AxEvent(new AxArtifactKey("TestEvent4", "0.0.1"));
        testEvent4.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te4Field0 = new AxField(new AxReferenceKey(testEvent4.getKey(), "yaml_field"),
                        arrayListSchema.getKey());
        testEvent4.getParameterMap().put("yaml_field", te4Field0);
        events.getEventMap().put(testEvent4.getKey(), testEvent4);

        AxEvent testEvent5 = new AxEvent(new AxArtifactKey("TestEvent5", "0.0.1"));
        testEvent5.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te5Field0 = new AxField(new AxReferenceKey(testEvent5.getKey(), "yaml_field"),
                        arrayListSchema.getKey());
        testEvent5.getParameterMap().put("yaml_field", te5Field0);
        events.getEventMap().put(testEvent5.getKey(), testEvent5);

        AxEvent testEvent6 = new AxEvent(new AxArtifactKey("TestEvent6", "0.0.1"));
        testEvent6.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te6Field0 = new AxField(new AxReferenceKey(testEvent6.getKey(), "MarkMcGwire"),
                        linkedHashMapSchema.getKey());
        testEvent6.getParameterMap().put("Mark McGwire", te6Field0);
        AxField te6Field1 = new AxField(new AxReferenceKey(testEvent6.getKey(), "SammySosa"),
                        linkedHashMapSchema.getKey());
        testEvent6.getParameterMap().put("Sammy Sosa", te6Field1);
        events.getEventMap().put(testEvent6.getKey(), testEvent6);

        AxEvent testEvent7 = new AxEvent(new AxArtifactKey("TestEvent7", "0.0.1"));
        testEvent7.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te7Field0 = new AxField(new AxReferenceKey(testEvent7.getKey(), "time"), simpleIntSchema.getKey());
        testEvent7.getParameterMap().put("time", te7Field0);
        AxField te7Field1 = new AxField(new AxReferenceKey(testEvent7.getKey(), "player"), simpleStringSchema.getKey());
        testEvent7.getParameterMap().put("player", te7Field1);
        AxField te7Field2 = new AxField(new AxReferenceKey(testEvent7.getKey(), "action"), simpleStringSchema.getKey());
        testEvent7.getParameterMap().put("action", te7Field2);
        events.getEventMap().put(testEvent7.getKey(), testEvent7);

        AxEvent testEvent8 = new AxEvent(new AxArtifactKey("TestEvent8", "0.0.1"));
        testEvent8.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te8Field0 = new AxField(new AxReferenceKey(testEvent8.getKey(), "hr"), arrayListSchema.getKey());
        testEvent8.getParameterMap().put("hr", te8Field0);
        AxField te8Field1 = new AxField(new AxReferenceKey(testEvent8.getKey(), "rbi"), arrayListSchema.getKey());
        testEvent8.getParameterMap().put("rbi", te8Field1);
        events.getEventMap().put(testEvent8.getKey(), testEvent8);

        AxEvent testEvent9 = new AxEvent(new AxArtifactKey("TestEvent9", "0.0.1"));
        testEvent9.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te9Field0 = new AxField(new AxReferenceKey(testEvent9.getKey(), "ChicagoCubs"),
                        arrayListSchema.getKey());
        testEvent9.getParameterMap().put("ChicagoCubs", te9Field0);
        AxField te9Field1 = new AxField(new AxReferenceKey(testEvent9.getKey(), "AtlantaBraves"),
                        arrayListSchema.getKey());
        testEvent9.getParameterMap().put("AtlantaBraves", te9Field1);
        events.getEventMap().put(testEvent9.getKey(), testEvent9);

        AxEvent testEvent10 = new AxEvent(new AxArtifactKey("TestEvent10", "0.0.1"));
        testEvent10.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te10Field0 = new AxField(new AxReferenceKey(testEvent10.getKey(), "yaml_field"),
                        arrayListSchema.getKey());
        testEvent10.getParameterMap().put("yaml_field", te10Field0);
        events.getEventMap().put(testEvent10.getKey(), testEvent10);

        AxEvent testEvent11 = new AxEvent(new AxArtifactKey("TestEvent11", "0.0.1"));
        testEvent11.setNameSpace("org.onap.policy.apex.plugins.event.protocol.yaml");
        AxField te11Field0 = new AxField(new AxReferenceKey(testEvent11.getKey(), "tosca_definitions_version"),
                        simpleStringSchema.getKey());
        testEvent11.getParameterMap().put("tosca_definitions_version", te11Field0);
        AxField te11Field1 = new AxField(new AxReferenceKey(testEvent11.getKey(), "description"),
                        simpleStringSchema.getKey(), true);
        testEvent11.getParameterMap().put("description", te11Field1);
        AxField te11Field2 = new AxField(new AxReferenceKey(testEvent11.getKey(), "node_types"),
                        linkedHashMapSchema.getKey(), true);
        testEvent11.getParameterMap().put("node_types", te11Field2);
        AxField te11Field3 = new AxField(new AxReferenceKey(testEvent11.getKey(), "topology_template"),
                        linkedHashMapSchema.getKey());
        testEvent11.getParameterMap().put("topology_template", te11Field3);
        events.getEventMap().put(testEvent11.getKey(), testEvent11);

        ModelService.registerModel(AxEvents.class, events);
    }

    /**
     * Unregister test events and schemas.
     */
    @AfterClass
    public static void unregisterTestEventsAndSchemas() {
        ModelService.clear();
        ParameterService.clear();
    }

    /**
     * Test yaml processing.
     *
     * @throws ApexEventException the apex event exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testYamlProcessing() throws ApexEventException, IOException {
        try {
            testYamlDecodeEncode("TestEvent0", 1, 0, "Empty0");
            fail("test should fail here");
        } catch (ApexEventException e) {
            assertEquals("event processing failed, event is null", e.getMessage());
        }

        testYamlDecodeEncode("TestEvent0", 1, 0, "Empty1");
        testYamlDecodeEncode("TestEvent1", 1, 1, "Collection0");
        testYamlDecodeEncode("TestEvent2", 1, 3, "Collection1");
        testYamlDecodeEncode("TestEvent3", 1, 2, "Collection2");
        testYamlDecodeEncode("TestEvent4", 1, 1, "Collection3");
        testYamlDecodeEncode("TestEvent5", 1, 1, "Collection4");
        testYamlDecodeEncode("TestEvent6", 1, 2, "Collection5");
        testYamlDecodeEncode("TestEvent1", 2, 1, "Structure0");
        testYamlDecodeEncode("TestEvent7", 2, 3, "Structure1");
        testYamlDecodeEncode("TestEvent8", 1, 2, "Structure2");
        testYamlDecodeEncode("TestEvent8", 1, 2, "Structure3");
        testYamlDecodeEncode("TestEvent9", 1, 2, "Structure4");
        testYamlDecodeEncode("TestEvent10", 1, 1, "Structure5");
        testYamlDecodeEncode("TestEvent11", 1, 4, "TOSCA0");
    }

    /**
     * Test yaml decode encode.
     *
     * @param eventName the event name
     * @param eventCount the event count
     * @param parCount the par count
     * @param fileName the file name
     * @throws ApexEventException the apex event exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void testYamlDecodeEncode(final String eventName, final int eventCount, final int parCount,
                    final String fileName) throws ApexEventException, IOException {
        YamlEventProtocolParameters parameters = new YamlEventProtocolParameters();
        parameters.setDelimiterAtStart(false);

        Apex2YamlEventConverter converter = new Apex2YamlEventConverter();
        converter.init(parameters);

        String filePath = "src/test/resources/yaml_in/" + fileName + ".yaml";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        HeaderDelimitedTextBlockReader reader = new HeaderDelimitedTextBlockReader(parameters);
        reader.init(fileInputStream);

        List<ApexEvent> eventList = new ArrayList<>();

        TextBlock textBlock;
        do {
            textBlock = reader.readTextBlock();

            eventList.addAll(converter.toApexEvent(eventName, textBlock.getText()));
        }
        while (!textBlock.isEndOfText());

        fileInputStream.close();

        assertEquals(eventCount, eventList.size());

        for (int eventNo = 0; eventNo < eventCount; eventNo++) {
            assertEquals(parCount, eventList.get(0).size());

            String eventYaml = (String) converter.fromApexEvent(eventList.get(eventNo));
            String expectedYaml = TextFileUtils
                            .getTextFileAsString("src/test/resources/yaml_out/" + fileName + '_' + eventNo + ".yaml");
            assertEquals(expectedYaml.replaceAll("\\s*", ""), eventYaml.replaceAll("\\s*", ""));
        }
    }
}
