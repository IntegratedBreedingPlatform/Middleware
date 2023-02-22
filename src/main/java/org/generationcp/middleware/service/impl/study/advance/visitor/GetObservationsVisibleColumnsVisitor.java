package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AbstractAdvanceRequest;
import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.etl.MeasurementVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetObservationsVisibleColumnsVisitor implements AdvanceRequestVisitor<Set<String>> {

	private final Integer observationDatasetId;
	private final List<MeasurementVariable> observationVariables;

	public GetObservationsVisibleColumnsVisitor(final Integer observationDatasetId,
		final List<MeasurementVariable> observationVariables) {
		this.observationDatasetId = observationDatasetId;
		this.observationVariables = observationVariables;
	}

	@Override
	public Set<String> visit(final AdvanceStudyRequest request) {
		final Set<String> variableNames = new HashSet<>();

		this.addSelectionTraitVariableNameFromObservation(request.getSelectionTraitRequest(), variableNames);

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		if (breedingMethodSelectionRequest != null && breedingMethodSelectionRequest.getMethodVariateId() != null) {
			this.getVariableName(breedingMethodSelectionRequest.getMethodVariateId(), variableNames);
		}

		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest = request.getLineSelectionRequest();
		if (lineSelectionRequest != null && lineSelectionRequest.getLineVariateId() != null) {
			this.getVariableName(lineSelectionRequest.getLineVariateId(), variableNames);
		}

		final AdvanceStudyRequest.BulkingRequest bulkingRequest = request.getBulkingRequest();
		if (bulkingRequest != null && bulkingRequest.getPlotVariateId() != null) {
			this.getVariableName(bulkingRequest.getPlotVariateId(), variableNames);
		}

		return variableNames;
	}

	@Override
	public Set<String> visit(final AdvanceSamplesRequest request) {
		final Set<String> variableNames = new HashSet<>();
		this.addSelectionTraitVariableNameFromObservation(request.getSelectionTraitRequest(), variableNames);
		return variableNames;
	}

	private void addSelectionTraitVariableNameFromObservation(final AbstractAdvanceRequest.SelectionTraitRequest selectionTraitRequest,
		final Set<String> variableNames) {
		if (selectionTraitRequest != null && this.observationDatasetId.equals(selectionTraitRequest.getDatasetId())) {
			this.getVariableName(selectionTraitRequest.getVariableId(), variableNames);
		}
	}

	private void getVariableName(final Integer variableId, final Set<String> variableNames) {
		this.observationVariables.stream()
			.filter(measurementVariable -> measurementVariable.getTermId() == variableId)
			.findFirst()
			.map(MeasurementVariable::getName)
			.ifPresent(variableNames::add);
	}

}
