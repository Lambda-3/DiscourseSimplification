/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Subordination
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

/**
 *
 */
public class Subordination extends DiscourseTree {
    private final Relation relation;
    private final String cuePhrase; // optional
    private final boolean contextRight;
    private DiscourseTree leftConstituent;
    private DiscourseTree rightConstituent;

    public Subordination(String extractionRule, Relation relation, String cuePhrase, DiscourseTree leftConstituent, DiscourseTree rightConstituent, boolean contextRight) {
        super(extractionRule);
        this.relation = relation;
        this.cuePhrase = cuePhrase;
        this.contextRight = contextRight;

        this.leftConstituent = new Leaf(); //tmp
        this.rightConstituent = new Leaf(); //tmp
        replaceLeftConstituent(leftConstituent);
        replaceRightConstituent(rightConstituent);
    }

    public void replaceLeftConstituent(DiscourseTree newLeftConstituent) {
        DiscourseTree oldLeftConstituent = this.leftConstituent;
        this.leftConstituent = newLeftConstituent;
        newLeftConstituent.parent = this;
        newLeftConstituent.setRecursiveUnsetSentenceIdx(oldLeftConstituent.getSentenceIdx());
    }

    private void replaceRightConstituent(DiscourseTree newRightConstituent) {
        DiscourseTree oldRightConstituent = this.rightConstituent;
        this.rightConstituent = newRightConstituent;
        newRightConstituent.parent = this;
        newRightConstituent.setRecursiveUnsetSentenceIdx(oldRightConstituent.getSentenceIdx());
    }

    public void replaceSuperordination(DiscourseTree newSuperordination) {
        if (contextRight) {
            replaceLeftConstituent(newSuperordination);
        } else {
            replaceRightConstituent(newSuperordination);
        }
    }

    public void replaceSubordination(DiscourseTree newSubordination) {
        if (contextRight) {
            replaceRightConstituent(newSubordination);
        } else {
            replaceLeftConstituent(newSubordination);
        }
    }

    public Relation getRelation() {
        return relation;
    }

    public DiscourseTree getLeftConstituent() {
        return leftConstituent;
    }

    public DiscourseTree getRightConstituent() {
        return rightConstituent;
    }

    public DiscourseTree getSuperordination() {
        return (contextRight) ? leftConstituent : rightConstituent;
    }

    public DiscourseTree getSubordination() {
        return (contextRight) ? rightConstituent : leftConstituent;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getPTPCaption() {
        String cuePhraseStr = (cuePhrase != null) ? "'" + cuePhrase + "'" : "NULL";
        return Collections.singletonList("SUB/" + relation + " (" + cuePhraseStr + ", " + extractionRule + ")");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        List<PrettyTreePrinter.Edge> res = new ArrayList<>();
        res.add(new PrettyTreePrinter.DefaultEdge((contextRight) ? "n" : "s", leftConstituent, true));
        res.add(new PrettyTreePrinter.DefaultEdge((contextRight) ? "s" : "n", rightConstituent, true));

        return res;
    }

}
