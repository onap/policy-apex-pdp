/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class is used to specify the configuration parameters that may be passed to a task
 * {@link AxTask}. Task parameters are read from a configuration file when Apex starts and are
 * passed to the task by the Apex engine when a task is executed. Each task parameter has a key and
 * a default value. If the task parameter is not set in a configuration file, the task uses its
 * default value.
 */

@Entity
@Table(name = "AxTaskParameter")

@ToString
@EqualsAndHashCode(callSuper = false)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexTaskParameter", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxTaskParameter", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"key", "defaultValue"})

public class AxTaskParameter extends AxConcept {
    private static final long serialVersionUID = 7351688156934099977L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    @Getter
    private AxReferenceKey key;

    @Column(name = "defaultValue")
    @XmlElement
    private String defaultValue;

    /**
     * The Default Constructor creates a task parameter with a null reference key and a null default
     * value.
     */
    public AxTaskParameter() {
        this(new AxReferenceKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxTaskParameter(final AxTaskParameter copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a task parameter with the given reference key and a null
     * default value.
     *
     * @param taskParameterKey the task parameter key
     */
    public AxTaskParameter(final AxReferenceKey taskParameterKey) {
        this(taskParameterKey, "");
    }

    /**
     * The Default Constructor creates a task parameter with the given reference key and default
     * value.
     *
     * @param key the reference key of the task parameter
     * @param defaultValue the default value of the task parameter
     */
    public AxTaskParameter(final AxReferenceKey key, final String defaultValue) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(defaultValue, "defaultValue may not be null");

        this.key = key;
        this.defaultValue = defaultValue.trim();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return key.getKeys();
    }

    /**
     * Sets the reference key of the task parameter.
     *
     * @param key the reference key of the task parameter
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the default value of the task parameter.
     *
     * @return the default value of the task parameter
     */
    public String getTaskParameterValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of the task parameter.
     *
     * @param defaultValue the default value of the task parameter
     */
    public void setDefaultValue(final String defaultValue) {
        Assertions.argumentNotNull(defaultValue, "defaultValue may not be null");
        this.defaultValue = defaultValue.trim();
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

        if (defaultValue.trim().length() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.WARNING,
                    "no defaultValue specified, defaultValue is blank"));
        }

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        defaultValue = defaultValue.trim();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxTaskParameter.class);

        final AxTaskParameter copy = ((AxTaskParameter) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setDefaultValue(defaultValue);

        return copy;
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

        final AxTaskParameter other = (AxTaskParameter) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        return defaultValue.compareTo(other.defaultValue);
    }
}
