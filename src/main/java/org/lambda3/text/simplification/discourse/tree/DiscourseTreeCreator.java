/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseTreeCreator
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

package org.lambda3.text.simplification.discourse.tree;

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.tree.extraction.model.CoordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.extraction.model.RefCoordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.extraction.model.RefSubordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.extraction.model.SubordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.extraction.rules.*;
import org.lambda3.text.simplification.discourse.tree.extraction.rules.ListNP.PreListNPExtractor;
import org.lambda3.text.simplification.discourse.tree.extraction.rules.ListNP.PostListNPExtractor;
import org.lambda3.text.simplification.discourse.tree.model.*;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeVisualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class DiscourseTreeCreator {
    private static final List<ExtractionRule> rules;

    static {
        rules = new ArrayList<>();

        rules.add(new ReferenceExtractor1());
        rules.add(new ReferenceExtractor2());
        rules.add(new CoordinationExtractor());

        rules.add(new EnablementPreExtractor());
        rules.add(new SubordinationPreEnablementExtractor());
        rules.add(new SharedNPPreParticipalExtractor());
        rules.add(new SubordinationPreExtractor());

        rules.add(new EnablementPostExtractor());
        rules.add(new SubordinationPostEnablementExtractor());
        rules.add(new SharedNPPostCoordinationExtractor());
        rules.add(new SharedNPPostParticipalExtractor());
        rules.add(new SubordinationPostISAExtractor());
        rules.add(new SubordinationPostISAExtractor2());
        rules.add(new SubordinationPostExtractor());

        // should be applied last (because they dont allow further splitting)
        rules.add(new PreListNPExtractor());
        rules.add(new PostListNPExtractor());
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Coordination discourseTree;

    public DiscourseTreeCreator() {
        reset();
    }

    public void reset() {
        this.discourseTree = new Coordination(
                "ROOT",
                Relation.UNKNOWN_COORDINATION,
                null,
                new ArrayList<>()

        );
    }

    public void addSentence(String sentence, int sentenceIdx) {
        discourseTree.addCoordination(new SentenceLeaf(sentence, sentenceIdx));
    }

    public DiscourseTree getLastSentenceTree() {
        DiscourseTree res = null;
        if (discourseTree.getCoordinations().size() > 0) {
            res = discourseTree.getCoordinations().get(discourseTree.getCoordinations().size() - 1);
        }

        return res;
    }

    public Coordination getDiscourseTree() {
        return discourseTree;
    }

    public void update() {
        processDiscourseTreeRec(discourseTree);
        discourseTree.cleanup();
    }

    private void processDiscourseTreeRec(DiscourseTree discourseTree) {

        if (discourseTree instanceof Coordination) {
            Coordination coordination = (Coordination) discourseTree;

            for (DiscourseTree child : coordination.getCoordinations()) {

                // process coordination-leaf if not processed yet
                if (child.isNotProcessed()) {
                    DiscourseTree c = child;

                    if (child instanceof Leaf) {
                        Optional<DiscourseTree> newChild = applyRules((Leaf) child);
                        if (newChild.isPresent()) {
                            coordination.replaceCoordination(child, newChild.get());
                            c = newChild.get();
                        }
                    }

                    child.setProcessed();

                    // recursion
                    processDiscourseTreeRec(c);
                }
            }
        }

        if (discourseTree instanceof Subordination) {
            Subordination subordination = (Subordination) discourseTree;

            // process superordination-leaf if not processed yet
            if (subordination.getSuperordination().isNotProcessed()) {

                if (subordination.getSuperordination() instanceof Leaf) {
                    Optional<DiscourseTree> newChild = applyRules((Leaf) subordination.getSuperordination());
                    newChild.ifPresent(subordination::replaceSuperordination);
                }

                subordination.getSuperordination().setProcessed();

                // recursion
                processDiscourseTreeRec(subordination.getSuperordination());
            }

            // process subordination-leaf if not processed yet
            if (subordination.getSubordination().isNotProcessed()) {

                if (subordination.getSubordination() instanceof Leaf) {
                    Optional<DiscourseTree> newChild = applyRules((Leaf) subordination.getSubordination());
                    newChild.ifPresent(subordination::replaceSubordination);
                }

                subordination.getSubordination().setProcessed();

                // recursion
                processDiscourseTreeRec(subordination.getSubordination());
            }
        }
    }

    private Optional<DiscourseTree> applyRules(Leaf leaf) {
        logger.debug("Processing leaf:");
        if (logger.isDebugEnabled()) {
            logger.debug(leaf.toString());
        }

        if (!leaf.isAllowSplit()) {
            logger.debug("Leaf will not be check.");
            return Optional.empty();
        }

        // try to generate parseTree
        Tree parseTree;
        try {
            parseTree = ParseTreeParser.parse(leaf.preferRephrasedText());
        } catch (ParseTreeException e) {
            logger.error("Failed to generate parse tree");

            return Optional.empty();
        }
        logger.debug("Parse tree:");
        if (logger.isDebugEnabled()) {
            logger.debug(ParseTreeVisualizer.prettyPrint(parseTree));
        }

        // check rules
        for (ExtractionRule rule : rules) {

            Optional<Extraction> extraction = rule.extract(parseTree);
            if (extraction.isPresent()) {
                logger.debug("Extraction rule " + rule.getClass().getSimpleName() + " matched.");

                // handle CoordinationExtraction
                if (extraction.get() instanceof CoordinationExtraction) {
                    return Optional.of(((CoordinationExtraction) extraction.get()).convert());
                }

                // handle SubordinationExtraction
                if (extraction.get() instanceof SubordinationExtraction) {
                    return Optional.of(((SubordinationExtraction) extraction.get()).convert());
                }

                // handle RefCoordinationExtraction
                if (extraction.get() instanceof RefCoordinationExtraction) {
                    Optional<DiscourseTree> r = ((RefCoordinationExtraction) extraction.get()).convert(leaf);
                    if (r.isPresent()) {
                        return r;
                    } else {
                        logger.debug("Reference could not be used, checking other model rules.");
                    }
                }

                // handle RefSubordinationExtraction
                if (extraction.get() instanceof RefSubordinationExtraction) {
                    Optional<DiscourseTree> r = ((RefSubordinationExtraction) extraction.get()).convert(leaf);
                    if (r.isPresent()) {
                        return r;
                    } else {
                        logger.debug("Reference could not be used, checking other model rules.");
                    }
                }
            }
        }
        logger.debug("No model rule applied.");

        return Optional.empty();
    }

}
