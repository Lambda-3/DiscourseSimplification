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

import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Leaf extends DiscourseTree {
    private String text;
    private boolean allowSplit; // true, if extraction-rules will be applied on the text
    private boolean toSimpleContext;

    public Leaf(String extractionRule, String text) {
        super(extractionRule);
        this.text = text;
        this.allowSplit = true;
        this.toSimpleContext = false;
    }

    public void dontAllowSplit() {
        this.allowSplit = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return Collections.singletonList("'" + text + "'");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }
}
