/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ClassificationResult
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

package org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification;

import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.model.TimeInformation;

import java.util.Optional;

/**
 *
 */
public class ClassificationResult {
    private Relation relation;
    private TimeInformation timeInformation; // optional

    public ClassificationResult(Relation relation) {
        this.relation = relation;
        this.timeInformation = null;
    }

    public void setTimeInformation(TimeInformation timeInformation) {
        this.timeInformation = timeInformation;
    }

    public Relation getRelation() {
        return relation;
    }

    public Optional<TimeInformation> getTimeInformation() {
        return Optional.ofNullable(timeInformation);
    }
}
