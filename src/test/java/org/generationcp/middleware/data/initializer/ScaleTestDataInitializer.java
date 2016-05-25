package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;

public class ScaleTestDataInitializer {

	public Scale createScale() {
		final Scale scale = new Scale(new Term());
		scale.setName("SEED_AMOUNT_kg");
		scale.setDefinition("for kg - Weighed");
		scale.setId(1);
		return scale;
	}

	public List<Scale> createScaleList() {
		final List<Scale> scales = new ArrayList<Scale>();
		scales.add(this.createScale());
		return scales;
	}
}

