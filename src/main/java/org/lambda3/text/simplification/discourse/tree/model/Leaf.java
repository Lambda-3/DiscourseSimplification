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

package org.lambda3.text.simplification.discourse.tree.model;

import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class Leaf extends DiscourseTree {
    private final String text;
    private boolean properSentence; // specifies whether text is a proper sentence
    private String rephrasedText; // optional
    private boolean allowSplit; // true, if extraction-rules will be applied on the text

    public Leaf(String extractionRule, String text) {
        super(extractionRule);
        this.text = text;
        this.properSentence = true;
        this.rephrasedText = null;
        this.allowSplit = true;
    }

    public void setProperSentence(boolean properSentence) {
        this.properSentence = properSentence;
    }

    public void setRephrasedText(String rephrasedText) {
        this.rephrasedText = rephrasedText;
    }

    public void dontAllowSplit() {
        this.allowSplit = false;
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

    public Optional<String> getRephrasedText() {
        return Optional.ofNullable(rephrasedText);
    }

    public boolean isAllowSplit() {
        return allowSplit;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getPTPCaption() {
        String rephrasedStr = (rephrasedText != null)? " ('" + rephrasedText + "')" : "";

        return Collections.singletonList("'" + text + "'" + rephrasedStr + " [p:" + properSentence + "][s:" + allowSplit + "]");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }
}
