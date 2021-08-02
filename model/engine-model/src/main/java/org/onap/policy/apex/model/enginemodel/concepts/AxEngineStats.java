/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.model.enginemodel.concepts;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class is a java bean that is used to record statistics on Apex engines as they execute. Statistics on the number
 * of events, the amount of time taken to execute the last policy, the average policy execution time, the up time of the
 * engine, and the time stamp of the last engine start are recorded.
 */

@Entity
@Table(name = "AxEngineStats")

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexEngineStats", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxEngineStats", namespace = "http://www.onap.org/policy/apex-pdp", propOrder = {"key", "timeStamp",
    "eventCount", "lastExecutionTime", "averageExecutionTime", "upTime", "lastStart"})
public class AxEngineStats extends AxConcept {
    private static final long serialVersionUID = -6981129081962785368L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxReferenceKey key;

    @Column
    @XmlElement(required = true)
    private long timeStamp;

    @Column
    @XmlElement(required = true)
    private long eventCount;

    @Column
    @XmlElement(required = true)
    private long lastExecutionTime;

    @Column
    @XmlElement(required = true)
    private double averageExecutionTime;

    @Column
    @XmlElement(required = true)
    private long upTime;

    @Transient
    @Getter
    private transient long lastEnterTime;

    @Column
    @XmlElement(required = true)
    @Setter(AccessLevel.PRIVATE)
    private long lastStart;

    /**
     * The Default Constructor creates an engine statistics instance with a null key and with all values cleared.
     */
    public AxEngineStats() {
        this(new AxReferenceKey());
        timeStamp = 0;
        eventCount = 0;
        lastExecutionTime = 0;
        averageExecutionTime = 0;
        upTime = 0;
        lastEnterTime = 0;
        lastStart = 0;
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxEngineStats(final AxEngineStats copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates an engine statistics instance with the given key and all values cleared.
     *
     * @param key the key
     */
    public AxEngineStats(final AxReferenceKey key) {
        this(key, 0, 0, 0, 0, 0, 0);
    }

    /**
     * This Constructor creates an engine statistics instance with all its fields set.
     *
     * @param key the engine statistics key
     * @param timeStamp the time stamp at which the statistics were recorded
     * @param eventCount the number of events processed by the engine
     * @param lastExecutionTime the amount of time taken to execute the last policy
     * @param averageExecutionTime the average amount of time taken to execute a policy
     * @param upTime the time that has elapsed since the policy engine was started
     * @param lastStart the time at which the policy engine was last started
     */
    public AxEngineStats(final AxReferenceKey key, final long timeStamp, final long eventCount,
            final long lastExecutionTime, final double averageExecutionTime, final long upTime, final long lastStart) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");

        this.key = key;
        this.timeStamp = timeStamp;
        this.eventCount = eventCount;
        this.lastExecutionTime = lastExecutionTime;
        this.averageExecutionTime = averageExecutionTime;
        this.upTime = upTime;
        this.lastStart = lastStart;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return key.getKeys();
    }

    /**
     * Sets the engine statistics key.
     *
     * @param key the engine statistics key
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the time stamp at which the statistics were recorded as a string.
     *
     * @return the time stamp at which the statistics were recorded as a string
     */
    public String getTimeStampString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timeStamp);
    }

    /**
     * Gets the time that has elapsed since the policy engine was started.
     *
     * @return the time that has elapsed since the policy engine was started
     */
    public long getUpTime() {
        if (this.getLastStart() != 0) {
            return upTime + (timeStamp - this.getLastStart());
        }
        return upTime;
    }

    /**
     * Resets all the statistic values to zero.
     */
    public synchronized void reset() {
        timeStamp = 0;
        eventCount = 0;
        lastExecutionTime = 0;
        averageExecutionTime = 0;
        upTime = 0;
        lastEnterTime = 0;
        lastStart = 0;
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it starts executing a policy.
     *
     * @param eventkey the key of the event that is being executed
     */
    public synchronized void executionEnter(final AxArtifactKey eventkey) {
        final long now = System.currentTimeMillis();
        eventCount++;
        if (eventCount < 0) {
            eventCount = 2;
        }
        lastEnterTime = now;
        timeStamp = now;
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it completes executing a policy.
     */
    public synchronized void executionExit() {
        final long now = System.currentTimeMillis();
        lastExecutionTime = now - lastEnterTime;

        averageExecutionTime = ((averageExecutionTime * (eventCount - 1.0)) + lastExecutionTime) / eventCount;
        lastEnterTime = 0;
        timeStamp = System.currentTimeMillis();
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it is started.
     */
    public synchronized void engineStart() {
        final long now = System.currentTimeMillis();
        timeStamp = now;
        this.setLastStart(now);
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it is stopped.
     */
    public synchronized void engineStop() {
        final long now = System.currentTimeMillis();
        timeStamp = now;
        upTime += (timeStamp - this.getLastStart());
        this.setLastStart(0);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        if (key.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        return key.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxEngineStats.class);

        final AxEngineStats copy = ((AxEngineStats) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setTimeStamp(timeStamp);
        copy.setEventCount(eventCount);
        copy.setLastExecutionTime(lastExecutionTime);
        copy.setAverageExecutionTime(averageExecutionTime);
        copy.setUpTime(upTime);
        copy.setLastStart(lastStart);

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

        final AxEngineStats other = (AxEngineStats) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (timeStamp != other.timeStamp) {
            return (int) (timeStamp - other.timeStamp);
        }
        if (eventCount != other.eventCount) {
            return (int) (eventCount - other.eventCount);
        }
        if (lastExecutionTime != other.lastExecutionTime) {
            return (int) (lastExecutionTime - other.lastExecutionTime);
        }
        final int result = Double.compare(averageExecutionTime, other.averageExecutionTime);
        if (result != 0) {
            return result;
        }
        if (upTime != other.upTime) {
            return (int) (upTime - other.upTime);
        }

        return Long.compare(lastStart, other.lastStart);
    }
}
