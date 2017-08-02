/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Test
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

import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeVisualizer;
import org.lambda3.text.simplification.discourse.utils.sentences.SentencesUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 *
 */
public class Test {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void printParseTree(File file) throws FileNotFoundException {
        List<String> sentences = SentencesUtils.splitIntoSentencesFromFile(file);

        printParseTree(sentences);
    }

    public static void printParseTree(String text) {
        List<String> sentences = SentencesUtils.splitIntoSentences(text);

        printParseTree(sentences);
    }

    public static void printParseTree(List<String> sentences) {
        for (String sentence : sentences) {
            LOGGER.info("Generate parse discourse_tree for sentence:\n'{}'", sentence);
            try {
                Tree parseTree = ParseTreeParser.parse(sentence);
                LOGGER.info(ParseTreeVisualizer.prettyPrint(parseTree));
            } catch (ParseTreeException e) {
                LOGGER.error("Failed to generate parse discourse_tree");
            }
        }
    }

}
