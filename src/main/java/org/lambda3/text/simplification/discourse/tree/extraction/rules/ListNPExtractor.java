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

package org.lambda3.text.simplification.discourse.tree.extraction.rules;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.tree.extraction.model.CoordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.extraction.utils.ListNPSplitter;
import org.lambda3.text.simplification.discourse.tree.extraction.utils.TregexUtils;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ListNPExtractor extends ExtractionRule {

    public ListNPExtractor(String pattern) {
        super(pattern);
    }

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        List<TregexUtils.MyMatch> matches = TregexUtils.sortedFindAt(parseTree, pattern, Collections.singletonList("np"));
        if (matches.size() > 0) {
            TregexUtils.MyMatch match = matches.get(0);

            Optional<ListNPSplitter.Result> r = ListNPSplitter.split(match.getNode("np"));
            if (r.isPresent()) {

                // constituents
                List<Word> precedingWords = ParseTreeExtractionUtils.getPrecedingWords(parseTree, match.getNode("np"), false);
                List<Word> followingWords = ParseTreeExtractionUtils.getFollowingWords(parseTree, match.getNode("np"), false);
                List<List<Word>> constituentsWords = new ArrayList<>();


                for (List<Word> element : r.get().getElementsWords()) {
                    List<Word> constituentWords = new ArrayList<>();

                    constituentWords.addAll(precedingWords);
                    constituentWords.addAll(element);
                    constituentWords.addAll(followingWords);

                    constituentsWords.add(constituentWords);
                }

                // result
                Extraction res = new CoordinationExtraction(
                        getClass().getSimpleName(),
                        r.get().getRelation(),
                        constituentsWords,
                        Leaf.Type.TERMINAL
                );

                return Optional.of(res);
            }
        }


        return Optional.empty();
    }
}
