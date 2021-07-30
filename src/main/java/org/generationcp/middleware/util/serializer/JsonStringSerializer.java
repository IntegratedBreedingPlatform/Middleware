package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Convert Json string to Json object
public class JsonStringSerializer extends JsonSerializer<String> {

	@Override
	public void serialize(final String value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {
		if (StringUtils.isNotEmpty(value)) {
			final Map<String, Object> jsonProp = new ObjectMapper().readValue(value, HashMap.class);
			gen.writeObject(jsonProp.get("geoCoordinates"));
		}
	}
}
