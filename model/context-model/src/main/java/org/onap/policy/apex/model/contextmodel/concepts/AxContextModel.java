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

package org.onap.policy.apex.model.contextmodel.concepts;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
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
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * A container class for an Apex context model. This class is a container class that allows an Apex model to be
 * constructed that just contains context and the key information for that context. The model contains schema
 * definitions and the definitions of context albums that use those schemas. In the case where Apex context is being
 * used without policy or independent of policy, an Apex context model is sufficient to get Apex context working.
 * <p>
 * Validation runs {@link AxModel} validation on the model. In addition, the {@link AxContextSchemas} and
 * {@link AxContextAlbums} validation is run on the context schemas and albums in the model.
 */
@Entity
@Table(name = "AxContextModel")

@XmlRootElement(name = "apexContextModel", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxContextModel", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = { "schemas", "albums" })

public class AxContextModel extends AxModel {
    private static final long serialVersionUID = 8800599637708309945L;

    // @formatter:off
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "schemasName", referencedColumnName = "name"),
        @JoinColumn(name = "schemasVersion", referencedColumnName = "version")
    })
    @XmlElement(name = "schemas", required = true)
    private AxContextSchemas schemas;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "albumsName", referencedColumnName = "name"),
        @JoinColumn(name = "albumsVersion", referencedColumnName = "version")
    })
    @XmlElement(name = "albums", required = true)
    private AxContextAlbums albums;
    // @formatter:on

    /**
     * The Default Constructor creates a {@link AxContextModel} object with a null artifact key and creates an empty
     * context model.
     */
    public AxContextModel() {
        this(new AxArtifactKey());
    }

    /**
     * The Key Constructor creates a {@link AxContextModel} object with the given artifact key and creates an empty
     * context model.
     *
     * @param key the key of the context model
     */
    public AxContextModel(final AxArtifactKey key) {
        this(key, new AxContextSchemas(new AxArtifactKey(key.getName() + "_Schemas", key.getVersion())),
                new AxContextAlbums(new AxArtifactKey(key.getName() + "_Albums", key.getVersion())),
                new AxKeyInformation(new AxArtifactKey(key.getName() + "_KeyInfo", key.getVersion())));
    }

    /**
     * Copy constructor
     *
     * @param copyConcept the concept to copy from
     */
    public AxContextModel(final AxContextModel copyConcept) {
        super(copyConcept);
    }

    /**
     * Constructor that initiates a {@link AxContextModel} with schemas and keys for those schemas. An empty
     * {@link AxContextAlbums} container is created.
     *
     * @param key the key of the context model
     * @param schemas the context schema definitions
     * @param keyInformation the key information for those context schemas
     */
    public AxContextModel(final AxArtifactKey key, final AxContextSchemas schemas,
            final AxKeyInformation keyInformation) {
        this(key, schemas, new AxContextAlbums(new AxArtifactKey(key.getName() + "_Albums", key.getVersion())),
                keyInformation);
    }

    /**
     * Constructor that initiates a {@link AxContextModel} with all its fields.
     *
     * @param key the key of the context model
     * @param schemas the context schema definitions
     * @param albums the context album container containing context albums
     * @param keyInformation the key information for those context schemas
     */
    public AxContextModel(final AxArtifactKey key, final AxContextSchemas schemas, final AxContextAlbums albums,
            final AxKeyInformation keyInformation) {
        super(key, keyInformation);
        Assertions.argumentNotNull(schemas, "schemas may not be null");
        Assertions.argumentNotNull(albums, "albums may not be null");
        this.schemas = schemas;
        this.albums = albums;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#register()
     */
    @Override
    public void register() {
        super.register();
        ModelService.registerModel(AxContextSchemas.class, getSchemas());
        ModelService.registerModel(AxContextAlbums.class, getAlbums());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = super.getKeys();

        keyList.addAll(schemas.getKeys());
        keyList.addAll(albums.getKeys());

        return keyList;
    }

    /**
     * Gets the context schemas from the model.
     *
     * @return the context schemas
     */
    public AxContextSchemas getSchemas() {
        return schemas;
    }

    /**
     * Sets the context schemas on the model.
     *
     * @param schemas the context schemas
     */
    public void setSchemas(final AxContextSchemas schemas) {
        Assertions.argumentNotNull(schemas, "schemas may not be null");
        this.schemas = schemas;
    }

    /**
     * Gets the context albums from the model.
     *
     * @return the context albums
     */
    public AxContextAlbums getAlbums() {
        return albums;
    }

    /**
     * Sets the context albums on the model.
     *
     * @param albums the context albums
     */
    public void setAlbums(final AxContextAlbums albums) {
        Assertions.argumentNotNull(albums, "albums may not be null");
        this.albums = albums;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#validate(org.onap.apex.model.
     * basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        result = super.validate(result);
        result = schemas.validate(result);
        return albums.validate(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#clean()
     */
    @Override
    public void clean() {
        super.clean();
        schemas.clean();
        albums.clean();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append(super.toString());
        builder.append(",schemas=");
        builder.append(schemas);
        builder.append(",albums=");
        builder.append(albums);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.apex.model. basicmodel.concepts.AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxContextModel.class);

        final AxContextModel copy = ((AxContextModel) copyObject);
        super.copyTo(target);
        copy.setSchemas(new AxContextSchemas(schemas));
        copy.setAlbums(new AxContextAlbums(albums));

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + schemas.hashCode();
        result = prime * result + albums.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#equals(java.lang.Object)
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

        final AxContextModel other = (AxContextModel) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (!schemas.equals(other.schemas)) {
            return false;
        }
        return albums.equals(other.albums);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxModel#compareTo(org.onap.apex.model.
     * basicmodel.concepts.AxConcept)
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

        final AxContextModel other = (AxContextModel) otherObj;
        if (!super.equals(other)) {
            return super.compareTo(other);
        }
        if (!schemas.equals(other.schemas)) {
            return schemas.compareTo(other.schemas);
        }
        return albums.compareTo(other.albums);
    }
}
