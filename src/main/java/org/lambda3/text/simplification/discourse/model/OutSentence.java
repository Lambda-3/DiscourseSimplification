/*
 * ==========================License-Start=============================
 * DiscourseSimplification : OutSentence
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

import org.lambda3.text.simplification.discourse.AbstractElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class OutSentence<E extends AbstractElement> {
    private int sentenceIdx;
    private String originalSentence;
    private HashMap<String, E> elementMap; // all extractions extracted from this sentence

    // for deserialization
    public OutSentence() {
    }

    public OutSentence(int sentenceIdx, String originalSentence) {
        this.sentenceIdx = sentenceIdx;
        this.originalSentence = originalSentence;
        this.elementMap = new LinkedHashMap<>();
    }

    public void addElement(E element) {
        if (sentenceIdx != element.getSentenceIdx()) {
            throw new AssertionError("Element should not be added to this sentence");
        }
        elementMap.putIfAbsent(element.id, element);
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    public E getElement(String id) {
        return elementMap.getOrDefault(id, null);
    }

    public List<E> getElements() {
        return new ArrayList<>(elementMap.values());
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append("# ");
        strb.append(originalSentence);
        strb.append("\n");
        getElements().forEach(e -> {
            strb.append("\n");
            strb.append(e);
        });

        return strb.toString();
    }
}
