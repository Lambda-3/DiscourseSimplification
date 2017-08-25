/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SharedNPPostCoordinationExtractor
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.rules;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.classification.SignalPhraseClassifier;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.model.CoordinationExtraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class SharedNPPostCoordinationExtractor extends ExtractionRule {
    private static final SignalPhraseClassifier CLASSIFIER = new SignalPhraseClassifier();

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {

        TregexPattern p = TregexPattern.compile("ROOT <<: (S < (NP $.. (VP <+(VP) (VP > VP=vp $.. VP))))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {
            List<Tree> siblings = getSiblings(matcher.getNode("vp"), Arrays.asList("VP"));

            // constituents
            List<Word> precedingWords = ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), siblings.get(0), false);
            List<Word> followingWords = ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), siblings.get(siblings.size() - 1), false);

            List<Leaf> constituents = new ArrayList<>();
            for (Tree sibling : siblings) {
                List<Word> words = new ArrayList<Word>();
                words.addAll(precedingWords);
                words.addAll(ParseTreeExtractionUtils.getContainingWords(sibling));
                words.addAll(followingWords);

                Leaf constituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(words));
                constituents.add(constituent);
            }

            if (constituents.size() == 2) {

                // relation
                List<Word> signalPhraseWords = ParseTreeExtractionUtils.getWordsInBetween(leaf.getParseTree(), siblings.get(0), siblings.get(siblings.size() - 1), false, false);
                Relation relation = CLASSIFIER.classifyDefault(signalPhraseWords).orElse(Relation.UNKNOWN_COORDINATION);

                Extraction res = new CoordinationExtraction(
                        getClass().getSimpleName(),
                        relation,
                        signalPhraseWords,
                        constituents.get(0),
                        constituents.get(constituents.size() - 1)
                );

                return Optional.of(res);
            } else {

                // relation
                Relation relation = Relation.UNKNOWN_COORDINATION;

                Extraction res = new CoordinationExtraction(
                        getClass().getSimpleName(),
                        relation,
                        constituents
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }

}
