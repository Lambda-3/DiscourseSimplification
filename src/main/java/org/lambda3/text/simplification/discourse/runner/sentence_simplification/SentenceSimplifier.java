/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SentenceSimplifier
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

package org.lambda3.text.simplification.discourse.runner.sentence_simplification;

import com.typesafe.config.Config;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.model.Element;
import org.lambda3.text.simplification.discourse.model.OutSentence;
import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification.ContextClassifier;
import org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification.SimpleContextClassifier;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.lambda3.text.simplification.sentence.transformation.CoreContextSentence;
import org.lambda3.text.simplification.sentence.transformation.SentenceSimplifyingException;
import org.lambda3.text.simplification.sentence.transformation.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SentenceSimplifier {
    private static class Result {
        private Tree core;
        private List<Tree> contexts;

        Result(Tree core, List<Tree> contexts) {
            this.core = core;
            this.contexts = contexts;
        }

        Tree getCore() {
            return core;
        }

        List<Tree> getContexts() {
            return contexts;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContextClassifier contextClassifier;

    public SentenceSimplifier(Config config) {
        this.contextClassifier = new SimpleContextClassifier(config);
    }

    private Result simplify(Tree parseTree) {
        Transformer transformer = new Transformer();
        CoreContextSentence s;
        try {
            s = transformer.simplify(WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree))); //TODO change transformer to accept parsed tree

            Tree core = parseTree;
            if ((s.getCore() != null) && (s.getCore().size() > 0) && (s.getCore().get(0) != null)) {
                core = s.getCore().get(0);
            }

            List<Tree> contexts = Collections.emptyList();
            if (s.getContext() != null) {
                contexts = s.getContext().stream().filter(Objects::nonNull).collect(Collectors.toList());
            }

            return new Result(core, contexts);
        } catch (SentenceSimplifyingException e) {
            logger.warn("Failed to simplify: \"{}\"", WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree)));
            return new Result(parseTree, Collections.emptyList());
        }
    }

    private void simplifyContexts(Element element) {

        // simple contexts
        List<SimpleContext> newSimpleContexts = new ArrayList<>();
        for (SimpleContext simpleContext : element.getSimpleContexts()) {

            // INTRA SENT ATTR
            if (simpleContext.getRelation().equals(Relation.ATTRIBUTION)) {
                Result r = simplify(simpleContext.getParseTree());
                simpleContext.setParseTree(r.getCore());
                for (Tree c : r.getContexts()) {
                    SimpleContext sc = new SimpleContext(c);

                    // classify
                    contextClassifier.classify(sc);

                    // add only if it is noun based
                    if (sc.getRelation().equals(Relation.NOUN_BASED)) {
                        newSimpleContexts.add(sc);
                    }
                }
            }
        }
        newSimpleContexts.forEach(element::addSimpleContext);
    }

    public void doSentenceSimplification(OutSentence<Element> sentence) {
        for (Element element : sentence.getElements()) {
            Result r = simplify(element.getParseTree());

            simplifyContexts(element);

            element.setParseTree(r.getCore());
            for (Tree c : r.getContexts()) {
                SimpleContext simpleContext = new SimpleContext(c);

                // classify
                contextClassifier.classify(simpleContext);

                // add simple context
                element.addSimpleContext(simpleContext);
            }
        }
    }
}
