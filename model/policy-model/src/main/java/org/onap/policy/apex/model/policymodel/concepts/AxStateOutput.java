/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import java.util.List;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class defines a single output that a state can have. A state can have many outputs with each
 * output defined as an instance of this class. Each state output defines the output event that will
 * be emitted when this output is selected and optionally the next state that is executed when this
 * state output is selected. If no next state is defined (the next state is a null
 * {@link AxReferenceKey} key), then this state output outputs its event to an external system and
 * is an output state for the full policy.
 *
 * <p>During validation of a state output, the validation checks listed below are executed:
 * <ol>
 * <li>The state output key must not be a null key and must be valid, see validation in
 * {@link AxReferenceKey}
 * <li>The outgoing event key must not be a null key and must be valid, see validation in
 * {@link AxArtifactKey}
 * <li>The next state key must be valid, see validation in {@link AxReferenceKey}
 * </ol>
 */

@Entity
@Table(name = "AxStateOutput")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexStateOutput", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxStateOutput", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"key", "outgoingEvent", "outgoingEventSet", "nextState"})
@Getter
@Setter
public class AxStateOutput extends AxConcept {
    private static final long serialVersionUID = 8041771382337655535L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    @NonNull
    private AxReferenceKey key;

    // @formatter:off
    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "outgoingEventName"))
    @AttributeOverride(name = "version", column = @Column(name = "outgoingEventVersion"))
    @Column(name = "outgoingEvent")
    @XmlElement(required = true)
    @NonNull
    private AxArtifactKey outgoingEvent;

    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "stateParentKeyName", referencedColumnName = "parentKeyName"),
        @JoinColumn(name = "stateParentKeyVersion", referencedColumnName = "parentKeyVersion"),
        @JoinColumn(name = "stateParentLocalName", referencedColumnName = "parentLocalName"),
        @JoinColumn(name = "stateLocalName", referencedColumnName = "localName")})
    @XmlElement(name = "outgoingEventReference", required = false)
    private Set<AxArtifactKey> outgoingEventSet;

    @Embedded
    @AttributeOverride(name = "parentKeyName", column = @Column(name = "nextStateParentKeyName"))
    @AttributeOverride(name = "parentKeyVersion", column = @Column(name = "nextStateParentKeyVersion"))
    @AttributeOverride(name = "parentLocalName", column = @Column(name = "nextStateParentLocalName"))
    @AttributeOverride(name = "localName", column = @Column(name = "nextStateLocalName"))
    @Column(name = "nextState")
    @XmlElement(required = true)
    @NonNull
    private AxReferenceKey nextState;
    // @formatter:on

    /**
     * The Default Constructor creates a state output instance with a null reference key, outgoing
     * event key and next state reference key.
     */
    public AxStateOutput() {
        this(new AxReferenceKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxStateOutput(final AxStateOutput copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a state output instance with the given reference key, outgoing
     * event key and next state reference key.
     *
     * @param key the reference key for the state output
     */
    public AxStateOutput(final AxReferenceKey key) {
        this(key, // Key
                AxArtifactKey.getNullKey(), // Outgoing Event
                AxReferenceKey.getNullKey() // Next State
        );
    }

    /**
     * This Constructor creates a state output with a reference key composed of the given parent key
     * and with a local name composed from the parent key local name concatenated with the next
     * state's local name. The next state and outgoing event of the state output are set as
     * specified.
     *
     * @param parentKey the parent key of the state output
     * @param nextState the next state to which execution will pass on use of this state output
     * @param outgoingEvent the outgoing event emitted on use of this state output
     */
    public AxStateOutput(final AxReferenceKey parentKey, final AxReferenceKey nextState,
            final AxArtifactKey outgoingEvent) {
        this(new AxReferenceKey(parentKey, parentKey.getLocalName() + '_' + nextState.getLocalName()), outgoingEvent,
                nextState);
    }

    /**
     * This Constructor creates a state output with the specified reference key. The next state and
     * outgoing event of the state output are set as specified.
     *
     * @param key the key
     * @param nextState the next state to which execution will pass on use of this state output
     * @param outgoingEvent the outgoing event emitted on use of this state output
     */
    public AxStateOutput(final AxReferenceKey key, final AxArtifactKey outgoingEvent, final AxReferenceKey nextState) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(outgoingEvent, "outgoingEvent may not be null");
        Assertions.argumentNotNull(nextState, "nextState may not be null");

        this.key = key;
        this.outgoingEvent = outgoingEvent;
        this.nextState = nextState;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();
        keyList.add(new AxKeyUse(outgoingEvent));

        if (!nextState.equals(AxReferenceKey.getNullKey())) {
            keyList.add(new AxKeyUse(nextState));
        }

        return keyList;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (outgoingEvent.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "outgoingEvent reference is a null key, an outgoing event must be specified"));
        }
        result = outgoingEvent.validate(result);

        // Note: Null keys are allowed on nextState as there may not be a next state
        result = nextState.validate(result);

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        outgoingEvent.clean();
        nextState.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("stateKey=");
        builder.append(key);
        builder.append(",outgoingEvent=");
        builder.append(outgoingEvent);
        builder.append(",nextState=");
        builder.append(nextState);
        builder.append(")");
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxStateOutput.class);

        final AxStateOutput copy = ((AxStateOutput) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setOutgoingEvent(new AxArtifactKey(outgoingEvent));
        copy.setNextState(nextState);

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + outgoingEvent.hashCode();
        result = prime * result + nextState.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxStateOutput other = (AxStateOutput) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!outgoingEvent.equals(other.outgoingEvent)) {
            return false;
        }
        return nextState.equals(other.nextState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null) {
            return -1;
        }
        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxStateOutput other = (AxStateOutput) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!outgoingEvent.equals(other.outgoingEvent)) {
            return outgoingEvent.compareTo(other.outgoingEvent);
        }
        return nextState.compareTo(other.nextState);
    }
}
