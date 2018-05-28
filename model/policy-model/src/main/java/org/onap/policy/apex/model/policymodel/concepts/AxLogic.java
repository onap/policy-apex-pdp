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

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.basicmodel.dao.converters.CDATAConditioner;
import org.onap.policy.apex.model.basicmodel.xml.AxReferenceKeyAdapter;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class holds Logic for executing a task or task selection on an Apex policy state. The
 * flavour of the logic describes the type of logic being used and it may be a language identifier
 * such as "Javascript" or "Jython". The logic itself is held as a string. The {@link AxLogic}
 * instance is used by the Apex engine to start an executor with the required flavour. Once the
 * executor is started, the Apex engine passes the logic to the executor and the executor executes
 * it. In the Apex engine, executors are deployed as plugins. Apex also provides the executor with
 * run-time context, which makes context such as input fields, output fields, and context albums
 * available to the task at runtime.
 * <p>
 * Validation checks that the logic key is valid, that the logic flavour is defined and is valid
 * when checked against the {@code LOGIC_FLAVOUR_REGEXP} regular expression, and that the specified
 * logic string is not null or blank.
 */

@Entity
@Table(name = "AxLogic")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexLogic", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxLogic", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"key", "logicFlavour", "logic"})

public class AxLogic extends AxConcept {
    private static final long serialVersionUID = -4260562004005697328L;

    private static final String WHITESPACE_REGEXP = "\\s+$";

    private static final String LOGIC_FLAVOUR_TOKEN = "logicFlavour";
    private static final String KEY_NULL_MESSAGE = "key may not be null";
    private static final String LOGIC_FLAVOUR_NULL_MESSAGE = "logicFlavour may not be null";
    private static final String LOGIC_NULL_MESSAGE = "logic may not be null";

    /** Regular expression that specifies the allowed characters in logic flavour tokens. */
    public static final String LOGIC_FLAVOUR_REGEXP = "[A-Za-z0-9\\-_]+";

    /** When logic flavour is undefined, it has this value. */
    public static final String LOGIC_FLAVOUR_UNDEFINED = "UNDEFINED";

    /** The maximum permissible size of a logic definition. */
    public static final int MAX_LOGIC_SIZE = 32672; // The maximum size supported by Apache Derby

    @EmbeddedId()
    @XmlElement(name = "key", required = true)
    @XmlJavaTypeAdapter(AxReferenceKeyAdapter.class)
    private AxReferenceKey key;

    @Column(name = LOGIC_FLAVOUR_TOKEN)
    @XmlElement(required = true)
    private String logicFlavour;

    @Column(name = "logic", length = MAX_LOGIC_SIZE)
    @Convert(converter = CDATAConditioner.class)
    @XmlJavaTypeAdapter(value = CDATAConditioner.class)
    @XmlElement(required = true)
    private String logic;

    /**
     * The Default Constructor creates a logic instance with a null key, undefined logic flavour and
     * a null logic string.
     */
    public AxLogic() {
        this(new AxReferenceKey());
        logicFlavour = LOGIC_FLAVOUR_UNDEFINED;
    }

    /**
     * Copy constructor
     * 
     * @param copyConcept the concept to copy from
     */
    public AxLogic(final AxLogic copyConcept) {
        super(copyConcept);
    }

    /**
     * The Key Constructor creates a logic instance with the given reference key, undefined logic
     * flavour and a null logic string.
     *
     * @param key the reference key of the logic
     */
    public AxLogic(final AxReferenceKey key) {
        this(key, LOGIC_FLAVOUR_UNDEFINED, "");
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
    public AxLogic(final AxReferenceKey parentKey, final String logicName, final String logicFlavour,
            final String logic) {
        this(new AxReferenceKey(parentKey, logicName), logicFlavour, logic);
    }

    /**
     * This Constructor creates a logic instance with the given reference key and all of its fields
     * defined.
     *
     * @param key the reference key of this logic
     * @param logicFlavour the flavour of this logic
     * @param logic the actual logic as a string
     */
    public AxLogic(final AxReferenceKey key, final String logicFlavour, final String logic) {
        super();
        Assertions.argumentNotNull(key, KEY_NULL_MESSAGE);
        Assertions.argumentNotNull(logicFlavour, LOGIC_FLAVOUR_NULL_MESSAGE);
        Assertions.argumentNotNull(logic, LOGIC_NULL_MESSAGE);

        this.key = key;
        this.logicFlavour = Assertions.validateStringParameter(LOGIC_FLAVOUR_TOKEN, logicFlavour, LOGIC_FLAVOUR_REGEXP);
        this.logic = logic.replaceAll(WHITESPACE_REGEXP, "");
    }

    /**
     * This Constructor creates a logic instance with the given reference key and logic flavour, the
     * logic is provided by the given logic reader instance.
     *
     * @param key the reference key of this logic
     * @param logicFlavour the flavour of this logic
     * @param logicReader the logic reader to use to read the logic for this logic instance
     */
    public AxLogic(final AxReferenceKey key, final String logicFlavour, final AxLogicReader logicReader) {
        super();
        Assertions.argumentNotNull(key, KEY_NULL_MESSAGE);
        Assertions.argumentNotNull(logicFlavour, LOGIC_FLAVOUR_NULL_MESSAGE);
        Assertions.argumentNotNull(logicReader, "logicReader may not be null");

        this.key = key;
        this.logicFlavour = Assertions.validateStringParameter(LOGIC_FLAVOUR_TOKEN, logicFlavour, LOGIC_FLAVOUR_REGEXP);
        logic = logicReader.readLogic(this);
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
        return key.getKeys();
    }

    /**
     * Sets the key.
     *
     * @param key the key
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, KEY_NULL_MESSAGE);
        this.key = key;
    }

    /**
     * Gets the logic flavour.
     *
     * @return the logic flavour
     */
    public String getLogicFlavour() {
        return logicFlavour;
    }

    /**
     * Sets the logic flavour.
     *
     * @param logicFlavour the logic flavour
     */
    public void setLogicFlavour(final String logicFlavour) {
        this.logicFlavour = Assertions.validateStringParameter(LOGIC_FLAVOUR_TOKEN, logicFlavour, LOGIC_FLAVOUR_REGEXP);
    }

    /**
     * Gets the logic.
     *
     * @return the logic
     */
    public String getLogic() {
        return logic;
    }

    /**
     * Sets the logic.
     *
     * @param logic the logic
     */
    public void setLogic(final String logic) {
        Assertions.argumentNotNull(logic, LOGIC_NULL_MESSAGE);
        this.logic = logic.replaceAll(WHITESPACE_REGEXP, "");
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

        if (logicFlavour.replaceAll(WHITESPACE_REGEXP, "").length() == 0
                || logicFlavour.equals(LOGIC_FLAVOUR_UNDEFINED)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "logic flavour is not defined"));
        }

        try {
            Assertions.validateStringParameter(LOGIC_FLAVOUR_TOKEN, logicFlavour, LOGIC_FLAVOUR_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "logic flavour invalid-" + e.getMessage()));
        }

        if (logic.replaceAll(WHITESPACE_REGEXP, "").length() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "no logic specified, logic may not be blank"));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        if (key != null) {
            key.clean();
        }
        logicFlavour = Assertions.validateStringParameter(LOGIC_FLAVOUR_TOKEN, logicFlavour, LOGIC_FLAVOUR_REGEXP);
        logic = logic.replaceAll(WHITESPACE_REGEXP, "");
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
        builder.append("key=");
        builder.append(key);
        builder.append(",logicFlavour=");
        builder.append(logicFlavour);
        builder.append(",logic=");
        builder.append(logic);
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
        Assertions.instanceOf(copyObject, AxLogic.class);

        final AxLogic copy = ((AxLogic) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setLogicFlavour(logicFlavour);
        copy.setLogic(logic);

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
        result = prime * result + logicFlavour.hashCode();
        result = prime * result + logic.hashCode();
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

        final AxLogic other = (AxLogic) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!logicFlavour.equals(other.logicFlavour)) {
            return false;
        }
        final String thislogic = CDATAConditioner.clean(logic).replaceAll("\n", "");
        final String otherlogic = CDATAConditioner.clean(other.logic).replaceAll("\n", "");
        return thislogic.equals(otherlogic);
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

        final AxLogic other = (AxLogic) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!logicFlavour.equals(other.logicFlavour)) {
            return logicFlavour.compareTo(other.logicFlavour);
        }
        return logic.compareTo(other.logic);
    }
}
