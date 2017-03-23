/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtractionRule
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

package org.lambda3.text.simplification.discourse.tree.extraction.rules;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.classification.SignalPhraseClassifier;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.tree.extraction.model.SubordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class Subordination2IntraSententialAttributionExtractor extends ExtractionRule {

    @Override
    public Optional<Extraction> extract(Tree parseTree) {
        TregexPattern p = TregexPattern.compile("ROOT <<: (S < (NP $.. (VP <+(VP) (SBAR=sbar <<, /that/ < (S=s)))))");
        TregexMatcher matcher = p.matcher(parseTree);

        while (matcher.findAt(parseTree)) {

            // the left, !subordinate! constituent
            List<Word> leftConstituentWords = new ArrayList<>();
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(parseTree, matcher.getNode("sbar"), false));
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("sbar"), false));
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));

            // rephrase
            leftConstituent.setProperSentence(false);
            List<Word> rephrasedWords = rephraseIntraSententialAttribution(leftConstituentWords);
            leftConstituent.setRephrasedText(WordsUtils.wordsToProperSentenceString(rephrasedWords));
            leftConstituent.dontAllowSplit();

            // the right, !superordinate! constituent
            List<Word> rightConstituentWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s"));
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));

            // relation
            Relation relation = Relation.INTRA_SENTENTIAL_ATTRIBUTION;

            Extraction res = new SubordinationExtraction(
                    getClass().getSimpleName(),
                    relation,
                    null,
                    leftConstituent, // the subordinate constituent
                    rightConstituent, // the superordinate constituent
                    false
            );

            return Optional.of(res);
        }

        return Optional.empty();
    }


//    // custom mappings
//    private static final List<SignalPhraseClassifier.Mapping> INTRA_SENTENTIAL_ATTRIBUTION_MAPPINGS = Arrays.asList(
//
//            // INTRA_SENTENTIAL_ATTRIBUTION
//            new SignalPhraseClassifier.Mapping(Relation.INTRA_SENTENTIAL_ATTRIBUTION, ""),
//            new SignalPhraseClassifier.Mapping(Relation.INTRA_SENTENTIAL_ATTRIBUTION, "...that...")
//    );

//    @Override
//    public Optional<Extraction> extract(Tree parseTree) {
//        //TODO enablement s not recognized because of NP $.. VP
//        TregexPattern p = TregexPattern.compile("ROOT <<: (S < (NP $.. (VP=vp << (SBAR=sbar < (S=s < (NP $.. VP))))))");
//        TregexMatcher matcher = p.matcher(parseTree);
//
//        while (matcher.findAt(parseTree)) {
//            List<Word> signalPhraseWords = ParseTreeExtractionUtils.getPrecedingWords(matcher.getNode("sbar"), matcher.getNode("s"), false);
//
//            // the left, (usually) superordinate constituent
//            List<Word> leftConstituentWords = new ArrayList<Word>();
//            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(parseTree, matcher.getNode("sbar"), false));
//            leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(parseTree, matcher.getNode("sbar"), false));
//            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));
//
//            // the right, (usually) subordinate constituent
//            List<Word> rightConstituentWords = ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s"));
//            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));
//
//            // result
//            Optional<Relation> relation = Optional.empty();
//            boolean superordinationIsLeft = true;
//
//            // intra sentential attribution
//            if (!relation.isPresent()) {
//                relation = CLASSIFIER.classifyCustom(INTRA_SENTENTIAL_ATTRIBUTION_MAPPINGS, signalPhraseWords);
//                if (relation.isPresent()) {
//
//                    // rephrase
//                    List<Word> rephrasedWords = rephraseIntraSententialAttribution(leftConstituentWords);
//                    leftConstituent.setProperSentence(false);
//                    leftConstituent.setRephrasedText(WordsUtils.wordsToProperSentenceString(rephrasedWords));
//
//                    leftConstituent.dontAllowSplit();
//
//                    // swap superordinate with subordinate assignment
//                    superordinationIsLeft = !superordinationIsLeft;
//                }
//            }
//
//            // enablement
//            if (!relation.isPresent()) {
//                if (isInfinitivalClause(matcher.getNode("s"))) {
//                    relation = Optional.of(Relation.ENABLEMENT);
//
//                    // rephrase
//                    List<Word> rephrasedWords = rephraseEnablement(matcher.getNode("s"), matcher.getNode("vp"));
//                    rightConstituent.setProperSentence(false);
//                    rightConstituent.setRephrasedText(WordsUtils.wordsToProperSentenceString(rephrasedWords));
//
//                    rightConstituent.dontAllowSplit();
//                }
//            }
//
//            // general
//            if (!relation.isPresent()) {
//                relation = CLASSIFIER.classifyGeneral(signalPhraseWords);
//            }
//
//            //TODO not always extract?
//            Extraction res = new SubordinationExtraction(
//                    getClass().getSimpleName(),
//                    (relation.isPresent())? relation.get() : Relation.UNKNOWN_SUBORDINATION,
//                    signalPhraseWords,
//                    leftConstituent, // the superordinate constituent
//                    rightConstituent, // the subordinate constituent
//                    superordinationIsLeft
//            );
//
//            return Optional.of(res);
//        }
//
//        return Optional.empty();
//    }

}
