/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ParseTreeParser
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

package org.lambda3.text.simplification.discourse.utils.parseTree;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;

import java.io.StringReader;
import java.util.List;

/**
 *
 */
public class ParseTreeParser {

    private static final TokenizerFactory<CoreLabel> TOKENIZER_FACTORY = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    private static final LexicalizedParser LEX_PARSER = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

    static {
        LEX_PARSER.setOptionFlags("-outputFormat", "penn,typedDependenciesCollapsed", "-retainTmpSubcategories");
    }

    public static Tree parse(String text) throws ParseTreeException {
        List<CoreLabel> rawWords = TOKENIZER_FACTORY.getTokenizer(new StringReader(text)).tokenize();
        Tree bestParse = LEX_PARSER.parseTree(rawWords);
        if (bestParse == null) {
            throw new ParseTreeException(text);
        }

        return bestParse;
    }
}
