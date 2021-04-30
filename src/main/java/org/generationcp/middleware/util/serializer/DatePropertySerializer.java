package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.generationcp.middleware.service.api.BrapiView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Custom serializer to format date according to view
// V1.2-3 - yyyy-MM-dd
// V2.0 - yyyy-MM-dd'T'HH:mm:ss.SSS'Z
public class DatePropertySerializer extends JsonSerializer<Date> {

	private final SimpleDateFormat formatterSimple = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat formatterWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public void serialize(final Date value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {

		if (serializers.getActiveView() != null) {
			if (BrapiView.BrapiV1_3.class.equals(serializers.getActiveView()) || BrapiView.BrapiV1_2.class
				.equals(serializers.getActiveView())) {
				gen.writeString(this.formatterSimple.format(value));
			} else {
				gen.writeString(this.formatterWithTime.format(value));
			}
		} else {
			gen.writeString(this.formatterSimple.format(value));
		}

	}
}

