/*
 * ==========================License-Start=============================
 * DiscourseSimplification : LinkedContext
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

package org.lambda3.text.simplification.discourse.model;

import org.lambda3.text.simplification.discourse.runner.discourse_tree.RelationType;

import java.util.Objects;

public class LinkedContext {
	private String targetID;
	private RelationType relation;

	// for deserialization
	private LinkedContext() {
	}

	public LinkedContext(String targetID, RelationType relation) {
		this.targetID = targetID;
		this.relation = relation;
	}

	public String getTargetID() {
		return targetID;
	}

	public RelationType getRelation() {
		return relation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LinkedContext that = (LinkedContext) o;
		return Objects.equals(targetID, that.targetID) &&
				relation == that.relation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(targetID, relation);
	}
}
