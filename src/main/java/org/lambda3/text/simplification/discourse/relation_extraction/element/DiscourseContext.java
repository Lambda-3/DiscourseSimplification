/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseContext
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

package org.lambda3.text.simplification.discourse.relation_extraction.element;

import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class DiscourseContext implements PrettyTreePrinter.Node {
    private final String text;
    private final int sentenceIdx;
    private boolean sentSimContext;

    public DiscourseContext(String text, int sentenceIdx) {
        this.text = text;
        this.sentenceIdx = sentenceIdx;
        this.sentSimContext = false;
    }

    public void setSentSimContext() {
        this.sentSimContext = true;
    }

    public String getText() {
        return text;
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public boolean isSentSimContext() {
        return sentSimContext;
    }

    @Override
    public List<String> getPTPCaption() {
        String sentSimContextStr = (sentSimContext) ? " [s-context]" : "";
        return Collections.singletonList("'" + text + "'" + sentSimContextStr);
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }
}
