/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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

import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

/**
 * This class holds Task Selection Logic for {@link AxState} states in Apex. It is a specialization
 * of the {@link AxLogic} class, so that Task Selection Logic in Apex states can be strongly typed.
 *
 * <p>Task Selection Logic is used to select the task {@link AxTask} that a state will execute. The
 * logic uses fields on the incoming trigger event and information from the context albums available
 * on a state to decide what task {@link AxTask} to select for execution in a given context.
 *
 * <p>Validation uses standard Apex Logic validation, see validation in {@link AxLogic}.
 */
public class AxTaskSelectionLogic extends AxLogic {
    private static final long serialVersionUID = 2090324845463750391L;

    /**
     * The Default Constructor creates a logic instance with a null key, undefined logic flavour and
     * a null logic string.
     */
    public AxTaskSelectionLogic() {
        super();
    }

    /**
     * The Key Constructor creates a logic instance with the given reference key, undefined logic
     * flavour and a null logic string.
     *
     * @param key the reference key of the logic
     */
    public AxTaskSelectionLogic(final AxReferenceKey key) {
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
    public AxTaskSelectionLogic(final AxReferenceKey parentKey, final String logicName, final String logicFlavour,
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
    public AxTaskSelectionLogic(final AxReferenceKey key, final String logicFlavour, final String logic) {
        super(key, logicFlavour, logic);
    }

    /**
     * This Constructor creates a logic instance by cloning the fields from another logic instance
     * into this logic instance.
     *
     * @param logic the logic instance to clone from
     */
    public AxTaskSelectionLogic(final AxLogic logic) {
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
    public AxTaskSelectionLogic(final AxReferenceKey parentKey, final String logicName, final String logicFlavour,
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
    public AxTaskSelectionLogic(final AxReferenceKey key, final String logicFlavour, final AxLogicReader logicReader) {
        super(key, logicFlavour, logicReader);
    }
}
