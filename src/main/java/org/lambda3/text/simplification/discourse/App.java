/*
 * ==========================License-Start=============================
 * DiscourseSimplification : App
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

package org.lambda3.text.simplification.discourse;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.lambda3.text.simplification.discourse.processing.OutSentence;
import org.lambda3.text.simplification.discourse.processing.Processor;
import org.lambda3.text.simplification.discourse.tree.extraction.utils.ListNPSplitter;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeVisualizer;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Hello world!
 */
public class App {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final Processor PROCESSOR = new Processor();

    public static void main(String[] args) throws IOException {

//        String text = "In the 1950s and the 1960s , Paris became one front of the Algerian War for independence ; in August 1961 , the pro-independence FLN targeted and killed 11 Paris policemen , leading to the imposition of a curfew on Muslims of Algeria -LRB- who , at that time , were French citizens -RRB- .";
        String text = "To leave monuments to his reign , he built the Collège des Quatre-Nations , Place Vendôme , Place des Victoires , and began Les Invalides .";
//        String text = "Paris is the hub of the national road network and is surrounded by three orbital roads : the Périphérique , the A86 motorway , and the Francilienne motorway .";
//        String text = "Other singers—of similar style—include Maurice Chevalier Charles Aznavour; Yves Montand; and Charles Trenet.";

//        List<OutSentence> sentences = PROCESSOR.process(new File("crucial_input.txt"), Processor.ProcessingType.WHOLE);
        List<OutSentence> sentences = PROCESSOR.process(text, Processor.ProcessingType.WHOLE);

//        try {
//            Tree t = ParseTreeParser.parse(text);
//            LOGGER.info(ParseTreeVisualizer.prettyPrint(t));
//
//            TregexPattern p = TregexPattern.compile("NP=np !>> NP");
//            TregexMatcher matcher = p.matcher(t);
//            while (matcher.find()) {
//                LOGGER.info("----------------------------------------------------");
//                LOGGER.info(ParseTreeVisualizer.prettyPrint(matcher.getNode("np")));
//                Optional<ListNPSplitter.Result> r = ListNPSplitter.splitList(t, matcher.getNode("np"));
//                if (r.isPresent()) {
//                    LOGGER.info("MATCHED RULE");
//                    if (r.get().getIntroductionWords().isPresent()) {
//                        LOGGER.info("Introduction: '{}'", r.get().getIntroductionWords().get());
//                    }
//                    for (List<Word> words : r.get().getElementsWords()) {
//                        LOGGER.info("{}", words);
//                    }
//                }
//            }
//        } catch (ParseTreeException e) {
//            e.printStackTrace();
//        }



    }
}
