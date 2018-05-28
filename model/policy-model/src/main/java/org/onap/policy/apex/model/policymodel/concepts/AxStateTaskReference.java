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

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class defines the type of output handling that will be used when a task in a state completes
 * execution. Each task {@link AxTask} in a state {@link AxState} must select a state output
 * {@link AxStateOutput} in order to pass its fields to an output event. Therefore, each task has an
 * associated instance of this class that defines how the state output of the state is selected and
 * how the output fields of the task are marshaled onto the fields of the output event. A
 * {@link AxStateTaskReference} instance defines the task output handling as either
 * {@link AxStateTaskOutputType#DIRECT} or {@link AxStateTaskOutputType#LOGIC}. In the case of
 * {@link AxStateTaskOutputType#DIRECT} output selection, the output reference key held in this
 * {@link AxStateTaskReference} instance to an instance of an {@link AxStateOutput} class. In the
 * case of {@link AxStateTaskOutputType#LOGIC} output selection, the output reference key held in
 * this {@link AxStateTaskReference} instance to an instance of an {@link AxStateFinalizerLogic}
 * class. See the explanation in the {@link AxState} class for a full description of this handling.
 * <p>
 *
 * During validation of a state task reference, the validation checks listed below are executed:
 * <ol>
 * <li>The state task reference key must not be a null key and must be valid, see validation in
 * {@link AxReferenceKey}
 * <li>The output type must be defined, that is not equal to {@link AxStateTaskOutputType#UNDEFINED}
 * <li>The output key must not be a null key and must be valid, see validation in
 * {@link AxReferenceKey}
 * </ol>
 */

@Entity
@Table(name = "AxStateTaskReference")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexStateTaskReference", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxStateTaskReference", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"key", "outputType", "output"})

public class AxStateTaskReference extends AxConcept {
    private static final long serialVersionUID = 8041771382337655535L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxReferenceKey key;

    @Enumerated
    @Column(name = "outputType")
    @XmlElement(required = true)
    private AxStateTaskOutputType outputType;

    // @formatter:off
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "parentKeyName", column = @Column(name = "outputParentKeyName")),
            @AttributeOverride(name = "parentKeyVersion", column = @Column(name = "outputParentKeyVersion")),
            @AttributeOverride(name = "parentLocalName", column = @Column(name = "outputParentLocalName")),
            @AttributeOverride(name = "localName", column = @Column(name = "outputLocalName"))})
    @Column(name = "output")
    @XmlElement(required = true)
    private AxReferenceKey output;
    // @formatter:on

    /**
     * The Default Constructor creates a state task reference with a null reference key, an
     * undefined output type and a null output reference key.
     */
    public AxStateTaskReference() {
        this(new AxReferenceKey());
    }

    /**
     * Copy constructor
     * 
     * @param copyConcept the concept to copy from
     */
    public AxStateTaskReference(final AxStateTaskReference copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a state task reference with the given reference key, an
     * undefined output type and a null output reference key.
     *
     * @param key the key
     */
    public AxStateTaskReference(final AxReferenceKey key) {
        this(key, // Key
                AxStateTaskOutputType.UNDEFINED, // Output type
                AxReferenceKey.getNullKey()); // Output
    }

    /**
     * This Constructor creates a state task reference instance with a reference key composed from
     * the given parent key with a local name composed by concatenating the name of the task key
     * with the local name of the output. The output type and output are set to the given values.
     *
     * @param parentKey the parent key to use for the key of the state task reference
     * @param taskKey the task key to use for the first part of the state task reference local name
     * @param outputType the type of output to perform when this state task reference instance is
     *        used
     * @param output the output to perform when this state task reference instance is used
     */
    public AxStateTaskReference(final AxReferenceKey parentKey, final AxArtifactKey taskKey,
            final AxStateTaskOutputType outputType, final AxReferenceKey output) {
        this(new AxReferenceKey(parentKey, taskKey.getName() + '_' + outputType.name() + '_' + output.getLocalName()),
                outputType, output);
    }

    /**
     * This Constructor creates a state task reference instance with the given reference key and all
     * its fields defined.
     *
     * @param key the key of the state task reference
     * @param outputType the type of output to perform when this state task reference instance is
     *        used
     * @param output the output to perform when this state task reference instance is used
     */
    public AxStateTaskReference(final AxReferenceKey key, final AxStateTaskOutputType outputType,
            final AxReferenceKey output) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(outputType, "outputType may not be null");
        Assertions.argumentNotNull(output, "output may not be null");

        this.key = key;
        this.outputType = outputType;
        this.output = output;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxReferenceKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();

        if (!output.equals(AxReferenceKey.getNullKey())) {
            keyList.add(new AxKeyUse(output));
        }

        return keyList;
    }

    /**
     * Sets the key of the state task reference.
     *
     * @param key the key of the state task reference
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the type of output to perform when this state task reference instance is used.
     *
     * @return the the type of output to perform when this state task reference instance is used
     */
    public AxStateTaskOutputType getStateTaskOutputType() {
        return outputType;
    }

    /**
     * Sets the type of output to perform when this state task reference instance is used.
     *
     * @param stateTaskOutputType the type of output to perform when this state task reference
     *        instance is used
     */
    public void setStateTaskOutputType(final AxStateTaskOutputType stateTaskOutputType) {
        Assertions.argumentNotNull(stateTaskOutputType, "outputType may not be null");
        this.outputType = stateTaskOutputType;
    }

    /**
     * Gets the output to perform when this state task reference instance is used.
     *
     * @return the output to perform when this state task reference instance is used
     */
    public AxReferenceKey getOutput() {
        return output;
    }

    /**
     * Sets the output to perform when this state task reference instance is used.
     *
     * @param output the output to perform when this state task reference instance is used
     */
    public void setOutput(final AxReferenceKey output) {
        Assertions.argumentNotNull(output, "output may not be null");
        this.output = output;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.
     * basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (outputType == AxStateTaskOutputType.UNDEFINED) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "outputType may not be UNDEFINED"));
        }

        if (output.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "output key " + output.getID() + " is a null key"));
        }
        result = output.validate(result);

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        output.clean();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("stateKey=");
        builder.append(key);
        builder.append(",outputType=");
        builder.append(outputType);
        builder.append(",output=");
        builder.append(output);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.
     * basicmodel.concepts.AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxStateTaskReference.class);

        final AxStateTaskReference copy = ((AxStateTaskReference) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setStateTaskOutputType(AxStateTaskOutputType.valueOf(outputType.name()));
        copy.setOutput(output);

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + outputType.hashCode();
        result = prime * result + output.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
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

        final AxStateTaskReference other = (AxStateTaskReference) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (outputType != other.outputType) {
            return false;
        }
        return output.equals(other.output);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
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

        final AxStateTaskReference other = (AxStateTaskReference) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!outputType.equals(other.outputType)) {
            return outputType.compareTo(other.outputType);
        }
        return output.compareTo(other.output);
    }
}
