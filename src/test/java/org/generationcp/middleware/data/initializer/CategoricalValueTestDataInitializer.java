package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.h2h.CategoricalValue;

public class CategoricalValueTestDataInitializer {
	public static CategoricalValue createCategoricalValue(final String name) {
		final CategoricalValue categoricalValue = new CategoricalValue();
		categoricalValue.setName(name);
		return categoricalValue;
	}

	public static List<CategoricalValue> createCategoricalValueList() {
		final List<CategoricalValue> categoricalValues = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			categoricalValues.add(CategoricalValueTestDataInitializer.createCategoricalValue(String.valueOf(i)));
		}
		return categoricalValues;
	}
}
