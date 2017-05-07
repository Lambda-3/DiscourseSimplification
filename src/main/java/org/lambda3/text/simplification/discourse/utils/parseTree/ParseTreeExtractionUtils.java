/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ParseTreeExtractionUtils
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

package org.lambda3.text.simplification.discourse.utils.parseTree;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.IndexRange;
import org.lambda3.text.simplification.discourse.utils.ner.NERExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.ner.NERString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParseTreeExtractionUtils {

    public interface INodeChecker {
        boolean check(Tree anchorTree, Tree node);
    }


    public static List<Integer> getLeafNumbers(Tree anchorTree, Tree node) {
        List<Integer> res = new ArrayList<>();
        for (Tree leaf : node.getLeaves()) {
            res.add(leaf.nodeNumber(anchorTree));
        }
        return res;
    }

    private static IndexRange getLeafIndexRange(Tree anchorTree, Tree node) {
        int fromIdx = -1;
        int toIdx = -1;

        List<Integer> leafNumbers = getLeafNumbers(anchorTree, anchorTree);
        List<Integer> nodeLeafNumbers = getLeafNumbers(anchorTree, node);
        int fromNumber = nodeLeafNumbers.get(0);
        int toNumber = nodeLeafNumbers.get(nodeLeafNumbers.size() - 1);

        int idx = 0;
        for (int leafNumber : leafNumbers) {
            if (leafNumber == fromNumber) {
                fromIdx = idx;
            }
            if (leafNumber == toNumber) {
                toIdx = idx;
            }
            ++idx;
        }

        if ((fromIdx >= 0) && (toIdx >= 0)) {
            return new IndexRange(fromIdx, toIdx);
        } else {
            throw new IllegalArgumentException("node should be a subtree of anchorTree.");
        }
    }

    // returns True, if the model of node would not check/divide a NER group, else False
    public static boolean isNERSafeExtraction(Tree anchorTree, NERString anchorNERString, Tree node) {
        IndexRange leafIdxRange = getLeafIndexRange(anchorTree, node);
        List<IndexRange> nerIdxRanges = NERExtractionUtils.getNERIndexRanges(anchorNERString);

        for (IndexRange nerIdxRange : nerIdxRanges) {
            if (((nerIdxRange.getFromIdx() < leafIdxRange.getFromIdx()) && (leafIdxRange.getFromIdx() <= nerIdxRange.getToIdx()))
                    || ((nerIdxRange.getFromIdx() <= leafIdxRange.getToIdx()) && (leafIdxRange.getToIdx() < nerIdxRange.getToIdx()))) {
                return false;
            }
        }

        return true;
    }




    public static List<Word> leavesToWords(List<Tree> leaves) {
        return leaves.stream().map(l -> l.yieldWords().get(0)).collect(Collectors.toList());
    }

    public static List<List<Tree>> splitLeaves(Tree anchorTree, List<Tree> leaves, INodeChecker leafChecker, boolean removeEmpty) {
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

    public static List<Tree> findLeaves(Tree anchorTree, List<Tree> leaves, INodeChecker leafChecker, boolean reversed) {
        List<Tree> res = leaves.stream().filter(l -> leafChecker.check(anchorTree, l)).collect(Collectors.toList());
        if (reversed) {
            Collections.reverse(res);
        }
        return res;
    }

    public static Tree getFirstLeaf(Tree tree) {
        if (tree.isLeaf()) {
            return tree;
        } else {
            return getFirstLeaf(tree.firstChild());
        }
    }

    public static Tree getLastLeaf(Tree tree) {
        if (tree.isLeaf()) {
            return tree;
        } else {
            return getLastLeaf(tree.lastChild());
        }
    }

    public static List<Tree> getLeavesInBetween(Tree anchorTree, Tree leftNode, Tree rightNode, boolean includeLeft, boolean includeRight) {
        List<Tree> res = new ArrayList<>();

        if (leftNode == null) {
            leftNode = getFirstLeaf(anchorTree);
        }
        if (rightNode == null) {
            rightNode = getLastLeaf(anchorTree);
        }

        int startLeafNumber = (includeLeft) ? getFirstLeaf(leftNode).nodeNumber(anchorTree) : getLastLeaf(leftNode).nodeNumber(anchorTree) + 1;
        int endLeafNumber = (includeRight) ? getLastLeaf(rightNode).nodeNumber(anchorTree) : getFirstLeaf(rightNode).nodeNumber(anchorTree) - 1;
        if ((startLeafNumber < 0) || (endLeafNumber < 0)) {
            return res;
        }

        for (int i = startLeafNumber; i <= endLeafNumber; ++i) {
            Tree node = anchorTree.getNodeNumber(i);
            if (node.isLeaf()) {
                res.addAll(node);
            }
        }

        return res;
    }

    public static List<Tree> getPrecedingLeaves(Tree anchorTree, Tree node, boolean include) {
        return getLeavesInBetween(anchorTree, getFirstLeaf(anchorTree), node, true, include);
    }

    public static List<Tree> getFollowingLeaves(Tree anchorTree, Tree node, boolean include) {
        return getLeavesInBetween(anchorTree, node, getLastLeaf(anchorTree), include, true);
    }

    public static List<Tree> getContainingLeaves(Tree node) {
        return getLeavesInBetween(node, getFirstLeaf(node), getLastLeaf(node), true, true);
    }

    public static List<Word> getWordsInBetween(Tree anchorTree, Tree leftNode, Tree rightNode, boolean includeLeft, boolean includeRight) {
        return leavesToWords(getLeavesInBetween(anchorTree, leftNode, rightNode, includeLeft, includeRight));
    }

    public static List<Word> getPrecedingWords(Tree anchorTree, Tree node, boolean include) {
        return leavesToWords(getPrecedingLeaves(anchorTree, node, include));
    }

    public static List<Word> getFollowingWords(Tree anchorTree, Tree node, boolean include) {
        return leavesToWords(getFollowingLeaves(anchorTree, node, include));
    }

    public static List<Word> getContainingWords(Tree node) {
        return leavesToWords(getContainingLeaves(node));
    }

    public static Optional<Tree> findSpanningTree(Tree anchorTree, Tree firstLeaf, Tree lastLeaf) {
        return findSpanningTreeRec(anchorTree, anchorTree, firstLeaf, lastLeaf);
    }

    private static Optional<Tree> findSpanningTreeRec(Tree anchorTree, Tree currTree, Tree firstLeaf, Tree lastLeaf) {
        int firstNumber = firstLeaf.nodeNumber(anchorTree);
        int lastNumber = lastLeaf.nodeNumber(anchorTree);
        int currFirstNumber = getFirstLeaf(currTree).nodeNumber(anchorTree);
        int currLastNumber = getLastLeaf(currTree).nodeNumber(anchorTree);
        if (((currFirstNumber <= firstNumber) && (firstNumber <= currLastNumber)) && ((currFirstNumber <= lastNumber) && (lastNumber <= currLastNumber))) {
            if ((currFirstNumber == firstNumber) && (lastNumber == currLastNumber)) {
                return Optional.of(currTree);
            } else {
                // recursion
                for (Tree child : currTree.getChildrenAsList()) {
                    Optional<Tree> cr = findSpanningTreeRec(anchorTree, child, firstLeaf, lastLeaf);
                    if (cr.isPresent()) {
                        return Optional.of(cr.get());
                    }
                }
            }
        }

        return Optional.empty();
    }
}
