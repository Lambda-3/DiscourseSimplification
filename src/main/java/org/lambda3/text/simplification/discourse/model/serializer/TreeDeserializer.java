/*
 * ==========================License-Start=============================
 * DiscourseSimplification : TreeDeSerializer
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;

import java.io.IOException;
import java.io.StringReader;

/**
 *
 */
public class TreeDeserializer extends StdDeserializer<Tree> {

    public TreeDeserializer() {
        this(null);
    }

    protected TreeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Tree deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String pennString = p.getValueAsString();
        return new PennTreeReader(new StringReader(pennString)).readTree();
    }
}
