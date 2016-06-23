package com.autoStock.internal;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Kevin Kowalewski
 * 
 */
public class GsonClassAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {
	@Override
	public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("class_name", new JsonPrimitive(src.getClass().getName()));
		result.add("class_properties", context.serialize(src, src.getClass()));
		return result;
	}

	@Override
	public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String jsonClass = jsonObject.get("class_name").getAsString();
		JsonElement element = jsonObject.get("class_properties");

		try {
			return context.deserialize(element, Class.forName(jsonClass));
		} catch (ClassNotFoundException cnfe) {
			throw new JsonParseException("Unknown element type: " + jsonClass, cnfe);
		}
	}
}