/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SContext
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

package org.lambda3.text.simplification.discourse.sentence_simplification.element;

import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class SContext implements PrettyTreePrinter.Node {
    private final String text;
    private final int sentenceIndex;
    private final Relation relation;

    public SContext(String text, int sentenceIndex, Relation relation) {
        this.text = text;
        this.sentenceIndex = sentenceIndex;
        this.relation = relation;
    }

    public String getText() {
        return text;
    }

    public int getSentenceIndex() {
        return sentenceIndex;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    public List<String> getPTPCaption() {
        return Collections.singletonList("'" + text + "'");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SContext)) return false;
        SContext sContext = (SContext) o;
        return getSentenceIndex() == sContext.getSentenceIndex() &&
                Objects.equals(getText(), sContext.getText()) &&
                getRelation() == sContext.getRelation();
    }
}
