/*
 * ==========================License-Start=============================
 * DiscourseSimplification : ExtensionsSerializer
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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.lambda3.text.simplification.discourse.model.Extensible;

import java.io.IOException;
import java.util.Map;

public class ExtensionsSerializer extends StdSerializer<Map> {

    public ExtensionsSerializer() {
        super(Map.class);
    }

    @Override
    public void serialize(Map extensions, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (Object o : extensions.keySet()) {

            jsonGenerator.writeStartObject();
            if (o instanceof Class<?>) {
                jsonGenerator.writeObjectField("class", o);
                jsonGenerator.writeObjectField("content", extensions.get(o));
            } else {
                //it should be an Extensible.Key object
                jsonGenerator.writeObjectField("class", ((Extensible.Key) o).clazz);
                jsonGenerator.writeObjectField("key", ((Extensible.Key) o).desc);
                jsonGenerator.writeObjectField("content", extensions.get(o));
            }
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }
}
