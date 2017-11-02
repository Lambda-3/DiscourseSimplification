/*
 * ==========================License-Start=============================
 * DiscourseSimplification : CuePhraseClassifier
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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import edu.stanford.nlp.ling.Word;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.*;

/**
 *
 */
public class CuePhraseClassifier {
    public static class Mapping {
        private final Relation relation;
        private final String cuePhrasePattern;
        private final int cuePhrasePatternSize;

        public Mapping(Relation relation, String cuePhrasePattern, boolean contain) {
            this.relation = relation;
            this.cuePhrasePattern = "^(?i:" + ((contain)? ".*?" : "") + "(?<!\\w)" + cuePhrasePattern + "(?!\\w)" + ((contain)? ".*?" : "") + ")$";
            this.cuePhrasePatternSize = cuePhrasePattern.length();
        }

        boolean check(String cuePhrase) {
            return cuePhrase.matches(cuePhrasePattern);
        }

        public Relation getRelation() {
            return relation;
        }

        public String getCuePhrasePattern() {
            return cuePhrasePattern;
        }

        public int getCuePhrasePatternSize() {
            return cuePhrasePatternSize;
        }
    }


    private final List<String> attributionVerbs;
    private final List<Mapping> coordinatingPhrases;
    private final List<Mapping> subordinatingPhrases;
    private final List<Mapping> adverbialPhrases;

    public CuePhraseClassifier(Config config) {

        // load attribution verbs
        attributionVerbs = new ArrayList<>();
        for (String verb : config.getStringList("attribution_verbs")) {
            attributionVerbs.add(verb);
        }

        // load patterns
        coordinatingPhrases = new ArrayList<>();
        boolean coordinatingContainMatching = config.getString("cue_phrases.coordinating_phrases.matching").toLowerCase().equals("contained");
        for (Map.Entry<String,ConfigValue> entry : config.getObject("cue_phrases.coordinating_phrases.phrases").entrySet()) {
            Relation relation = Relation.valueOf(entry.getValue().unwrapped().toString());
            String pattern = entry.getKey();
            coordinatingPhrases.add(new Mapping(relation, pattern, coordinatingContainMatching));
        }

        subordinatingPhrases = new ArrayList<>();
        boolean subordinatingContainMatching = config.getString("cue_phrases.subordinating_phrases.matching").toLowerCase().equals("contained");
        for (Map.Entry<String,ConfigValue> entry : config.getObject("cue_phrases.subordinating_phrases.phrases").entrySet()) {
            Relation relation = Relation.valueOf(entry.getValue().unwrapped().toString());
            String pattern = entry.getKey();
            subordinatingPhrases.add(new Mapping(relation, pattern, subordinatingContainMatching));
        }

        adverbialPhrases = new ArrayList<>();
        boolean adverbialContainMatching = config.getString("cue_phrases.adverbial_phrases.matching").toLowerCase().equals("contained");
        for (Map.Entry<String,ConfigValue> entry : config.getObject("cue_phrases.adverbial_phrases.phrases").entrySet()) {
            Relation relation = Relation.valueOf(entry.getValue().unwrapped().toString());
            String pattern = entry.getKey();
            adverbialPhrases.add(new Mapping(relation, pattern, adverbialContainMatching));
        }
    }

    private Optional<Relation> classify(List<Mapping> mappings, String cuePhrase) {
        if (cuePhrase.length() == 0) {
            return Optional.empty();
        }

        Optional<Mapping> bestMapping = Optional.empty();
        for (Mapping mapping : mappings) {
            if (mapping.check(cuePhrase)) {
                if (!bestMapping.isPresent()) {
                    bestMapping = Optional.of(mapping);
                } else if (mapping.getCuePhrasePatternSize() >= bestMapping.get().getCuePhrasePatternSize()) {
                    bestMapping = Optional.of(mapping);
                }
            }
        }

        return bestMapping.map(Mapping::getRelation);
    }


    public Optional<Relation> classifyCustom(List<Mapping> mappings, String cuePhrase) {
        return classify(mappings, cuePhrase);
    }

    public Optional<Relation> classifyCustom(List<Mapping> mappings, List<Word> cuePhraseWords) {
        return classifyCustom(mappings, WordsUtils.wordsToString(cuePhraseWords));
    }

    public Optional<Relation> classifyCoordinating(String cuePhrase) {
        return classify(coordinatingPhrases, cuePhrase);
    }

    public Optional<Relation> classifyCoordinating(List<Word> cuePhraseWords) {
        return classifyCoordinating(WordsUtils.wordsToString(cuePhraseWords));
    }

    public Optional<Relation> classifySubordinating(String cuePhrase) {
        return classify(subordinatingPhrases, cuePhrase);
    }

    public Optional<Relation> classifySubordinating(List<Word> cuePhraseWords) {
        return classifySubordinating(WordsUtils.wordsToString(cuePhraseWords));
    }

    public Optional<Relation> classifyAdverbial(String cuePhrase) {
        return classify(adverbialPhrases, cuePhrase);
    }

    public Optional<Relation> classifyAdverbial(List<Word> cuePhraseWords) {
        return classifyAdverbial(WordsUtils.wordsToString(cuePhraseWords));
    }

    public boolean checkAttribution(List<Word> cuePhraseWords) {
        for (Word word : cuePhraseWords) {
            if (attributionVerbs.contains(WordsUtils.lemmatize(word).value().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAttribution(Word cuePhraseWord) {
        return checkAttribution(Arrays.asList(cuePhraseWord));
    }
}
