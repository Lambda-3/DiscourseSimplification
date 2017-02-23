/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ParseTreeVisualizer
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

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ParseTreeVisualizer {

    public static String prettyPrint(Tree parseTree) {
        MyNode node = new MyNode(parseTree, parseTree);
        return PrettyTreePrinter.prettyPrint(node, false);
    }

    private static class MyNode implements PrettyTreePrinter.Node {
        private final List<PrettyTreePrinter.Node> children;
        private final String caption;
        private final int nr;

        public MyNode(Tree parseNode, Tree anchor) {
            this.caption = parseNode.value();
            this.children = new ArrayList<>();
            for (Tree childNode : parseNode.getChildrenAsList()) {
                this.children.add(new MyNode(childNode, anchor));
            }
            this.nr = parseNode.nodeNumber(anchor);
        }

        @Override
        public List<String> getPTPCaption() {
            return Arrays.asList(caption, "#" + nr);
        }

        @Override
        public List<PrettyTreePrinter.Edge> getPTPEdges() {
            return children.stream().map(c -> new PrettyTreePrinter.DefaultEdge("", c, true)).collect(Collectors.toList());
        }

    }
}
