/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ListNPExtractor
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.rules.ListNP;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.utils.ListNPSplitter;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public abstract class ListNPExtractor extends ExtractionRule {
    private final String pattern;

    public ListNPExtractor(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {

        TregexPattern p = TregexPattern.compile(pattern);
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {

            Optional<ListNPSplitter.Result> r = ListNPSplitter.splitList(leaf.getParseTree(), matcher.getNode("np"));
            if (r.isPresent()) {

                // constituents
                List<Word> precedingWords = ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("np"), false);
                List<Word> followingWords = ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("np"), false);

                List<Leaf> constituents = new ArrayList<>();

                if (r.get().getIntroductionWords().isPresent()) {
                    List<Word> words = new ArrayList<Word>();
                    words.addAll(precedingWords);
                    words.addAll(r.get().getIntroductionWords().get());
                    words.addAll(followingWords);

                    Leaf constituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(words));
                    constituent.dontAllowSplit();
                    constituents.add(constituent);
                }

                for (List<Word> element : r.get().getElementsWords()) {
                    List<Word> words = new ArrayList<Word>();
                    words.addAll(precedingWords);
                    words.addAll(element);
                    words.addAll(followingWords);

                    Leaf constituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(words));
                    constituent.dontAllowSplit();
                    constituents.add(constituent);
                }


                Extraction res = new Extraction(
                    getClass().getSimpleName(),
                    false,
                    null,
                    r.get().getRelation(),
                    true,
                    constituents
                );

                return Optional.of(res);
            }
        }

        return Optional.empty();
    }
}
