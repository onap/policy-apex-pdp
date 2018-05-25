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

/**
 * Contains utility classes for managing marshaling and unmarshaling of APEX models using JAXB.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

@XmlSchema(namespace = "http://www.onap.org/policy/apex-pdp", elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = { @XmlNs(namespaceURI = "http://www.onap.org/policy/apex-pdp", prefix = "") })

package org.onap.policy.apex.model.basicmodel.xml;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
