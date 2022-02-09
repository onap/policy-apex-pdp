/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022 Nordix Foundation.
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

package org.onap.policy.apex.model.contextmodel.concepts;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * A container class for an Apex context model. This class is a container class that allows an Apex model to be
 * constructed that just contains context and the key information for that context. The model contains schema
 * definitions and the definitions of context albums that use those schemas. In the case where Apex context is being
 * used without policy or independent of policy, an Apex context model is sufficient to get Apex context working.
 *
 * <p>Validation runs {@link AxModel} validation on the model. In addition, the {@link AxContextSchemas} and
 * {@link AxContextAlbums} validation is run on the context schemas and albums in the model.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AxContextModel extends AxModel {
    private static final long serialVersionUID = 8800599637708309945L;

    private AxContextSchemas schemas;
    private AxContextAlbums albums;

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
     * Copy constructor.
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void register() {
        super.register();
        ModelService.registerModel(AxContextSchemas.class, getSchemas());
        ModelService.registerModel(AxContextAlbums.class, getAlbums());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = super.getKeys();

        keyList.addAll(schemas.getKeys());
        keyList.addAll(albums.getKeys());

        return keyList;
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
     * Sets the context albums on the model.
     *
     * @param albums the context albums
     */
    public void setAlbums(final AxContextAlbums albums) {
        Assertions.argumentNotNull(albums, "albums may not be null");
        this.albums = albums;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        result = super.validate(result);
        result = schemas.validate(result);
        return albums.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        super.clean();
        schemas.clean();
        albums.clean();
    }

    /**
     * {@inheritDoc}.
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

    /**
     * {@inheritDoc}.
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
