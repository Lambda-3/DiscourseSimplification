/*
 * ==========================License-Start=============================
 * DiscourseSimplification : TreeSerializer
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

package org.lambda3.text.simplification.discourse.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.stanford.nlp.trees.Tree;

import java.io.IOException;

/**
 *
 */
public class TreeSerializer extends StdSerializer<Tree> {

    public TreeSerializer() {
        this(null);
    }

    protected TreeSerializer(Class<Tree> t) {
        super(t);
    }

    @Override
    public void serialize(Tree value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.pennString().trim().replaceAll("\\s+", " ").replaceAll("[\\n\\t]", ""));
    }
}
