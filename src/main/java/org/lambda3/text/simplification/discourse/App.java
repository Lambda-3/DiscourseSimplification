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

import org.lambda3.text.simplification.discourse.processing.DiscourseSimplifier;
import org.lambda3.text.simplification.discourse.processing.ProcessingType;
import org.lambda3.text.simplification.discourse.model.SimplificationContent;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final DiscourseSimplifier DISCOURSE_SIMPLIFIER = new DiscourseSimplifier();

    private static void saveLines(File file, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(lines.stream().collect(Collectors.joining("\n")));

            // no need to close it.
            //bw.close()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        SimplificationContent content = DISCOURSE_SIMPLIFIER.doDiscourseSimplification(new File("input.txt"), ProcessingType.SEPARATE, true);
        content.serializeToJSON(new File("output.json"));
        saveLines(new File("output_default.txt"), Arrays.asList(content.defaultFormat(false)));
        saveLines(new File("output_flat.txt"), Arrays.asList(content.flatFormat(false)));
        LOGGER.info("done");
    }
}
