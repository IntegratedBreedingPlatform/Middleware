package org.generationcp.middleware.service.impl.study.advance.resolver;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.advance.AdvanceUtils;

import java.util.Map;

public class PlantSelectedResolver {

	public Integer resolvePlantSelected(final AdvanceStudyRequest request, final ObservationUnitRow plotObservation,
		final Map<String, Method> breedingMethodsByCode, final boolean isBulkMethod) {

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		if (breedingMethodSelectionRequest.getBreedingMethodId() != null) {
			// User has selected the same Breeding Method for each advance
			return this.getLineSelectedForBreedingMethodVariable(request, isBulkMethod, plotObservation);
		}

		if (breedingMethodSelectionRequest.getMethodVariateId() != null) {
			// User has selected a variate that defines the breeding method for each advance
			final String rowBreedingMethodCode =
				plotObservation.getVariableValueByVariableId(breedingMethodSelectionRequest.getMethodVariateId());
			if (!StringUtils.isEmpty(rowBreedingMethodCode) && breedingMethodsByCode.containsKey(rowBreedingMethodCode)) {
				final Method method = breedingMethodsByCode.get(rowBreedingMethodCode);
				return this.getLineSelectedForBreedingMethodVariable(request, method.isBulkingMethod(), plotObservation);
			}

			return null;
		}

		return null;
	}

	private Integer getLineSelectedForBreedingMethodVariable(final AdvanceStudyRequest request, final Boolean isBulkMethod,
		final ObservationUnitRow plotObservation) {
		if (isBulkMethod == null) {
			return null;
		}

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest =
			request.getLineSelectionRequest();
		if (isBulkMethod) {
			if (breedingMethodSelectionRequest.getAllPlotsSelected() == null || !breedingMethodSelectionRequest.getAllPlotsSelected()) {
				// User has selected a variable that defines the number of lines selected from each plot. However, this is tricky because
				// the variable works as a boolean. It return 1 if there is a value present, otherwise it returns zero.
				final String plotVariateValue =
					plotObservation.getVariableValueByVariableId(breedingMethodSelectionRequest.getPlotVariateId());
				return StringUtils.isEmpty(plotVariateValue) ? 0 : 1;
			} else {
				return 1;
			}
		} else {
			// User has selected the same number of lines for each plot
			if (lineSelectionRequest.getLinesSelected() == null) {
				final String lineVariateValue =
					plotObservation.getVariableValueByVariableId(lineSelectionRequest.getLineVariateId());
				return AdvanceUtils.getIntegerValue(lineVariateValue);
			} else {
				// User has selected the same number of lines for each plot
				return lineSelectionRequest.getLinesSelected();
			}
		}
	}

}
