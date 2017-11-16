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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.utils;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils.findLeaves;
import static org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils.splitLeaves;

/**
 *
 */
public class ListNPSplitter {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ListNPSplitter.class);

    public static class Result {
        private final List<Word> introductionWords; //optional
        private final List<List<Word>> elementsWords;

        private final Relation relation;

        public Result(List<Word> introductionWords, List<List<Word>> elementsWords, Relation relation) {
            this.introductionWords = introductionWords;
            this.elementsWords = elementsWords;
            this.relation = relation;
        }

        public Optional<List<Word>> getIntroductionWords() {
            return Optional.ofNullable(introductionWords);
        }

        public List<List<Word>> getElementsWords() {
            return elementsWords;
        }

        public Relation getRelation() {
            return relation;
        }

    }

    private static class ConjunctionLeafChecker implements ParseTreeExtractionUtils.INodeChecker {
        private final String word;

        public ConjunctionLeafChecker(String word) {
            this.word = word;
        }

        @Override
        public boolean check(Tree anchorTree, Tree leaf) {
            return ((leaf.yieldWords().get(0).value().equals(word)) && (leaf.parent(anchorTree) != null) && (leaf.parent(anchorTree).value().equals("CC")));
        }
    }

    private static class ValueLeafChecker implements ParseTreeExtractionUtils.INodeChecker {
        private final String word;

        public ValueLeafChecker(String word) {
            this.word = word;
        }

        @Override
        public boolean check(Tree anchorTree, Tree leaf) {
            return leaf.value().equals(word);
        }
    }

    private static boolean checkElementLeaves(Tree anchorTree, List<Tree> leaves) {
        Optional<Tree> spanningTree = ParseTreeExtractionUtils.findSpanningTree(anchorTree, leaves.get(0), leaves.get(leaves.size() - 1));
        return (spanningTree.isPresent()) && (spanningTree.get().value().equals("NP"));

    }

    private static boolean isFollowedByConjDisjunction(Tree anchorTree, Tree np, ParseTreeExtractionUtils.INodeChecker conjDisjChecker, ParseTreeExtractionUtils.INodeChecker separatorChecker) {
        List<Tree> followingLeaves = ParseTreeExtractionUtils.getFollowingLeaves(anchorTree, np, false);
        for (Tree followingLeaf : followingLeaves) {
            if (conjDisjChecker.check(anchorTree, followingLeaf)) {
                return true;
            } else if (separatorChecker.check(anchorTree, followingLeaf)) {
                // nothing
            } else {
                return false;
            }
        }
        return false;
    }

    private static Optional<Result> check(Tree anchorTree, Tree np, ParseTreeExtractionUtils.INodeChecker conjDisjChecker, ParseTreeExtractionUtils.INodeChecker separatorChecker, Relation relation) {
        List<Tree> checkLeaves = ParseTreeExtractionUtils.getContainingLeaves(np);

        // find introduction
        List<Word> introductionWords = null;
        List<Tree> introSeparators = findLeaves(anchorTree, checkLeaves, new ValueLeafChecker(":"), false);
        if (introSeparators.size() > 0) {
            List<Word> iws = ParseTreeExtractionUtils.getPrecedingWords(np, introSeparators.get(0), false);
            if (iws.size() > 0) {
                introductionWords = iws;
                checkLeaves = ParseTreeExtractionUtils.getFollowingLeaves(np, introSeparators.get(0), false);
            }
        }

        if (checkLeaves.size() == 0) {
            return Optional.empty();
        }

        // special case (Con/Disjunction is right after the NP e.g. initiating a verb phrase)
        // e.g. "To leave monuments to his reign , he built [the Collège des Quatre-Nations , Place Vendôme , Place des Victoires ,] and began Les Invalides ."
        if (isFollowedByConjDisjunction(anchorTree, np, conjDisjChecker, separatorChecker)) {
            List<List<Tree>> elements = splitLeaves(np, checkLeaves, separatorChecker, true);
            boolean valid = true;

            // check elements
            if (elements.size() >= 2) {
                for (List<Tree> element : elements) {
                    if (!checkElementLeaves(np, element)) {
                        valid = false;
                        break;
                    }
                }
            } else {
                valid = false;
            }

            if (valid) {
                List<List<Word>> elementsWords = elements.stream().map(e -> ParseTreeExtractionUtils.leavesToWords(e)).collect(Collectors.toList());

                return Optional.of(new Result(introductionWords, elementsWords, relation));
            }
        }

        // check different conjunction/disjunction leaves (from right to left)
        for (Tree cdLeaf : findLeaves(np, checkLeaves, conjDisjChecker, true)) {
            boolean valid = true;
            List<Tree> beforeLeaves = ParseTreeExtractionUtils.getLeavesInBetween(np, checkLeaves.get(0), cdLeaf, true, false);
            List<Tree> afterLeaves = ParseTreeExtractionUtils.getLeavesInBetween(np, cdLeaf, checkLeaves.get(checkLeaves.size() - 1), false, true);

            List<List<Tree>> beforeElements = splitLeaves(np, beforeLeaves, separatorChecker, true);
            List<Tree> afterElement = afterLeaves;

            // check before elements
            if (beforeElements.size() >= 1) {
                for (List<Tree> beforeElement : beforeElements) {
                    if (!checkElementLeaves(np, beforeElement)) {
                        valid = false;
                        break;
                    }
                }
            } else {
                valid = false;
            }

            // check after element
            if (afterElement.size() >= 1) {
//                if (!checkElementLeaves(anchorTree, afterElement)) {
//                    valid = false;
//                }
            } else {
                valid = false;
            }

            if (valid) {
                List<List<Word>> elementsWords = new ArrayList<>();
                elementsWords.addAll(beforeElements.stream().map(e -> ParseTreeExtractionUtils.leavesToWords(e)).collect(Collectors.toList()));
                elementsWords.add(ParseTreeExtractionUtils.leavesToWords(afterLeaves));

                return Optional.of(new Result(introductionWords, elementsWords, relation));
            }
        }

        return Optional.empty();
    }

    public static Optional<Result> splitList(Tree anchorTree, Tree np) {
        boolean containsSemicolon = ParseTreeExtractionUtils.findLeaves(np, ParseTreeExtractionUtils.getContainingLeaves(np), new ValueLeafChecker(";"), false).size() > 0;
        if (containsSemicolon) {

            // check for conjunction with elements separated by ;
            Optional<Result> r = check(anchorTree, np, new ConjunctionLeafChecker("and"), new ValueLeafChecker(";"), Relation.LIST);
            if (r.isPresent()) {
                return r;
            }

            // check for disjunction with elements separated by ;
            r = check(anchorTree, np, new ConjunctionLeafChecker("or"), new ValueLeafChecker(";"), Relation.DISJUNCTION);
            if (r.isPresent()) {
                return r;
            }
        } else {

            // check for conjunction with elements separated by ,
            Optional<Result> r = check(anchorTree, np, new ConjunctionLeafChecker("and"), new ValueLeafChecker(","), Relation.LIST);
            if (r.isPresent()) {
                return r;
            }

            // check for disjunction with elements separated by ,
            r = check(anchorTree, np, new ConjunctionLeafChecker("or"), new ValueLeafChecker(","), Relation.DISJUNCTION);
            if (r.isPresent()) {
                return r;
            }
        }

        return Optional.empty();
    }
}
