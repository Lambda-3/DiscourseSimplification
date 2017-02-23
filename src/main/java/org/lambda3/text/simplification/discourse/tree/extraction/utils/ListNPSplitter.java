/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ListNPSplitter
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

package org.lambda3.text.simplification.discourse.tree.extraction.utils;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.tree.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class ListNPSplitter {

    public static Optional<Result> split(Tree np) {

        // representation
        String representation = np.getChildrenAsList().stream().map(c -> (c.value().equals("CC")) ? c.yieldWords().get(0).value() : c.value()).collect(Collectors.joining(""));

        final String LIST_CONJUNCTION_PATTERN = "^(NP|,)*NP(NP|,)*(and(NP|,)*NP(NP|,)*)+$";
        final String LIST_DISJUNCTION_PATTERN = "^(NP|,)*NP(NP|,)*(or(NP|,)*NP(NP|,)*)+$";
        if (representation.matches(LIST_CONJUNCTION_PATTERN) || representation.matches(LIST_DISJUNCTION_PATTERN)) {
            Relation relation = representation.matches(LIST_CONJUNCTION_PATTERN) ? Relation.JOINT_NP_LIST : Relation.JOINT_NP_DISJUNCTION;

            // get last CC index
            int lastCCIdx = 0;
            for (int i = np.getChildrenAsList().size() - 1; i >= 0; i--) {
                Tree child = np.getChildrenAsList().get(i);
                if (child.value().equals("CC")) {
                    lastCCIdx = i;
                    break;
                }
            }

            // extract
            List<List<Word>> elementsWords = new ArrayList<>();
            boolean foundFirstNPAfterCC = false;
            for (int i = 0; i < np.getChildrenAsList().size(); i++) {
                Tree child = np.getChildrenAsList().get(i);

                if (foundFirstNPAfterCC) {
                    elementsWords.get(elementsWords.size() - 1).addAll(child.yieldWords());
                } else if (child.value().equals("NP")) {
                    elementsWords.add(child.yieldWords());
                    if (i > lastCCIdx) {
                        foundFirstNPAfterCC = true;
                    }
                }
            }

            return Optional.of(new Result(elementsWords, relation));
        }

        return Optional.empty();
    }

    public static class Result {
        private final List<List<Word>> elementsWords;
        private final Relation relation;

        public Result(List<List<Word>> elementsWords, Relation relation) {
            this.elementsWords = elementsWords;
            this.relation = relation;
        }

        public List<List<Word>> getElementsWords() {
            return elementsWords;
        }

        public Relation getRelation() {
            return relation;
        }
    }

}
