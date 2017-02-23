/*
 * ==========================License-Start=============================
 * DiscourseSimplification : CoordinationExtractor
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
import org.lambda3.text.simplification.discourse.tree.extraction.model.CoordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CoordinationExtractor extends ExtractionRule {

    private final String node1;
    private final String node2;

    public CoordinationExtractor() {
        this("ROOT <<: (S=s < (S $.. S))", "s", "S");
    }

    CoordinationExtractor(String pattern, String node1, String node2) {
        super(pattern);
        this.node1 = node1;
        this.node2 = node2;
    }

    private List<List<Word>> combineSiblings(List<Word> precedingWords, List<Word> followingWords, List<Tree> siblings) {
        List<List<Word>> constituentsWords = new ArrayList<>();
        for (Tree sibling : siblings) {
            List<Word> constituentWords = new ArrayList<>();

            constituentWords.addAll(precedingWords);
            constituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(sibling));
            constituentWords.addAll(followingWords);

            constituentsWords.add(constituentWords);
        }

        return constituentsWords;
    }

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexMatcher matcher = pattern.matcher(parseTree);

        if (matcher.findAt(parseTree)) {
            List<Tree> siblings = getSiblings(matcher.getNode(node1), Collections.singletonList(node2));

            // constituents
            List<Word> precedingWords = ParseTreeExtractionUtils.getPrecedingWords(parseTree, siblings.get(0), false);
            List<Word> followingWords = ParseTreeExtractionUtils.getFollowingWords(parseTree, siblings.get(siblings.size() - 1), false);
            List<List<Word>> constituentsWords = combineSiblings(precedingWords, followingWords, siblings);

            // result
            if (constituentsWords.size() == 2) {
                List<Word> signalPhraseWords = ParseTreeExtractionUtils.getWordsInBetween(parseTree, siblings.get(0), siblings.get(siblings.size() - 1), false, false);
                Optional<Relation> relation = SignalPhraseClassifier.classifyGeneral(signalPhraseWords);

                return Optional.of(new CoordinationExtraction(
                        getClass().getSimpleName(),
                        relation.orElse(Relation.UNKNOWN_COORDINATION),
                        signalPhraseWords,
                        constituentsWords.get(0),
                        constituentsWords.get(constituentsWords.size() - 1),
                        Leaf.Type.DEFAULT)
                );
            } else {
                return Optional.of(new CoordinationExtraction(
                        getClass().getSimpleName(),
                        Relation.UNKNOWN_COORDINATION,
                        constituentsWords,
                        Leaf.Type.DEFAULT)
                );
            }
        }

        return Optional.empty();
    }

}
