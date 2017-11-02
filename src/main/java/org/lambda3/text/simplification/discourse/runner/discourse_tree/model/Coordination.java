/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Coordination
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

import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class Coordination extends DiscourseTree {
    final Relation relation;
    private final String cuePhrase; // optional
    private final List<DiscourseTree> coordinations;

    public Coordination(String extractionRule, Relation relation, String cuePhrase, List<DiscourseTree> coordinations) {
        super(extractionRule);
        this.relation = relation;
        this.cuePhrase = cuePhrase;
        this.coordinations = new ArrayList<>();
        coordinations.forEach(this::addCoordination);
    }

    public void addCoordination(DiscourseTree coordination) {
        this.coordinations.add(coordination);
        coordination.parent = this;
    }

    public void invalidateCoordination(DiscourseTree coordination) {
        replaceCoordination(coordination, new Invalidation());
    }

    public void replaceCoordination(DiscourseTree oldCoordination, DiscourseTree newCoordination) {
        for (int i = 0; i < coordinations.size(); i++) {
            if (coordinations.get(i).equals(oldCoordination)) {
                coordinations.set(i, newCoordination);
                newCoordination.parent = this;
                newCoordination.setRecursiveUnsetSentenceIdx(oldCoordination.getSentenceIdx());
                break;
            }
        }
    }

    public void removeInvalidations() {
        for (int i = coordinations.size() - 1; i >= 0; i--) {
            if (coordinations.get(i) instanceof Invalidation) {
                coordinations.remove(i);
            }
        }
    }

    public Relation getRelation() {
        return relation;
    }

    public List<DiscourseTree> getCoordinations() {
        return coordinations;
    }

    public List<DiscourseTree> getOtherCoordinations(DiscourseTree coordination) {
        return coordinations.stream().filter(c -> !c.equals(coordination)).collect(Collectors.toList());
    }

    public List<DiscourseTree> getOtherPrecedingCoordinations(DiscourseTree coordination) {
        List<DiscourseTree> res = new ArrayList<>();

        for (DiscourseTree child : coordinations) {
            if (child.equals(coordination)) {
                break;
            } else {
                res.add(child);
            }
        }

        return res;
    }

    public List<DiscourseTree> getOtherFollowingCoordinations(DiscourseTree coordination) {
        List<DiscourseTree> res = new ArrayList<>();

        boolean found = false;
        for (DiscourseTree child : coordinations) {
            if (child.equals(coordination)) {
                found = true;
            } else {
                if (found) {
                    res.add(child);
                }
            }
        }

        return res;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getPTPCaption() {
        String cuePhraseStr = (cuePhrase != null) ? "'" + cuePhrase + "'" : "NULL";
        return Collections.singletonList("CO/" + relation + " (" + cuePhraseStr + ", " + extractionRule + ")");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return coordinations.stream().map(c -> new PrettyTreePrinter.DefaultEdge("n", c, true)).collect(Collectors.toList());
    }

}
