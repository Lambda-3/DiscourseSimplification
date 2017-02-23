/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SubordinationEnablementExtractor
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
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.tree.extraction.model.SubordinationExtraction;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
abstract class SubordinationEnablementExtractor extends ExtractionRule {

    SubordinationEnablementExtractor(String pattern) {
        super(pattern);
    }

    private List<Word> getSuperordinateConstituentWords(Tree parseTree, Tree node) {
        List<Word> constituentWords = new ArrayList<>();
        constituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(parseTree, node, false));
        constituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(parseTree, node, false));
        return constituentWords;
    }

    private List<Word> getSubordinateConstituentWords(Tree node) {
        return ParseTreeExtractionUtils.getContainingWords(node);
    }

    @Override
    public Optional<Extraction> extract(Tree parseTree) {

        TregexMatcher matcher = pattern.matcher(parseTree);

        if (matcher.findAt(parseTree)) {

            List<Word> superordinateConstituentWords;
            List<Word> subordinateConstituentWords;

            superordinateConstituentWords = getSuperordinateConstituentWords(parseTree, matcher.getNode("s"));
            subordinateConstituentWords = getSubordinateConstituentWords(matcher.getNode("s"));

            // result
            Optional<Relation> relation = Optional.empty();
            Leaf.Type leftConstituentType = Leaf.Type.DEFAULT;
            Leaf.Type rightConstituentType = Leaf.Type.DEFAULT;

            // enablement
            if (isInfinitival(matcher.getNode("s"))) {
                relation = Optional.of(Relation.ENABLEMENT);
                subordinateConstituentWords = rephraseEnablement(matcher.getNode("s"), matcher.getNode("vp"));
                rightConstituentType = Leaf.Type.SENT_SIM_CONTEXT;
            }

            if (relation.isPresent()) {
                return Optional.of(
                        new SubordinationExtraction(
                                getClass().getSimpleName(),
                                relation.get(),
                                null,
                                superordinateConstituentWords, // the superordinate constituent
                                subordinateConstituentWords, // the subordinate constituent
                                true,
                                leftConstituentType,
                                rightConstituentType)
                );
            }
        }

        return Optional.empty();
    }
}
