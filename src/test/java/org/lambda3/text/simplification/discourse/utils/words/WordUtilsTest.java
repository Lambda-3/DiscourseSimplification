/*
 * ==========================License-Start=============================
 * DiscourseSimplification : WordUtilsTest
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

package org.lambda3.text.simplification.discourse.utils.words;

import edu.stanford.nlp.ling.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
class WordUtilsTest {

    @Test
    void wordsToProperSentence() throws Exception {
        List<Word> words = Arrays.asList(
                new Word("."),
                new Word("."),
                new Word("hello"),
                new Word(","),
                new Word(","),
                new Word("this"),
                new Word("is"),
                new Word("a"),
                new Word("test"),
                new Word("."),
                new Word(".")
        );

        String sentence = WordsUtils.wordsToProperSentenceString(words);
        Assertions.assertEquals("Hello , this is a test .", sentence);
    }
}