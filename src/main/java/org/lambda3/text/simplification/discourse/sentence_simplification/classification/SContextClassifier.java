/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SContextClassifier
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

package org.lambda3.text.simplification.discourse.sentence_simplification.classification;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParseException;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParser;
import org.lambda3.text.simplification.discourse.utils.ner.tner.TNERString;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class SContextClassifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(SContextClassifier.class);

    private static final String PATTERN_PREFIX = "^.*(?<!\\w)";
    private static final String PATTERN_SUFFIX = "(?!\\w).*$";

    private static boolean isTimeNP(Tree np) {
        final List<String> monthPatterns = Stream.of(
                "january", "jan.",
                "february", "feb.",
                "march", "mar.",
                "april", "apr.",
                "may",
                "june",
                "july",
                "august", "aug.",
                "september", "sept.",
                "october", "oct.",
                "november", "nov.",
                "december", "dec."
        ).map(p -> PATTERN_PREFIX + p + PATTERN_SUFFIX).collect(Collectors.toList());

        final List<String> days = Stream.of(
                "monday", "mon.",
                "tuesday", "tues.",
                "wednesday", "wed.",
                "thursday", "thurs.",
                "friday", "fri.",
                "saturday", "sat.",
                "sunday", "sun."
        ).map(p -> PATTERN_PREFIX + p + PATTERN_SUFFIX).collect(Collectors.toList());

        final String yearPattern = PATTERN_PREFIX + "[1-2]\\d\\d\\d" + PATTERN_SUFFIX;
        final String bcadPattern = PATTERN_PREFIX + "(\\d+\\s+(bc|ad)|ad\\s+\\d+)" + PATTERN_SUFFIX;
        final String centuryPattern = PATTERN_PREFIX + "(1st|2nd|3rd|\\d+th)\\s+century" + PATTERN_SUFFIX;
        final String timePattern = PATTERN_PREFIX + "([0-1]?\\d|2[0-4])\\s*:\\s*[0-5]\\d" + PATTERN_SUFFIX;

        String text = WordsUtils.wordsToString(np.yieldWords()).toLowerCase();
        return ((monthPatterns.stream().anyMatch(text::matches))
                || (days.stream().anyMatch(text::matches))
                || (text.matches(yearPattern))
                || (text.matches(bcadPattern))
                || (text.matches(centuryPattern))
                || (text.matches(timePattern)));
    }

    private static boolean isLocationNP(Tree np) {
        try {
            TNERString ner = NERStringParser.parse(np);

            return ner.getTokens().stream().anyMatch(t -> t.getCategory().equals("LOCATION"));
        } catch (NERStringParseException e) {
            return false;
        }
    }

    public static Optional<Relation> classify(String sContext) {

        try {
            Tree parseTree = ParseTreeParser.parse(sContext);

            // find TIME-relation
            TregexPattern p = TregexPattern.compile("ROOT <<, (/This/ . (/(is|was)/ . (/(in|at|around)/ . NP=np)))");
            TregexMatcher matcher = p.matcher(parseTree);

            if (matcher.findAt(parseTree)) {
                if (isTimeNP(matcher.getNode("np"))) {
                    return Optional.of(Relation.TIME);
                }
            }

            // find LOCATION-relation
            p = TregexPattern.compile("ROOT <<, (/This/ . (/(is|was)/ . (__ . NP=np)))");
            matcher = p.matcher(parseTree);

            if (matcher.findAt(parseTree)) {
                if (isLocationNP(matcher.getNode("np"))) {
                    return Optional.of(Relation.LOCATION);
                }
            }


        } catch (ParseTreeException e) {
            LOGGER.error("Could not generate parse tree for sContext: '" + sContext + "'");
        }

        return Optional.empty();
    }
}
