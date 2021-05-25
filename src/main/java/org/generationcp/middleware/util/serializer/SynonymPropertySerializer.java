package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.Synonym;
import org.generationcp.middleware.service.api.BrapiView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SynonymPropertySerializer extends JsonSerializer<List<Synonym>> {

	@Override
	public void serialize(final List<Synonym> value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {

		if (serializers.getActiveView() != null && CollectionUtils.isNotEmpty(value)) {
			if (BrapiView.BrapiV1_3.class.equals(serializers.getActiveView()) || BrapiView.BrapiV1_2.class
				.equals(serializers.getActiveView())) {
				gen.writeObject(value.stream().map(Synonym::getSynonym).collect(Collectors.toList()));
			} else {
				gen.writeObject(value);
			}
		} else {
			gen.writeObject(value);
		}

	}
}

