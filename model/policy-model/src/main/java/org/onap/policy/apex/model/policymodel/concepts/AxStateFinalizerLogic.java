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

package org.onap.policy.apex.model.policymodel.concepts;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

/**
 * This class holds State Finalizer Logic for {@link AxState} states in Apex. It is a specialization
 * of the {@link AxLogic} class, so that State Finalizer Logic in Apex states can be strongly typed.
 * <p>
 * State Finalizer Logic is used to select the output {@link AxStateOutput} that a state will use.
 * The logic uses fields emitted by the executed {@link AxTask} task and information from the
 * context albums available on a state to decide what state output {@link AxStateOutput} to select
 * in a given context. State Finalizer Logic must marshal the output fields from the task onto the
 * output event in whatever manner is appropriate for the domain being handled.
 * <p>
 * Validation uses standard Apex Logic validation, see validation in {@link AxLogic}.
 */
@Entity
@Table(name = "AxStateFinalizerLogic")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexStateFinalizerLogic", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxStateFinalizerLogic", namespace = "http://www.onap.org/policy/apex-pdp")

public class AxStateFinalizerLogic extends AxLogic {
    private static final long serialVersionUID = 2090324845463750391L;

    /**
     * The Default Constructor creates a logic instance with a null key, undefined logic flavour and
     * a null logic string.
     */
    public AxStateFinalizerLogic() {
        super();
    }

    /**
     * The Key Constructor creates a logic instance with the given reference key, undefined logic
     * flavour and a null logic string.
     *
     * @param key the reference key of the logic
     */
    public AxStateFinalizerLogic(final AxReferenceKey key) {
        super(key, LOGIC_FLAVOUR_UNDEFINED, "");
    }

    /**
     * This Constructor creates a logic instance with a reference key constructed from the parents
     * key and the logic local name and all of its fields defined.
     *
     * @param parentKey the reference key of the parent of this logic
     * @param logicName the logic name, held as the local name of the reference key of this logic
     * @param logicFlavour the flavour of this logic
     * @param logic the actual logic as a string
     */
    public AxStateFinalizerLogic(final AxReferenceKey parentKey, final String logicName, final String logicFlavour,
            final String logic) {
        super(parentKey, logicName, logicFlavour, logic);
    }

    /**
     * This Constructor creates a logic instance with the given reference key and all of its fields
     * defined.
     *
     * @param key the reference key of this logic
     * @param logicFlavour the flavour of this logic
     * @param logic the actual logic as a string
     */
    public AxStateFinalizerLogic(final AxReferenceKey key, final String logicFlavour, final String logic) {
        super(key, logicFlavour, logic);
    }

    /**
     * This Constructor creates a logic instance by cloning the fields from another logic instance
     * into this logic instance.
     *
     * @param logic the logic instance to clone from
     */
    public AxStateFinalizerLogic(final AxLogic logic) {
        super(new AxReferenceKey(logic.getKey()), logic.getLogicFlavour(), logic.getLogic());
    }

    /**
     * This Constructor creates a logic instance with a reference key constructed from the parents
     * key and the logic local name, the given logic flavour, with the logic being provided by the
     * given logic reader instance.
     *
     * @param parentKey the reference key of the parent of this logic
     * @param logicName the logic name, held as the local name of the reference key of this logic
     * @param logicFlavour the flavour of this logic
     * @param logicReader the logic reader to use to read the logic for this logic instance
     */
    public AxStateFinalizerLogic(final AxReferenceKey parentKey, final String logicName, final String logicFlavour,
            final AxLogicReader logicReader) {
        super(new AxReferenceKey(parentKey, logicName), logicFlavour, logicReader);
    }

    /**
     * This Constructor creates a logic instance with the given reference key and logic flavour, the
     * logic is provided by the given logic reader instance.
     *
     * @param key the reference key of this logic
     * @param logicFlavour the flavour of this logic
     * @param logicReader the logic reader to use to read the logic for this logic instance
     */
    public AxStateFinalizerLogic(final AxReferenceKey key, final String logicFlavour, final AxLogicReader logicReader) {
        super(key, logicFlavour, logicReader);
    }
}
