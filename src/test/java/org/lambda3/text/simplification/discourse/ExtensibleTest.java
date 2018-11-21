/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtensibleTest
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

package org.lambda3.text.simplification.discourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.model.Extensible;
import org.lambda3.text.simplification.discourse.model.LinkedContext;
import org.lambda3.text.simplification.discourse.model.Sentence;
import org.lambda3.text.simplification.discourse.model.serializer.TreeDeserializer;
import org.lambda3.text.simplification.discourse.model.serializer.TreeSerializer;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.RelationType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExtensibleTest {

    static class SimpleExtensible extends Extensible {
        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @Test
    public void testSingleExtension() {
        Extensible e = new SimpleExtensible();
        String test = "Joaquim do Amor Divino Rabelo";
        e.addExtension(test);
        String out = e.getExtension(String.class);

        Assert.assertEquals(test, out);
    }

    @Test
    public void testListExtension() {
        Extensible e = new SimpleExtensible();

        String[] strings = new String[]{"Switzerland", "Brazil", "Germany"};
        Arrays.stream(strings).forEach(e::addListExtension);

        List<String> outs = e.getListExtension(String.class);
        Assert.assertEquals(strings.length, outs.size());

        for (int i = 0; i < strings.length; i++) {
            Assert.assertEquals(strings[i], outs.get(i));
        }

        e.addExtension("something");
        Assert.assertEquals(strings.length, e.getListExtension(String.class).size());
    }

    @Test
    public void testDeSerialization() throws IOException {
        Extensible e = new SimpleExtensible();
        Arrays.asList("Switzerland", "Brazil", "Germany").forEach(e::addListExtension);
        e.addExtension(new LinkedContext("bla bla", RelationType.UNKNOWN));
        e.addExtension("key", 7);

        File temp = File.createTempFile("ext-discourse-simplification", ".json");
        System.out.println(temp);
        temp.deleteOnExit();

        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(temp, e);
        Extensible ne = mapper.readValue(temp, SimpleExtensible.class);

        Assert.assertEquals(e, ne);
    }
}
