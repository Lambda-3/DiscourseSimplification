/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseSimplifier
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
import org.lambda3.text.simplification.discourse.runner.discourse_extraction.DiscourseExtractor;
import org.lambda3.text.simplification.discourse.model.Element;
import org.lambda3.text.simplification.discourse.model.OutSentence;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.DiscourseTreeCreator;
import org.lambda3.text.simplification.discourse.model.SimplificationContent;
import org.lambda3.text.simplification.discourse.runner.sentence_simplification.SentenceSimplifier;
import org.lambda3.text.simplification.discourse.utils.ConfigUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.sentences.SentencesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class DiscourseSimplifier {
    private final DiscourseTreeCreator discourseTreeCreator;
    private final DiscourseExtractor discourseExtractor;
    private final SentenceSimplifier sentenceSimplifier;
    private final boolean withSentenceSimplification;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DiscourseSimplifier(Config config) {
        this.discourseTreeCreator = new DiscourseTreeCreator(config);
        this.discourseExtractor = new DiscourseExtractor(config);
        this.sentenceSimplifier = new SentenceSimplifier(config);

        this.withSentenceSimplification = config.getBoolean("with-sentence-simplification");

        logger.debug("DiscourseSimplifier initialized");
        logger.debug("\n{}", ConfigUtils.prettyPrint(config));
    }

    public DiscourseSimplifier() {
        this(ConfigFactory.load().getConfig("discourse-simplification"));
    }

    public SimplificationContent doDiscourseSimplification(File file, ProcessingType type) throws FileNotFoundException {
        List<String> sentences = SentencesUtils.splitIntoSentencesFromFile(file);
        return doDiscourseSimplification(sentences, type);
    }

    public SimplificationContent doDiscourseSimplification(String text, ProcessingType type) {
        List<String> sentences = SentencesUtils.splitIntoSentences(text);
        return doDiscourseSimplification(sentences, type);
    }

    public SimplificationContent doDiscourseSimplification(List<String> sentences, ProcessingType type) {
        if (type.equals(ProcessingType.SEPARATE)) {
            return processSeparate(sentences);
        } else if (type.equals(ProcessingType.WHOLE)) {
            return processWhole(sentences);
        } else {
            throw new IllegalArgumentException("Unknown ProcessingType.");
        }
    }

    // creates one discourse discourse_tree over all sentences (investigates intra-sentential and inter-sentential relations)
    private SimplificationContent processWhole(List<String> sentences) {
        SimplificationContent content = new SimplificationContent();

        // Step 1) create document discourse discourse_tree
        logger.info("### STEP 1) CREATE DOCUMENT DISCOURSE TREE ###");
        discourseTreeCreator.reset();
        int idx = 0;
        for (String sentence : sentences) {
            logger.info("# Processing sentence {}/{} #", (idx + 1), sentences.size());
            logger.info("'" + sentence + "'");

            content.addSentence(new OutSentence(idx, sentence));

            // extend discourse discourse_tree
            try {
                discourseTreeCreator.addSentence(sentence, idx);
                discourseTreeCreator.update();
                if (logger.isDebugEnabled()) {

                    Optional.ofNullable(discourseTreeCreator.getLastSentenceTree())
                            .ifPresent(t -> logger.debug(t.toString()));

//                logger.debug(discourseTreeCreator.getDiscourseTree().toString()); // to show the current document discourse discourse_tree
                }
            } catch (ParseTreeException e) {
                logger.error("Failed to process sentence: {}", sentence);
            }

            ++idx;
        }

        // Step 2) do discourse extraction
        logger.info("### STEP 2) DO DISCOURSE EXTRACTION ###");
        List<Element> elements = discourseExtractor.doDiscourseExtraction(discourseTreeCreator.getDiscourseTree());
        elements.forEach(e -> content.addElement(e));
        if (logger.isDebugEnabled()) {
            logger.debug(content.toString());
        }

        // Step 3) do sentence simplification
        logger.info("### STEP 3) DO SENTENCE SIMPLIFICATION ###");
        if (withSentenceSimplification) {
            content.getSentences().forEach(s -> sentenceSimplifier.doSentenceSimplification(s));
            if (logger.isDebugEnabled()) {
                logger.debug(content.toString());
            }
        } else {
            logger.info("DEACTIVATED");
        }

        logger.info("### FINISHED");
        return content;
    }

    // creates discourse trees for each individual sentence (investigates intra-sentential relations only)
    private SimplificationContent processSeparate(List<String> sentences) {
        SimplificationContent content = new SimplificationContent();

        int idx = 0;
        for (String sentence : sentences) {
            OutSentence outSentence = new OutSentence(idx, sentence);

            logger.info("# Processing sentence {}/{} #", (idx + 1), sentences.size());
            logger.info("'" + sentence + "'");

            // Step 1) create sentence discourse tree
            logger.debug("### Step 1) CREATE SENTENCE DISCOURSE TREE ###");
            discourseTreeCreator.reset();
            try {
                discourseTreeCreator.addSentence(sentence, idx);
                discourseTreeCreator.update();
                if (logger.isDebugEnabled()) {
                    logger.debug(discourseTreeCreator.getDiscourseTree().toString());
                }

                // Step 2) do discourse extraction
                logger.debug("### STEP 2) DO DISCOURSE EXTRACTION ###");
                List<Element> elements = discourseExtractor.doDiscourseExtraction(discourseTreeCreator.getDiscourseTree());
                elements.forEach(e -> outSentence.addElement(e));
                logger.debug(outSentence.toString());

                // Step 3) do sentence simplification
                logger.debug("### STEP 3) DO SENTENCE SIMPLIFICATION ###");
                if (withSentenceSimplification) {
                    sentenceSimplifier.doSentenceSimplification(outSentence);
                    logger.debug(outSentence.toString());
                } else {
                    logger.info("DEACTIVATED");
                }
            } catch (ParseTreeException e) {
                logger.error("Failed to process sentence: {}", sentence);
            }

            content.addSentence(outSentence);

            ++idx;
        }

        logger.info("### FINISHED");
        return content;
    }
}
