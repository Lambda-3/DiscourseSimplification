/*
 * ==========================License-Start=============================
 * DiscourseSimplification : EnablementPostExtractor
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
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.model.SubordinationExtraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class EnablementPostExtractor extends ExtractionRule {

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexPattern p = TregexPattern.compile("ROOT <<: (S < (NP $.. (VP=vp <+(VP) (NP|PP $.. (S=s <<, (VP <<, /(T|t)o/))))))");
        TregexMatcher matcher = p.matcher(parseTree);

        while (matcher.findAt(parseTree)) {

            // the left, superordinate constituent
            List<Word> leftConstituentWords = new ArrayList<>();
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(parseTree, matcher.getNode("s"), false));
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("s"), false));
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));

            // the right, subordinate constituent
            List<Word> rightConstituentWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s"));
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));

            // rephrase
            List<Word> rephrasedWords = rephraseEnablement(matcher.getNode("s"), matcher.getNode("vp"));
            rightConstituent.setText(WordsUtils.wordsToProperSentenceString(rephrasedWords));
            rightConstituent.dontAllowSplit();
            rightConstituent.setToSimpleContext(true);

            // relation
            Relation relation = Relation.ENABLEMENT;

            Extraction res = new SubordinationExtraction(
                    getClass().getSimpleName(),
                    relation,
                    null,
                    leftConstituent, // the superordinate constituent
                    rightConstituent, // the subordinate constituent
                    true
            );

            return Optional.of(res);
        }

        return Optional.empty();
    }
}
