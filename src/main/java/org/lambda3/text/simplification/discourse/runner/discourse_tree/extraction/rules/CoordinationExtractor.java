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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.rules;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class CoordinationExtractor extends ExtractionRule {

    private static boolean isNPVPClause(Tree s) {
        TregexPattern p = TregexPattern.compile(s.value() + " < (NP $.. VP)");
        TregexMatcher matcher = p.matcher(s);

        return (matcher.findAt(s));
    }

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {

//        TregexPattern p = TregexPattern.compile("ROOT <<: (S=s < (S < (NP $.. VP) $.. (S < (NP $.. VP))))");
        TregexPattern p = TregexPattern.compile("ROOT <<: (S=s < (S $.. S))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {
//            List<Tree> siblings = getSiblings(matcher.getNode("s"), Arrays.asList("S")).stream().filter(t -> isNPVPClause(t)).collect(Collectors.toList());
            List<Tree> siblings = getSiblings(matcher.getNode("s"), Arrays.asList("S")).stream().collect(Collectors.toList());
            if (siblings.size() >= 2) {

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

                List<Word> cuePhraseWords = null;
                Relation relation = Relation.UNKNOWN_COORDINATION;
                if (constituents.size() == 2) {
                    cuePhraseWords = ParseTreeExtractionUtils.getWordsInBetween(leaf.getParseTree(), siblings.get(0), siblings.get(siblings.size() - 1), false, false);
                    relation = classifer.classifyCoordinating(cuePhraseWords).orElse(Relation.UNKNOWN_COORDINATION);
                }

                Extraction res = new Extraction(
                    getClass().getSimpleName(),
                    false,
                    cuePhraseWords,
                    relation,
                    true,
                    constituents
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }
}
