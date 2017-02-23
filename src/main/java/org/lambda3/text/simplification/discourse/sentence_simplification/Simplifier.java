/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Simplifier
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

package org.lambda3.text.simplification.discourse.sentence_simplification;

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.sentence.transformation.CoreContextSentence;
import org.lambda3.text.simplification.sentence.transformation.SentenceSimplifyingException;
import org.lambda3.text.simplification.sentence.transformation.Transformer;
import org.lambda3.text.simplification.discourse.relation_extraction.element.DiscourseContext;
import org.lambda3.text.simplification.discourse.relation_extraction.element.DiscourseCore;
import org.lambda3.text.simplification.discourse.relation_extraction.relation.DiscourseCoreContextRelation;
import org.lambda3.text.simplification.discourse.relation_extraction.relation.DiscourseCoreCoreRelation;
import org.lambda3.text.simplification.discourse.sentence_simplification.classification.SContextClassifier;
import org.lambda3.text.simplification.discourse.sentence_simplification.element.DContext;
import org.lambda3.text.simplification.discourse.sentence_simplification.element.DCore;
import org.lambda3.text.simplification.discourse.sentence_simplification.element.SContext;
import org.lambda3.text.simplification.discourse.sentence_simplification.relation.DContextRelation;
import org.lambda3.text.simplification.discourse.sentence_simplification.relation.DCoreRelation;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class Simplifier {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private LinkedHashMap<DiscourseCore, DCore> processedDiscourseCores;
    private LinkedHashMap<DiscourseContext, DContext> processedDiscourseContexts;

    public Simplifier() {
        this.processedDiscourseCores = new LinkedHashMap<>();
        this.processedDiscourseContexts = new LinkedHashMap<>();
    }

    private static SContext createSContext(String text, int sentenceIdx) {
        Optional<Relation> relation = SContextClassifier.classify(text);
        return relation.map(relation1 -> new SContext(text, sentenceIdx, relation1)).orElseGet(() -> new SContext(text, sentenceIdx, Relation.UNKNOWN_SENT_SIM));
    }

    private DContext getDContext(DiscourseContext discourseContext) {
        DContext res;

        if (processedDiscourseContexts.containsKey(discourseContext)) {
            res = processedDiscourseContexts.get(discourseContext);
        } else {
            String text = discourseContext.getText();
            List<SContext> sentSimContexts = new ArrayList<>();

            // apply sentence simplification
            Transformer t = new Transformer();
            try {
                logger.debug("Simplifying: '{}'", discourseContext.getText());
                CoreContextSentence s = t.simplify(discourseContext.getText());

                // set coreText (assume that there is usually only one core)
                if ((s.getCore() != null) && (s.getCore().size() > 0)) {
                    Tree c = s.getCore().get(0);
                    if (c != null) {
                        text = WordsUtils.wordsToString(c.yieldWords());
                    }
                }

                // add (sentence simplification) contexts
                if (s.getContext() != null) {
                    for (Tree c : s.getContext()) {
                        if (c != null) {
                            sentSimContexts.add(createSContext(WordsUtils.wordsToString(c.yieldWords()), discourseContext.getSentenceIdx()));
                        }
                    }
                }
            } catch (SentenceSimplifyingException e) {
                // nothing
            }

            res = new DContext(text, discourseContext.getSentenceIdx(), discourseContext.getText());

            // add (sentence simplification) context relations
            for (SContext sentSimContext : sentSimContexts) {
                res.addSContext(sentSimContext);
            }

            processedDiscourseContexts.put(discourseContext, res);
        }

        return res;
    }

    private DCore getDCore(DiscourseCore discourseCore) {
        DCore res;

        if (processedDiscourseCores.containsKey(discourseCore)) {
            res = processedDiscourseCores.get(discourseCore);
        } else {
            String text = discourseCore.getText();
            List<SContext> sentSimContexts = new ArrayList<>();

            // apply sentence simplification
            Transformer t = new Transformer();
            try {
                logger.debug("Simplifying: '{}'", discourseCore.getText());
                CoreContextSentence s = t.simplify(discourseCore.getText());

                // set coreText (assume that there is usually only one core)
                if ((s.getCore() != null) && (s.getCore().size() > 0)) {
                    Tree c = s.getCore().get(0);
                    if (c != null) {
                        text = WordsUtils.wordsToString(c.yieldWords());
                    }
                }

                // add (sentence simplification) contexts
                if (s.getContext() != null) {
                    for (Tree c : s.getContext()) {
                        if (c != null) {
                            sentSimContexts.add(createSContext(WordsUtils.wordsToString(c.yieldWords()), discourseCore.getSentenceIdx()));
                        }
                    }
                }
            } catch (SentenceSimplifyingException e) {
                // nothing
            }

            res = new DCore(text, discourseCore.getSentenceIdx(), discourseCore.getText());

            // add (sentence simplification) context relations
            for (SContext sentSimContext : sentSimContexts) {
                res.addSContext(sentSimContext);
            }

            processedDiscourseCores.put(discourseCore, res);
        }

        return res;
    }

    public List<DCore> simplify(List<DiscourseCore> discourseCores) {
        this.processedDiscourseCores = new LinkedHashMap<>();
        this.processedDiscourseContexts = new LinkedHashMap<>();

        List<DCore> res = new ArrayList<>();

        for (DiscourseCore discourseCore : discourseCores) {
            DCore dCore = getDCore(discourseCore);

            // add (discourse) core relations
            for (DiscourseCoreCoreRelation discourseCoreCoreRelation : discourseCore.getCoreRelations()) {
                dCore.addDCoreRelation(new DCoreRelation(discourseCoreCoreRelation.getRelation(), getDCore(discourseCoreCoreRelation.getCore())));
            }

            // add (discourse) context relations
            for (DiscourseCoreContextRelation discourseCoreContextRelation : discourseCore.getContextRelations()) {

                // convert into a DContext or a SContext
                if (discourseCoreContextRelation.getContext().isSentSimContext()) {
                    dCore.addSContext(new SContext(discourseCoreContextRelation.getContext().getText(), discourseCoreContextRelation.getContext().getSentenceIdx(), discourseCoreContextRelation.getRelation()));
                } else {
                    dCore.addDContextRelation(new DContextRelation(discourseCoreContextRelation.getRelation(), getDContext(discourseCoreContextRelation.getContext())));
                }
            }

            res.add(dCore);
        }

        return res;
    }
}
