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

package org.onap.policy.apex.model.enginemodel.concepts;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * A container class for an Apex engine model. This class is a container class that allows an Apex
 * model to be constructed that contains the current context {@link AxContextModel}, current state
 * {@link AxEngineState} and current statistics {@link AxEngineStats} of an Apex engine. This model
 * is used by an Apex engine to pass its current execution state to any system that wishes to query
 * that information. The time stamp of the engine model is the time at which the state and
 * statistics of the engine were read.
 * 
 * <p>Validation checks that the current state {@link AxEngineState} is defined and that the time stamp
 * is set on the engine model.
 */
@Entity
@Table(name = "AxEngineModel")
@XmlRootElement(name = "apexEngineModel", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxEngineModel", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"timestamp", "state", "stats"})

public class AxEngineModel extends AxContextModel {
    private static final long serialVersionUID = 6381235864606564046L;
    private static final int HASH_CODE_PRIME = 32;

    @Column(name = "timestamp")
    private long timestamp;

    @Enumerated
    @Column(name = "state")
    @XmlElement(required = true)
    private AxEngineState state;

    // @formatter:off
    @JoinColumns({
            @JoinColumn(name = "statsParentKeyName", referencedColumnName = "parentKeyName", updatable = false,
                    insertable = false),
            @JoinColumn(name = "statsParentKeyVersion", referencedColumnName = "parentKeyVersion", updatable = false,
                    insertable = false),
            @JoinColumn(name = "statsParentLocalName ", referencedColumnName = "parentLocalName", updatable = false,
                    insertable = false),
            @JoinColumn(name = "statsLocalName", referencedColumnName = "localName", updatable = false,
                    insertable = false)})
    private AxEngineStats stats;
    // @formatter:on

    /**
     * The Default Constructor creates an engine model with a null key and all its fields undefined.
     */
    public AxEngineModel() {
        this(new AxArtifactKey());
        timestamp = -1;
    }

    /**
     * Copy constructor.
     * 
     * @param copyConcept the concept to copy from
     */
    public AxEngineModel(final AxEngineModel copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates an engine model with the given key and all its fields
     * undefined.
     *
     * @param key the engine model key
     */
    public AxEngineModel(final AxArtifactKey key) {
        this(key, new AxContextSchemas(new AxArtifactKey(key.getName() + "_DataTypes", key.getVersion())),
                new AxKeyInformation(new AxArtifactKey(key.getName() + "_KeyInfo", key.getVersion())),
                new AxContextAlbums(new AxArtifactKey(key.getName() + "_Context", key.getVersion())));
    }

    /**
     * This Constructor creates an engine model with its context model data types all defined, the
     * state of the engine model is undefined.
     *
     * @param key the engine model key
     * @param contextSchemas the context schemas used by the engine model
     * @param keyInformation the key information used by the engine model
     * @param contextAlbums the context albums used by the engine model
     */
    public AxEngineModel(final AxArtifactKey key, final AxContextSchemas contextSchemas,
            final AxKeyInformation keyInformation, final AxContextAlbums contextAlbums) {
        this(key, contextSchemas, keyInformation, contextAlbums, AxEngineState.UNDEFINED,
                new AxEngineStats(new AxReferenceKey(key, "_EngineStats", key.getVersion())));
    }

    /**
     * This Constructor creates an engine model with all its fields defined.
     *
     * @param key the engine model key
     * @param contextSchemas the context schemas used by the engine model
     * @param keyInformation the key information used by the engine model
     * @param contextAlbums the context albums used by the engine model
     * @param state the state of the engine in the engine model
     * @param stats the statistics of the engine in the engine model
     */
    public AxEngineModel(final AxArtifactKey key, final AxContextSchemas contextSchemas,
            final AxKeyInformation keyInformation, final AxContextAlbums contextAlbums, final AxEngineState state,
            final AxEngineStats stats) {
        super(key, contextSchemas, contextAlbums, keyInformation);
        Assertions.argumentNotNull(state, "state may not be null");
        Assertions.argumentNotNull(stats, "stats may not be null");

        this.state = state;
        this.stats = stats;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = super.getKeys();
        keyList.addAll(stats.getKeys());
        return keyList;
    }

    /**
     * Gets the time stamp at which the engine model measurements were taken.
     *
     * @return the time stamp at which the engine model measurements were taken
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the time stamp at which the engine model measurements were taken as a string.
     *
     * @return the time stamp string
     */
    public String getTimeStampString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timestamp);
    }

    /**
     * Sets the time stamp at which the engine model measurements were taken.
     *
     * @param timestamp the time stamp at which the engine model measurements were taken
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the state of the engine at the time the measurements were taken.
     *
     * @return the state of the engine at the time the measurements were taken
     */
    public AxEngineState getState() {
        return state;
    }

    /**
     * Sets the state of the engine.
     *
     * @param state the state of the engine
     */
    public void setState(final AxEngineState state) {
        Assertions.argumentNotNull(state, "state may not be null");
        this.state = state;
    }

    /**
     * Gets the statistics of the engine at the time the measurements were taken.
     *
     * @return the statistics of the engine at the time the measurements were taken
     */
    public AxEngineStats getStats() {
        return stats;
    }

    /**
     * Sets the the statistics of the engine.
     *
     * @param stats the the statistics of the engine
     */
    public void setStats(final AxEngineStats stats) {
        Assertions.argumentNotNull(stats, "stats may not be null");
        this.stats = stats;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#validate(org.onap.policy.apex
     * .model .basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        result = stats.validate(result);

        if (timestamp == -1) {
            result.addValidationMessage(new AxValidationMessage(getKey(), this.getClass(), ValidationResult.INVALID,
                    this.getClass().getSimpleName() + " - timestamp is not set"));
        }

        if (state == AxEngineState.UNDEFINED) {
            result.addValidationMessage(new AxValidationMessage(getKey(), this.getClass(), ValidationResult.INVALID,
                    this.getClass().getSimpleName() + " - state is UNDEFINED"));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#clean()
     */
    @Override
    public void clean() {
        super.clean();
        stats.clean();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append(super.toString());
        builder.append(",timestamp=");
        builder.append(timestamp);
        builder.append(",state=");
        builder.append(state);
        builder.append(",stats=");
        builder.append(stats);
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
        Assertions.instanceOf(copyObject, AxEngineModel.class);

        final AxEngineModel copy = ((AxEngineModel) copyObject);
        super.copyTo(targetObject);
        copy.timestamp = timestamp;
        copy.setState(state);
        copy.setStats(new AxEngineStats(stats));

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + (int) (timestamp ^ (timestamp >>> HASH_CODE_PRIME));
        result = prime * result + state.hashCode();
        result = prime * result + stats.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("comparison object may not be null");
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxEngineModel other = (AxEngineModel) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (timestamp != other.timestamp) {
            return false;
        }
        if (!state.equals(other.state)) {
            return false;
        }
        return stats.equals(other.stats);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.contextmodel.concepts.AxContextModel#compareTo(org.onap.policy.
     * apex.model.basicmodel.concepts.AxConcept)
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        Assertions.argumentNotNull(otherObj, "comparison object may not be null");

        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxEngineModel other = (AxEngineModel) otherObj;
        if (!super.equals(other)) {
            return super.compareTo(other);
        }
        if (timestamp != other.timestamp) {
            return (int) (timestamp - other.timestamp);
        }
        if (!state.equals(other.state)) {
            return state.compareTo(other.state);
        }
        return stats.compareTo(other.stats);
    }
}
