/*
 * ==========================License-Start=============================
 * DiscourseSimplification : RefCoordinationExtraction
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

package org.lambda3.text.simplification.discourse.tree.extraction.model;

import edu.stanford.nlp.ling.Word;
import org.lambda3.text.simplification.discourse.tree.Relation;
import org.lambda3.text.simplification.discourse.tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.tree.model.Coordination;
import org.lambda3.text.simplification.discourse.tree.model.DiscourseTree;
import org.lambda3.text.simplification.discourse.tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class RefCoordinationExtraction extends Extraction {
    private final String extractionRule;
    private final Relation relation;
    private final String signalPhrase; // optional
    private final String rightCoordination;
    private final Leaf.Type rightCoordinationType;

    // binary
    public RefCoordinationExtraction(String extractionRule, Relation relation, List<Word> signalPhraseWords, List<Word> rightCoordinationWords, Leaf.Type rightCoordinationType) {
        this.extractionRule = extractionRule;
        this.relation = relation;
        this.signalPhrase = (signalPhraseWords != null) ? WordsUtils.wordsToString(signalPhraseWords) : null;
        this.rightCoordination = WordsUtils.wordsToProperSentenceString(rightCoordinationWords);
        this.rightCoordinationType = rightCoordinationType;
    }

    public Optional<DiscourseTree> convert(Leaf currChild) {

        // find previous node to use as a reference
        Optional<DiscourseTree> prevNode = currChild.getPreviousNode();
        if ((prevNode.isPresent()) && (prevNode.get().usableAsReference())) {

            // use prev node as a reference
            prevNode.get().useAsReference();

            Coordination res = new Coordination(
                    extractionRule,
                    relation,
                    signalPhrase,
                    Collections.emptyList()
            );
            res.addCoordination(prevNode.get()); // set prev node as a reference
            res.addCoordination(new Leaf(rightCoordinationType, extractionRule, rightCoordination));

            return Optional.of(res);

        }

        return Optional.empty();
    }

}
