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

package org.lambda3.text.simplification.discourse.runner.discourse_tree;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import org.lambda3.text.simplification.discourse.processing.SentencePreprocessor;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.*;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeVisualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class DiscourseTreeCreator {
    private final Config config;
    private final SentencePreprocessor preprocessor;
    private final List<ExtractionRule> rules;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Coordination discourseTree;

    public DiscourseTreeCreator(Config config, SentencePreprocessor preprocessor) {
        this.config = config;
        this.preprocessor = preprocessor;

        // create rules from config
        this.rules = new ArrayList<>();
        for (String className : this.config.getStringList("rules")) {
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getConstructor();
                ExtractionRule rule = (ExtractionRule) constructor.newInstance();
                rule.setConfig(config);
                rules.add(rule);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                logger.error("{}", e);
                logger.error("Failed to create instance of {}", className);
                throw new ConfigException.BadValue("rules." + className, "Failed to create instance.");
            }
        }

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

    public void addSentence(String sentence, int sentenceIdx) throws ParseTreeException {
        String preprocessedSentence = preprocessor.preprocessSentence(sentence);
        discourseTree.addCoordination(new SentenceLeaf(preprocessedSentence, sentenceIdx));
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

                // doDiscourseSimplification coordination-leaf if not processed yet
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

            // doDiscourseSimplification superordination-leaf if not processed yet
            if (subordination.getSuperordination().isNotProcessed()) {

                if (subordination.getSuperordination() instanceof Leaf) {
                    Optional<DiscourseTree> newChild = applyRules((Leaf) subordination.getSuperordination());
                    newChild.ifPresent(subordination::replaceSuperordination);
                }

                subordination.getSuperordination().setProcessed();

                // recursion
                processDiscourseTreeRec(subordination.getSuperordination());
            }

            // doDiscourseSimplification subordination-leaf if not processed yet
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

        logger.debug("Process leaf:");
        if (logger.isDebugEnabled()) {
            logger.debug(ParseTreeVisualizer.prettyPrint(leaf.getParseTree()));
        }

        // check rules
        for (ExtractionRule rule : rules) {

            Optional<Extraction> extraction = null;
            try {
                extraction = rule.extract(leaf);
            } catch (ParseTreeException e) {
                continue;
            }

            if (extraction.isPresent()) {
                logger.debug("Extraction rule " + rule.getClass().getSimpleName() + " matched.");

                // handle extractions
                Optional<DiscourseTree> r = extraction.get().generate(leaf);
                if (r.isPresent()) {
                    return r;
                } else {
                    logger.debug("Reference could not be used, checking other model rules.");
                }
            }
        }
        logger.debug("No model rule applied.");

        return Optional.empty();
    }

}
