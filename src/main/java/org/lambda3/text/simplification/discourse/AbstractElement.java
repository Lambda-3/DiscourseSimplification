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

package org.lambda3.text.simplification.discourse;

import org.lambda3.text.simplification.discourse.model.LinkedContext;
import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.utils.IDGenerator;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElement {

    public final String id = IDGenerator.generateUUID();
    protected int sentenceIdx;
    protected int contextLayer;
    protected List<SimpleContext> simpleContexts;
    protected List<LinkedContext> linkedContexts;

    // for deserialization
    public AbstractElement() {
    }

    public AbstractElement(int sentenceIdx, int contextLayer) {
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
}
