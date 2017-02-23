/*
 * ==========================License-Start=============================
 * DiscourseSimplification : CoordinationExtraction
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

package org.lambda3.text.simplification.discourse.tree.extraction.model;

import edu.stanford.nlp.ling.Word;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.model.Coordination;
import org.lambda3.text.simplification.discourse.tree.model.DiscourseTree;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class CoordinationExtraction extends Extraction {
    private final String extractionRule;
    private final Relation relation;
    private final String signalPhrase; // optional
    private final List<String> coordinations;
    private final Leaf.Type coordinationsType;

    public CoordinationExtraction(String extractionRule, Relation relation, List<List<Word>> coordinationsWords, Leaf.Type coordinationsType) {
        this.extractionRule = extractionRule;
        this.relation = relation;
        this.signalPhrase = null;
        this.coordinations = coordinationsWords.stream().map(WordsUtils::wordsToProperSentenceString).collect(Collectors.toList());
        this.coordinationsType = coordinationsType;
    }

    // binary
    public CoordinationExtraction(String extractionRule, Relation relation, List<Word> signalPhraseWords, List<Word> leftCoordinationWords, List<Word> rightCoordinationWords, Leaf.Type coordinationsType) {
        this.extractionRule = extractionRule;
        this.relation = relation;
        this.signalPhrase = (signalPhraseWords != null) ? WordsUtils.wordsToString(signalPhraseWords) : null;
        this.coordinations = Stream.of(leftCoordinationWords, rightCoordinationWords).map(WordsUtils::wordsToProperSentenceString).collect(Collectors.toList());
        this.coordinationsType = coordinationsType;
    }

    public DiscourseTree convert() {
        return new Coordination(
                extractionRule,
                relation,
                signalPhrase,
                coordinations.stream().map(
                        s -> new Leaf(coordinationsType, extractionRule, s)
                ).collect(Collectors.toList())
        );
    }

}
