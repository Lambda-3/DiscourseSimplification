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

import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.model.Coordination;
import org.lambda3.text.simplification.discourse.tree.model.DiscourseTree;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.tree.model.Subordination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DiscourseExtractor {
    private static final List<Relation> IGNORED_RELATIONS = Arrays.asList(
            Relation.UNKNOWN_COORDINATION,
            Relation.JOINT_LIST,
            Relation.JOINT_DISJUNCTION,
            Relation.JOINT_NP_LIST,
            Relation.JOINT_NP_DISJUNCTION

    );
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private LinkedHashMap<Leaf, Element> processedLeaves;

    public DiscourseExtractor() {
        this.processedLeaves = new LinkedHashMap<Leaf, Element>();
    }

    public List<Element> extract(DiscourseTree discourseTree) {
        this.processedLeaves = new LinkedHashMap<Leaf, Element>();

        extractRec(discourseTree, 0);

        return processedLeaves.values().stream().collect(Collectors.toList());
    }

    private void extractRec(DiscourseTree node, int contextLayer) {

        if (node instanceof Leaf) {
            Leaf leaf = (Leaf)node;

            // create new element
            Element element = new Element(
                    leaf.getText(),
                    leaf.isProperSentence(),
                    leaf.getRephrasedText().orElse(null),
                    leaf.getSentenceIdx(),
                    contextLayer
            );

            processedLeaves.put(leaf, element);
        }

        if (node instanceof Coordination) {
            Coordination coordination = (Coordination) node;

            // recursion
            for (DiscourseTree child : coordination.getCoordinations()) {
                extractRec(child, contextLayer);
            }

            // set relations
            if (!IGNORED_RELATIONS.contains(coordination.getRelation())) {
                for (DiscourseTree child : coordination.getCoordinations()) {
                    List<Element> childNElements = child.getNucleusPathLeaves().stream().map(n -> processedLeaves.get(n)).collect(Collectors.toList());

                    // forward direction
                    for (DiscourseTree sibling : coordination.getOtherFollowingCoordinations(child)) {
                        List<Element> siblingNElements = sibling.getNucleusPathLeaves().stream().map(n -> processedLeaves.get(n)).collect(Collectors.toList());

                        for (Element childNElement : childNElements) {
                            for (Element siblingNElement : siblingNElements) {
                                childNElement.addRelation(new ElementRelation(siblingNElement, coordination.getRelation(), true));
                            }
                        }
                    }

                    // reverse direction
                    if (coordination.getRelation().getReverseRelation().isPresent()) {
                        for (DiscourseTree sibling : coordination.getOtherPrecedingCoordinations(child)) {
                            List<Element> siblingNElements = sibling.getNucleusPathLeaves().stream().map(n -> processedLeaves.get(n)).collect(Collectors.toList());

                            for (Element childNElement : childNElements) {
                                for (Element siblingNElement : siblingNElements) {
                                    childNElement.addRelation(new ElementRelation(siblingNElement, coordination.getRelation().getReverseRelation().get(), true));
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
            extractRec(subordination.getSuperordination(), contextLayer);
            extractRec(subordination.getSubordination(), contextLayer + 1);

            // add relations
            if (!IGNORED_RELATIONS.contains(subordination.getRelation())) {
                List<Element> superordinationNElements = subordination.getSuperordination().getNucleusPathLeaves().stream().map(n -> processedLeaves.get(n)).collect(Collectors.toList());
                List<Element> subordinationNElements = subordination.getSubordination().getNucleusPathLeaves().stream().map(n -> processedLeaves.get(n)).collect(Collectors.toList());

                for (Element superordinationNElement : superordinationNElements) {
                    for (Element subordinationNElement : subordinationNElements) {
                        superordinationNElement.addRelation(new ElementRelation(subordinationNElement, subordination.getRelation(), false));
                    }
                }
            }
        }
    }
}
