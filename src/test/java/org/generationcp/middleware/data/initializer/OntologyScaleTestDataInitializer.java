package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;

public class OntologyScaleTestDataInitializer {
	public static Scale createScale() {
		final Scale scale = new Scale();
		scale.setId(1);
		scale.setName("Scale Name");
		scale.setDefinition("Scale Definition");
		scale.setDataType(DataType.NUMERIC_VARIABLE);
		return scale;
	}

	public static Scale createScaleWithNameAndDataType(final String scaleName, final DataType dataType) {
		final Scale scale = new Scale();
		scale.setId(1);
		scale.setName(scaleName);
		scale.setDefinition("Scale Definition");
		scale.setDataType(dataType);
		return scale;
	}
}
