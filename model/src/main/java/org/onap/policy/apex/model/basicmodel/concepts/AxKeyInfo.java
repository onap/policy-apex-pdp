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

package org.onap.policy.apex.model.basicmodel.concepts;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * The key information on an {@link AxArtifactKey} key in an Apex policy model. Each {@link AxArtifactKey} must have an
 * {@link AxKeyInfo} object. THe information held is the key's UUID and it's description.
 *
 * <p>Validation checks that all fields are defined and that the key is valid. It also observes that descriptions are
 * blank and warns if the UUID is a zero UUID.
 */
public class AxKeyInfo extends AxConcept {
    private static final long serialVersionUID = -4023935924068914308L;

    private static final int UUID_BYTE_LENGTH_16 = 16;

    /*
     * This is not used for encryption/security, thus disabling sonar.
     */
    private static final Random sharedRandom = new Random();    // NOSONAR

    private AxArtifactKey key;

    @SerializedName("UUID")
    private UUID uuid;

    private String description;

    /**
     * The Default Constructor creates this concept with a NULL artifact key.
     */
    public AxKeyInfo() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxKeyInfo(final AxKeyInfo copyConcept) {
        super(copyConcept);
    }

    /**
     * Constructor to create this concept with the specified key.
     *
     * @param key the key of the concept
     */
    public AxKeyInfo(final AxArtifactKey key) {
        this(key, UUID.randomUUID(), "Generated description for concept referred to by key \"" + key.getId() + "\"");
    }

    /**
     * Constructor to create this concept and set all its fields.
     *
     * @param key the key of the concept
     * @param uuid the UUID of the concept
     * @param description the description of the concept
     */
    public AxKeyInfo(final AxArtifactKey key, final UUID uuid, final String description) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(uuid, "uuid may not be null");
        Assertions.argumentNotNull(description, "description may not be null");

        this.key = key;
        this.uuid = uuid;
        this.description = description.trim();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return key.getKeys();
    }

    /**
     * Sets the key of the concept.
     *
     * @param key the concept key
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the UUID of the concept.
     *
     * @return the uuid of the concept
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of the concept.
     *
     * @param uuid the uuid of the concept
     */
    public void setUuid(final UUID uuid) {
        Assertions.argumentNotNull(uuid, "uuid may not be null");
        this.uuid = uuid;
    }

    /**
     * Gets the description of the concept.
     *
     * @return the description of the concept
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the concept.
     *
     * @param description the description of the concept
     */
    public void setDescription(final String description) {
        Assertions.argumentNotNull(description, "description may not be null");
        this.description = description.trim();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (description.trim().length() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                    "description is blank"));
        }

        if (uuid.equals(new UUID(0, 0))) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.WARNING,
                    "UUID is a zero UUID: " + new UUID(0, 0)));
        }

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        description = description.trim();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("artifactId=");
        builder.append(key);
        builder.append(",uuid=");
        builder.append(uuid);
        builder.append(",description=");
        builder.append(description);
        builder.append(")");
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxKeyInfo.class);

        final AxKeyInfo copy = ((AxKeyInfo) copyObject);
        copy.setKey(new AxArtifactKey(key));
        copy.setUuid(UUID.fromString(uuid.toString()));
        copy.setDescription(description);

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final var prime = 31;
        var result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + uuid.hashCode();
        result = prime * result + description.hashCode();
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

        final AxKeyInfo other = (AxKeyInfo) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!uuid.equals(other.uuid)) {
            return false;
        }
        return description.equals(description);
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

        final AxKeyInfo other = (AxKeyInfo) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!uuid.equals(other.uuid)) {
            return uuid.compareTo(other.uuid);
        }
        return description.compareTo(other.description);
    }

    /**
     * Generate a reproducible UUID for a given string seed.
     *
     * @param seed the seed
     * @return the uuid
     */
    public static UUID generateReproducibleUuid(final String seed) {
        var random = sharedRandom;
        if (!StringUtils.isEmpty(seed)) {
            /*
             * This is not used for encryption/security, thus disabling sonar.
             */
            random = new Random(seed.hashCode());   // NOSONAR
        }
        final var array = new byte[UUID_BYTE_LENGTH_16];
        random.nextBytes(array);
        return UUID.nameUUIDFromBytes(array);
    }
}
