/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020,2022 Nordix Foundation.
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

import java.util.Arrays;
import java.util.List;

public class DummyEntity extends AxConcept {
    private static final long serialVersionUID = -2962570563281067894L;

    protected AxReferenceKey key;
    private double doubleValue;

    public DummyEntity() {
        this.key = new AxReferenceKey();
        this.doubleValue = 0;
    }

    public DummyEntity(Double doubleValue) {
        this.key = new AxReferenceKey();
        this.doubleValue = doubleValue;
    }

    public DummyEntity(AxReferenceKey key, Double doubleValue) {
        this.key = key;
        this.doubleValue = doubleValue;
    }

    @Override
    public AxReferenceKey getKey() {
        return key;
    }

    @Override
    public List<AxKey> getKeys() {
        return Arrays.asList((AxKey) getKey());
    }

    public void setKey(AxReferenceKey key) {
        this.key = key;
    }

    public boolean checkSetKey() {
        return (this.key != null);
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public AxValidationResult validate(AxValidationResult result) {
        return key.validate(result);
    }

    @Override
    public void clean() {
        key.clean();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("doubleValue=");
        builder.append(doubleValue);
        return builder.toString();
    }

    @Override
    public AxConcept copyTo(AxConcept target) {
        final Object copyObject = ((target == null) ? new DummyEntity() : target);
        if (copyObject instanceof DummyEntity) {
            final DummyEntity copy = ((DummyEntity) copyObject);
            if (this.checkSetKey()) {
                copy.setKey(new AxReferenceKey(key));
            } else {
                copy.key = null;
            }
            copy.doubleValue = doubleValue;
            return copy;
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DummyEntity other = (DummyEntity) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (doubleValue != other.doubleValue) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(AxConcept otherObj) {
        if (otherObj == null) {
            return -1;
        }
        if (this == otherObj) {
            return 0;
        }
        DummyEntity other = (DummyEntity) otherObj;
        if (key == null) {
            if (other.key != null) {
                return 1;
            }
        } else if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (doubleValue != other.doubleValue) {
            return Double.valueOf(doubleValue).compareTo(other.doubleValue);
        }

        return 0;
    }
}
