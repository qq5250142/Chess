package com.chess.util;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * 
 * @author Lanux
 * 
 */
public class JsonUtil {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JsonUtil() {
	}

	public static <T> Object readValue(String value, Class<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				return MAPPER.readValue(value, Object);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> Object readValue(String value, TypeReference<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				return MAPPER.readValue(value, Object);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String writeValue(Object object) {
		if (object == null) {
			return null;
		}
		StringWriter sw = new StringWriter();
		try {
			MAPPER.writeValue(sw, object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}
