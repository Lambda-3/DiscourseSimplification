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
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class ListNPSplitter {
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

    private interface ILeafChecker {
        boolean check(Tree anchorTree, Tree leaf);
    }

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ListNPSplitter.class);

    private static class ConjunctionLeafChecker implements ILeafChecker {

        @Override
        public boolean check(Tree anchorTree, Tree leaf) {
            return ((leaf.yieldWords().get(0).value().equals("and")) && (leaf.parent(anchorTree) != null) && (leaf.parent(anchorTree).value().equals("CC")));
        }
    }

    private static class DisjunctionLeafChecker implements ILeafChecker {

        @Override
        public boolean check(Tree anchorTree, Tree leaf) {
            return ((leaf.yieldWords().get(0).value().equals("or")) && (leaf.parent(anchorTree) != null) && (leaf.parent(anchorTree).value().equals("CC")));
        }
    }

    private static class SeparatorLeafChecker implements ILeafChecker {

        @Override
        public boolean check(Tree anchorTree, Tree leaf) {
            return ((leaf.parent(anchorTree) != null) && (leaf.parent(anchorTree).value().equals(",")));
        }
    }

    private static boolean checkElementLeaves(Tree anchorTree, List<Tree> leaves) {
        Optional<Tree> spanningTree = ParseTreeExtractionUtils.findSpanningTree(anchorTree, leaves.get(0), leaves.get(leaves.size() - 1));
        return (spanningTree.isPresent()) && ((spanningTree.get().value().equals("NP")) || (spanningTree.get().value().equals("NNP")));

    }

    private static List<List<Tree>> splitLeaves(Tree anchorTree, List<Tree> leaves, ILeafChecker leafChecker, boolean removeEmpty) {
        List<List<Tree>> res = new ArrayList<>();
        List<Tree> currElement = new ArrayList<>();
        for (Tree leaf : leaves) {
            if (leafChecker.check(anchorTree, leaf)) {
                if ((currElement.size() > 0) || (!removeEmpty))
                    res.add(currElement);
                currElement = new ArrayList<>();
            } else {
                currElement.add(leaf);
            }
        }
        if ((currElement.size() > 0) || (!removeEmpty))
            res.add(currElement);

        return res;
    }

    private static List<Tree> findLeaves(Tree anchorTree, List<Tree> leaves, ILeafChecker leafChecker, boolean reversed) {
        List<Tree> res = leaves.stream().filter(l -> leafChecker.check(anchorTree, l)).collect(Collectors.toList());
        if (reversed) {
            Collections.reverse(res);
        }
        return res;
    }

    private static Optional<Result> check(Tree np, ILeafChecker cdChecker, ILeafChecker separatorChecker, Relation relation) {

        for (Tree cdLeaf : findLeaves(np, ParseTreeExtractionUtils.getContainingLeaves(np), cdChecker, true)) {
            boolean valid = true;
            List<Tree> beforeLeaves = ParseTreeExtractionUtils.getPrecedingLeaves(np, cdLeaf, false);
            List<Tree> afterLeaves = ParseTreeExtractionUtils.getFollowingLeaves(np, cdLeaf, false);

            List<List<Tree>> beforeElements = splitLeaves(np, beforeLeaves, separatorChecker, true);
            List<Tree> afterElement = afterLeaves;

            // check before elements
            if (beforeElements.size() > 0) {
                for (List<Tree> beforeElement : beforeElements) {
                    if (!checkElementLeaves(np, beforeElement)) {
                        valid = false;
                        break;
                    }
                }
            }

            // check after element
            if (afterElement.size() > 0) {
                if (!checkElementLeaves(np, afterElement)) {
                    valid = false;
                }
            } else {
                valid = false;
            }

            if (valid) {
                List<List<Word>> elementsWords = new ArrayList<>();
                elementsWords.addAll(beforeElements.stream().map(ls -> ls.stream().map(l -> l.yieldWords().get(0)).collect(Collectors.toList())).collect(Collectors.toList()));
                elementsWords.add(afterElement.stream().map(l -> l.yieldWords().get(0)).collect(Collectors.toList()));

                return Optional.of(new Result(elementsWords, relation));
            }
        }

        return Optional.empty();
    }

    public static Optional<Result> split(Tree np) {

        // check for conjunction
        Optional<Result> r = check(np, new ConjunctionLeafChecker(), new SeparatorLeafChecker(), Relation.JOINT_LIST);
        if (r.isPresent()) {
            return r;
        }

        // check for disjunction
        r = check(np, new DisjunctionLeafChecker(), new SeparatorLeafChecker(), Relation.JOINT_DISJUNCTION);
        if (r.isPresent()) {
            return r;
        }

        return Optional.empty();
    }
}
