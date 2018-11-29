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

    UNKNOWN,

    // Coordinations
    UNKNOWN_COORDINATION, // the default for coordination
    CONTRAST,
    CAUSE_C,
    RESULT_C,
    LIST,
    DISJUNCTION,
    TEMPORAL_AFTER_C,
    TEMPORAL_BEFORE_C,

    // Subordinations
    UNKNOWN_SUBORDINATION, // the default for subordination
    ATTRIBUTION,
    BACKGROUND,
    CAUSE,
    RESULT,
    CONDITION,
    ELABORATION,
    PURPOSE,
    TEMPORAL_AFTER,
    TEMPORAL_BEFORE,

    // for sentence simplification
    NOUN_BASED,
    SPATIAL,
    TEMPORAL,
    TEMPORAL_TIME, // indicating a particular instance on a time scale (e.g. “Next Sunday 2 pm”).
    TEMPORAL_DURATION, // the amount of time between the two end-points of a time interval (e.g. “2 weeks").
    TEMPORAL_DATE, // particular date (e.g. “On 7 April 2013”).
    TEMPORAL_SET, IDENTIFYING_DEFINITION, DESCRIBING_DEFINITION; // periodic temporal sets representing times that occur with some frequency (“Every Tuesday”).

    static {
        UNKNOWN_COORDINATION.coordination = true;
        CONTRAST.coordination = true;
        CAUSE_C.coordination = true;
        RESULT_C.coordination = true;
        LIST.coordination = true;
        DISJUNCTION.coordination = true;
        TEMPORAL_AFTER_C.coordination = true;
        TEMPORAL_BEFORE_C.coordination = true;

        CAUSE.coordinateVersion = CAUSE_C;
        RESULT.coordinateVersion = RESULT_C;
        TEMPORAL_AFTER.coordinateVersion = TEMPORAL_AFTER_C;
        TEMPORAL_BEFORE.coordinateVersion = TEMPORAL_BEFORE_C;

        CAUSE_C.subordinateVersion = CAUSE;
        RESULT_C.subordinateVersion = RESULT;
        TEMPORAL_AFTER_C.subordinateVersion = TEMPORAL_AFTER;
        TEMPORAL_BEFORE_C.subordinateVersion = TEMPORAL_BEFORE;

        CAUSE_C.inverse = RESULT_C;
        RESULT_C.inverse = CAUSE_C;
        TEMPORAL_AFTER_C.inverse = TEMPORAL_BEFORE_C;
        TEMPORAL_BEFORE_C.inverse = TEMPORAL_AFTER_C;
        CAUSE.inverse = RESULT;
        RESULT.inverse = CAUSE;
        TEMPORAL_AFTER.inverse = TEMPORAL_BEFORE;
        TEMPORAL_BEFORE.inverse = TEMPORAL_AFTER;
    }

    private boolean coordination;
    private Relation regular; // class of context span (in subordination) or right span (coordination)
    private Relation inverse; // class of core span (in subordination) or left span (coordination)
    private Relation coordinateVersion; // optional
    private Relation subordinateVersion; // optional

    Relation() {
        this.coordination = false;
        this.regular = this;
        this.inverse = this; // only used in coordinations
        this.coordinateVersion = null;
        this.subordinateVersion = null;
    }

    public boolean isCoordination() {
        return coordination;
    }

    public Relation getRegulatRelation() {
        return regular;
    }

    public Relation getInverseRelation() {
        return inverse;
    }

    public Optional<Relation> getCoordinateVersion() {
        return Optional.ofNullable(coordinateVersion);
    }

    public Optional<Relation> getSubordinateVersion() {
        return Optional.ofNullable(subordinateVersion);
    }
}
