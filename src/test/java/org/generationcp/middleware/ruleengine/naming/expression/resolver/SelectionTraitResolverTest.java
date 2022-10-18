package org.generationcp.middleware.ruleengine.naming.expression.resolver;

import org.apache.commons.lang3.RandomStringUtils;
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
import static org.junit.Assert.assertThat;

public class SelectionTraitResolverTest {

	@InjectMocks
	private SelectionTraitResolver selectionTraitResolver;

	private ValueReferenceTestDataInitializer valueReferenceTestDataInitializer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		this.valueReferenceTestDataInitializer = new ValueReferenceTestDataInitializer();
	}

	@Test
	public void resolveStudyLevelData_withCategoricalVariable() {
		final String selectionTraitValue = RandomStringUtils.random(10);
		final Integer measurementVariableValue = new Random().nextInt(Integer.MAX_VALUE);

		final MeasurementVariable selectionTraitVariable =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(new Random().nextInt(Integer.MAX_VALUE),
				measurementVariableValue.toString());
		selectionTraitVariable.setProperty(SelectionTraitResolver.SELECTION_TRAIT_PROPERTY);
		selectionTraitVariable.setDataType(DataType.CATEGORICAL_VARIABLE.getName());

		final List<ValueReference> possibleValues = Arrays.asList(this.valueReferenceTestDataInitializer
			.createValueReference(measurementVariableValue, selectionTraitValue));
		selectionTraitVariable.setPossibleValues(possibleValues);

		final String expectedSelectionTrait = this.selectionTraitResolver.resolveStudyLevelData(Arrays.asList(selectionTraitVariable));
		assertThat(expectedSelectionTrait, is(selectionTraitValue));
	}

	@Test
	public void resolveStudyLevelData_withNonCategoricalVariable() {
		final String selectionTraitValue = RandomStringUtils.random(10);

		final MeasurementVariable selectionTraitVariable =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(new Random().nextInt(Integer.MAX_VALUE),
				selectionTraitValue);
		selectionTraitVariable.setProperty(SelectionTraitResolver.SELECTION_TRAIT_PROPERTY);
		selectionTraitVariable.setDataType(DataType.NUMERIC_VARIABLE.getName());

		final String expectedSelectionTrait = this.selectionTraitResolver.resolveStudyLevelData(Arrays.asList(selectionTraitVariable));
		assertThat(expectedSelectionTrait, is(selectionTraitValue));
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

}
