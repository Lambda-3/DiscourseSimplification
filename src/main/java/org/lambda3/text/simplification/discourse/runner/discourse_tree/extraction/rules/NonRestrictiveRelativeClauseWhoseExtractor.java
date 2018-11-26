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
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class NonRestrictiveRelativeClauseWhoseExtractor extends ExtractionRule {
	
	@Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {
		//TregexPattern p = TregexPattern.compile("ROOT <<: (S << (NP < (NP=np $+ (/,/ $+ (SBAR=sbar <, (/WP\\$/ $+ S=s))))))");
        TregexPattern p = TregexPattern.compile("ROOT <<: (S << (NP=head < (NP=np $+ (/,/=comma $+ (SBAR=sbar <, (WHNP $+ S=s & <, (/WP\\$/ $+ /.*/=nn)) & ?$+ /,/=comma2)))))");
        TregexMatcher matcher = p.matcher(leaf.getParseTree());

        while (matcher.findAt(leaf.getParseTree())) {
        
        	
            // the left, superordinate constituent
            List<Word> leftConstituentWords = new ArrayList<>();
            leftConstituentWords.addAll(ParseTreeExtractionUtils.getPrecedingWords(leaf.getParseTree(), matcher.getNode("comma"), false));
            
            if (matcher.getNode("comma2") != null) {
            	leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("comma2"), false));
            } else {
            	leftConstituentWords.addAll(ParseTreeExtractionUtils.getFollowingWords(leaf.getParseTree(), matcher.getNode("sbar"), false));
            }
            Leaf leftConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(leftConstituentWords));

            // the right, subordinate constituent
            List<Word> rightConstituentWords = new ArrayList<>();
            
            if (matcher.getNode("s").firstChild().nodeString().split(" ")[0].equals("NP")) {
            	rightConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s")));
            	rightConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np")));
                rightConstituentWords.add(new Word("'s"));
                rightConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("nn")));
            } else {
            	rightConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("np")));
                rightConstituentWords.add(new Word("'s"));
                rightConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("nn")));
            	rightConstituentWords.addAll(ParseTreeExtractionUtils.getContainingWords(matcher.getNode("s")));
            }
            Leaf rightConstituent = new Leaf(getClass().getSimpleName(), WordsUtils.wordsToProperSentenceString(rightConstituentWords));

            // relation
            //List<Word> cuePhraseWords = ParseTreeExtractionUtils.getPrecedingWords(matcher.getNode("sbar"), matcher.getNode("s"), false);
            //Relation relation = classifer.classifySubordinating(cuePhraseWords).orElse(Relation.UNKNOWN_SUBORDINATION);

            Relation relation = Relation.DESCRIBING_DEFINITION;
            
            //TODO not always doDiscourseExtraction?
            Extraction res = new Extraction(
                getClass().getSimpleName(),
                false,
                null,//cuePhraseWords,
                relation,
                true,
                Arrays.asList(leftConstituent, rightConstituent)
            );

            return Optional.of(res);
        }

        return Optional.empty();
    }

}
