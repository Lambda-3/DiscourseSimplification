/*
 * ==========================License-Start=============================
 * DiscourseSimplification : DiscourseSimplifierTest
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
import org.lambda3.text.simplification.discourse.model.OutSentence;
import org.lambda3.text.simplification.discourse.model.SimplificationContent;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 */
class DiscourseSimplifierTest {
    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private DiscourseSimplifier simplifier = new DiscourseSimplifier();

    @Test
    void processSingleSentence() {
        String text = "Peter went to Paris because he likes the city.";
        SimplificationContent c = simplifier.doDiscourseSimplification(text, ProcessingType.WHOLE);

        Assertions.assertEquals(1, c.getSentences().size());
        OutSentence sent = c.getSentences().get(0);

        Assertions.assertEquals(2, sent.getElements().size());
    }

    @Test
    void serializationTest() throws IOException {
        String text = "After graduating from Columbia University in 1983, Barack Obama worked as a community organizer in Chicago.";
        SimplificationContent c = simplifier.doDiscourseSimplification(text, ProcessingType.WHOLE);

        final String filename = "tmp-w8weg3q493ewqieh.json";

        log.info("SAVE TO FILE...");
        c.serializeToJSON(new File(filename));

        log.info("LOAD FROM FILE...");
        SimplificationContent loaded = SimplificationContent.deserializeFromJSON(new File(filename), SimplificationContent.class);

        log.info(loaded.prettyPrintJSON());
        log.info("---------------------------------");
        log.info(loaded.defaultFormat(false));

        log.info("DELETE FILE...");
        File file = new File(filename);
        file.delete();
    }

}