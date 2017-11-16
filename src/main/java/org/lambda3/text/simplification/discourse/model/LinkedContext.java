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

import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;

public class LinkedContext {
	private String targetID;
	private Relation relation;

	// for deserialization
	public LinkedContext() {
	}

	public LinkedContext(String targetID, Relation relation) {
		this.targetID = targetID;
		this.relation = relation;
	}

	public String getTargetID() {
		return targetID;
	}

	public Element getTargetElement(SimplificationContent content) {
		return content.getElement(targetID);
	}

	public Relation getRelation() {
		return relation;
	}

	@Override
	public boolean equals(Object o) {
		return ((o instanceof LinkedContext)
			&& (((LinkedContext) o).targetID.equals(targetID))
			&& (((LinkedContext) o).relation.equals(relation)));
	}
}
