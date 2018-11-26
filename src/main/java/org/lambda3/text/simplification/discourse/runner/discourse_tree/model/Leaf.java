/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Leaf
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.model;

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Leaf extends DiscourseTree {
    private Tree parseTree;
    private boolean allowSplit; // true, if extraction-rules will be applied on the text
    private boolean toSimpleContext;

    public Leaf() {
        super("UNKNOWN");
    }

    public Leaf(String extractionRule, Tree parseTree) {
        super(extractionRule);
        this.parseTree = parseTree;
        this.allowSplit = true;
        this.toSimpleContext = false;
    }

    // not efficient -> prefer to use constructor with tree
    public Leaf(String extractionRule, String text) throws ParseTreeException {
        this(extractionRule, ParseTreeParser.parse(text));
    }

    public void dontAllowSplit() {
        this.allowSplit = false;
    }

    public Tree getParseTree() {
        return parseTree;
    }

    public void setParseTree(Tree parseTree) {
        this.parseTree = parseTree;
    }

    public String getText() {
        return WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree));
    }

    public void setToSimpleContext(boolean toSimpleContext) {
        this.toSimpleContext = toSimpleContext;
    }

    public boolean isAllowSplit() {
        return allowSplit;
    }

    public boolean isToSimpleContext() {
        return toSimpleContext;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getPTPCaption() {
        return Collections.singletonList("'" + getText() + "'");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }
}
