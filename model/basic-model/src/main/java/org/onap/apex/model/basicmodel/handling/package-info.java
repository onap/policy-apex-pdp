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

/**
 * Contains a number of utility classes for handling APEX {@link org.onap.apex.model.basicmodel.concepts.AxModel} models and
 * {@link org.onap.apex.model.basicmodel.concepts.AxConcept} concepts.
 * Classes to read and write models to files, strings, and databases are included, as
 * well as classes to generate XML schemas for models.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

@XmlSchema(namespace = "http://www.ericsson.com/apex", elementFormDefault = XmlNsForm.QUALIFIED, xmlns = {
        @XmlNs(namespaceURI = "http://www.ericsson.com/apex", prefix = "") })

package org.onap.apex.model.basicmodel.handling;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
