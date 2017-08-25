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

package org.lambda3.text.simplification.discourse.runner.discourse_tree;

import java.util.Optional;

public enum Relation {

    // default relations
    UNKNOWN,
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
    JOINT_CONJUNCTION,
    JOINT_DISJUNCTION,
    TEMPORAL_BEFORE,
    TEMPORAL_AFTER,
    TEMPORAL_SEQUENCE,

    // special relations
    ATTRIBUTION,
    JOINT_NP_CONJUNCTION,
    JOINT_NP_DISJUNCTION,

    // for sentence simplification
    NOUN_BASED,
    TEMPORAL,
    TEMPORAL_TIME, // indicating a particular instance on a time scale (e.g. “Next Sunday 2 pm”).
    TEMPORAL_DURATION, // the amount of time between the two end-points of a time interval (e.g. “2 weeks").
    TEMPORAL_DATE, // particular date (e.g. “On 7 April 2013”).
    TEMPORAL_SET, // periodic temporal sets representing times that occur with some frequency (“Every Tuesday”).
    SPATIAL;

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
