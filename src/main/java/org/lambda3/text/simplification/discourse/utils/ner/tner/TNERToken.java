/*
 * ==========================License-Start=============================
 * DiscourseSimplification : TNERToken
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

package org.lambda3.text.simplification.discourse.utils.ner.tner;

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.ner.NERToken;

/**
 *
 */
public class TNERToken extends NERToken {

    private final Tree leafNode;
    private TNERString nerString;
    private Tree posNode;

    public TNERToken(int index, String token, String category, Tree leafNode) {
        super(index, token, category);
        this.nerString = null;
        this.leafNode = leafNode;
        this.posNode = null; // wait until nerString is set
    }

    public void setNerString(TNERString nerString) {
        this.nerString = nerString;
        this.posNode = leafNode.parent(getParseTree());
    }

    private Tree getParseTree() {
        return nerString.getParseTree();
    }

    public Tree getLeafNode() {
        return leafNode;
    }

    public Tree getPosNode() {
        return posNode;
    }

    private String getPOSTag() {
        return posNode.value();
    }

    @Override
    public String toString() {
        return "(" + index + ": " + category + ", '" + text + "', " + getPOSTag() + ")";
    }
}
