/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SharedNPPreParticipalExtractor
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
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
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
public class SharedNPPreParticipalExtractor extends ExtractionRule {

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {

        String participalNode = "(__=node [== S=s | == (PP|ADVP <+(PP|ADVP) S=s)]) : (=s <: (VP <<, VBG|VBN=vbgn))";
        TregexPattern p = TregexPattern.compile("ROOT <<: (S < " + participalNode + ") : (=node $.. (NP=np $.. VP=vp))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {
            List<Word> cuePhraseWords = ParseTreeExtractionUtils.getPrecedingWords(matcher.getNode("node"), matcher.getNode("s"), false);

            // the left, subordinate constituent
            List<Word> leftConstituentWords = new ArrayList<>();
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("node"), false));
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np")));
            leftConstituentWords.addAll(getRephrasedParticipalS(matcher.getNode("np"), matcher.getNode("vp"), matcher.getNode("s"), matcher.getNode("vbgn")));
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("vp"), false));
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));

            // the right, superordinate constituent
            List<Word> rightConstituentWords = new ArrayList<>();
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("node"), false));
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("node"), false));
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));

            // relation
            Relation relation = classifer.classifySubordinating(cuePhraseWords).orElse(Relation.UNKNOWN_COORDINATION);

            Extraction res = new Extraction(
                getClass().getSimpleName(),
                false,
                cuePhraseWords,
                relation,
                false,
                Arrays.asList(leftConstituent, rightConstituent)
            );

            return Optional.of(res);
        }

        return Optional.empty();
    }

}
