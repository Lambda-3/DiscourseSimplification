/*
 * ==========================License-Start=============================
 * DiscourseSimplification : IntraSententialSubordinationExtraction
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
import org.lambda3.text.simplification.discourse.tree.extraction.model.SubordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class IntraSententialSubordinationExtraction extends SubordinationExtractor {
    // custom mappings
    private static final List<SignalPhraseClassifier.Mapping> INTRA_SENTENTIAL_ATTRIBUTION_MAPPINGS = Arrays.asList(

            // INTRA_SENTENTIAL_ATTRIBUTION
            new SignalPhraseClassifier.Mapping(Relation.INTRA_SENTENTIAL_ATTRIBUTION, ""),
            new SignalPhraseClassifier.Mapping(Relation.INTRA_SENTENTIAL_ATTRIBUTION, "...that...")
    );


    public IntraSententialSubordinationExtraction() {
        super("ROOT <<: (S < (NP $.. (VP=vp <+(VP) (SBAR=sbar < (S=s)))))");
    }

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexMatcher matcher = pattern.matcher(parseTree);

        if (matcher.findAt(parseTree)) {
            List<Word> signalPhraseWords = ParseTreeExtractionUtils.getPrecedingWords(matcher.getNode("sbar"), matcher.getNode("s"), false);

            // the left, (usually) superordinate constituent
            List<Word> leftConstituentWords = new ArrayList<>();
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(parseTree, matcher.getNode("sbar"), false));
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("sbar"), false));

            // the right, (usually) subordinate constituent
            List<Word> rightConstituentWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s"));

            // result
            Optional<Relation> relation;
            boolean superordinationIsLeft = true;
            Leaf.Type leftConstituentType = Leaf.Type.DEFAULT;
            Leaf.Type rightConstituentType = Leaf.Type.DEFAULT;

            // intra sentential attribution
            relation = SignalPhraseClassifier.classifyCustom(INTRA_SENTENTIAL_ATTRIBUTION_MAPPINGS, signalPhraseWords);
            if (relation.isPresent()) {
                leftConstituentWords = rephraseIntraSententialAttribution(leftConstituentWords);
                leftConstituentType = Leaf.Type.SENT_SIM_CONTEXT;

                // swap superordinate with subordinate assignment
                superordinationIsLeft = false;
            }

            // enablement
            if (!relation.isPresent()) {
                if (isInfinitival(matcher.getNode("s"))) {
                    relation = Optional.of(Relation.ENABLEMENT);
                    rightConstituentWords = rephraseEnablement(matcher.getNode("s"), matcher.getNode("vp"));
                    rightConstituentType = Leaf.Type.SENT_SIM_CONTEXT;
                }
            }

            // general
            if (!relation.isPresent()) {
                relation = SignalPhraseClassifier.classifyGeneral(signalPhraseWords);
            }

            return Optional.of(new SubordinationExtraction(
                    getClass().getSimpleName(),
                    relation.orElse(Relation.UNKNOWN_SUBORDINATION),
                    signalPhraseWords,
                    leftConstituentWords, // the superordinate constituent
                    rightConstituentWords, // the subordinate constituent
                    superordinationIsLeft,
                    leftConstituentType,
                    rightConstituentType));
        }

        return Optional.empty();
    }

}
