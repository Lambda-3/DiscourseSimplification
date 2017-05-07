/*
 * ==========================License-Start=============================
 * DiscourseSimplification : App
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

import org.lambda3.text.simplification.discourse.processing.OutSentence;
import org.lambda3.text.simplification.discourse.processing.Processor;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final Processor PROCESSOR = new Processor();

    public static void main(String[] args) throws IOException {

        List<OutSentence> sentences = PROCESSOR.process(new File("input.txt"), Processor.ProcessingType.WHOLE);
//        List<OutSentence> sentences = PROCESSOR.process("The text.", Processor.ProcessingType.WHOLE);
    }
}
