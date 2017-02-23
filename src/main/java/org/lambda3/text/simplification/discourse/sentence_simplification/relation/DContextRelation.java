/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DContextRelation
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

package org.lambda3.text.simplification.discourse.sentence_simplification.relation;

import org.lambda3.text.simplification.discourse.sentence_simplification.element.DContext;
import org.lambda3.text.simplification.discourse.tree.Relation;

import java.util.Objects;

/**
 *
 */
public class DContextRelation {
    private final Relation relation;
    private final DContext dContext;

    public DContextRelation(Relation relation, DContext dContext) {
        this.relation = relation;
        this.dContext = dContext;
    }

    public Relation getRelation() {
        return relation;
    }

    public DContext getDContext() {
        return dContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DContextRelation)) return false;
        DContextRelation that = (DContextRelation) o;
        return getRelation() == that.getRelation() &&
                Objects.equals(dContext, that.dContext);
    }
}
