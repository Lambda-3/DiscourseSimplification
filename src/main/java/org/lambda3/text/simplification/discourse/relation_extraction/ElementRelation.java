/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseExtractor
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

package org.lambda3.text.simplification.discourse.relation_extraction;

import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

/**
 *
 */
public class ElementRelation implements PrettyTreePrinter.Edge {
    private final Element target;
    private final Relation relation;
    private final boolean commutative;

    public ElementRelation(Element target, Relation relation, boolean commutative) {
        this.target = target;
        this.relation = relation;
        this.commutative = commutative;
    }

    public Element getTarget() {
        return target;
    }

    public Relation getRelation() {
        return relation;
    }

    public boolean isCommutative() {
        return commutative;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof ElementRelation)
                && (((ElementRelation) o).target.equals(target))
                && (((ElementRelation) o).relation.equals(relation)));
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getPTPCaption() {
        return relation.toString();
    }

    @Override
    public PrettyTreePrinter.Node getPTPChild() {
        return target;
    }

    @Override
    public boolean followPTPChild() {
        return (!commutative);
    }
}
