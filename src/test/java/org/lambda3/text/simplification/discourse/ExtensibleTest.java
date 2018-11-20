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

import org.lambda3.text.simplification.discourse.model.Extensible;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class ExtensibleTest {

    public Extensible newExtensible() {
        return new Extensible() {
        };
    }

    @Test
    public void testSingleExtension() {
        Extensible e = newExtensible();
        String test = "Joaquim do Amor Divino Rabelo";
        e.addExtension(test);
        String out = e.getExtension(String.class);

        Assert.assertEquals(test, out);
    }

    @Test
    public void testListExtension() {
        Extensible e = newExtensible();

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
}
