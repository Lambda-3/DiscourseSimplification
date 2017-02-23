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

import org.lambda3.text.simplification.discourse.relation_extraction.element.DiscourseContext;
import org.lambda3.text.simplification.discourse.relation_extraction.element.DiscourseCore;
import org.lambda3.text.simplification.discourse.relation_extraction.relation.DiscourseCoreContextRelation;
import org.lambda3.text.simplification.discourse.relation_extraction.relation.DiscourseCoreCoreRelation;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.model.Coordination;
import org.lambda3.text.simplification.discourse.tree.model.DiscourseTree;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.tree.model.Subordination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Matthias on 08.12.16.
 */
public class DiscourseExtractor {
    private static final List<Relation> IGNORED_RELATIONS = Arrays.asList(
            Relation.UNKNOWN_COORDINATION
    );
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private LinkedHashMap<Leaf, DiscourseCore> processedCores;
    private LinkedHashMap<Leaf, DiscourseContext> processedContexts;

    public DiscourseExtractor() {
        this.processedCores = new LinkedHashMap<Leaf, DiscourseCore>();
        this.processedContexts = new LinkedHashMap<Leaf, DiscourseContext>();
    }

    public List<DiscourseCore> extract(DiscourseTree discourseTree) {
        this.processedCores = new LinkedHashMap<Leaf, DiscourseCore>();
        this.processedContexts = new LinkedHashMap<Leaf, DiscourseContext>();

        extractRec(discourseTree);

        return processedCores.values().stream().collect(Collectors.toList());
    }

    // should be called on a superordinate node
    private List<DiscourseCore> getCores(DiscourseTree node) {
        List<DiscourseCore> res = new ArrayList<DiscourseCore>();

        for (Leaf leaf : node.getNucleusPathLeaves()) {
            DiscourseCore core;
            if (processedCores.containsKey(leaf)) {
                core = processedCores.get(leaf);
            } else {
                core = new DiscourseCore(leaf.getText(), leaf.getSentenceIdx());
                processedCores.put(leaf, core);
            }
            res.add(core);
        }

        return res;
    }

    // should be called on a subordinate node
    private List<DiscourseContext> getContexts(DiscourseTree node) {
        List<DiscourseContext> res = new ArrayList<DiscourseContext>();

        for (Leaf leaf : node.getNucleusPathLeaves()) {
            DiscourseContext context;
            if (processedContexts.containsKey(leaf)) {
                context = processedContexts.get(leaf);
            } else {
                context = new DiscourseContext(leaf.getText(), leaf.getSentenceIdx());
                if (leaf.getType().equals(Leaf.Type.SENT_SIM_CONTEXT)) {
                    context.setSentSimContext();
                }
                processedContexts.put(leaf, context);

            }
            res.add(context);
        }

        return res;
    }

    // only visit nucleus nodes, do not handle References
    private void extractRec(DiscourseTree node) {

        if (node instanceof Leaf) {
            getCores(node);
        }

        if (node instanceof Coordination) {
            Coordination coordination = (Coordination) node;

            // recursion
            for (DiscourseTree child : coordination.getCoordinations()) {
                extractRec(child);
            }

            // add core relations
            if (!IGNORED_RELATIONS.contains(coordination.getRelation())) {
                for (DiscourseTree child : coordination.getCoordinations()) {
                    List<DiscourseCore> childCores = getCores(child);

                    // forward direction
                    for (DiscourseTree sibling : coordination.getOtherFollowingCoordinations(child)) {
                        List<DiscourseCore> siblingCores = getCores(sibling);

                        for (DiscourseCore childCore : childCores) {
                            for (DiscourseCore siblingCore : siblingCores) {
                                childCore.addCoreRelation(new DiscourseCoreCoreRelation(coordination.getRelation(), siblingCore));
                            }
                        }
                    }

                    // reverse direction
                    if (coordination.getRelation().getReverseRelation().isPresent()) {
                        for (DiscourseTree sibling : coordination.getOtherPrecedingCoordinations(child)) {
                            List<DiscourseCore> siblingCores = getCores(sibling);

                            for (DiscourseCore childCore : childCores) {
                                for (DiscourseCore siblingCore : siblingCores) {
                                    childCore.addCoreRelation(new DiscourseCoreCoreRelation(coordination.getRelation().getReverseRelation().get(), siblingCore));
                                }
                            }
                        }
                    }

                }
            }
        }

        if (node instanceof Subordination) {
            Subordination subordination = (Subordination) node;

            // recursion
            extractRec(subordination.getSuperordination());

            // add context relations
            if (!IGNORED_RELATIONS.contains(subordination.getRelation())) {
                List<DiscourseCore> cores = getCores(subordination.getSuperordination());
                List<DiscourseContext> contexts = getContexts(subordination.getSubordination());

                for (DiscourseCore core : cores) {
                    for (DiscourseContext context : contexts) {
                        core.addContextRelation(new DiscourseCoreContextRelation(subordination.getRelation(), context));
                    }
                }
            }
        }
    }
}
