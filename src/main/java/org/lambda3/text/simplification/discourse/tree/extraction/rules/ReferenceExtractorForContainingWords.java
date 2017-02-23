/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ReferenceExtractorForContainingWords
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
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.classification.SignalPhraseClassifier;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.tree.extraction.model.RefCoordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ReferenceExtractorForContainingWords extends ExtractionRule {

    public ReferenceExtractorForContainingWords() {
        super("ROOT <<: S <<, (__=node >1 S <<: (__=leaf !< __))");
    }


    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexMatcher matcher = pattern.matcher(parseTree);

        if (matcher.findAt(parseTree)) {
            List<Word> signalPhraseWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("leaf"));

            // the right constituent
            List<Word> rightConstituentWords = ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("node"), false);

            // result
            Optional<Relation> relation = SignalPhraseClassifier.classifyGeneral(signalPhraseWords);
            if (relation.isPresent()) {
                Extraction res = new RefCoordinationExtraction(
                        getClass().getSimpleName(),
                        relation.get(),
                        signalPhraseWords,
                        rightConstituentWords,
                        Leaf.Type.DEFAULT
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }
}
