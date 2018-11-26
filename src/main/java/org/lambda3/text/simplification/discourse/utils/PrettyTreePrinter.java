/*
 * ==========================License-Start=============================
 * DiscourseSimplification : PrettyTreePrinter
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

package org.lambda3.text.simplification.discourse.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PrettyTreePrinter {

    private static final String DOT_INDENT = "    ";

    private static final NodeShape DEFAULT_NODE_SHAPE = NodeShape.box;
    private static final List<NodeStyle> DEFAULT_NODE_STYLES = Collections.singletonList(NodeStyle.solid);
    private static final String DEFAULT_NODE_COLOR = "black";
    private static final String DEFAULT_NODE_FILLCOLOR = "white";

    private static final EdgeShape DEFAULT_EDGE_SHAPE = EdgeShape.box;
    private static final List<EdgeStyle> DEFAULT_EDGE_STYLES = Collections.singletonList(EdgeStyle.solid);
    private static final String DEFAULT_EDGE_COLOR = "black";

    // INTERFACES & CLASSES ////////////////////////////////////////////////////////////////////////////////////////////

    public interface Node {
        List<String> getPTPCaption();
        List<Edge> getPTPEdges();
    }

    public interface Edge {
        String getPTPCaption();
        Node getPTPChild();
        boolean followPTPChild();
    }

    public interface GNode extends Node {
        NodeShape getPTPNodeShape();
        List<NodeStyle> getPTPNodeStyles();
        String getPTPColor();
        String getPTPFillColor();
    }

    public interface GEdge extends Edge {
        EdgeShape getPTPEdgeShape();
        List<EdgeStyle> getPTPEdgeStyles();
        String getPTPColor();
    }

    public static class DefaultEdge implements Edge {
        private final String caption;
        private final Node child;
        private final boolean followPTPChild;

        public DefaultEdge(String caption, Node child, boolean followPTPChild) {
            this.caption = caption;
            this.child = child;
            this.followPTPChild = followPTPChild;
        }

        @Override
        public String getPTPCaption() {
            return caption;
        }

        @Override
        public Node getPTPChild() {
            return child;
        }

        @Override
        public boolean followPTPChild() {
            return followPTPChild;
        }

    }

    // GRAPHICAL ENUMS /////////////////////////////////////////////////////////////////////////////////////////////////

    public enum NodeShape {
        box,
        polygon,
        ellipse,
        circle,
        point,
        egg,
        triangle,
        plaintext,
        diamond,
        trapezium,
        parallelogram,
        house,
        pentagon,
        hexagon,
        septagon,
        octagon,
        doublecircle,
        doubleoctagon,
        tripleoctagon,
        invtriangle,
        invtrapezium,
        invhouse,
        Mdiamond,
        Msquare,
        Mcircle,
        rect,
        rectangle,
        square,
        none,
        note,
        tab,
        folder,
        box3d,
        component
    }

    public enum NodeStyle {
        dashed,
        dotted,
        solid,
        invis,
        bold,
        filled,
        diagonals,
        rounded
    }

    public enum EdgeShape {
        box,
        crow,
        diamond,
        dot,
        inv,
        none,
        normal,
        tee,
        vee
    }

    public enum EdgeStyle {
        dashed,
        dotted,
        solid,
        invis,
        bold
    }

    public enum EdgeDir {
        forward,
        back,
        both,
        none
    }

    // GENERAL FUNCTIONS ///////////////////////////////////////////////////////////////////////////////////////////////

    private static String trimText(String text, Integer maxTextLen) {
        final String SUFFIX = "...";

        if ((maxTextLen != null) && (text.length() > maxTextLen)) {
            if (maxTextLen < SUFFIX.length()) {
                throw new IllegalArgumentException("maxTextLen should have at least the length: " + SUFFIX.length());
            }

            return text.substring(0, maxTextLen - SUFFIX.length()) + SUFFIX;
        } else {
            return text;
        }
    }

    // TEXTUAL REPRESENTATION //////////////////////////////////////////////////////////////////////////////////////////

    private static int getBottomDepth(Node node, boolean follow) {
        if ((!follow) || (node.getPTPEdges().size() <= 0)) {
            return 0;
        } else {
            return node.getPTPEdges().stream().mapToInt(e -> getBottomDepth(e.getPTPChild(), e.followPTPChild())).max().getAsInt() + 1;
        }
    }

    private static String getEdgeIndent(int size, String edgeCaption, boolean lastChild) {
        String front = (lastChild)? "└─" : "├─";
        String back = "─> ";

        String middle = trimText(edgeCaption, size - front.length() - back.length());

        boolean right = true;
        while (front.length() + middle.length() + back.length() < size) {
            middle = (right)? middle + "─" : "─" + middle;
            right = !right;
        }

        return front + middle + back;
    }

    private static String getIndent(int size, boolean lastChild) {
        String res = (lastChild)? " " : "|";
        while (res.length() < size) {
            res = res + " ";
        }

        return res;
    }

    private static List<String> prettyPrintRec(Node node, boolean follow, boolean reversed, int size) {
        List<String> res = new ArrayList<String>();

        int bottomDepth = getBottomDepth(node, follow);

        // this node
        res.addAll(node.getPTPCaption());

        // edges
        if (follow) {

            ListIterator<Edge> iter = (reversed) ? node.getPTPEdges().listIterator(node.getPTPEdges().size()) : node.getPTPEdges().listIterator();
            while ((reversed) ? iter.hasPrevious() : iter.hasNext()) {
                Edge edge = (reversed) ? iter.previous() : iter.next();
                boolean endChild = ((reversed) ? !iter.hasPrevious() : !iter.hasNext());
                int indentSize = (bottomDepth - getBottomDepth(edge.getPTPChild(), edge.followPTPChild())) * size;

                boolean firstChildLine = true;
                for (String childLine : prettyPrintRec(edge.getPTPChild(), edge.followPTPChild(), reversed, size)) {
                    if (firstChildLine) {
                        res.add(getEdgeIndent(indentSize, edge.getPTPCaption(), endChild) + childLine);
                        firstChildLine = false;
                    } else {
                        res.add(getIndent(indentSize, endChild) + childLine);
                    }
                }
            }
        }

        return res;
    }

    public static String prettyPrint(Node node, boolean reversed, int size) {
        return prettyPrintRec(node, true, reversed, size).stream().collect(Collectors.joining("\n"));
    }

    public static String prettyPrint(Node node, boolean reversed) {
        return prettyPrint(node, reversed, 10);
    }

    public static String prettyPrint(Node node, int size) {
        return prettyPrint(node, false, size);
    }

    public static String prettyPrint(Node node) {
        return prettyPrintRec(node, true, false, 10).stream().collect(Collectors.joining("\n"));
    }

    // GRAPHICAL REPRESENTATION ////////////////////////////////////////////////////////////////////////////////////////

    private static long addDotLineRec(Node node, boolean follow, StringBuilder strb, HashMap<Node, Long> idMap) {

        long id;
        if (idMap.containsKey(node)) {
            id = idMap.get(node);
        } else {
            id = idMap.size();
            idMap.put(node, id);
        }

        NodeShape nodeShape = (node instanceof GNode)? ((GNode)node).getPTPNodeShape() : DEFAULT_NODE_SHAPE;
        List<NodeStyle> nodeStyles = (node instanceof GNode)? ((GNode)node).getPTPNodeStyles() : DEFAULT_NODE_STYLES;
        String nodeColor = (node instanceof GNode)? ((GNode)node).getPTPFillColor() : DEFAULT_NODE_COLOR;
        String nodeFillColor = (node instanceof GNode)? ((GNode)node).getPTPFillColor() : DEFAULT_NODE_FILLCOLOR;
        String nodeLabel = node.getPTPCaption().stream().collect(Collectors.joining("\n"));
        nodeLabel = nodeLabel.replaceAll("\\n", "\\\\n");

        // this node
        String nodeLine = String.format("\"%d\" [shape=\"%s\", style=\"%s\", color=\"%s\", fillcolor=\"%s\", label=\"%s\"];",
                id,
                nodeShape,
                nodeStyles.stream().map(s -> s.name()).collect(Collectors.joining(",")),
                nodeColor,
                nodeFillColor,
                nodeLabel
        );
        strb.append(DOT_INDENT + nodeLine + "\n");

        // edges
        if (follow) {

            for (Edge edge : node.getPTPEdges()) {

                // child (recursion)
                long childId = addDotLineRec(edge.getPTPChild(), edge.followPTPChild(), strb, idMap);

                EdgeShape edgeShape = (edge instanceof GEdge) ? ((GEdge) edge).getPTPEdgeShape() : DEFAULT_EDGE_SHAPE;
                List<EdgeStyle> edgeStyles = (edge instanceof GEdge) ? ((GEdge) edge).getPTPEdgeStyles() : DEFAULT_EDGE_STYLES;
                String edgeColor = (edge instanceof GEdge) ? ((GEdge) edge).getPTPColor() : DEFAULT_EDGE_COLOR;
                String edgeLabel = edge.getPTPCaption();
                edgeLabel = edgeLabel.replaceAll("\\n", "\\\\n");

                String edgeLine = String.format("\"%s\" -> \"%s\" [shape=\"%s\", style=\"%s\", color=\"%s\", label=\"%s\"];",
                        id,
                        childId,
                        edgeShape,
                        edgeStyles.stream().map(s -> s.name()).collect(Collectors.joining(",")),
                        edgeColor,
                        edgeLabel
                );
                strb.append(DOT_INDENT + edgeLine + "\n");
            }
        }

        return id;
    }

    public static String visualize(Node node, String graphName, String title) {
        StringBuilder strb = new StringBuilder();
        strb.append(String.format("digraph %s {", graphName) + "\n");

        if (title != null) {
            strb.append(DOT_INDENT + "labelloc=\"t\";" + "\n");
            strb.append(DOT_INDENT + String.format("label=\"%s\";", title) + "\n");
        }

        HashMap<Node, Long> idMap = new HashMap<>();
        addDotLineRec(node, true, strb, idMap);

        List<Long> leafIDs = idMap.keySet().stream().filter(n -> n.getPTPEdges().size() <= 0).map(n -> idMap.get(n)).collect(Collectors.toList());
        String sameRankLine = String.format("{rank = same; %s};", leafIDs.stream().map(i -> "\"" + i + "\"").collect(Collectors.joining("; ")));
        strb.append(DOT_INDENT + sameRankLine + "\n");

        strb.append("}");
        return strb.toString();
    }

    public static void visualizeToFile(Node node, String graphName, String title, String filepath) throws IOException {
        String str = visualize(node, graphName, title);

        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(filepath));
        writer.write(str);
        writer.close();
    }
}