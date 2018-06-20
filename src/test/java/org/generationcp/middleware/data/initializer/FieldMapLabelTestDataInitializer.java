
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;

public class FieldMapLabelTestDataInitializer {

	private static final int NO_OF_GERMPLASM_LIST_OBSERVATION = 10;

	public static List<FieldMapLabel> createFieldMapLabelList() {
		final List<FieldMapLabel> labelFields = new ArrayList<>();

		for (int i = 1; i <= FieldMapLabelTestDataInitializer.NO_OF_GERMPLASM_LIST_OBSERVATION; i++) {
			final FieldMapLabel fieldMapLabel = new FieldMapLabel();
			fieldMapLabel.setExperimentId(i);
			labelFields.add(fieldMapLabel);
		}
		return labelFields;
	}

	public static List<FieldMapLabel> createFieldMapLabelList(final int numberOfObservations) {
		final List<FieldMapLabel> labelFields = new ArrayList<>();

		for (int i = 1; i <= numberOfObservations; i++) {
			final FieldMapLabel fieldMapLabel = new FieldMapLabel();
			fieldMapLabel.setExperimentId(i);
			labelFields.add(fieldMapLabel);
		}
		return labelFields;
	}
}
