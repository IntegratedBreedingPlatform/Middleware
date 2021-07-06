package org.generationcp.middleware.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.service.api.BrapiView;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchObservationDTO;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ObservationSerializer extends JsonSerializer<List<PhenotypeSearchObservationDTO>> {

	@Override
	public void serialize(final List<PhenotypeSearchObservationDTO> value, final JsonGenerator gen, final SerializerProvider serializers)
		throws IOException {

		if (serializers.getActiveView() != null && CollectionUtils.isNotEmpty(value)) {
			if (BrapiView.BrapiV1_2.class.equals(serializers.getActiveView())) {
				gen.writeObject(value);
			} else {
				gen.writeObject(value.stream().map(PhenotypeSearchObservationDTO::getObservationDbId).collect(Collectors.toList()));
			}
		} else {
			gen.writeObject(value);
		}

	}
}

