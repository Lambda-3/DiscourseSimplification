/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Leaf
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

package org.lambda3.text.simplification.discourse.processing;

import org.lambda3.text.simplification.discourse.relation_extraction.Element;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class OutSentence {
    private final int sentenceIdx;
    private final String originalSentence;
    private final List<Element> elements;

    public OutSentence(int sentenceIdx, String originalSentence) {
        this.sentenceIdx = sentenceIdx;
        this.originalSentence = originalSentence;
        this.elements = new ArrayList<>();
    }

    public void addElement(Element element) {
        if (!elements.contains(element)) {
            elements.add(element);
        }
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    public List<Element> getElements() {
        return elements;
    }

    public List<Element> getRootElements() {
        return elements.stream().filter(e -> e.getContextLayer() == 0).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Sentence: '" + originalSentence + "'\n\n" + getRootElements().stream().map(e -> e.toString()).collect(Collectors.joining("\n"));
    }
}
