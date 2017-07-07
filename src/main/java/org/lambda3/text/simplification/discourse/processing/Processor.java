/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Processor
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

package org.lambda3.text.simplification.discourse.processing;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.lambda3.text.simplification.discourse.relation_extraction.DiscourseExtractor;
import org.lambda3.text.simplification.discourse.relation_extraction.Element;
import org.lambda3.text.simplification.discourse.tree.DiscourseTreeCreator;
import org.lambda3.text.simplification.discourse.utils.ConfigUtils;
import org.lambda3.text.simplification.discourse.utils.sentences.SentencesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class Processor {
    private final DiscourseTreeCreator discourseTreeCreator;
    private final DiscourseExtractor discourseExtractor;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Processor(Config config) {
        this.discourseTreeCreator = new DiscourseTreeCreator(config);
        this.discourseExtractor = new DiscourseExtractor(config);

        logger.info("Processor initialized");
        logger.info("\n{}", ConfigUtils.prettyPrint(config));
    }

    public Processor() {
        this(ConfigFactory.load().getConfig("discourse-simplification"));
    }

    public List<OutSentence> process(File file, ProcessingType type) throws FileNotFoundException {
        List<String> sentences = SentencesUtils.splitIntoSentencesFromFile(file);
        return process(sentences, type);
    }

    public List<OutSentence> process(String text, ProcessingType type) {
        List<String> sentences = SentencesUtils.splitIntoSentences(text);
        return process(sentences, type);
    }

    public List<OutSentence> process(List<String> sentences, ProcessingType type) {
        if (type.equals(ProcessingType.SEPARATE)) {
            return processSeparate(sentences);
        } else if (type.equals(ProcessingType.WHOLE)) {
            return processWhole(sentences);
        } else {
            throw new IllegalArgumentException("Unknown ProcessingType.");
        }
    }

    private List<OutSentence> createSentences(List<String> sentences, List<Element> elements) {
        List<OutSentence> res = new ArrayList<>();
        int idx = 0;
        for (String sentence : sentences) {
            res.add(new OutSentence(idx, sentence));
            idx += 1;
        }

        // assign elements to sentences
        for (Element element : elements) {
            if ((0 <= element.getSentenceIdx()) && (element.getSentenceIdx() < res.size())) {
                res.get(element.getSentenceIdx()).addElement(element);
            } else {
                logger.error("Sentence-Index is out of range.");
            }
        }

        return res;
    }

    // creates one discourse tree over all sentences (investigates intra-sentential and inter-sentential relations)
    private List<OutSentence> processWhole(List<String> sentences) {

        // Step 1) create document discourse tree
        logger.info("### STEP 1) CREATE DOCUMENT DISCOURSE TREE ###");

        discourseTreeCreator.reset();

        int idx = 0;
        for (String sentence : sentences) {
            logger.info("# Processing sentence {}/{} #", (idx + 1), sentences.size());
            logger.info("'" + sentence + "'");

            // extend discourse tree
            discourseTreeCreator.addSentence(sentence, idx);
            discourseTreeCreator.update();
            if (logger.isDebugEnabled()) {

                Optional.ofNullable(discourseTreeCreator.getLastSentenceTree())
                        .ifPresent(t -> logger.debug(t.toString()));

//                logger.debug(discourseTreeCreator.getDiscourseTree().toString()); // to show the current document discourse tree
            }

            ++idx;
        }

        // Step 2) extract elements
        logger.info("### STEP 2) EXTRACT ELEMENTS ###");

        List<Element> elements = discourseExtractor.extract(discourseTreeCreator.getDiscourseTree());
        if (logger.isDebugEnabled()) {
            elements.stream().filter(e -> e.getContextLayer() == 0).forEach(x -> logger.debug(x.toString()));
        }

        // Finalize) create sentences
        logger.info("### FINALIZE) CREATE SENTENCES ###");

        List<OutSentence> outSentences = createSentences(sentences, elements);
        if (logger.isDebugEnabled()) {
            outSentences.forEach(x -> logger.debug(x.toString()));
        }

        logger.info("### FINISHED");

        return outSentences;
    }

    // creates discourse trees for each individual sentence (investigates intra-sentential relations only)
    private List<OutSentence> processSeparate(List<String> sentences) {
        List<Element> cores = new ArrayList<>();

        int idx = 0;
        for (String sentence : sentences) {
            logger.info("# Processing sentence {}/{} #", (idx + 1), sentences.size());
            logger.info("'" + sentence + "'");

            // Step 1) create sentence discourse tree
            logger.debug("### Step 1) CREATE SENTENCE DISCOURSE TREE ###");

            discourseTreeCreator.reset();
            discourseTreeCreator.addSentence(sentence, idx);
            discourseTreeCreator.update();
            if (logger.isDebugEnabled()) {
                logger.debug(discourseTreeCreator.getDiscourseTree().toString());
            }

            // Step 2) extract elements
            logger.debug("### STEP 2) EXTRACT ELEMENTS ###");

            List<Element> es = discourseExtractor.extract(discourseTreeCreator.getDiscourseTree());
            if (logger.isDebugEnabled()) {
                es.stream().filter(e -> e.getContextLayer() == 0).forEach(x -> logger.debug(x.toString()));
            }

            cores.addAll(es);

            ++idx;
        }

        // Finalize) create sentences
        logger.info("### FINALIZE) CREATE SENTENCES ###");

        List<OutSentence> outSentences = createSentences(sentences, cores);
        if (logger.isDebugEnabled()) {
            outSentences.forEach(x -> logger.debug(x.toString()));
        }

        logger.info("### FINISHED");

        return outSentences;
    }

    public enum ProcessingType {
        SEPARATE,
        WHOLE
    }
}
