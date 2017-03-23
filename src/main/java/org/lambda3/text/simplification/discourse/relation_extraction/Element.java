/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseExtractor
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

package org.lambda3.text.simplification.discourse.relation_extraction;

import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class Element implements PrettyTreePrinter.Node {
    private final String text;
    private final boolean properSentence; // specifies whether text is a proper sentence
    private final String rephrasedText; // optional
    private final int sentenceIdx;
    private final int contextLayer;
    private final List<ElementRelation> relations;

    public Element(String text, boolean properSentence, String rephrasedText, int sentenceIdx, int contextLayer) {
        this.text = text;
        this.properSentence = properSentence;
        this.rephrasedText = rephrasedText;
        this.sentenceIdx = sentenceIdx;
        this.contextLayer = contextLayer;
        this.relations = new ArrayList<>();
    }

    public void addRelation(ElementRelation relation) {
        if (!relations.contains(relation)) {
            relations.add(relation);
        }
    }

    public String getText() {
        return text;
    }

    public boolean isProperSentence() {
        return properSentence;
    }

    public String preferRephrasedText() {
        if (rephrasedText != null) {
            return rephrasedText;
        } else {
            return text;
        }
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public int getContextLayer() {
        return contextLayer;
    }

    public List<ElementRelation> getRelations() {
        return relations;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return PrettyTreePrinter.prettyPrint(this, 25);
    }

    @Override
    public List<String> getPTPCaption() {
        String rephrasedStr = (rephrasedText != null)? " ('" + rephrasedText + "')" : "";

        return Collections.singletonList("'" + text + "'" + rephrasedStr + " [p:" + properSentence + "]");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return relations.stream().collect(Collectors.toList());
    }
}
