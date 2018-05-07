/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtendedDiscourseSimplifier
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

import org.lambda3.text.simplification.discourse.model.SimplificationContent;
import org.lambda3.text.simplification.discourse.utils.sentences.SentencesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ExtendedDiscourseSimplifier extends DiscourseSimplifier {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static List<String> filterSentences(List<String> sentences, boolean shuffleSentences, Integer maxSentenceLength, Integer maxSentences) {

        // select sentences to doDiscourseSimplification
        List<String> res = new ArrayList<>();
        res.addAll(sentences);

        // shuffle
        if (shuffleSentences) {
            Collections.shuffle(res);
        }

        // remove too long sentences
        if (maxSentenceLength != null) {
            res = res.stream().filter(s -> s.length() <= maxSentenceLength).collect(Collectors.toList());
        }

        // limit number of sentences
        if (maxSentences != null) {
            if (res.size() > maxSentences) {
                res = res.subList(0, maxSentences);
            }
        }

        return res;
    }

    public SimplificationContent process(File file, ProcessingType type, boolean shuffleSentences, Integer maxSentenceLength, Integer maxSentences) throws IOException {
        return process(file, type, shuffleSentences, maxSentenceLength, maxSentences, false);
    }

    public SimplificationContent process(File file, ProcessingType type, boolean shuffleSentences, Integer maxSentenceLength, Integer maxSentences, boolean separateLines) throws IOException {
        return process(SentencesUtils.splitIntoSentencesFromFile(file, separateLines), type, shuffleSentences, maxSentenceLength, maxSentences);
    }

    public SimplificationContent process(String text, ProcessingType type, boolean shuffleSentences, Integer maxSentenceLength, Integer maxSentences) {
        return process(SentencesUtils.splitIntoSentences(text), type, shuffleSentences, maxSentenceLength, maxSentences);
    }

    public SimplificationContent process(List<String> sentences, ProcessingType type, boolean shuffleSentences, Integer maxSentenceLength, Integer maxSentences) {
        return doDiscourseSimplification(filterSentences(sentences, shuffleSentences, maxSentenceLength, maxSentences), type);
    }
}
