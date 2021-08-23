/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

package org.onap.policy.apex.auth.clieditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.onap.policy.apex.model.utilities.TreeMapUtils;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * The Class KeywordNode holds the structure of a command keyword for the Apex CLI editor. The
 * keyword itself and all its children are held as a recursive tree. This class is used to manage
 * interactive sub-modes in the Apex CLI editor.
 */
@Getter
@ToString
@EqualsAndHashCode
public class KeywordNode implements Comparable<KeywordNode> {
    private final String keyword;
    private final TreeMap<String, KeywordNode> children;
    private CommandLineCommand command;

    /**
     * This Constructor creates a keyword node with the given keyword and no command.
     *
     * @param keyword the keyword of the node
     */
    public KeywordNode(final String keyword) {
        this(keyword, null);
    }

    /**
     * This Constructor creates a keyword node with the given keyword and command.
     *
     * @param keyword the keyword of the keyword node
     * @param command the command associated with this keyword
     */
    public KeywordNode(final String keyword, final CommandLineCommand command) {
        Assertions.argumentNotNull(keyword, "commands may not be null");

        this.keyword = keyword;
        children = new TreeMap<>();
        this.command = command;
    }

    /**
     * Process a list of keywords on this keyword node, recursing the keyword node tree, creating
     * new branches for the keyword list if required. When the end of a branch has been reached,
     * store the command in that keyword node..
     *
     * @param keywordList the list of keywords to process on this keyword node
     * @param incomingCommand the command
     */
    public void processKeywords(final List<String> keywordList, final CommandLineCommand incomingCommand) {
        if (keywordList.isEmpty()) {
            this.command = incomingCommand;
            return;
        }

        if (!children.containsKey(keywordList.get(0))) {
            children.put(keywordList.get(0), new KeywordNode(keywordList.get(0)));
        }

        final ArrayList<String> nextLevelKeywordList = new ArrayList<>(keywordList);
        nextLevelKeywordList.remove(0);
        children.get(keywordList.get(0)).processKeywords(nextLevelKeywordList, incomingCommand);
    }

    /**
     * Adds the system commands to the keyword node.
     *
     * @param systemCommandNodes the system command nodes to add to the keyword node
     */
    public void addSystemCommandNodes(final Set<KeywordNode> systemCommandNodes) {
        if (children.isEmpty()) {
            return;
        }

        for (final KeywordNode node : children.values()) {
            node.addSystemCommandNodes(systemCommandNodes);
        }

        for (final KeywordNode systemCommandNode : systemCommandNodes) {
            children.put(systemCommandNode.getKeyword(), systemCommandNode);
        }

    }

    /**
     * Gets the commands.
     *
     * @return the commands
     */
    public Set<CommandLineCommand> getCommands() {
        final Set<CommandLineCommand> commandSet = new TreeSet<>();

        for (final KeywordNode child : children.values()) {
            if (child.getCommand() != null) {
                commandSet.add(child.getCommand());
            }
            commandSet.addAll(child.getCommands());
        }

        return commandSet;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(@NonNull final KeywordNode otherKeywordNode) {
        if (this == otherKeywordNode) {
            return 0;
        }
        return new CompareToBuilder()
                        .append(keyword, otherKeywordNode.keyword)
                        .append(command, otherKeywordNode.command)
                        .append(TreeMapUtils.compareMaps(children, otherKeywordNode.children), 0)
                        .append(getClass(), otherKeywordNode.getClass())
                        .toComparison();
    }
}
