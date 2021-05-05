package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimestampPropertySerializer extends JsonSerializer<Date> {

	private final SimpleDateFormat formatterWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public void serialize(final Date value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {

		if (value != null) {
			final Map<String, String> timestamp = new HashMap<>();
			timestamp.put("timestamp", this.formatterWithTime.format(value));
			timestamp.put("version", "1.0");
			gen.writeObject(timestamp);
		}

	}

}
