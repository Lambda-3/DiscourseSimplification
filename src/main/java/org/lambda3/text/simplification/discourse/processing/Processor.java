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

import org.lambda3.text.simplification.discourse.relation_extraction.DiscourseExtractor;
import org.lambda3.text.simplification.discourse.relation_extraction.element.DiscourseCore;
import org.lambda3.text.simplification.discourse.sentence_simplification.Simplifier;
import org.lambda3.text.simplification.discourse.sentence_simplification.element.DCore;
import org.lambda3.text.simplification.discourse.tree.DiscourseTreeCreator;
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
    private final static DiscourseTreeCreator DISCOURSE_TREE_CREATOR = new DiscourseTreeCreator();
    private final static DiscourseExtractor DISCOURSE_EXTRACTOR = new DiscourseExtractor();
    private final static Simplifier SIMPLIFIER = new Simplifier();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<DCore> process(File file, ProcessingType type) throws FileNotFoundException {
        List<String> sentences = SentencesUtils.splitIntoSentencesFromFile(file);
        return process(sentences, type);
    }

    public List<DCore> process(String text, ProcessingType type) {
        List<String> sentences = SentencesUtils.splitIntoSentences(text);
        return process(sentences, type);
    }

    public List<DCore> process(List<String> sentences, ProcessingType type) {
        if (type.equals(ProcessingType.SEPARATE)) {
            return processSeparate(sentences);
        } else if (type.equals(ProcessingType.WHOLE)) {
            return processWhole(sentences);
        } else {
            throw new IllegalArgumentException("Unknown ProcessingType.");
        }
    }

    // creates one discourse tree over all sentences (investigates intra-sentential and inter-sentential relations)
    private List<DCore> processWhole(List<String> sentences) {
        List<DCore> res = new ArrayList<>();

        // Step 1) create document discourse tree
        logger.info("Step 1) Create document discourse tree");
        DISCOURSE_TREE_CREATOR.reset();

        int idx = 0;
        for (String sentence : sentences) {
            logger.info("### Processing sentence ###");
            logger.info(sentence);

            // extend discourse tree
            DISCOURSE_TREE_CREATOR.addSentence(sentence, idx);
            DISCOURSE_TREE_CREATOR.update();
            if (logger.isDebugEnabled()) {

                Optional.ofNullable(DISCOURSE_TREE_CREATOR.getLastSentenceTree())
                        .ifPresent(t -> logger.debug(t.toString()));

//                logger.debug(DISCOURSE_TREE_CREATOR.getDiscourseTree().toString()); // to show the current document discourse tree
            }

            ++idx;
        }

        // Step 2) extract discourse cores
        logger.info("Step 2) extract discourse cores");

        List<DiscourseCore> discourseCores = DISCOURSE_EXTRACTOR.extract(DISCOURSE_TREE_CREATOR.getDiscourseTree());
        if (logger.isDebugEnabled()) {
            discourseCores.forEach(x -> logger.debug(x.toString()));
        }

        // Step 3) generate output format
        logger.info("Step 3) Generate output format");

        List<DCore> dCores = SIMPLIFIER.simplify(discourseCores);
        res.addAll(dCores);

        if (logger.isInfoEnabled()) {
            dCores.forEach(core -> logger.info(core.toString()));
        }

        return res;
    }

    // creates discourse trees for each individual sentence (investigates intra-sentential relations only)
    private List<DCore> processSeparate(List<String> sentences) {
        List<DCore> res = new ArrayList<>();

        int idx = 0;
        for (String sentence : sentences) {
            logger.info("### Processing sentence ###");
            logger.info("'" + sentence + "'");

            // Step 1) create sentence discourse tree
            logger.debug("Step 1) Create sentence discourse tree");
            DISCOURSE_TREE_CREATOR.reset();
            DISCOURSE_TREE_CREATOR.addSentence(sentence, idx);
            DISCOURSE_TREE_CREATOR.update();
            if (logger.isDebugEnabled()) {
                logger.debug(DISCOURSE_TREE_CREATOR.getDiscourseTree().toString());
            }

            // Step 2) extract discourse cores
            logger.debug("Step 2) extract discourse cores");

            List<DiscourseCore> discourseCores = DISCOURSE_EXTRACTOR.extract(DISCOURSE_TREE_CREATOR.getDiscourseTree());
            if (logger.isDebugEnabled()) {
                discourseCores.forEach(x -> logger.debug(x.toString()));
            }

            // Step 3) generate output format
            logger.debug("Step 3) generate output format");

            List<DCore> dCores = SIMPLIFIER.simplify(discourseCores);
            res.addAll(dCores);

            if (logger.isInfoEnabled()) {
                dCores.forEach(core -> logger.info(core.toString()));
            }

            ++idx;
        }

        return res;
    }

    public enum ProcessingType {
        SEPARATE,
        WHOLE
    }
}
