package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.generationcp.middleware.service.api.BrapiView;

import java.io.IOException;

// custom serializer to convert string to boolean if active view is for 2.0
public class StringToBooleanSerializer extends JsonSerializer<String> {

	public void serialize(final String value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {
		gen.writeObject(BrapiView.BrapiV2.class.equals(serializers.getActiveView())
			? Boolean.valueOf(value) : value); 
	}
}
