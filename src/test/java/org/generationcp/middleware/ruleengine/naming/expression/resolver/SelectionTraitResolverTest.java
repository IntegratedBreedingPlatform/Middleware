package org.generationcp.middleware.ruleengine.naming.expression.resolver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.ValueReferenceTestDataInitializer;
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

public class SelectionTraitResolverTest {

	private static final Integer DATASET_ID = new Random().nextInt(Integer.MAX_VALUE);
	private static final Integer SELECTION_TRAIT_VARIABLE_ID = new Random().nextInt(Integer.MAX_VALUE);

	@InjectMocks
	private SelectionTraitResolver selectionTraitResolver;

	private ValueReferenceTestDataInitializer valueReferenceTestDataInitializer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		this.valueReferenceTestDataInitializer = new ValueReferenceTestDataInitializer();
	}

	@Test
	public void shouldResolveLevel_OK() {
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();
		assertTrue(this.selectionTraitResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));
	}

	@Test
	public void shouldResolveLevel_notResolve() {
		assertFalse(this.selectionTraitResolver.shouldResolveLevel(DATASET_ID, null));

		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest =
			Mockito.mock(AdvanceStudyRequest.SelectionTraitRequest.class);
		assertFalse(this.selectionTraitResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));

		Mockito.when(selectionTraitRequest.getDatasetId()).thenReturn(new Random().nextInt());
		Mockito.when(selectionTraitRequest.getVariableId()).thenReturn(SELECTION_TRAIT_VARIABLE_ID);
		assertFalse(this.selectionTraitResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));

		Mockito.when(selectionTraitRequest.getDatasetId()).thenReturn(DATASET_ID);
		Mockito.when(selectionTraitRequest.getVariableId()).thenReturn(null);
		assertFalse(this.selectionTraitResolver.shouldResolveLevel(DATASET_ID, selectionTraitRequest));
	}

	@Test
	public void resolveStudyLevelData_selectionTraitVariableNotPresent() {
		final Integer measurementVariableValue = new Random().nextInt(Integer.MAX_VALUE);
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		// Set a random variable Id to force not finding the selected selection trait variable
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(new Random().nextInt(),
			measurementVariableValue.toString(), SelectionTraitResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName());

		final String expectedSelectionTrait =
			this.selectionTraitResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
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
			measurementVariableValue.toString(), SelectionTraitResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName(),
			Arrays.asList(valueReference));

		final String expectedSelectionTrait =
			this.selectionTraitResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertNull(expectedSelectionTrait);

		Mockito.verify(valueReference, Mockito.never()).getName();
	}

	@Test
	public void resolveStudyLevelData_shouldResolveForCategoricalVariable() {
		final String selectionTraitValue = RandomStringUtils.random(10);
		final Integer measurementVariableValue = new Random().nextInt(Integer.MAX_VALUE);
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final ValueReference valueReference = this.mockValueReference(measurementVariableValue, selectionTraitValue);
		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			measurementVariableValue.toString(), SelectionTraitResolver.SELECTION_TRAIT_PROPERTY, DataType.CATEGORICAL_VARIABLE.getName(),
			Arrays.asList(valueReference));

		final String expectedSelectionTrait =
			this.selectionTraitResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertThat(expectedSelectionTrait, is(selectionTraitValue));

		Mockito.verify(valueReference).getName();
	}

	@Test
	public void resolveStudyLevelData_shouldResolveForNonCategoricalVariable() {
		final String selectionTraitValue = RandomStringUtils.random(10);
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest = this.mockSelectionTraitRequest();

		final MeasurementVariable selectionTraitVariable = this.mockMeasurementVariable(SELECTION_TRAIT_VARIABLE_ID,
			selectionTraitValue, SelectionTraitResolver.SELECTION_TRAIT_PROPERTY, DataType.NUMERIC_VARIABLE.getName());

		final String expectedSelectionTrait =
			this.selectionTraitResolver.resolveStudyLevelData(DATASET_ID, selectionTraitRequest, Arrays.asList(selectionTraitVariable));
		assertThat(expectedSelectionTrait, is(selectionTraitValue));

		Mockito.verify(selectionTraitVariable).getValue();
	}

	@Test
	public void resolveEnvironmentLevelData_withCategoricalVariable() {
		final Integer variableId = new Random().nextInt(Integer.MAX_VALUE);
		final String selectionTraitValue = RandomStringUtils.randomAlphabetic(10);

		final MeasurementVariable selectionTraitVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(variableId, null);
		selectionTraitVariable.setProperty(SelectionTraitResolver.SELECTION_TRAIT_PROPERTY);
		selectionTraitVariable.setDataType(DataType.CATEGORICAL_VARIABLE.getName());

		final ValueReference valueReference = this.valueReferenceTestDataInitializer.createValueReference(variableId, selectionTraitValue);
		selectionTraitVariable.setPossibleValues(Arrays.asList(valueReference));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(variableId, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData(variableId, valueReference.getId().toString());
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setEnvironmentVariables(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrailInstanceObservation()).thenReturn(observationUnitRow);

		this.selectionTraitResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);

		Mockito.verify(source).setSelectionTraitValue(selectionTraitValue);
	}

	@Test
	public void resolveEnvironmentLevelData_withNonCategoricalVariable() {
		final Integer variableId = new Random().nextInt(Integer.MAX_VALUE);
		final String selectionTraitValue = RandomStringUtils.randomAlphabetic(10);

		final MeasurementVariable selectionTraitVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(variableId, null);
		selectionTraitVariable.setProperty(SelectionTraitResolver.SELECTION_TRAIT_PROPERTY);
		selectionTraitVariable.setDataType(DataType.NUMERIC_VARIABLE.getName());

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(variableId, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData(variableId, selectionTraitValue);
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setEnvironmentVariables(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrailInstanceObservation()).thenReturn(observationUnitRow);

		this.selectionTraitResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);

		Mockito.verify(source).setSelectionTraitValue(selectionTraitValue);
	}

	@Test
	public void resolvePlotLevelData_withCategoricalVariable() {
		final Integer variableId = new Random().nextInt(Integer.MAX_VALUE);
		final String selectionTraitValue = RandomStringUtils.randomAlphabetic(10);

		final MeasurementVariable selectionTraitVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(variableId, null);
		selectionTraitVariable.setProperty(SelectionTraitResolver.SELECTION_TRAIT_PROPERTY);
		selectionTraitVariable.setDataType(DataType.CATEGORICAL_VARIABLE.getName());

		final ValueReference valueReference = this.valueReferenceTestDataInitializer.createValueReference(variableId, selectionTraitValue);
		selectionTraitVariable.setPossibleValues(Arrays.asList(valueReference));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(variableId, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData();
		selectionTraitObservation.setVariableId(variableId);
		selectionTraitObservation.setCategoricalValueId(valueReference.getId());

		final Map<String, ObservationUnitData> environmentvariables = new HashMap<>();
		environmentvariables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setVariables(environmentvariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);

		this.selectionTraitResolver.resolvePlotLevelData(source, observationUnitRow, plotDataVariablesByTermId);

		Mockito.verify(source).setSelectionTraitValue(selectionTraitValue);
	}

	@Test
	public void resolvePlotLevelData_withNonCategoricalVariable() {
		final Integer variableId = new Random().nextInt(Integer.MAX_VALUE);
		final String selectionTraitValue = RandomStringUtils.randomAlphabetic(10);

		final MeasurementVariable selectionTraitVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(variableId, null);
		selectionTraitVariable.setProperty(SelectionTraitResolver.SELECTION_TRAIT_PROPERTY);
		selectionTraitVariable.setDataType(DataType.NUMERIC_VARIABLE.getName());

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(variableId, selectionTraitVariable);

		final ObservationUnitData selectionTraitObservation = new ObservationUnitData(variableId, selectionTraitValue);
		final Map<String, ObservationUnitData> variables = new HashMap<>();
		variables.put(RandomStringUtils.randomAlphabetic(10), selectionTraitObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setVariables(variables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);

		this.selectionTraitResolver.resolvePlotLevelData(source, observationUnitRow, plotDataVariablesByTermId);

		Mockito.verify(source).setSelectionTraitValue(selectionTraitValue);
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
