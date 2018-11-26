/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SPOSplitter
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

package org.lambda3.text.simplification.discourse.utils;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.App;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeVisualizer;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class SPOSplitter {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static class Result {
        private final String subject;
        private final String predicate;
        private final String object;

        public Result(String subject, String predicate, String object) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
        }

        public String getSubject() {
            return subject;
        }

        public String getPredicate() {
            return predicate;
        }

        public String getObject() {
            return object;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "subject='" + subject + '\'' +
                    ", predicate='" + predicate + '\'' +
                    ", object='" + object + '\'' +
                    '}';
        }
    }

    public static Optional<Result> split(String sentence) {
        try {
            Tree parseTree = ParseTreeParser.parse(sentence);
//            LOGGER.info(ParseTreeVisualizer.prettyPrint(parseTree));

            // pattern with object
            TregexPattern p = TregexPattern.compile("ROOT <<: (S < (NP=np $.. (VP=vp [ <+(VP) (VP=lowestvp !< VP) | ==VP=lowestvp !< VP ])))");
            TregexMatcher matcher = p.matcher(parseTree);
            while (matcher.findAt(parseTree)) {
                Tree np = matcher.getNode("np");
                Tree vp = matcher.getNode("vp");
                Tree lowestvp = matcher.getNode("lowestvp");

                // has object?
                TregexPattern op = TregexPattern.compile(lowestvp.value() + " < (PP|NP|S|SBAR=ob !$,, (PP|NP|S|SBAR))");
                TregexMatcher omatcher = op.matcher(lowestvp);
                if (omatcher.findAt(lowestvp)) {
                    Tree ob = omatcher.getNode("ob");

                    List<Word> subjectWords = ParseTreeExtractionUtils.getContainingWords(np);
                    List<Word> predicateWords = ParseTreeExtractionUtils.getWordsInBetween(parseTree, vp, ob, true, false);
                    List<Word> objectWords = ParseTreeExtractionUtils.getFollowingWords(vp, ob, true);

                    return Optional.of(new Result(
                            WordsUtils.wordsToString(subjectWords),
                            WordsUtils.wordsToString(predicateWords),
                            WordsUtils.wordsToString(objectWords)
                    ));
                } else {
                    List<Word> subjectWords = ParseTreeExtractionUtils.getContainingWords(np);
                    List<Word> predicateWords = ParseTreeExtractionUtils.getContainingWords(vp);
                    List<Word> objectWords = new ArrayList<>();

                    return Optional.of(new Result(
                            WordsUtils.wordsToString(subjectWords),
                            WordsUtils.wordsToString(predicateWords),
                            WordsUtils.wordsToString(objectWords)
                    ));
                }
            }

        } catch (ParseTreeException e) {
            LOGGER.error("Could not parse sentence '{}'", sentence);
        }

        return Optional.empty();
    }
}
