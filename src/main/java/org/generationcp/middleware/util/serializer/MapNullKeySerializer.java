package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;



public class MapNullKeySerializer extends StdSerializer<Map> implements ContextualSerializer {

	class NullKeySerializer extends StdSerializer<Object> {

		private static final long serialVersionUID = -4478531309177369056L;

		private String keyName;

		public NullKeySerializer() {
			this(null, null);
		}

		NullKeySerializer(final String keyName) {
			this(null, keyName);
		}

		NullKeySerializer(final Class<Object> t, final String keyName) {
			super(t);
			this.keyName = keyName;
		}

		@Override
		public void serialize(final Object value, final JsonGenerator jsonGenerator, final SerializerProvider provider)
			throws IOException {
			jsonGenerator.writeFieldName(keyName);
		}

		public String getKeyName() {
			return keyName;
		}

		public void setKeyName(final String keyName) {
			this.keyName = keyName;
		}
	}


	private final ObjectMapper mapper = new ObjectMapper();
	private String keyName;

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(final String keyName) {
		this.keyName = keyName;
	}

	public MapNullKeySerializer() {
		this(null, null);
	}

	private MapNullKeySerializer(final String keyName) {
		this(null, keyName);
	}

	private MapNullKeySerializer(final Class<Map> t, final String keyName) {
		super(t);
		this.keyName = keyName;
	}

	@Override
	public void serialize(final Map s, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
		throws IOException {
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.getSerializerProvider()
			.setNullKeySerializer(new NullKeySerializer(keyName));
		mapper.writeValue(jsonGenerator, s);
	}

	@Override
	public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) {
		String unit = null;
		NullKeyReplacement ann = null;
		if (property != null) {
			ann = property.getAnnotation(NullKeyReplacement.class);
		}
		if (ann != null) {
			unit = ann.value();
		}
		return new MapNullKeySerializer(unit);
	}
}
