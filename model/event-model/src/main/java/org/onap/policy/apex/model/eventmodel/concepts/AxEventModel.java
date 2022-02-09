/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019,2022 Nordix Foundation.
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

package org.onap.policy.apex.model.eventmodel.concepts;

import java.util.List;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * A container class for an Apex event model. This class is a container class that allows an Apex model to be
 * constructed that contains events and context and the key information for those events and context. The model contains
 * schema definitions and the definitions of events that use those schemas.
 *
 * <p>Validation runs {@link AxModel} validation on the model. In addition, the {@link AxContextSchemas} and
 * {@link AxEvents} validation is run on the context schemas and events in the model.
 */
public class AxEventModel extends AxModel {
    private static final long serialVersionUID = 8800599637708309945L;

    private AxContextSchemas schemas;
    private AxEvents events;

    /**
     * The Default Constructor creates a {@link AxEventModel} object with a null artifact key and creates an empty event
     * model.
     */
    public AxEventModel() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxEventModel(final AxEventModel copyConcept) {
        super(copyConcept);
    }

    /**
     * The Key Constructor creates a {@link AxEventModel} object with the given artifact key and creates an empty event
     * model.
     *
     * @param key the event model key
     */
    public AxEventModel(final AxArtifactKey key) {
        this(key, new AxContextSchemas(new AxArtifactKey(key.getName() + "_Schemas", key.getVersion())),
                new AxKeyInformation(new AxArtifactKey(key.getName() + "_KeyInfo", key.getVersion())),
                new AxEvents(new AxArtifactKey(key.getName() + "_Events", key.getVersion())));
    }

    /**
     * Constructor that initiates a {@link AxEventModel} with all its fields.
     *
     * @param key the event model key
     * @param schemas the schemas for events in the event model
     * @param keyInformation the key information for context schemas and events in the event model
     * @param events the events in the event model
     */
    public AxEventModel(final AxArtifactKey key, final AxContextSchemas schemas, final AxKeyInformation keyInformation,
            final AxEvents events) {
        super(key, keyInformation);
        Assertions.argumentNotNull(events, "events may not be null");

        this.schemas = schemas;
        this.events = events;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void register() {
        super.register();
        ModelService.registerModel(AxContextSchemas.class, getSchemas());
        ModelService.registerModel(AxEvents.class, getEvents());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = super.getKeys();

        keyList.addAll(schemas.getKeys());
        keyList.addAll(events.getKeys());

        return keyList;
    }

    /**
     * Gets the context schemas.
     *
     * @return the context schemas
     */
    public AxContextSchemas getSchemas() {
        return schemas;
    }

    /**
     * Sets the context schemas.
     *
     * @param schemas the context schemas
     */
    public void setSchemas(final AxContextSchemas schemas) {
        Assertions.argumentNotNull(schemas, "schemas may not be null");
        this.schemas = schemas;
    }

    /**
     * Gets the events from the model.
     *
     * @return the events
     */
    public AxEvents getEvents() {
        return events;
    }

    /**
     * Sets the events in the model.
     *
     * @param events the events
     */
    public void setEvents(final AxEvents events) {
        Assertions.argumentNotNull(events, "events may not be null");
        this.events = events;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        result = super.validate(result);
        result = schemas.validate(result);
        return events.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        super.clean();
        schemas.clean();
        events.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append(super.toString());
        builder.append(",schemas=");
        builder.append(schemas);
        builder.append(",events=");
        builder.append(events);
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
        Assertions.instanceOf(copyObject, AxEventModel.class);

        final AxEventModel copy = ((AxEventModel) copyObject);
        super.copyTo(targetObject);
        copy.setSchemas(new AxContextSchemas(schemas));
        copy.setEvents(new AxEvents(events));

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + schemas.hashCode();
        result = prime * result + events.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
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

        final AxEventModel other = (AxEventModel) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (!schemas.equals(other.schemas)) {
            return false;
        }
        return events.equals(other.events);
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

        final AxEventModel other = (AxEventModel) otherObj;
        if (!super.equals(other)) {
            return super.compareTo(other);
        }
        if (!schemas.equals(other.schemas)) {
            return schemas.compareTo(other.schemas);
        }
        return events.compareTo(other.events);
    }
}
