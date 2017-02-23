/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DCore
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

import org.lambda3.text.simplification.discourse.sentence_simplification.relation.DContextRelation;
import org.lambda3.text.simplification.discourse.sentence_simplification.relation.DCoreRelation;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 */
public class DCore implements PrettyTreePrinter.Node {
    private final String text;
    private final int sentenceIndex;
    private final String notSimplifiedText;
    private final List<DCoreRelation> dCoreRelations;
    private final List<DContextRelation> dContextRelations;
    private final List<SContext> sContexts;

    public DCore(String text, int sentenceIndex, String notSimplifiedText) {
        this.text = text;
        this.sentenceIndex = sentenceIndex;
        this.notSimplifiedText = notSimplifiedText;
        this.dCoreRelations = new ArrayList<>();
        this.dContextRelations = new ArrayList<>();
        this.sContexts = new ArrayList<>();
    }

    public void addDCoreRelation(DCoreRelation dCoreRelation) {
        if (!dCoreRelations.contains(dCoreRelation)) {
            dCoreRelations.add(dCoreRelation);
        }
    }

    public void addDContextRelation(DContextRelation dContextRelation) {
        if (!dContextRelations.contains(dContextRelation)) {
            dContextRelations.add(dContextRelation);
        }
    }

    public void addSContext(SContext sContext) {
        this.sContexts.add(sContext);
    }

    public String getText() {
        return text;
    }

    public int getSentenceIndex() {
        return sentenceIndex;
    }

    public String getNotSimplifiedText() {
        return notSimplifiedText;
    }

    public List<DCoreRelation> getDCoreRelations() {
        return dCoreRelations;
    }

    public List<DContextRelation> getDContextRelations() {
        return dContextRelations;
    }

    public List<SContext> getSContexts() {
        return sContexts;
    }


    @Override
    public List<String> getPTPCaption() {
        return Collections.singletonList("'" + text + "'");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        List<PrettyTreePrinter.Edge> res = new ArrayList<>();

        res.addAll(dCoreRelations.stream().map(
                cr -> new PrettyTreePrinter.DefaultEdge("<d-core:" + cr.getRelation() + ">", cr.getDCore(), false)
        ).collect(Collectors.toList()));

        res.addAll(dContextRelations.stream().map(
                cr -> new PrettyTreePrinter.DefaultEdge("<d-context:" + cr.getRelation() + ">", cr.getDContext(), true)
        ).collect(Collectors.toList()));

        res.addAll(sContexts.stream().map(
                sc -> new PrettyTreePrinter.DefaultEdge("<s-context:" + sc.getRelation() + ">", sc, true)
        ).collect(Collectors.toList()));

        return res;
    }

    @Override
    public String toString() {
        return PrettyTreePrinter.prettyPrint(this, false, 40);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DCore)) return false;
        DCore dCore = (DCore) o;
        return getSentenceIndex() == dCore.getSentenceIndex() &&
                Objects.equals(getText(), dCore.getText()) &&
                Objects.equals(getNotSimplifiedText(), dCore.getNotSimplifiedText()) &&
                Objects.equals(toString(), dCore.toString());
    }

}
