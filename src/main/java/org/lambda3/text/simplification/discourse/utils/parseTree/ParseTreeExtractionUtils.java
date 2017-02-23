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
import java.util.List;

public class ParseTreeExtractionUtils {

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

    // returns True, if the model of node would not split/divide a NER group, else False
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


    private static Tree getFirstLeaf(Tree tree) {
        if (tree.isLeaf()) {
            return tree;
        } else {
            return getFirstLeaf(tree.firstChild());
        }
    }

    private static Tree getLastLeaf(Tree tree) {
        if (tree.isLeaf()) {
            return tree;
        } else {
            return getLastLeaf(tree.lastChild());
        }
    }

    public static List<Word> getWordsInBetween(Tree anchorTree, Tree leftNode, Tree rightNode, boolean includeLeft, boolean includeRight) {
        List<Word> res = new ArrayList<>();

        int startLeafNumber = (includeLeft) ? getFirstLeaf(leftNode).nodeNumber(anchorTree) : getLastLeaf(leftNode).nodeNumber(anchorTree) + 1;
        int endLeafNumber = (includeRight) ? getLastLeaf(rightNode).nodeNumber(anchorTree) : getFirstLeaf(rightNode).nodeNumber(anchorTree) - 1;
        if ((startLeafNumber < 0) || (endLeafNumber < 0)) {
            return res;
        }

        for (int i = startLeafNumber; i <= endLeafNumber; ++i) {
            Tree node = anchorTree.getNodeNumber(i);
            if (node.isLeaf()) {
                res.addAll(node.yieldWords());
            }
        }

        return res;
    }

    public static List<Word> getPrecedingWords(Tree anchorTree, Tree node, boolean include) {
        return getWordsInBetween(anchorTree, getFirstLeaf(anchorTree), node, true, include);
    }

    public static List<Word> getFollowingWords(Tree anchorTree, Tree node, boolean include) {
        return getWordsInBetween(anchorTree, node, getLastLeaf(anchorTree), include, true);
    }

    public static List<Word> getContainingWords(Tree node) {
        return node.yieldWords();
    }
}
