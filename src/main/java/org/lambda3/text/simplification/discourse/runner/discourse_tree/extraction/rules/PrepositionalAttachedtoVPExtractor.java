/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SubordinationPostExtractor
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.rules;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParseException;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParser;
import org.lambda3.text.simplification.discourse.utils.ner.tner.TNERString;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.pos.POSTagger;
import org.lambda3.text.simplification.discourse.utils.pos.POSToken;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class PrepositionalAttachedtoVPExtractor extends ExtractionRule {

	@Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {
		
		TregexPattern p = TregexPattern.compile("ROOT <<: (S <+(S|VP) (VP < (PP=prep $- NP|PP)) & < VP=vp)");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {

        	// rephrase
            List<Word> leftConstituentWords = rephraseEnablement(matcher.getNode("prep"), matcher.getNode("vp"));
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));
            //leftConstituent.dontAllowSplit();
            leftConstituent.setToSimpleContext(true);

            // the right, superordinate constituent
            List<Word> rightConstituentWords = new ArrayList<>();
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("prep"), false));
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("prep"), false));
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));
            System.out.println(rightConstituent);

            // relation
            Tree t = leftConstituent.getParseTree();
            Relation relation = Relation.UNKNOWN_SUBORDINATION;
            List<POSToken> pos = POSTagger.parse(leftConstituent.getText());
    		try {
				TNERString ner = NERStringParser.parse(t);
				for (int i = 0; i < ner.getTokens().size(); i++) {
					if (ner.getTokens().get(i).getCategory().equals("LOCATION")) {
						relation = Relation.SPATIAL;
					} else if (ner.getTokens().get(i).getCategory().equals("DATE")) {
						relation = Relation.TEMPORAL;
					}
				}
			} catch (NERStringParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            Extraction res = new Extraction(
                getClass().getSimpleName(),
                false,
                null,
                relation,
                false,
                Arrays.asList(leftConstituent, rightConstituent)
            );

            return Optional.of(res);
        }

        return Optional.empty();
    }
}
