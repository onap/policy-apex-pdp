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

package org.onap.policy.apex.model.modelapi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ApexEditorAPIResult return the result of and messages from all model API method calls
 * on the {@link ApexModel} API.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ApexApiResult {

    /**
     * This enumeration is used to represent the result status of a call on the {@link ApexModel}
     * API.
     */
    @XmlEnum(value = String.class)
    public enum Result {
        /** The method call succeeded. */
        SUCCESS,
        /** The method call succeeded and all operations are now completed. */
        FINISHED,
        /** The method call for a create operation failed because the concept already exists. */
        CONCEPT_EXISTS,
        /**
         * The method call for a create operation failed because multiple concepts already exists.
         */
        MULTIPLE_CONCEPTS_EXIST,
        /** The method call on a concept failed because the referenced concept does not exist. */
        CONCEPT_DOES_NOT_EXIST,
        /** The method call failed because no action was specified on the method call. */
        NO_ACTION_SPECIFIED,
        /**
         * The method call failed because of a structural error, a missing reference, or other error
         * on the model.
         */
        FAILED,
        /**
         * The method call failed for another reason such as the method call is not implemented yet
         * on the concept on which it was called.
         */
        OTHER_ERROR
    }

    private Result result;
    private List<String> messages = new ArrayList<>();

    /**
     * The Default Constructor creates a result for a successful operation with no messages.
     */
    public ApexApiResult() {
        result = Result.SUCCESS;
    }

    /**
     * This Constructor creates a result with the given result status with no messages.
     *
     * @param result the result status to use on this result
     */
    public ApexApiResult(final Result result) {
        this.result = result;
    }

    /**
     * This Constructor creates a result with the given result status and message.
     *
     * @param result the result status to use on this result
     * @param message the message to return with the result
     */
    public ApexApiResult(final Result result, final String message) {
        this.result = result;
        addMessage(message);
    }

    /**
     * This Constructor creates a result with the given result status and {@link Throwable} object
     * such as an exception. The message and stack trace from the {@link Throwable} object are added
     * to the message list of this message.
     *
     * @param result the result status to use on this result
     * @param throwable the throwable object from which to add the message and stack trace
     */
    public ApexApiResult(final Result result, final Throwable throwable) {
        this.result = result;
        addThrowable(throwable);
    }

    /**
     * This Constructor creates a result with the given result status, message, and
     * {@link Throwable} object such as an exception. The message and stack trace from the
     * {@link Throwable} object are added to the message list of this message.
     *
     * @param result the result status to use on this result
     * @param message the message to return with the result
     * @param throwable the throwable object from which to add the message and stack trace
     */
    public ApexApiResult(final Result result, final String message, final Throwable throwable) {
        this.result = result;
        addMessage(message);
        addThrowable(throwable);
    }

    /**
     * This message is a utility message that checks if the result of an operation on the API was
     * OK.
     *
     * @return true, if the result indicates the API operation succeeded
     */
    @XmlAttribute(required = true)
    public boolean isOk() {
        return result == Result.SUCCESS || result == Result.FINISHED;
    }

    /**
     * This message is a utility message that checks if the result of an operation on the API was
     * not OK.
     *
     * @return true, if the result indicates the API operation did not succeed
     */
    public boolean isNok() {
        return !isOk();
    }

    /**
     * Gets the result status of an API operation.
     *
     * @return the result status
     */
    @XmlAttribute(required = true)
    public Result getResult() {
        return result;
    }

    /**
     * Sets the result status of an API operation.
     *
     * @param result the result status
     */
    public void setResult(final Result result) {
        this.result = result;
    }

    /**
     * Gets the list of messages returned by an API operation.
     *
     * @return the list of messages returned by an API operation
     */
    @XmlElementWrapper(required = false, nillable = true)
    @XmlElement(nillable = true, name = "message")
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Sets the list of messages to return as a result of an API operation.
     *
     * @param messages the list of messages to return as a result of an API operation
     */
    public void setMessages(final List<String> messages) {
        this.messages = messages;
    }

    /**
     * Gets all the messages returned by an API operation as a single string.
     *
     * @return the messages returned by an API operation as a single string
     */
    @XmlElement(required = true, name = "content")
    public String getMessage() {
        final StringBuilder builder = new StringBuilder();
        for (final String message : messages) {
            builder.append(message);
            builder.append('\n');
        }

        return builder.toString();
    }

    /**
     * Adds a message from an API operation to the bottom of the list of messages to be returned.
     *
     * @param message the message from an API operation to add to the bottom of the list of messages
     *        to be returned
     */
    public void addMessage(final String message) {
        if (message != null && message.trim().length() > 0) {
            messages.add(message);
        }
    }

    /**
     * Adds the message and stack trace from a {@link Throwable} object such as an exception from an
     * API operation to the bottom of the list of messages to be returned.
     *
     * @param throwable the {@link Throwable} object such as an exception from an API operation from
     *        which the message and stack trace are to be extracted and placed at the bottom of the
     *        list of messages to be returned
     */
    public void addThrowable(final Throwable throwable) {
        final StringWriter throwableStringWriter = new StringWriter();
        final PrintWriter throwablePrintWriter = new PrintWriter(throwableStringWriter);
        throwable.printStackTrace(throwablePrintWriter);
        messages.add(throwable.getMessage());
        messages.add(throwableStringWriter.toString());
    }

    /**
     * Gets a representation of the {@link ApexApiResult} instance as a JSON string.
     *
     * @return the result instance JSON string
     */
    public String toJson() {
        final StringBuilder builder = new StringBuilder();
        builder.append("{\n");

        builder.append("\"result\": \"");
        builder.append(result.toString());
        builder.append("\",\n");

        builder.append("\"messages\": [");
        boolean first = true;
        for (final String message : messages) {
            if (first) {
                builder.append("\n\"");
                first = false;
            } else {
                builder.append(",\n\"");
            }
            builder.append(message.replaceAll("\"", "\\\\\""));
            builder.append("\"");
        }
        builder.append("]\n");

        builder.append("}\n");

        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("result: ");
        builder.append(result);
        builder.append('\n');
        builder.append(getMessage());
        return builder.toString();
    }
}
