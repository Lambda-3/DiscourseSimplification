/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ReferenceExtractor1
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
public class ReferenceInitialAdverbialExtractor extends ExtractionRule {

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {

        TregexPattern p = TregexPattern.compile("ROOT <<: (S <, (ADVP|PP=adv))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        if (matcher.findAt(leaf.getParseTree())) {
            List<Word> cuePhraseWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("adv"));

            // the right constituent
            List<Word> words = new ArrayList<>();
            words.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("adv"), false));
            words.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("adv"), false));
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(words));

            // relation
            Optional<Relation> relation = classifer.classifyAdverbial(cuePhraseWords);

            // only if present
            if (relation.isPresent()) {
                Extraction res = new Extraction(
                    getClass().getSimpleName(),
                    true,
                    cuePhraseWords,
                    relation.get(),
                    true,
                    Arrays.asList(rightConstituent)
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }
}
