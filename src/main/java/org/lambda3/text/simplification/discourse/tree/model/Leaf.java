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

package org.lambda3.text.simplification.discourse.tree.model;

import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Leaf extends DiscourseTree {
    private final Type type;
    private final String text;

    public Leaf(Type type, String extractionRule, String text) {
        super(extractionRule);
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public List<String> getPTPCaption() {
        String typeStr = "";
        if (type.equals(Type.TERMINAL)) {
            typeStr = " [terminal]";
        } else if (type.equals(Type.SENT_SIM_CONTEXT)) {
            typeStr = " [s-context]";
        }

        return Collections.singletonList("'" + text + "'" + typeStr);
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }

    public enum Type {
        DEFAULT, // can be splitted
        TERMINAL, // will not be splitted
        SENT_SIM_CONTEXT // will not be splitted and will act like a sentence-simplification content in Step 3
    }

}
