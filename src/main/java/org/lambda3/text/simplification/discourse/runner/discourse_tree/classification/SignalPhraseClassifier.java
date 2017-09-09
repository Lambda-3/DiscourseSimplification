/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SignalPhraseClassifier
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.classification;

import edu.stanford.nlp.ling.Word;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class SignalPhraseClassifier {

    private static final List<Mapping> GENERAL_MAPPINGS = Arrays.asList(

            // BACKGROUND
            new Mapping(Relation.BACKGROUND, "...as..."),
            new Mapping(Relation.BACKGROUND, "...now..."),
            new Mapping(Relation.BACKGROUND, "...once..."),
//            new Mapping(Relation.BACKGROUND, "previously"),
            new Mapping(Relation.BACKGROUND, "...when..."),
            new Mapping(Relation.BACKGROUND, "...with..."),
            new Mapping(Relation.BACKGROUND, "...without..."),

            // CAUSE
            new Mapping(Relation.CAUSE, "...largely because..."),
            new Mapping(Relation.CAUSE, "...as a result..."),
            new Mapping(Relation.CAUSE, "...as a result of..."),
            new Mapping(Relation.CAUSE, "...because..."),
            new Mapping(Relation.CAUSE, "...since..."),

            // CONDITION
            new Mapping(Relation.CONDITION, "...if..."),
            new Mapping(Relation.CONDITION, "...in case..."),
            new Mapping(Relation.CONDITION, "...unless..."),
            new Mapping(Relation.CONDITION, "...until..."),

            // CONTRAST
            new Mapping(Relation.CONTRAST, "...although..."),
            new Mapping(Relation.CONTRAST, "...but..."),
            new Mapping(Relation.CONTRAST, "...but now..."),
            new Mapping(Relation.CONTRAST, "...despite..."),
            new Mapping(Relation.CONTRAST, "...even though..."),
            new Mapping(Relation.CONTRAST, "...even when..."),
            new Mapping(Relation.CONTRAST, "...however..."),
            new Mapping(Relation.CONTRAST, "...instead..."),
            new Mapping(Relation.CONTRAST, "...rather..."),
            new Mapping(Relation.CONTRAST, "...still..."),
            new Mapping(Relation.CONTRAST, "...though..."),
            new Mapping(Relation.CONTRAST, "...thus..."),
            new Mapping(Relation.CONTRAST, "...until recently..."),
            new Mapping(Relation.CONTRAST, "...while..."),
            new Mapping(Relation.CONTRAST, "...yet..."),

            // ELABORATION
            new Mapping(Relation.ELABORATION, "...more provocatively..."),
            new Mapping(Relation.ELABORATION, "...even before..."),
            new Mapping(Relation.ELABORATION, "...for example..."),
            new Mapping(Relation.ELABORATION, "...further..."),
            new Mapping(Relation.ELABORATION, "...recently..."),
            new Mapping(Relation.ELABORATION, "...since...now..."),
            new Mapping(Relation.ELABORATION, "...so..."),
            new Mapping(Relation.ELABORATION, "...so far..."),
            new Mapping(Relation.ELABORATION, "...where..."),
            new Mapping(Relation.ELABORATION, "...whereby..."),
            new Mapping(Relation.ELABORATION, "...whether..."),

            // EXPLANATION
            new Mapping(Relation.EXPLANATION, "...simply because..."),
            new Mapping(Relation.EXPLANATION, "...because of..."),
            new Mapping(Relation.EXPLANATION, "...indeed..."),
            new Mapping(Relation.EXPLANATION, "...so...that..."),

            // JOINT_CONJUNCTION
            new Mapping(Relation.JOINT_CONJUNCTION, "...and..."),

            // JOINT_DISJUNCTION
            new Mapping(Relation.JOINT_DISJUNCTION, "...or..."),

            // TEMPORAL_BEFORE
            new Mapping(Relation.TEMPORAL_BEFORE, "...before..."),
            new Mapping(Relation.TEMPORAL_BEFORE, "...previously..."), // changed from BACKGROUND TO TEMPORAL_BEFORE

            // TEMPORAL_AFTER
            new Mapping(Relation.TEMPORAL_AFTER, "...after..."),
            new Mapping(Relation.TEMPORAL_AFTER, "...and after..."),
            new Mapping(Relation.TEMPORAL_AFTER, "...next..."),
            new Mapping(Relation.TEMPORAL_AFTER, "...then..."),

            // TEMPORAL_SEQUENCE
            new Mapping(Relation.TEMPORAL_SEQUENCE, "...thereafter...")
    );

    private static Optional<Relation> classify(List<Mapping> mappings, String signalPhrase) {
        if (signalPhrase.length() == 0) {
            return Optional.empty();
        }

        Optional<Mapping> bestMapping = Optional.empty();
        for (Mapping mapping : mappings) {
            if (mapping.check(signalPhrase)) {
                if (!bestMapping.isPresent()) {
                    bestMapping = Optional.of(mapping);
                } else if (mapping.getSignalPhrasePatternSize() >= bestMapping.get().getSignalPhrasePatternSize()) {
                    bestMapping = Optional.of(mapping);
                }
            }
        }

        return bestMapping.map(Mapping::getRelation);
    }

    public Optional<Relation> classifyCustom(List<Mapping> mappings, String signalPhrase) {
        return classify(mappings, signalPhrase);
    }

    public Optional<Relation> classifyCustom(List<Mapping> mappings, List<Word> signalPhraseWords) {
        return classifyCustom(mappings, WordsUtils.wordsToString(signalPhraseWords));
    }

    public Optional<Relation> classifyDefault(String signalPhrase) {
        return classify(GENERAL_MAPPINGS, signalPhrase);
    }

    public Optional<Relation> classifyDefault(List<Word> signalPhraseWords) {
        return classifyDefault(WordsUtils.wordsToString(signalPhraseWords));
    }

    public static class Mapping {
        private final Relation relation;

        private final String signalPhrasePattern; // optional
        private final int signalPhrasePatternSize;

        public Mapping(Relation relation, String signalPhrasePattern) {
            this.relation = relation;
            this.signalPhrasePattern = "^(?i:" + signalPhrasePattern.replaceAll("\\.\\.\\.", "((?<=^)(.*\\\\W)?|\\\\W|\\\\W.*\\\\W|(\\\\W.*)?(?=\\$))") + ")$";
            this.signalPhrasePatternSize = signalPhrasePattern.length();
        }

        boolean check(String signalPhrase) {
            return signalPhrase.matches(signalPhrasePattern);
        }

        public Relation getRelation() {
            return relation;
        }

        int getSignalPhrasePatternSize() {
            return signalPhrasePatternSize;
        }
    }
}
