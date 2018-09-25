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

package org.onap.policy.apex.auth.clieditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class chops a command line up into commands, parameters and arguments.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CommandLineParser {

    /**
     * This method breaks a line of input up into commands, parameters, and arguments. Commands are standalone words at
     * the beginning of the line, of which there may be multiple Parameters are single words followed by an '='
     * character Arguments are single words or a block of quoted text following an '=' character.
     *
     * <p>Format: command [command....] parameter=argument [parameter = argument]
     *
     * <p>Examples entity create name=hello description="description of hello" help entity list
     *
     * @param line The line to parse
     * @param logicBlock A block of logic code to be taken literally
     * @return the string array list
     */
    public List<String> parse(final String line, final String logicBlock) {
        return checkFormat(
                        mergeArguments(mergeEquals(splitOnEquals(
                                        stripAndSplitWords(mergeQuotes(splitOnChar(stripComments(line), '\"')))))),
                        logicBlock);
    }

    /**
     * Strip comments from lines, comments start with a # character.
     *
     * @param line the line
     * @return the line without comments
     */
    private String stripComments(final String line) {
        final int commentPos = line.indexOf('#');
        if (commentPos == -1) {
            return line;
        } else {
            return line.substring(0, commentPos);
        }
    }

    /**
     * This method merges an array with separate quotes into an array with quotes delimiting the start and end of quoted
     * words Example [Humpty ],["],[Dumpty sat on the wall],["],[, Humpty Dumpty had ],["],["],a ["],[great],["],[ fall]
     * becomes [Humpty ],["Dumpty sat on the wall"],[, Humpty Dumpty had ],[""],[a],["great"],[ fall].
     *
     * @param wordsSplitOnQuotes the words split on quotes
     * @return the merged array list
     */
    private ArrayList<String> mergeQuotes(final ArrayList<String> wordsSplitOnQuotes) {
        final ArrayList<String> wordsWithQuotesMerged = new ArrayList<>();

        int loopWordIndex = 0;
        for (int wordIndex = 0; wordIndex < wordsSplitOnQuotes.size(); wordIndex = loopWordIndex) {
            loopWordIndex = mergeQuote(wordsSplitOnQuotes, wordsWithQuotesMerged, wordIndex);
        }

        return wordsWithQuotesMerged;
    }

    /**
     * This method merges the next set of quotes.
     * 
     * @param wordsSplitOnQuotes the words split on quotes
     * @param wordsWithQuotesMerged the merged words
     * @param wordIndex the current word index
     * @return the next word index
     */
    private int mergeQuote(final ArrayList<String> wordsSplitOnQuotes, final ArrayList<String> wordsWithQuotesMerged,
                    int wordIndex) {

        if ("\"".equals(wordsSplitOnQuotes.get(wordIndex))) {
            StringBuilder quotedWord = new StringBuilder(wordsSplitOnQuotes.get(wordIndex++));

            for (; wordIndex < wordsSplitOnQuotes.size(); wordIndex++) {
                quotedWord.append(wordsSplitOnQuotes.get(wordIndex));
                if ("\"".equals(wordsSplitOnQuotes.get(wordIndex))) {
                    wordIndex++;
                    break;
                }
            }
            String quotedWordToString = quotedWord.toString();
            if (quotedWordToString.matches("^\".*\"$")) {
                wordsWithQuotesMerged.add(quotedWordToString);
            } else {
                throw new CommandLineException("trailing quote found in input " + wordsSplitOnQuotes);
            }
        } else {
            wordsWithQuotesMerged.add(wordsSplitOnQuotes.get(wordIndex++));
        }
        return wordIndex;
    }

    /**
     * This method splits the words on an array list into an array list where each portion of the line is split into
     * words by '=', quoted words are ignored Example: aaa = bbb = ccc=ddd=eee = becomes [aaa ],[=],[bbb
     * ],[=],[ccc],[=],[ddd],[=],[eee ],[=].
     *
     * @param words the words
     * @return the merged array list
     */
    private ArrayList<String> splitOnEquals(final ArrayList<String> words) {
        final ArrayList<String> wordsSplitOnEquals = new ArrayList<>();

        for (final String word : words) {
            // Is this a quoted word ?
            if (word.startsWith("\"")) {
                wordsSplitOnEquals.add(word);
                continue;
            }

            // Split on equals character
            final ArrayList<String> splitWords = splitOnChar(word, '=');
            for (final String splitWord : splitWords) {
                wordsSplitOnEquals.add(splitWord);
            }
        }

        return wordsSplitOnEquals;
    }

    /**
     * This method merges an array with separate equals into an array with equals delimiting the start of words Example:
     * [aaa ],[=],[bbb ],[=],[ccc],[=],[ddd],[=],[eee ],[=] becomes [aaa ],[= bbb ],[= ccc],[=ddd],[=eee ],[=].
     *
     * @param wordsSplitOnEquals the words split on equals
     * @return the merged array list
     */
    private ArrayList<String> mergeEquals(final ArrayList<String> wordsSplitOnEquals) {
        final ArrayList<String> wordsWithEqualsMerged = new ArrayList<>();

        int loopWordIndex = 0;
        for (int wordIndex = 0; wordIndex < wordsSplitOnEquals.size(); wordIndex = loopWordIndex) {
            loopWordIndex = wordIndex;

            // Is this a quoted word ?
            if (wordsSplitOnEquals.get(loopWordIndex).startsWith("\"")) {
                wordsWithEqualsMerged.add(wordsSplitOnEquals.get(loopWordIndex));
                continue;
            }

            if ("=".equals(wordsSplitOnEquals.get(loopWordIndex))) {
                if (loopWordIndex < wordsSplitOnEquals.size() - 1
                                && !wordsSplitOnEquals.get(loopWordIndex + 1).startsWith("=")) {
                    wordsWithEqualsMerged.add(
                                    wordsSplitOnEquals.get(loopWordIndex) + wordsSplitOnEquals.get(loopWordIndex + 1));
                    loopWordIndex += 2;
                } else {
                    wordsWithEqualsMerged.add(wordsSplitOnEquals.get(loopWordIndex++));
                }
            } else {
                wordsWithEqualsMerged.add(wordsSplitOnEquals.get(loopWordIndex++));
            }
        }

        return wordsWithEqualsMerged;
    }

    /**
     * This method merges words that start with an '=' character with the previous word if that word does not start with
     * an '='.
     *
     * @param words the words
     * @return the merged array list
     */
    private ArrayList<String> mergeArguments(final ArrayList<String> words) {
        final ArrayList<String> mergedArguments = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            // Is this a quoted word ?
            if (words.get(i).startsWith("\"")) {
                mergedArguments.add(words.get(i));
                continue;
            }

            if (words.get(i).startsWith("=")) {
                if (i > 0 && !words.get(i - 1).startsWith("=")) {
                    mergedArguments.remove(mergedArguments.size() - 1);
                    mergedArguments.add(words.get(i - 1) + words.get(i));
                } else {
                    mergedArguments.add(words.get(i));
                }
            } else {
                mergedArguments.add(words.get(i));
            }
        }

        return mergedArguments;
    }

    /**
     * This method strips all non quoted white space down to single spaces and splits non-quoted words into separate
     * words.
     *
     * @param words the words
     * @return the array list with white space stripped and words split
     */
    private ArrayList<String> stripAndSplitWords(final ArrayList<String> words) {
        final ArrayList<String> strippedAndSplitWords = new ArrayList<>();

        for (String word : words) {
            // Is this a quoted word
            if (word.startsWith("\"")) {
                strippedAndSplitWords.add(word);
            } else {
                // Split the word on blanks
                strippedAndSplitWords.addAll(stripAndSplitWord(word));
            }
        }

        return strippedAndSplitWords;
    }

    /**
     * Strip and split a word on blanks into an array of words split on blanks.
     * 
     * @param word the word to split
     * @return the array of split words
     */
    private Collection<? extends String> stripAndSplitWord(final String word) {
        final ArrayList<String> strippedAndSplitWords = new ArrayList<>();

        // Strip white space by replacing all white space with blanks and then removing leading
        // and trailing blanks
        String singleSpaceWord = word.replaceAll("\\s+", " ").trim();

        if (singleSpaceWord.length() == 0) {
            return strippedAndSplitWords;
        }

        // Split on space characters
        final String[] splitWords = singleSpaceWord.split(" ");
        for (final String splitWord : splitWords) {
            strippedAndSplitWords.add(splitWord);
        }

        return strippedAndSplitWords;
    }

    /**
     * This method splits a line of text into an array list where each portion of the line is split into words by a
     * character, with the characters themselves as separate words Example: Humpty "Dumpty sat on the wall", Humpty
     * Dumpty had ""a "great" fall becomes [Humpty ],["],[Dumpty sat on the wall],["],[, Humpty Dumpty had ],["],["],a
     * ["],[great],["],[ fall].
     *
     * @param line the input line
     * @param splitChar the split char
     * @return the split array list
     */
    private ArrayList<String> splitOnChar(final String line, final char splitChar) {
        final ArrayList<String> wordsSplitOnQuotes = new ArrayList<>();

        int currentPos = 0;
        while (currentPos != -1) {
            final int quotePos = line.indexOf(splitChar, currentPos);
            if (quotePos != -1) {
                if (currentPos < quotePos) {
                    wordsSplitOnQuotes.add(line.substring(currentPos, quotePos));
                }
                wordsSplitOnQuotes.add("" + splitChar);
                currentPos = quotePos + 1;

                if (currentPos == line.length()) {
                    currentPos = -1;
                }
            } else {
                wordsSplitOnQuotes.add(line.substring(currentPos));
                currentPos = quotePos;
            }
        }

        return wordsSplitOnQuotes;
    }

    /**
     * This method checks that an array list containing a command is in the correct format.
     *
     * @param commandWords the command words
     * @param logicBlock A block of logic code to be taken literally
     * @return the checked array list
     */
    private ArrayList<String> checkFormat(final ArrayList<String> commandWords, final String logicBlock) {
        // There should be at least one word
        if (commandWords.isEmpty()) {
            return commandWords;
        }

        // The first word must be alphanumeric, that is a command
        if (!commandWords.get(0).matches("^[a-zA-Z0-9]*$")) {
            throw new CommandLineException(
                            "first command word is not alphanumeric or is not a command: " + commandWords.get(0));
        }

        // Now check that we have a sequence of commands at the beginning
        int currentWordPos = 0;
        while (currentWordPos < commandWords.size()) {
            if (commandWords.get(currentWordPos).matches("^[a-zA-Z0-9]*$")) {
                currentWordPos++;
            } else {
                break;
            }
        }

        while (currentWordPos < commandWords.size()) {
            // From now on we should have a sequence of parameters with arguments delimited by a
            // single '=' character
            if (currentWordPos < commandWords.size() - 1 || logicBlock == null) {
                // No logic block
                if (commandWords.get(currentWordPos).matches("^[a-zA-Z0-9]+=[a-zA-Z0-9/\"].*$")) {
                    currentWordPos++;
                } else {
                    throw new CommandLineException(
                                    "command argument is not properly formed: " + commandWords.get(currentWordPos));
                }
            } else {
                // Logic block
                if (commandWords.get(currentWordPos).matches("^[a-zA-Z0-9]+=")) {
                    commandWords.set(currentWordPos, commandWords.get(currentWordPos) + logicBlock);
                    currentWordPos++;
                } else {
                    throw new CommandLineException(
                                    "command argument is not properly formed: " + commandWords.get(currentWordPos));
                }
            }
        }

        return commandWords;
    }
}
