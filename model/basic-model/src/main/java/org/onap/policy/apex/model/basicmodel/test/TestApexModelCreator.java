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

package org.onap.policy.apex.model.basicmodel.test;

import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelCreator;

/**
 * The Interface TestApexModelCreator is used to create models for Apex model tests. It is mainly used by unit tests for Apex domain models so that
 * developers can write test Java programs to create models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <M> the generic type
 */
public interface TestApexModelCreator<M extends AxModel> extends ApexModelCreator<M> {

    /**
     * Gets the malstructured model.
     *
     * @return the malstructured model
     */
    M getMalstructuredModel();

    /**
     * Gets the observation model.
     *
     * @return the observation model
     */
    M getObservationModel();

    /**
     * Gets the warning model.
     *
     * @return the warning model
     */
    M getWarningModel();

    /**
     * Gets the invalid model.
     *
     * @return the invalid model
     */
    M getInvalidModel();
}
