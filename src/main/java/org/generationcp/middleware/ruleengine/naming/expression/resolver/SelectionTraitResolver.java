package org.generationcp.middleware.ruleengine.naming.expression.resolver;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;

// TODO: it doesn't look good overwriting the value of the selection trait value for every declared variable with SELECTION TRAIT PROPERTY as property.
//  Check if it's better to ask for the user to select which variable with SELECTION TRAIT PROPERTY wants to use.
//  Otherwise, we need to ensure to keep the same variable order (if it's possible) as the old advance
public class SelectionTraitResolver {

	public static final String SELECTION_TRAIT_PROPERTY = "Selection Criteria";

	/**
	 * Returns the selection trait value at study level.
	 *
	 * @param studyVariates
	 * @return the selection trait value
	 */
	public String resolveStudyLevelData(final List<MeasurementVariable> studyVariates) {
		final AtomicReference<String> selectionTraitValue = new AtomicReference<>();
		studyVariates.stream()
			.filter(variable -> SELECTION_TRAIT_PROPERTY.equalsIgnoreCase(variable.getProperty()))
			.forEach(variable -> {
				if (DataType.CATEGORICAL_VARIABLE.getName().equals(variable.getDataType())) {
					variable.getPossibleValues().stream()
						.filter(valueReference -> valueReference.getId().toString().equals(variable.getValue()))
						.findFirst()
						.ifPresent(valueReference -> selectionTraitValue.set(valueReference.getName()));
				} else {
					selectionTraitValue.set(variable.getValue());
				}
			});
		return selectionTraitValue.get();
	}

	/**
	 * Resolves selection trait value at environment level. The required data will be set into the provided {@link NewAdvancingSource}
	 *
	 * @param source
	 * @param plotDataVariablesByTermId
	 */
	public void resolveEnvironmentLevelData(final NewAdvancingSource source,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {
		if (DataResolverHelper.checkHasTrailInstanceObservations(source.getTrailInstanceObservation())) {
			final BiPredicate<ValueReference, ObservationUnitData> predicate =
				(valueReference, observationUnitData) -> valueReference.getId().toString().equals(observationUnitData.getValue());
			this.getTraitSelectionValue(source, source.getTrailInstanceObservation().getEnvironmentVariables().values(),
				plotDataVariablesByTermId, predicate);
		}
	}

	/**
	 * Resolves selection trait value at plot level. The required data will be set into the provided {@link NewAdvancingSource}
	 *
	 * @param source
	 * @param row
	 * @param plotDataVariablesByTermId
	 */
	public void resolvePlotLevelData(final NewAdvancingSource source, final ObservationUnitRow row,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {
		final BiPredicate<ValueReference, ObservationUnitData> predicate =
			(valueReference, observationUnitData) -> valueReference.getId().equals(observationUnitData.getCategoricalValueId());
		this.getTraitSelectionValue(source, row.getVariables().values(), plotDataVariablesByTermId, predicate);
	}

	private void getTraitSelectionValue(final NewAdvancingSource source,
		final Collection<ObservationUnitData> observationUnitDataCollection,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId,
		final BiPredicate<ValueReference, ObservationUnitData> predicate) {
		observationUnitDataCollection.forEach(observationUnitData -> {
			final MeasurementVariable variable = plotDataVariablesByTermId.get(observationUnitData.getVariableId());
			if (variable != null && SELECTION_TRAIT_PROPERTY.equalsIgnoreCase(variable.getProperty())) {
				if (DataType.CATEGORICAL_VARIABLE.getName().equals(variable.getDataType())) {
					variable.getPossibleValues().stream()
						.filter(valueReference -> predicate.test(valueReference, observationUnitData))
						.findFirst()
						.ifPresent(valueReference -> source.setSelectionTraitValue(valueReference.getName()));
				} else {
					source.setSelectionTraitValue(observationUnitData.getValue());
				}
			}
		});
	}

}
