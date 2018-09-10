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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * The Class is used to return the tree that represents the state branches or chains in a policy. It
 * creates a tree that holds the state fan out branches in a policy that starts from the given top
 * state of the tree. Each branch from a state is held in a set of next states for the top state and
 * each branch in the state tree is itself a {@link AxStateTree} instance.
 * 
 * <p>Validation checks for recursive state use, in other words validation forbids the use of a given
 * state more than once in a state tree.
 */
public class AxStateTree implements Comparable<AxStateTree> {
    private final AxState thisState;
    private final Set<AxStateTree> nextStates;

    /**
     * This constructor recursively creates a state tree for the given policy starting at the given
     * state.
     *
     * @param policy the policy from which to read states
     * @param thisState the state to start state tree construction at
     * @param referencedStateNameSet a set of state names already referenced in the tree, null for
     *        the first recursive call
     */
    public AxStateTree(final AxPolicy policy, final AxState thisState, Set<AxReferenceKey> referencedStateNameSet) {
        Assertions.argumentNotNull(policy, "policy may not be null");
        Assertions.argumentNotNull(thisState, "thisState may not be null");

        this.thisState = thisState;
        nextStates = new TreeSet<>();

        for (final AxStateOutput stateOutput : thisState.getStateOutputs().values()) {
            final AxState nextState = policy.getStateMap().get(stateOutput.getNextState().getLocalName());

            // Check for end of state branch
            if (stateOutput.getNextState().equals(AxReferenceKey.getNullKey())) {
                continue;
            }

            if (referencedStateNameSet == null) {
                referencedStateNameSet = new LinkedHashSet<>();
                referencedStateNameSet.add(thisState.getKey());
            }

            // Check for state tree loops
            if (referencedStateNameSet.contains(nextState.getKey())) {
                throw new PolicyRuntimeException("loop detected in state tree for policy " + policy.getId() + " state "
                        + thisState.getKey().getLocalName() + ", next state " + nextState.getKey().getLocalName()
                        + " referenced more than once");
            }
            referencedStateNameSet.add(stateOutput.getNextState());
            nextStates.add(new AxStateTree(policy, nextState, referencedStateNameSet));
        }
    }

    /**
     * Gets the state for this state tree node.
     *
     * @return the state
     */
    public AxState getState() {
        return thisState;
    }

    /**
     * Gets the next states for this state tree node.
     *
     * @return the next states
     */
    public Set<AxStateTree> getNextStates() {
        return nextStates;
    }

    /**
     * Gets the list of states referenced by this state tree as a list.
     *
     * @return the list of states referenced
     */
    public List<AxState> getReferencedStateList() {
        final List<AxState> referencedStateList = new ArrayList<>();

        referencedStateList.add(thisState);
        for (final AxStateTree nextStateTree : nextStates) {
            referencedStateList.addAll(nextStateTree.getReferencedStateList());
        }

        return referencedStateList;
    }

    /**
     * Gets the list of states referenced by this state tree as a set.
     *
     * @return the set of states referenced
     */
    public Set<AxState> getReferencedStateSet() {
        final Set<AxState> referencedStateSet = new TreeSet<>();

        referencedStateSet.add(thisState);
        for (final AxStateTree nextStateTree : nextStates) {
            referencedStateSet.addAll(nextStateTree.getReferencedStateSet());
        }

        return referencedStateSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AxStateTree otherObj) {
        Assertions.argumentNotNull(otherObj, "comparison object may not be null");

        if (this == otherObj) {
            return 0;
        }

        final AxStateTree other = otherObj;
        if (!thisState.equals(other.thisState)) {
            return thisState.compareTo(other.thisState);
        }
        if (!nextStates.equals(other.nextStates)) {
            return (nextStates.hashCode() - other.nextStates.hashCode());
        }
        return 0;
    }
}
