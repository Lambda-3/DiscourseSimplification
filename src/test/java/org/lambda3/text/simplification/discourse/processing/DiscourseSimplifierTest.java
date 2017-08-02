/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ProcessorTest
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

package org.lambda3.text.simplification.discourse.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lambda3.text.simplification.discourse.runner.model.OutSentence;
import org.lambda3.text.simplification.discourse.runner.model.SimplificationContent;

/**
 *
 */
class DiscourseSimplifierTest {

    @Test
    void processSingleSentence() {
        DiscourseSimplifier p = new DiscourseSimplifier();

        String text = "Peter went to Paris because he likes the city.";
        SimplificationContent c = p.doDiscourseSimplification(text, ProcessingType.WHOLE);

        Assertions.assertEquals(1, c.getSentences().size());
        OutSentence sent = c.getSentences().get(0);

        Assertions.assertEquals(2, sent.getElements().size());
    }

}