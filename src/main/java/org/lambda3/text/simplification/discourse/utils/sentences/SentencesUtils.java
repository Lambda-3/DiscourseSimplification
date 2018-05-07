/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SentencesUtils
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

package org.lambda3.text.simplification.discourse.utils.sentences;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SentencesUtils {

    private static List<String> splitIntoSentences(Reader reader) {
        List<String> res = new ArrayList<>();

        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List<HasWord> sentence : dp) {
            res.add(SentenceUtils.listToString(sentence));
        }

        return res;
    }

    public static List<String> splitIntoSentences(String text) {
        return splitIntoSentences(new StringReader(text));
    }

    public static List<String> splitIntoSentencesFromFile(File file, boolean byLines) throws IOException {
        if (byLines) {
            List<String> res = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    res.add(line);
                }
            }

            return res;
        } else {
            return splitIntoSentences(new BufferedReader(new FileReader(file)));
        }
    }
}
