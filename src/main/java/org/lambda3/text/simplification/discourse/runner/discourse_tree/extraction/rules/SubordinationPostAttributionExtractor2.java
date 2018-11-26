/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SubordinationPostAttributionExtractor2
 *
 * Copyright © 2018 Lambda³
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
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
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
public class SubordinationPostAttributionExtractor2 extends ExtractionRule {

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {
        TregexPattern p = TregexPattern.compile("ROOT <<: (S < (NP $.. (VP=vp <+(VP) (SBAR=sbar <<, /that/ < (S=s)))))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {

            // the left, !subordinate! constituent
            List<Word> leftConstituentWords = new ArrayList<>();
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("sbar"), false));
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("sbar"), false));

            // rephrase
            leftConstituentWords = rephraseIntraSententialAttribution(leftConstituentWords);
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));
            leftConstituent.dontAllowSplit();
            leftConstituent.setToSimpleContext(true);

            // the right, !superordinate! constituent
            List<Word> rightConstituentWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s"));
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));

            // relation
            Optional<Word> headVerb = getHeadVerb(matcher.getNode("vp"));

            // only extract if verb matches
            if (headVerb.isPresent() && classifer.checkAttribution(headVerb.get())) {
                Relation relation = Relation.ATTRIBUTION;

                Extraction res = new Extraction(
                    getClass().getSimpleName(),
                    false,
                    null,
                    relation,
                    false,
                    Arrays.asList(leftConstituent, rightConstituent)
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }
}
