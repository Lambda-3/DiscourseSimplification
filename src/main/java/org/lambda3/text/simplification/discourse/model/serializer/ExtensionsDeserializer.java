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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.lambda3.text.simplification.discourse.model.Extensible;

import java.io.IOException;
import java.util.*;

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
            try {
                Class<?> clazz = Class.forName(en.get("class").asText());
                String key = en.has("key") ? en.get("key").asText() : null;
                JsonNode nodeContent = en.get("content");

                Object mapKey;
                Object content;

                if (nodeContent.isArray()) {
                    content = new LinkedList<>();
                    for (JsonNode aNodeContent : nodeContent) {
                        JsonParser panc = aNodeContent.traverse();
                        panc.nextToken();
                        Object co = deserializationContext.readValue(panc, clazz);
                        ((List) content).add(co);
                    }
                } else {
                    JsonParser cjp = nodeContent.traverse();
                    cjp.nextToken();
                    content = deserializationContext.readValue(cjp, clazz);
                }

                if (key == null) {
                    mapKey = clazz;
                } else {
                    mapKey = new Extensible.Key(clazz, key);
                }

                map.put(mapKey, content);

            } catch (ClassNotFoundException e) {
                throw new JsonGenerationException(e);
            }
        }

        return map;
    }
}
