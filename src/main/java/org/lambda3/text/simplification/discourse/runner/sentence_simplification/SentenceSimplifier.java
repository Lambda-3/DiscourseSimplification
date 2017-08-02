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
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.model.Element;
import org.lambda3.text.simplification.discourse.runner.model.OutSentence;
import org.lambda3.text.simplification.discourse.runner.model.SimpleContext;
import org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification.ContextClassifier;
import org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification.ExtContextClassifier;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.lambda3.text.simplification.sentence.transformation.CoreContextSentence;
import org.lambda3.text.simplification.sentence.transformation.SentenceSimplifyingException;
import org.lambda3.text.simplification.sentence.transformation.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SentenceSimplifier {
    private static class Result {
        private String core;
        private List<String> contexts;

        public Result(String core, List<String> contexts) {
            this.core = core;
            this.contexts = contexts;
        }

        public String getCore() {
            return core;
        }

        public List<String> getContexts() {
            return contexts;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContextClassifier CONTEXT_CLASSIFIER = new ExtContextClassifier();

    private final Config config;

    public SentenceSimplifier(Config config) {
        this.config = config;
    }

    private Result simplify(String text) {
        Transformer transformer = new Transformer();
        CoreContextSentence s;
        try {
            s = transformer.simplify(text);
            String core = text;
            if ((s.getCore() != null) && (s.getCore().size() > 0)) {
                Tree c = s.getCore().get(0);
                if (c != null) {
                    core = WordsUtils.wordsToProperSentenceString(c.yieldWords());
                }
            }

            List<String> contexts = Collections.emptyList();
            if (s.getContext() != null) {
                contexts = s.getContext().stream().filter(c -> c != null).map(c -> WordsUtils.wordsToProperSentenceString(c.yieldWords())).collect(Collectors.toList());
            }

            return new Result(core, contexts);
        } catch (SentenceSimplifyingException e) {
            logger.warn("Failed to simplify text: \"{}\"", text);
            return new Result(text, Collections.emptyList());
        }
    }

    private void simplifyContexts(Element element) {

        // simple contexts
        List<SimpleContext> newSimpleContexts = new ArrayList<>();
        for (SimpleContext simpleContext : element.getSimpleContexts()) {

            // INTRA SENT ATTR
            if (simpleContext.getRelation().equals(Relation.ATTRIBUTION)) {
                Result r = simplify(simpleContext.getText());
                simpleContext.setText(r.getCore());
                for (String c : r.getContexts()) {
                    SimpleContext sc = new SimpleContext(c);

                    // classify
                    CONTEXT_CLASSIFIER.classify(sc);

                    // add only if it has no phrase
                    if (!sc.getPhrase().isPresent()) {
                        newSimpleContexts.add(sc);
                    }
                }
            }
        }
        newSimpleContexts.forEach(c -> element.addSimpleContext(c));
    }

    public void doSentenceSimplification(OutSentence sentence) {
        for (Element element : sentence.getElements()) {
            Result r = simplify(element.getText());

            simplifyContexts(element);

            element.setText(r.getCore());
            for (String c : r.getContexts()) {
                SimpleContext simpleContext = new SimpleContext(c);

                // classify
                CONTEXT_CLASSIFIER.classify(simpleContext);

                // add simple context
                element.addSimpleContext(simpleContext);
            }
        }
    }
}
