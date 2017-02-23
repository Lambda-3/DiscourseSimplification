/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SubordinationExtractor
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
import org.lambda3.text.simplification.discourse.tree.extraction.model.SubordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubordinationExtractor extends ExtractionRule {

    SubordinationExtractor(String pattern) {
        super(pattern);
    }

    public SubordinationExtractor() {
        this("ROOT <<: (S < (SBAR=sbar < (S=s) $.. (NP $.. VP=vp)))");
    }

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexMatcher matcher = pattern.matcher(parseTree);

        if (matcher.findAt(parseTree)) {
            List<Word> signalPhraseWords = ParseTreeExtractionUtils.getPrecedingWords(matcher.getNode("sbar"), matcher.getNode("s"), false);

            // the left, subordinate constituent
            List<Word> leftConstituentWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s"));

            // the right, superordinate constituent
            List<Word> rightConstituentWords = new ArrayList<>();
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(parseTree, matcher.getNode("sbar"), false));
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("sbar"), false));

            // result
            Optional<Relation> relation = Optional.empty();
            Leaf.Type leftConstituentType = Leaf.Type.DEFAULT;
            Leaf.Type rightConstituentType = Leaf.Type.DEFAULT;

            // enablement
            if (isInfinitival(matcher.getNode("s"))) {
                relation = Optional.of(Relation.ENABLEMENT);
                leftConstituentWords = rephraseEnablement(matcher.getNode("s"), matcher.getNode("vp"));
                leftConstituentType = Leaf.Type.SENT_SIM_CONTEXT;
            }

            // general
            if (!relation.isPresent()) {
                relation = SignalPhraseClassifier.classifyGeneral(signalPhraseWords);
            }

            return Optional.of(new SubordinationExtraction(
                    getClass().getSimpleName(),
                    relation.orElse(Relation.UNKNOWN_SUBORDINATION),
                    signalPhraseWords,
                    leftConstituentWords, // the subordinate constituent
                    rightConstituentWords, // the superordinate constituent
                    false,
                    leftConstituentType,
                    rightConstituentType));
        }

        return Optional.empty();
    }

}
