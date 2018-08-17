/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SentencePreprocessorTest
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
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SentencePreprocessorTest {
    private final Config config = ConfigFactory.load().getConfig("discourse-simplification");
    private final SentencePreprocessor preprocessor = new SentencePreprocessor(config);

    @Test
    void preprocessSentence() {
        preprocessor.setRemoveBrackets(true);

        String sentence = "This is a test (in brackets) and [the last (one)].";
        String sentence2 = "This is -LRB- a second test -RRB-.";

        String psentence = preprocessor.preprocessSentence(sentence);
        Assertions.assertEquals("This is a test and .", psentence);

        String psentence2 = preprocessor.preprocessSentence(sentence2);
        Assertions.assertEquals("This is .", psentence2);
    }
}
