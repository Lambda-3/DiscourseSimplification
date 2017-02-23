/*
 * ==========================License-Start=============================
 * DiscourseSimplification : NERStringParser
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

package org.lambda3.text.simplification.discourse.utils.ner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.ner.tner.TNERString;
import org.lambda3.text.simplification.discourse.utils.ner.tner.TNERToken;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NERStringParser {

    private static final AbstractSequenceClassifier NER_CLASSIFIER = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");

    public static NERString parse(String text) {
        List<NERToken> tokens = new ArrayList<>();

        String nerString = NER_CLASSIFIER.classifyToString(text);
        String[] nerTokens = nerString.split(" ");

        int idx = 0;
        for (String nerToken : nerTokens) {
            int sep_idx = nerToken.lastIndexOf("/");

            // create text
            String txt = nerToken.substring(0, sep_idx);
            String category = nerToken.substring(sep_idx + 1);
            NERToken token = new NERToken(idx, txt, category);
            tokens.add(token);

            ++idx;
        }

        return new NERString(tokens);
    }

    public static TNERString parse(Tree parseTree) throws NERStringParseException {
        List<TNERToken> tokens = new ArrayList<>();

        List<Integer> parseTreeLeafNumbers = ParseTreeExtractionUtils.getLeafNumbers(parseTree, parseTree);
        String nerString = NER_CLASSIFIER.classifyToString(WordsUtils.wordsToString(parseTree.yieldWords()));
        String[] nerTokens = nerString.split(" ");

        if (parseTreeLeafNumbers.size() != nerTokens.length) {
            throw new NERStringParseException("Could not map NER string to parseTree");
        }

        int idx = 0;
        for (String nerToken : nerTokens) {
            int sep_idx = nerToken.lastIndexOf("/");

            // create token
            String text = nerToken.substring(0, sep_idx);
            String category = nerToken.substring(sep_idx + 1);
            TNERToken token = new TNERToken(idx, text, category, parseTree.getNodeNumber(parseTreeLeafNumbers.get(idx)));
            tokens.add(token);

            ++idx;
        }

        return new TNERString(tokens, parseTree);
    }
}
