/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Element
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

package org.lambda3.text.simplification.discourse.runner.model;

import org.lambda3.text.simplification.discourse.utils.IDGenerator;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Element {
    private String id;
    private String text;
    private int sentenceIdx;
    private int contextLayer;
    private List<SimpleContext> simpleContexts;
    private List<LinkedContext> linkedContexts;

    // for deserialization
    public Element() {
    }

    public Element(String text, int sentenceIdx, int contextLayer) {
        this.id = IDGenerator.generateUUID();
        this.text = text;
        this.sentenceIdx = sentenceIdx;
        this.contextLayer = contextLayer;
        this.simpleContexts = new ArrayList<>();
        this.linkedContexts = new ArrayList<>();
    }

    public void addLinkedContext(LinkedContext context) {
        if (!linkedContexts.contains(context)) {
            linkedContexts.add(context);
        }
    }

    public void addSimpleContext(SimpleContext context) {
        if (!simpleContexts.contains(context)) {
            simpleContexts.add(context);
        }
    }

    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public int getContextLayer() {
        return contextLayer;
    }

    public List<SimpleContext> getSimpleContexts() {
        return simpleContexts;
    }

    public List<LinkedContext> getLinkedContexts() {
        return linkedContexts;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append(id + "     " + contextLayer + "     " + text + "\n");
        getSimpleContexts().forEach(c -> strb.append("\tS:" + c.getRelation() + "    " + c.getText() + "\n"));
        getLinkedContexts().forEach(c -> strb.append("\tL:" + c.getRelation() + "    " + c.getTargetID() + "\n"));
        return strb.toString();
    }
}
