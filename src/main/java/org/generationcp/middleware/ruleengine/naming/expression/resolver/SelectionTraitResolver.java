package org.generationcp.middleware.ruleengine.naming.expression.resolver;

import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public class SelectionTraitResolver {

	public static final String SELECTION_TRAIT_PROPERTY = "Selection Criteria";

	/**
	 * Returns the selection trait value at study level.
	 *
	 * @param datasetId
	 * @param selectionTraitRequest
	 * @param studyVariates
	 * @return the selection trait value
	 */
	public String resolveStudyLevelData(
		final Integer datasetId, final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest,
		final List<MeasurementVariable> studyVariates) {

		if (!this.shouldResolveLevel(datasetId, selectionTraitRequest)) {
			return null;
		}

		// Find if the selected selection trait variable is present
		final Optional<MeasurementVariable> optionalSelectionTraitVariable = studyVariates.stream()
			.filter(variable -> SELECTION_TRAIT_PROPERTY.equalsIgnoreCase(variable.getProperty())
				&& variable.getTermId() == selectionTraitRequest
				.getVariableId())
			.findFirst();
		if (!optionalSelectionTraitVariable.isPresent()) {
			return null;
		}

		// Get the selection trait value
		final MeasurementVariable selectionTraitVariable = optionalSelectionTraitVariable.get();
		if (DataType.CATEGORICAL_VARIABLE.getName().equals(selectionTraitVariable.getDataType())) {
			final Optional<ValueReference> optionalReference = selectionTraitVariable.getPossibleValues().stream()
				.filter(valueReference -> valueReference.getId().toString().equals(selectionTraitVariable.getValue()))
				.findFirst();
			if (optionalReference.isPresent()) {
				return optionalReference.get().getName();
			}
		} else {
			return selectionTraitVariable.getValue();
		}
		return null;
	}

	/**
	 * Resolves selection trait value at environment level. The required data will be set into the provided {@link NewAdvancingSource}
	 *
	 * @param source
	 * @param plotDataVariablesByTermId
	 */
	public void resolveEnvironmentLevelData(final Integer datasetId, final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest,
		final NewAdvancingSource source,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {

		if (!this.shouldResolveLevel(datasetId, selectionTraitRequest)) {
			return;
		}

		if (DataResolverHelper.checkHasTrailInstanceObservations(source.getTrailInstanceObservation())) {
			// In this case, the value of the environment observations corresponds to the id of the categorical reference
			final BiPredicate<ValueReference, ObservationUnitData> predicate =
				(valueReference, observationUnitData) -> valueReference.getId().toString().equals(observationUnitData.getValue());
			this.getTraitSelectionValue(source, source.getTrailInstanceObservation().getEnvironmentVariables().values(),
				plotDataVariablesByTermId, predicate, selectionTraitRequest.getVariableId());
		}
	}

	/**
	 * Resolves selection trait value at plot level. The required data will be set into the provided {@link NewAdvancingSource}
	 *
	 * @param source
	 * @param row
	 * @param plotDataVariablesByTermId
	 */
	public void resolvePlotLevelData(final Integer datasetId, final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest,
		final NewAdvancingSource source, final ObservationUnitRow row,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {

		if (!this.shouldResolveLevel(datasetId, selectionTraitRequest)) {
			return;
		}

		// In this case, the categorical value id of the plot observations corresponds to the id of the categorical reference
		final BiPredicate<ValueReference, ObservationUnitData> predicate =
			(valueReference, observationUnitData) -> valueReference.getId().equals(observationUnitData.getCategoricalValueId());
		this.getTraitSelectionValue(source, row.getVariables().values(), plotDataVariablesByTermId, predicate,
			selectionTraitRequest.getVariableId());
	}

	private void getTraitSelectionValue(final NewAdvancingSource source,
		final Collection<ObservationUnitData> observationUnitDataCollection,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId,
		final BiPredicate<ValueReference, ObservationUnitData> filterPredicate, final Integer selectedSelectionTraitVariableId) {

		observationUnitDataCollection.stream()
			.filter(observationUnitData -> {
				final MeasurementVariable variable = plotDataVariablesByTermId.get(observationUnitData.getVariableId());
				return variable != null && SELECTION_TRAIT_PROPERTY.equalsIgnoreCase(variable.getProperty())
					&& variable.getTermId() == selectedSelectionTraitVariableId;
			}).findFirst()
			.ifPresent(observationUnitData -> {
				final MeasurementVariable variable = plotDataVariablesByTermId.get(observationUnitData.getVariableId());
				if (DataType.CATEGORICAL_VARIABLE.getName().equals(variable.getDataType())) {
					variable.getPossibleValues().stream()
						.filter(valueReference -> filterPredicate.test(valueReference, observationUnitData))
						.findFirst()
						.ifPresent(valueReference -> source.setSelectionTraitValue(valueReference.getName()));
				} else {
					source.setSelectionTraitValue(observationUnitData.getValue());
				}
			});
	}

	boolean shouldResolveLevel(final Integer datasetId, final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest) {
		return selectionTraitRequest != null && datasetId.equals(selectionTraitRequest.getDatasetId())
			&& selectionTraitRequest.getVariableId() != null;
	}

}
