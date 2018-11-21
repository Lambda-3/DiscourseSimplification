/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtensionsDeserializer
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

package org.lambda3.text.simplification.discourse.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.lambda3.text.simplification.discourse.model.Extensible;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExtensionsDeserializer extends StdDeserializer<Map> {

    protected ExtensionsDeserializer() {
        super(Map.class);
    }

    @Override
    public Map deserialize(JsonParser parser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        JsonNode node = parser.getCodec().readTree(parser);
        Iterator<JsonNode> iter = node.iterator();

        Map<Object, Object> map = new HashMap<>();

        while (iter.hasNext()) {
            JsonNode en = iter.next();
            Class<?> clazz = deserializationContext.readValue(en.get("class").traverse(), Class.class);
            String key = en.get("key").asText();

            if (key == null) {
                Object content = deserializationContext.readValue(en.get("content").traverse(), clazz);
                map.put(clazz, content);

            } else if (key.equals(Extensible.LIST_KEY)) {
                Object content = deserializationContext.readValue(en.get("content").traverse(), List.class);
                map.put(new Extensible.Key(clazz, key), content);
            } else {
                Object content = deserializationContext.readValue(en.get("content").traverse(), clazz);
                map.put(new Extensible.Key(clazz, key), content);
            }
        }

        return map;
    }
}
