/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Relation
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

package org.lambda3.text.simplification.discourse.tree;

import java.util.Optional;

public enum Relation {

    // default relations
    UNKNOWN_COORDINATION, // the default for coordination
    UNKNOWN_SUBORDINATION, // the default for subordination

    // rst inspired
    BACKGROUND,
    CAUSE,
    CONDITION,
    CONTRAST,
    ELABORATION,
    ENABLEMENT,
    EXPLANATION,
    JOINT_LIST,
    JOINT_DISJUNCTION,
    TEMPORAL_BEFORE,
    TEMPORAL_AFTER,
    TEMPORAL_SEQUENCE,

    // special relations
    INTRA_SENT_ATTR,
    JOINT_NP_LIST,
    JOINT_NP_DISJUNCTION;

    static {
        TEMPORAL_AFTER.reverseRelation = TEMPORAL_BEFORE;
        TEMPORAL_BEFORE.reverseRelation = TEMPORAL_AFTER;
    }

    private Relation reverseRelation;

    Relation() {
        /*
         * by default, each relation is bidirectional with an equal reverse relation.
         * To make a relation unidirectional, set reverseRelation to null.
         */
        this.reverseRelation = this;
    }


    public Optional<Relation> getReverseRelation() {
        return Optional.ofNullable(reverseRelation);
    }
}
