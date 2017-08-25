/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Content
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

package org.lambda3.text.simplification.discourse.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.discourse.model.serializer.TreeDeserializer;
import org.lambda3.text.simplification.discourse.model.serializer.TreeSerializer;

import java.io.File;
import java.io.IOException;

public abstract class Content {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final SimpleModule MODULE = new SimpleModule();

	static {
		MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		// register custom de-/serializers
		MODULE.addSerializer(Tree.class, new TreeSerializer());
		MODULE.addDeserializer(Tree.class, new TreeDeserializer());

		MAPPER.registerModule(MODULE);
	}

	public static <T extends Content> T deserializeFromJSON(String json, Class<T> clazz) throws IOException {
		return MAPPER.readValue(json, clazz);
	}

	public static <T extends Content> T deserializeFromJSON(File file, Class<T> clazz) throws IOException {
		return MAPPER.readValue(file, clazz);
	}

	public String prettyPrintJSON() throws JsonProcessingException {
		return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
	}

	public String serializeToJSON() throws JsonProcessingException {
		return MAPPER.writeValueAsString(this);
	}

	public void serializeToJSON(File file) throws IOException {
		MAPPER.writeValue(file, this);
	}
}
