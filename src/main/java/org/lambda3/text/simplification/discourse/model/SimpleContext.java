/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SimpleContext
 *
 * Copyright © 2017 Lambda³
 *
 * GNU General Public License 3
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 * ==========================License-End==============================
 */

package org.lambda3.text.simplification.discourse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleContext {
    private static final Pattern PHRASE_PATTERN = Pattern.compile("^\\W*this\\W+\\w+\\W+(?<phrase>.*\\w+.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ATTRIBUTION_PHRASE_PATTERN = Pattern.compile("^\\W*this\\W+\\w+\\W+what\\W+(?<phrase>.*\\w+.*)$", Pattern.CASE_INSENSITIVE);

    private Tree parseTree;
    private Tree phrase;
    private Relation relation;
    private TimeInformation timeInformation; // optional

    // for deserialization
    public SimpleContext() {
    }

    public SimpleContext(Tree parseTree) {
        this.parseTree = parseTree;
        this.relation = Relation.UNKNOWN;
        this.timeInformation = null;
        extractPhrase();
    }

    // not efficient -> prefer to use constructor with tree
    public SimpleContext(String text) throws ParseTreeException {
        this(ParseTreeParser.parse(text));
    }

    public Tree getParseTree() {
        return parseTree;
    }

    public void setParseTree(Tree parseTree) {
        this.parseTree = parseTree;
        extractPhrase();
    }

    private void extractPhrase() {
        this.phrase = parseTree;

        boolean matched = false;
        if (relation.equals(Relation.ATTRIBUTION)) {
            Matcher matcher = ATTRIBUTION_PHRASE_PATTERN.matcher(getText());
            if (matcher.matches()) {
                try {
                    this.phrase = ParseTreeParser.parse(matcher.group("phrase"));
                    matched = true;
                } catch (ParseTreeException e) {}
            }
        } else {
            Matcher matcher = PHRASE_PATTERN.matcher(getText());
            if (matcher.matches()) {
                try {
                    this.phrase = ParseTreeParser.parse(matcher.group("phrase"));
                    matched = true;
                } catch (ParseTreeException e) {}
            }
        }

        if (!matched) {
            this.relation = Relation.NOUN_BASED;
        }
    }

    public Tree getPhrase() {
        return phrase;
    }

    @JsonProperty("text")
    public String getText() {
        return WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree));
    }

    @JsonProperty("phraseText")
    public String getPhraseText()  {
        return WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(phrase));
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
        extractPhrase();
    }

    public Relation getRelation() {
        return relation;
    }

    public void setTimeInformation(TimeInformation timeInformation) {
        this.timeInformation = timeInformation;
    }

	public Optional<TimeInformation> getTimeInformation() {
		return Optional.ofNullable(timeInformation);
	}
}
