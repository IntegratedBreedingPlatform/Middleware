package org.generationcp.middleware.service.impl.study.advance.resolver;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrialInstanceObservationsResolver {

	public ObservationUnitRow getTrialInstanceObservations(final Integer trialInstanceNumber,
		final List<ObservationUnitRow> trialObservations,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber) {

		final Optional<ObservationUnitRow> optionalTrialObservation = trialObservations.stream()
			.filter(observationUnitRow -> trialInstanceNumber.equals(observationUnitRow.getTrialInstance()))
			.findFirst();
		if (optionalTrialObservation.isPresent()) {
			final ObservationUnitRow trialObservation = optionalTrialObservation.get();
			if (studyInstancesByInstanceNumber.containsKey(trialInstanceNumber)) {
				trialObservation.getVariableById(trialObservation.getEnvironmentVariables().values(), TermId.LOCATION_ID.getId())
					.ifPresent(
						observationUnitData -> observationUnitData
							.setValue(String.valueOf(studyInstancesByInstanceNumber.get(trialInstanceNumber).getLocationId()))
					);
			}
			return trialObservation;
		}
		return null;
	}

}
