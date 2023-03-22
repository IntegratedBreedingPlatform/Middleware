package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GetFilteredObservationsVisitor implements AdvanceRequestVisitor<List<ObservationUnitRow>> {

	private final List<ObservationUnitRow> observations;

	public GetFilteredObservationsVisitor(final List<ObservationUnitRow> observations) {
		this.observations = observations;
	}

	@Override
	public List<ObservationUnitRow> visit(final AdvanceStudyRequest request) {
		if (!CollectionUtils.isEmpty(request.getExcludedAdvancedRows())) {
			return this.observations.stream()
				.filter(observationUnitRow -> !request.getExcludedAdvancedRows().contains(
					observationUnitRow.getObservationUnitId()))
				.collect(Collectors.toList());
		}

		return this.observations;
	}

	@Override
	public List<ObservationUnitRow> visit(final AdvanceSamplesRequest request) {
		return this.observations;
	}

}
