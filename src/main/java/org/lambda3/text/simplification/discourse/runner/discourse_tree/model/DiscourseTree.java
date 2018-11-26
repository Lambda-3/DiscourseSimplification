/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseTree
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
import java.util.List;
import java.util.Optional;

public abstract class DiscourseTree implements PrettyTreePrinter.Node {
    final String extractionRule;
    DiscourseTree parent; //optional
    private boolean processed;
    private int sentenceIdx;

    DiscourseTree(String extractionRule) {
        this.extractionRule = extractionRule;
        this.processed = false;
        this.parent = null; // should be set by inherited classes
        this.sentenceIdx = -1; // should be set by inherited classes
    }

    void setRecursiveUnsetSentenceIdx(int sentenceIdx) {
        if (this.sentenceIdx < 0) {
            this.sentenceIdx = sentenceIdx;

            // recursive
            if (this instanceof Coordination) {
                ((Coordination) this).getCoordinations().forEach(c -> c.setRecursiveUnsetSentenceIdx(sentenceIdx));
            }
            if (this instanceof Subordination) {
                ((Subordination) this).getLeftConstituent().setRecursiveUnsetSentenceIdx(sentenceIdx);
                ((Subordination) this).getRightConstituent().setRecursiveUnsetSentenceIdx(sentenceIdx);
            }
        }
    }

    public void cleanup() {
        if (this instanceof Coordination) {

            // remove invalidations
            ((Coordination) this).removeInvalidations();

            // recursion
            ((Coordination) this).getCoordinations().forEach(DiscourseTree::cleanup);
        }

        if (this instanceof Subordination) {

            // recursion
            ((Subordination) this).getLeftConstituent().cleanup();
            ((Subordination) this).getRightConstituent().cleanup();
        }
    }

    public boolean usableAsReference() {
        return ((parent != null) && (parent instanceof Coordination) && (((Coordination) parent).relation.equals(Relation.UNKNOWN_COORDINATION)));
    }

    public void useAsReference() {
        if (usableAsReference()) {
            ((Coordination) parent).invalidateCoordination(this);
        } else {
            throw new AssertionError("Not useable as reference");
        }
    }

    public List<Leaf> getCorePathLeaves() {
        List<Leaf> res = new ArrayList<>();

        if (this instanceof Leaf) {
            res.add((Leaf) this);
        } else {
            // recursion on coordinations
            if (this instanceof Coordination) {
                for (DiscourseTree child : ((Coordination) this).getCoordinations()) {
                    res.addAll(child.getCorePathLeaves());
                }
            }

            // recursion on superordinations
            if (this instanceof Subordination) {
                res.addAll(((Subordination) this).getSuperordination().getCorePathLeaves());
            }
        }

        return res;
    }

    public Optional<DiscourseTree> getPreviousNode() {
        if (parent != null) {
            if (parent instanceof Coordination) {
                Coordination p = (Coordination) parent;
                DiscourseTree prev = null;
                for (DiscourseTree child : p.getCoordinations()) {
                    if ((child.equals(this)) && (prev != null)) {
                        return Optional.of(prev);
                    }
                    prev = child;
                }
            }
            if (parent instanceof Subordination) {
                Subordination p = (Subordination) parent;
                if (p.getRightConstituent().equals(this)) {
                    return Optional.of(p.getLeftConstituent());
                }
            }

            // recursion
            return parent.getPreviousNode();
        }

        return Optional.empty();
    }

    public void setProcessed() {
        this.processed = true;
    }

    public boolean isNotProcessed() {
        return !processed;
    }

    public String getExtractionRule() {
        return extractionRule;
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return PrettyTreePrinter.prettyPrint(this, false);
    }
}
