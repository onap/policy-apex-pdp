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

package org.onap.policy.apex.model.modelapi;

import java.util.Properties;

import org.onap.policy.apex.model.modelapi.impl.ApexModelImpl;

/**
 * A factory for creating ApexModel objects using the Apex Model implementation.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexModelFactory {

    /**
     * Creates a new ApexModel object from its implementation.
     *
     * @param apexProperties default values and other configuration information for the apex model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     * @return the apex model
     */
    public ApexModel createApexModel(final Properties apexProperties, final boolean jsonMode) {
        return new ApexModelImpl(setDefaultPropertyValues(apexProperties), jsonMode);
    }

    /**
     * Sets default property values for Apex properties that must be set for the Apex model
     * implementation if those properties are not already set.
     *
     * @param apexPropertiesIn the default property values
     * @return the properties
     */
    private Properties setDefaultPropertyValues(final Properties apexPropertiesIn) {
        Properties apexProperties = apexPropertiesIn;

        if (apexProperties == null) {
            apexProperties = new Properties();
        }

        if (apexProperties.getProperty("DEFAULT_CONCEPT_VERSION") == null) {
            apexProperties.setProperty("DEFAULT_CONCEPT_VERSION", "0.0.1");
        }
        if (apexProperties.getProperty("DEFAULT_EVENT_NAMESPACE") == null) {
            apexProperties.setProperty("DEFAULT_EVENT_NAMESPACE", "com.ericsson.apex");
        }
        if (apexProperties.getProperty("DEFAULT_EVENT_SOURCE") == null) {
            apexProperties.setProperty("DEFAULT_EVENT_SOURCE", "source");
        }
        if (apexProperties.getProperty("DEFAULT_EVENT_TARGET") == null) {
            apexProperties.setProperty("DEFAULT_EVENT_TARGET", "target");
        }
        if (apexProperties.getProperty("DEFAULT_POLICY_TEMPLATE") == null) {
            apexProperties.setProperty("DEFAULT_POLICY_TEMPLATE", "FREEFORM");
        }

        return apexProperties;
    }
}
