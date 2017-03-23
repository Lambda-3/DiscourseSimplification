/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtractionRule
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

package org.lambda3.text.simplification.discourse.tree.extraction.rules;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.classification.SignalPhraseClassifier;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.tree.extraction.model.RefCoordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ReferenceExtractor2 extends ExtractionRule {
    private static final SignalPhraseClassifier CLASSIFIER = new SignalPhraseClassifier();

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexPattern p = TregexPattern.compile("ROOT <<: S <<, (__=node >1 S << /this|that/=det)");
        TregexMatcher matcher = p.matcher(parseTree);

        if (matcher.findAt(parseTree)) {
            List<Word> signalPhraseWords = ParseTreeExtractionUtils.getPrecedingWords(matcher.getNode("node"), matcher.getNode("det"), true);

            // the right constituent
            List<Word> words = ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("node"), false);
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(words));

            // relation
            Optional<Relation> relation = CLASSIFIER.classifyGeneral(signalPhraseWords);

            if (relation.isPresent()) {
                Extraction res = new RefCoordinationExtraction(
                        getClass().getSimpleName(),
                        relation.get(),
                        signalPhraseWords,
                        rightConstituent
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }
}
