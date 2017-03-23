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

import org.junit.jupiter.api.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 */
class ProcessorTest {

    @Test
    void processSingleSentence() {

        //TODO implement test
//        String text = "Bernhard is working on a project for PACE but he also works for MARIO.";
//
//        Core first = new Core("Bernhard is working on a project for PACE .", 0, "Bernhard is working on a project for PACE .");
//        Core second = new Core("he also works .", 0, "He also works for MARIO .");
//
//        first.addDCoreRelation(new CoreRelation(
//                Relation.CONTRAST, second
//        ));
//
//        second.addDCoreRelation(new CoreRelation(
//                Relation.CONTRAST, first
//        ));
//        second.addSContext(new Context(
//                "This is for MARIO .", 0, Relation.UNKNOWN_SENT_SIM
//        ));
//
//        final List<Core> expected = Arrays.asList(first, second);
//
//
//        Processor p = new Processor();
//        final List<Core> actual = p.process(text, Processor.ProcessingType.SEPARATE);
//
//        Assertions.assertIterableEquals(expected, actual);
        throw new NotImplementedException();

    }

}