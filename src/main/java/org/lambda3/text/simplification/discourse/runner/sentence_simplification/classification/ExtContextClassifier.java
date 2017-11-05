/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtContextClassifier
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
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.model.TimeInformation;
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
import java.util.Properties;

public class ExtContextClassifier implements ContextClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(ExtContextClassifier.class);

    private static final Properties PROPS = new Properties();
	private static final AnnotationPipeline PIPELINE = new AnnotationPipeline();
	static {
		PIPELINE.addAnnotator(new TokenizerAnnotator(false));
		PIPELINE.addAnnotator(new TimeAnnotator("sutime", PROPS));
	}

    private final CuePhraseClassifier cuePhraseClassifier;

    public ExtContextClassifier(Config config) {
        this.cuePhraseClassifier = new CuePhraseClassifier(config);
    }

    private boolean checkTemporal(SimpleContext simpleContext) {

        // use first 10 words as cue phrase
        List<Word> cuePhraseWords = ParseTreeExtractionUtils.getContainingWords(simpleContext.getPhrase());
        if (cuePhraseWords.size() > 10) {
            cuePhraseWords = cuePhraseWords.subList(0, 10);
        }
        String cuePhrase = WordsUtils.wordsToString(cuePhraseWords);

        Annotation annotation = new Annotation(cuePhrase);
//            annotation.set(CoreAnnotations.DocDateAnnotation.class, "2013-07-14"); // not yet supported by Graphene
        PIPELINE.annotate(annotation);
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

        // only fetch first occurrence
        if (timexAnnsAll.size() > 0) {
            CoreMap cm = timexAnnsAll.get(0);
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();

            Relation relation = Relation.TEMPORAL;
            switch (temporal.getTimexType()) {
                case TIME:
                    relation = Relation.TEMPORAL_TIME;
                    break;
                case DURATION:
                    relation = Relation.TEMPORAL_DURATION;
                    break;
                case DATE:
                    relation = Relation.TEMPORAL_DATE;
                    break;
                case SET:
                    relation = Relation.TEMPORAL_SET;
                    break;
            }

            simpleContext.setRelation(relation);
            simpleContext.setTimeInformation(new TimeInformation(temporal.getTimexValue()));

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