/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SentencePreprocessor
 *
 * Copyright © 2018 Lambda³
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

package org.lambda3.text.simplification.discourse.processing;

import com.typesafe.config.Config;
import edu.stanford.nlp.simple.Sentence;

public class SentencePreprocessor {
    private static String ROUND_BRACKET_PATTERN = "\\([^\\(\\)]*?\\)";
    private static String SQUARE_BRACKET_PATTERN = "\\[[^\\[\\]]*?\\]";
    private static String CURLY_BRACKET_PATTERN = "\\{[^\\{\\}]*?\\}";

    private static String ROUND_BRACKET_PATTERN2 = "-LRB-((?!-LRB-|-RRB-).)*?-RRB-";
    private static String SQUARE_BRACKET_PATTERN2 = "-LSB-((?!-LSB-|-RSB-).)*?-RSB-";
    private static String CURLY_BRACKET_PATTERN2 = "-LCB-((?!-LCB-|-RCB-).)*?-RCB-";


    private static String WHITESPACE_PATTERN = "\\s+";

    public boolean removeBrackets;

    public SentencePreprocessor(Config config) {
        this.removeBrackets = config.getBoolean("remove-brackets");
    }

    public void setRemoveBrackets(boolean removeBrackets) {
        this.removeBrackets = removeBrackets;
    }

    public String preprocessSentence(String sentence) {
        String res = sentence;

        if (removeBrackets) {
            res = sentence.replaceAll(ROUND_BRACKET_PATTERN, "")
                    .replaceAll(SQUARE_BRACKET_PATTERN, "")
                    .replaceAll(CURLY_BRACKET_PATTERN, "")
                    .replaceAll(ROUND_BRACKET_PATTERN2, "")
                    .replaceAll(SQUARE_BRACKET_PATTERN2, "")
                    .replaceAll(CURLY_BRACKET_PATTERN2, "");
        }

        res = res.replaceAll(WHITESPACE_PATTERN, " ");
        return res;
    }
}
