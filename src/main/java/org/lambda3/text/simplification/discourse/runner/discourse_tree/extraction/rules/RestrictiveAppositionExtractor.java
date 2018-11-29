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

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.pos.POSTagger;
import org.lambda3.text.simplification.discourse.utils.pos.POSToken;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParseException;
import org.lambda3.text.simplification.discourse.utils.ner.NERStringParser;
import org.lambda3.text.simplification.discourse.utils.ner.tner.TNERString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class RestrictiveAppositionExtractor extends ExtractionRule {
	
	@Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {
        
        Tree t = leaf.getParseTree();
        
        try {
			TNERString ner = NERStringParser.parse(t);
			Pattern pattern = Pattern.compile("(((PRP\\$|DT)\\s)*(JJ\\s)*((NN|NNS|NNP|NNPS)\\s))+(((CC|IN)\\s)((PRP\\$|DT)\\s)*(JJ\\s)*((NN|NNS|NNP|NNPS)\\s))*STOP");
			List<POSToken> pos = POSTagger.parse(leaf.getText());
			
			for (int i = 1; i < ner.getWords().size(); i++) {
			    
			    if ((ner.getTokens().get(i).getCategory().equals("PERSON") && !ner.getTokens().get(i-1).getCategory().equals("PERSON"))
			    		|| (ner.getTokens().get(i).getCategory().equals("ORGANIZATION") && !ner.getTokens().get(i-1).getCategory().equals("ORGANIZATION"))
			    		|| (ner.getTokens().get(i).getCategory().equals("LOCATION") && !ner.getTokens().get(i-1).getCategory().equals("LOCATION"))) {
			    	
			    	POSToken po = pos.get(i);
			    	String number =  po.getPos();
			    	
			    	int n = i+1;
			    	String attach = ner.getTokens().get(i).getText();
			    	while (n < ner.getTokens().size() && (ner.getTokens().get(n).getCategory().equals("PERSON") || ner.getTokens().get(n).getCategory().equals("ORGANIZATION") || ner.getTokens().get(n).getCategory().equals("LOCATION"))) {
			    		attach = attach + ' ' + ner.getTokens().get(n).getText();
			    		n++;
			    	}
			    	
			    	String posString = "";
			    	String text = "";
			    	for (int j = 0; j < i; j++) {
			    		//if (!ner.getTokens().get(j).getCategory().equals("PERSON")) {
			    			//if (!ner.getTokens().get(j).getCategory().equals("ORGANIZATION")) {
			    				posString = posString + ' ' + pos.get(j).getPos();
			    				text = text + ' ' + pos.get(j).getText();
			    			//}
			    		//}
			    	}
			    	posString = posString + ' ' + "STOP";
			    	Matcher matcher = pattern.matcher(posString);
			    	
			    	while (matcher.find()) {
			             String[] m = matcher.group().split(" ");
			             String appos = "";
			             String[] a = text.split(" ");
			             for (int k = 0; k < m.length-1; k++) {
			            	 appos = a[a.length-1-k] + ' ' + appos;
			             }
			             
			             TregexPattern p = TregexPattern.compile("ROOT <<: (S < VP=vp)");
			             TregexMatcher matcher_tree = p.matcher(leaf.getParseTree());
			             
			             while (matcher_tree.findAt(leaf.getParseTree())) {
				             String apposition = "";
				             if (!appos.equals("")) {
						    		apposition = attach + rephraseApposition(matcher_tree.getNode("vp"), number) + appos + ".";
				             }
				             String rest = leaf.getText().replaceAll(appos, "");
				             Tree appTree = ParseTreeParser.parse(apposition);
				             Leaf leftConstituent = new Leaf(getClass().getSimpleName(), appTree);
			             
				             Tree restTree = ParseTreeParser.parse(rest);
				             Leaf rightConstituent = new Leaf(getClass().getSimpleName(), restTree);
			             
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
			    	}
			    }
			    
			}
		} catch (NERStringParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return Optional.empty();
    }

}
