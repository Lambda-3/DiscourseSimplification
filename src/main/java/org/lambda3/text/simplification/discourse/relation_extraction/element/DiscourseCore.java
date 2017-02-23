/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseCore
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

import org.lambda3.text.simplification.discourse.relation_extraction.relation.DiscourseCoreContextRelation;
import org.lambda3.text.simplification.discourse.relation_extraction.relation.DiscourseCoreCoreRelation;
import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DiscourseCore implements PrettyTreePrinter.Node {
    private final String text;
    private final int sentenceIdx;
    private final List<DiscourseCoreCoreRelation> coreRelations;
    private final List<DiscourseCoreContextRelation> contextRelations;

    public DiscourseCore(String text, int sentenceIdx) {
        this.text = text;
        this.sentenceIdx = sentenceIdx;
        this.coreRelations = new ArrayList<>();
        this.contextRelations = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public int getSentenceIdx() {
        return sentenceIdx;
    }

    public void addCoreRelation(DiscourseCoreCoreRelation coreRelation) {
        if (!coreRelations.contains(coreRelation)) {
            coreRelations.add(coreRelation);
        }
    }

    public List<DiscourseCoreCoreRelation> getCoreRelations() {
        return coreRelations;
    }

    public void addContextRelation(DiscourseCoreContextRelation contextRelation) {
        if (!contextRelations.contains(contextRelation)) {
            contextRelations.add(contextRelation);
        }
    }

    public List<DiscourseCoreContextRelation> getContextRelations() {
        return contextRelations;
    }

    @Override
    public List<String> getPTPCaption() {
        return Collections.singletonList("'" + text + "'");
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        List<PrettyTreePrinter.Edge> res = new ArrayList<>();

        res.addAll(coreRelations.stream().map(
                cr -> new PrettyTreePrinter.DefaultEdge("<d-core:" + cr.getRelation() + ">", cr.getCore(), false)
        ).collect(Collectors.toList()));

        res.addAll(contextRelations.stream().map(
                cr -> new PrettyTreePrinter.DefaultEdge("<d-context:" + cr.getRelation() + ">", cr.getContext(), true)
        ).collect(Collectors.toList()));

        return res;
    }

    @Override
    public String toString() {
        return PrettyTreePrinter.prettyPrint(this, false, 40);
    }
}
