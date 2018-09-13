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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.onap.policy.apex.plugins.event.protocol.xml.jaxb.XMLApexEvent;
import org.onap.policy.apex.plugins.event.protocol.xml.jaxb.XMLApexEventData;

/**
 * The Class TestKafkaXmlEventProducer.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestKafkaXmlEventProducer {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:49092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        final XMLApexEvent xmlEvent = new XMLApexEvent();
        xmlEvent.setName("XMLEvent-1");
        xmlEvent.setVersion("0.0.1");
        xmlEvent.getData().add(new XMLApexEventData("Data-1", "Data Value -1"));

        final Producer<String, String> producer = new KafkaProducer<String, String>(props);
        for (int i = 0; i < 100; i++) {
            xmlEvent.setName("XMLEvent" + Integer.toString(i));
            xmlEvent.setVersion("0.0.1");
            xmlEvent.getData()
                    .add(new XMLApexEventData("Data" + Integer.toString(i), "Data Value " + Integer.toString(i)));

            producer.send(new ProducerRecord<String, String>("apex-in-0", xmlEvent.getName(), xmlEvent.toString()));
        }
        producer.close();
    }
}
