package org.generationcp.middleware.ruleengine.naming.resolver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SelectionTraitDataResolverTest {

	private static final Integer DATASET_ID = new Random().nextInt(Integer.MAX_VALUE);
	private static final Integer SELECTION_TRAIT_VARIABLE_ID = new Random().nextInt(Integer.MAX_VALUE);
	private static final String SELECTION_TRAIT_VALUE = RandomStringUtils.random(10);

	@InjectMocks
	private SelectionTraitDataResolver selectionTraitDataResolver;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void shouldResolveLevel_OK() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();
		assertTrue(this.selectionTraitDataResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));
	}

	@Test
	public void shouldResolveLevel_notResolve() {
		assertFalse(this.selectionTraitDataResolver.shouldResolveLevel(DATASET_ID, null));

		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest =
			Mockito.mock(AdvanceStudyRequest.SelectionTraitRequest.class);
		assertFalse(this.selectionTraitDataResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));

		Mockito.when(selectionTraitRequest.getDatasetId()).thenReturn(new Random().nextInt());
		Mockito.when(selectionTraitRequest.getVariableId()).thenReturn(SELECTION_TRAIT_VARIABLE_ID);
		assertFalse(this.selectionTraitDataResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));

		Mockito.when(selectionTraitRequest.getDatasetId()).thenReturn(DATASET_ID);
		Mockito.when(selectionTraitRequest.getVariableId()).thenReturn(null);
		assertFalse(this.selectionTraitDataResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));
	}

	@Test
	public void resolveStudyLevelData_selectionTraitVariableNotPresent() {
		final Integer measurementVariableValue = new Random().nextInt(Integer.MAX_VALUE);
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		// Set a random variable Id to force not finding the selected selection trait variable
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(new Random().nextInt(),
			measurementVariableValue.toString(), SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName());

		final String expectedSelectionTrait =
			this.selectionTraitDataResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertNull(expectedSelectionTrait);

		Mockito.verify(selectionTraitVariable, Mockito.never()).getDataType();
	}

	@Test
	public void resolveStudyLevelData_categoryValueNotPresent() {
		final Integer measurementVariableValue = new Random().nextInt(Integer.MAX_VALUE);
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		// Set a random value reference Id to force not finding the categorical value
		final ValueReference valueReference = this.mockValueReference(new Random().nextInt(), RandomStringUtils.random(10));
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			measurementVariableValue.toString(), SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName(),
			Arrays.asList(valueReference));

		final String expectedSelectionTrait =
			this.selectionTraitDataResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertNull(expectedSelectionTrait);

		Mockito.verify(valueReference, Mockito.never()).getName();
	}

	@Test
	public void resolveStudyLevelData_shouldResolveForCategoricalVariable() {
		final Integer measurementVariableValue = new Random().nextInt(Integer.MAX_VALUE);
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final ValueReference valueReference = this.mockValueReference(measurementVariableValue, SELECTION_TRAIT_VALUE);
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			measurementVariableValue.toString(), SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName(),
			Arrays.asList(valueReference));

		final String expectedSelectionTrait =
			this.selectionTraitDataResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertThat(expectedSelectionTrait, is(SELECTION_TRAIT_VALUE));

		Mockito.verify(valueReference).getName();
	}

	@Test
	public void resolveStudyLevelData_shouldResolveForNonCategoricalVariable() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			SELECTION_TRAIT_VALUE, SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.NUMERIC_VARIABLE.getName());

		final String expectedSelectionTrait =
			this.selectionTraitDataResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertThat(expectedSelectionTrait, is(SELECTION_TRAIT_VALUE));

		Mockito.verify(selectionTraitVariable).getValue();
	}

	@Test
	public void resolveEnvironmentLevelData_shouldResolveForCategoricalVariable() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final ValueReference valueReference = this.mockValueReference(SELECTION_TRAIT_VARIABLE_ID, SELECTION_TRAIT_VALUE);
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			null, SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName(),
			Arrays.asList(valueReference));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(SELECTION_TRAIT_VARIABLE_ID, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation =
			new ObservationUnitData(SELECTION_TRAIT_VARIABLE_ID, valueReference.getId().toString());
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setEnvironmentVariables(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrialInstanceObservation()).thenReturn(observationUnitRow);

		this.selectionTraitDataResolver.resolveEnvironmentLevelData(DATASET_ID, selectionTraitRequest, source, plotDataVariablesByTermId);

		Mockito.verify(selectionTraitVariable).getPossibleValues();
		Mockito.verify(source).setSelectionTraitValue(SELECTION_TRAIT_VALUE);
	}

	@Test
	public void resolveEnvironmentLevelData_shouldResolveForNonCategoricalVariable() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			null, SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.NUMERIC_VARIABLE.getName());

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(SELECTION_TRAIT_VARIABLE_ID, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData(SELECTION_TRAIT_VARIABLE_ID, SELECTION_TRAIT_VALUE);
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setEnvironmentVariables(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrialInstanceObservation()).thenReturn(observationUnitRow);

		this.selectionTraitDataResolver.resolveEnvironmentLevelData(DATASET_ID, selectionTraitRequest, source, plotDataVariablesByTermId);

		Mockito.verify(selectionTraitVariable, Mockito.never()).getPossibleValues();
		Mockito.verify(source).setSelectionTraitValue(SELECTION_TRAIT_VALUE);
	}

	@Test
	public void resolvePlotLevelData_shouldResolveForCategoricalVariable() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final ValueReference valueReference = this.mockValueReference(SELECTION_TRAIT_VARIABLE_ID, SELECTION_TRAIT_VALUE);
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			null, SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName(),
			Arrays.asList(valueReference));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(SELECTION_TRAIT_VARIABLE_ID, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData();
		selectionTraitObservation.setVariableId(SELECTION_TRAIT_VARIABLE_ID);
		selectionTraitObservation.setCategoricalValueId(valueReference.getId());

		final Map<String, ObservationUnitData> environmentvariables = new HashMap<>();
		environmentvariables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setVariables(environmentvariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);

		this.selectionTraitDataResolver
			.resolvePlotLevelData(DATASET_ID, selectionTraitRequest, source, observationUnitRow, plotDataVariablesByTermId);

		Mockito.verify(selectionTraitVariable).getPossibleValues();
		Mockito.verify(source).setSelectionTraitValue(SELECTION_TRAIT_VALUE);
	}

	@Test
	public void resolvePlotLevelData_shouldResolveForNonCategoricalVariable() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			null, SelectionTraitDataResolver.SELECTION_TRAIT_PROPERTY, DataType.NUMERIC_VARIABLE.getName());

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(SELECTION_TRAIT_VARIABLE_ID, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData(SELECTION_TRAIT_VARIABLE_ID, SELECTION_TRAIT_VALUE);
		final Map<String, ObservationUnitData> variables = new HashMap<>();
		variables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setVariables(variables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);

		this.selectionTraitDataResolver
			.resolvePlotLevelData(DATASET_ID, selectionTraitRequest, source, observationUnitRow, plotDataVariablesByTermId);

		Mockito.verify(selectionTraitVariable, Mockito.never()).getPossibleValues();
		Mockito.verify(source).setSelectionTraitValue(SELECTION_TRAIT_VALUE);
	}

	private AdvanceStudyRequest.SelectionTraitRequest mockSelectionTraitRequest() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest =
			Mockito.mock(AdvanceStudyRequest.SelectionTraitRequest.class);
		Mockito.when(selectionTraitRequest.getDatasetId()).thenReturn(DATASET_ID);
		Mockito.when(selectionTraitRequest.getVariableId()).thenReturn(SELECTION_TRAIT_VARIABLE_ID);
		return selectionTraitRequest;
	}

	private MeasurementVariable mockMeasurementVariable(final Integer variableId, final String value, final String property,
		final String dataType) {
		return this.mockMeasurementVariable(variableId, value, property, dataType, null);
	}

	private MeasurementVariable mockMeasurementVariable(final Integer variableId, final String value, final String property,
		final String dataType, final List<ValueReference> possibleValues) {
		final MeasurementVariable measurementVariable = Mockito.mock(MeasurementVariable.class);
		Mockito.when(measurementVariable.getTermId()).thenReturn(variableId);
		Mockito.when(measurementVariable.getValue()).thenReturn(value);
		Mockito.when(measurementVariable.getProperty()).thenReturn(property);
		Mockito.when(measurementVariable.getDataType()).thenReturn(dataType);
		Mockito.when(measurementVariable.getPossibleValues()).thenReturn(possibleValues);
		return measurementVariable;
	}

	private ValueReference mockValueReference(final int id, final String name) {
		final ValueReference valueReference = Mockito.mock(ValueReference.class);
		Mockito.when(valueReference.getId()).thenReturn(id);
		Mockito.when(valueReference.getName()).thenReturn(name);
		return valueReference;
	}

}
