/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Sentence
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

import java.util.*;

public class Sentence {
    public final int index;
    public final String original;
    private HashMap<String, Element> elementMap; // all extractions extracted from this sentence

    public Sentence() {
        // for deserialization
        this(-1, "");
    }

    public Sentence(int index, String original) {
        this.index = index;
        this.original = original;
        this.elementMap = new LinkedHashMap<>();
    }

    public Optional<String> containsElement(Element extraction) {
        for (Element e : elementMap.values()) {
            if (e.equals(extraction)) {
                return Optional.of(e.id);
            }
        }
        return Optional.empty();
    }

    public void addElement(Element element) {
        if (index != element.getSentenceIdx()) {
            throw new AssertionError("Element should not be added to this sentence");
        }
        elementMap.putIfAbsent(element.id, element);
    }

    public Element getElement(String id) {
        return elementMap.getOrDefault(id, null);
    }

    public List<Element> getElements() {
        return new ArrayList<>(elementMap.values());
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append("# ");
        strb.append(original);
        strb.append("\n");
        getElements().forEach(e -> {
            strb.append("\n");
            strb.append(e);
        });

        return strb.toString();
    }
}
