/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SimpleContextClassifier
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

package org.lambda3.text.simplification.discourse.runner.sentence_simplification.classification;

import com.typesafe.config.Config;
import edu.stanford.nlp.ling.Word;
import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.classification.CuePhraseClassifier;
import org.lambda3.text.simplification.discourse.utils.ner.NERString;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParser;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleContextClassifier implements ContextClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleContextClassifier.class);

    private static final String PATTERN_PREFIX = "^(?i:.*(?<!\\w)";
    private static final String PATTERN_SUFFIX = "(?!\\w).*)$";

    private static final List<String> MONTH_PATTERNS = Stream.of(
            "january", "jan\\.",
            "february", "feb\\.",
            "march", "mar\\.",
            "april", "apr\\.",
            "may",
            "june",
            "july",
            "august", "aug\\.",
            "september", "sept\\.",
            "october", "oct\\.",
            "november", "nov\\.",
            "december", "dec\\."
    ).map(p -> PATTERN_PREFIX + p + PATTERN_SUFFIX).collect(Collectors.toList());

    private static final List<String> DAY_PATTERNS = Stream.of(
            "today", "yesterday",
            "monday", "mon\\.",
            "tuesday", "tues\\.",
            "wednesday", "wed\\.",
            "thursday", "thurs\\.",
            "friday", "fri\\.",
            "saturday", "sat\\.",
            "sunday", "sun\\."
    ).map(p -> PATTERN_PREFIX + p + PATTERN_SUFFIX).collect(Collectors.toList());

    private static final String YEAR_PATTERN = PATTERN_PREFIX + "[1-2]\\d\\d\\d" + PATTERN_SUFFIX;
    private static final String BC_AD_PATTERN = PATTERN_PREFIX + "(\\d+\\s+(bc|ad)|ad\\s+\\d+)" + PATTERN_SUFFIX;
    private static final String CENTURY_PATTERN = PATTERN_PREFIX + "(1st|2nd|3rd|\\d+th)\\s+century" + PATTERN_SUFFIX;
    private static final String TIME_PATTERN = PATTERN_PREFIX + "([0-1]?\\d|2[0-4])\\s*:\\s*[0-5]\\d" + PATTERN_SUFFIX;

    private final CuePhraseClassifier cuePhraseClassifier;

    public SimpleContextClassifier(Config config) {
        this.cuePhraseClassifier = new CuePhraseClassifier(config);
    }

    private boolean checkTemporal(SimpleContext simpleContext) {

        // use first 10 words as cue phrase
        List<Word> cuePhraseWords = ParseTreeExtractionUtils.getContainingWords(simpleContext.getPhrase());
        if (cuePhraseWords.size() > 10) {
            cuePhraseWords = cuePhraseWords.subList(0, 10);
        }
        String cuePhrase = WordsUtils.wordsToString(cuePhraseWords);

        if ((MONTH_PATTERNS.stream().anyMatch(cuePhrase::matches))
                || (DAY_PATTERNS.stream().anyMatch(cuePhrase::matches))
                || (cuePhrase.matches(YEAR_PATTERN))
                || (cuePhrase.matches(BC_AD_PATTERN))
                || (cuePhrase.matches(CENTURY_PATTERN))
                || (cuePhrase.matches(TIME_PATTERN))) {

            simpleContext.setRelation(Relation.TEMPORAL);
            return true;
        }

        return false;
    }

    private boolean checkSpatial(SimpleContext simpleContext) {

        // use first 10 words as cue phrase
        List<Word> cuePhraseWords = ParseTreeExtractionUtils.getContainingWords(simpleContext.getPhrase());
        if (cuePhraseWords.size() > 10) {
            cuePhraseWords = cuePhraseWords.subList(0, 10);
        }
        String cuePhrase = WordsUtils.wordsToString(cuePhraseWords);

        NERString ner = NERStringParser.parse(cuePhrase);

        if (ner.getTokens().stream().anyMatch(t -> t.getCategory().equals("LOCATION"))) {
            simpleContext.setRelation(Relation.SPATIAL);
            return true;
        }

        return false;
    }

    private boolean checkCues(SimpleContext simpleContext) {

        // use first 3 words as cue phrase
        List<Word> cuePhraseWords = ParseTreeExtractionUtils.getContainingWords(simpleContext.getPhrase());
        if (cuePhraseWords.size() > 3) {
            cuePhraseWords = cuePhraseWords.subList(0, 3);
        }
        String cuePhrase = WordsUtils.wordsToString(cuePhraseWords);

        Optional<Relation> relation = cuePhraseClassifier.classifySubordinating(cuePhrase);
        if (relation.isPresent()) {
            simpleContext.setRelation(relation.get());
            return true;
        }

        return false;
    }

    @Override
    public void classify(SimpleContext simpleContext) {

        // CUE
        if (checkCues(simpleContext)) {
            return;
        }

        // TEMPORAL
        if (checkTemporal(simpleContext)) {
            return;
        }

        // SPATIAL
        if (checkSpatial(simpleContext)) {
            return;
        }
    }
}
