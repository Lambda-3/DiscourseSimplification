/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SubordinationExtraction
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
import org.lambda3.text.simplification.discourse.tree.model.DiscourseTree;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.tree.model.Subordination;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.List;

/**
 *
 */
public class SubordinationExtraction extends Extraction {
    private final String extractionRule;
    private final Relation relation;
    private final String signalPhrase; // optional
    private final String leftConstituent;
    private final String rightConstituent;
    private final boolean superordinationIsLeft;
    private final Leaf.Type leftConstituentType;
    private final Leaf.Type rightConstituentType;

    // binary
    public SubordinationExtraction(String extractionRule, Relation relation, List<Word> signalPhraseWords, List<Word> leftConstituentWords, List<Word> rightConstituentWords, boolean superordinationIsLeft, Leaf.Type leftConstituentType, Leaf.Type rightConstituentType) {
        this.extractionRule = extractionRule;
        this.relation = relation;
        this.signalPhrase = (signalPhraseWords != null) ? WordsUtils.wordsToString(signalPhraseWords) : null;
        this.leftConstituent = WordsUtils.wordsToProperSentenceString(leftConstituentWords);
        this.rightConstituent = WordsUtils.wordsToProperSentenceString(rightConstituentWords);
        this.superordinationIsLeft = superordinationIsLeft;
        this.leftConstituentType = leftConstituentType;
        this.rightConstituentType = rightConstituentType;
    }

    public DiscourseTree convert() {
        return new Subordination(
                extractionRule,
                relation,
                signalPhrase,
                new Leaf(leftConstituentType, extractionRule, leftConstituent),
                new Leaf(rightConstituentType, extractionRule, rightConstituent),
                superordinationIsLeft
        );
    }
}
