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
public class NonRestrictiveAppositionExtractor extends ExtractionRule {

	@Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {
		//"ROOT <<: (S < VP=vp & << (NP=np1 !< NNP|NNPS & $+ (/,/=comma1 $+ (NP=np2 !$ CC & < NNP|NNPS=entity))))"
        TregexPattern p = TregexPattern.compile("ROOT <<: (S < VP=vp & << (NP=np1 $+ (/,/=comma $+ (NP=np2 !$ CC & ?$+ /,/=comma2))))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {
        	
        	
        	List<Word> leftConstituentWords = new ArrayList<>();
        	List<Word> words = new ArrayList<>();
        	words.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np2")));
            Leaf l = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(words));
            List<POSToken> pos = POSTagger.parse(l.getText());
            //boolean entity = false;
            //for (int i = 0; i < pos.size(); i++) {
            	//if (pos.get(i).getPos().equals("NNP") || pos.get(i).getPos().equals("NNPS")) {
            		//entity = true;
            		//break;
            	//}
            //}
            
            Tree t1 = matcher.getNode("np1");
            Tree t2 = matcher.getNode("np2");
            boolean entity1 = false;
			boolean entity2 = false;
            
            try {
				TNERString ner1 = NERStringParser.parse(t1);
				TNERString ner2 = NERStringParser.parse(t2);
				
				boolean loc1 = false;
				boolean loc2 = false;
				
				for (int i = 0; i < ner1.getWords().size(); i++) {
					if (ner1.getTokens().get(i).getCategory().equals("PERSON") || ner1.getTokens().get(i).getCategory().equals("ORGANIZATION")) {
						entity1 = true;
						break;
					}
				    if (ner1.getTokens().get(i).getCategory().equals("LOCATION")) {
				    	loc1 = true;
				    	break;
				    }
				}
				for (int i = 0; i < ner2.getWords().size(); i++) {
					if (ner2.getTokens().get(i).getCategory().equals("PERSON") || ner2.getTokens().get(i).getCategory().equals("ORGANIZATION")) {
						entity2 = true;
						break;
					}
				    if (ner2.getTokens().get(i).getCategory().equals("LOCATION")) {
				    	loc2 = true;
				    	break;
				    }
				}
				
				if (loc1 && loc2) {
					continue;
				}
			} catch (NERStringParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            if (entity1) {
            	leftConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np1")));
            	leftConstituentWords.addAll(rephraseAppositionNonRes(matcher.getNode("vp"), matcher.getNode("np1"), matcher.getNode("np2")));
            } else if (entity2) {
            	leftConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np2")));
            	leftConstituentWords.addAll(rephraseAppositionNonRes(matcher.getNode("vp"), matcher.getNode("np2"), matcher.getNode("np1")));
            } else {
            	leftConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np1")));
            	leftConstituentWords.addAll(rephraseAppositionNonRes(matcher.getNode("vp"), matcher.getNode("np1"), matcher.getNode("np2")));
            }
            
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));

            // the right, superordinate constituent
            List<Word> rightConstituentWords = new ArrayList<>();
            rightConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("comma"), false));
            if (matcher.getNode("comma2") != null) {
            	rightConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("comma2"), false));
            } else {
            	rightConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("np2"), false));
            }
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));

            // relation
            Relation relation = Relation.ELABORATION; //TODO

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
