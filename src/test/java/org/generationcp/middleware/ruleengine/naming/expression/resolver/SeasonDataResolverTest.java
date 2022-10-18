package org.generationcp.middleware.ruleengine.naming.expression.resolver;

import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.ValueReferenceTestDataInitializer;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SeasonDataResolverTest {

	public static final String SEASON_NAME_WET = "1";
	public static final String SEASON_NAME_DRY = "2";
	public static final String SEASON_DESCRIPTION_DRY = "Dry Season";
	public static final String SEASON_DESCRIPTION_WET = "Wet Season";
	public static final int SEASON_ID_WET = 10101;
	public static final int SEASON_ID_DRY = 20202;

	private static final String SEASON_CATEGORY_ID = "10290";
	private static final String SEASON_CATEGORY_VALUE = "Dry Season";
	private static final String SEASON_MONTH_VALUE = "201608";

	@InjectMocks
	private SeasonDataResolver seasonDataResolver;

	private ValueReferenceTestDataInitializer valueReferenceTestDataInitializer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		this.valueReferenceTestDataInitializer = new ValueReferenceTestDataInitializer();
	}

	@Test
	public void resolveStudyLevelData_withSeasonMonthVariable() {
		final MeasurementVariable
			seasonMeasurementVariable = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_MONTH.getId(),
			SEASON_MONTH_VALUE);

		final String expectedSeason = seasonDataResolver.resolveStudyLevelData(Arrays.asList(seasonMeasurementVariable));
		assertThat(expectedSeason, is(SEASON_MONTH_VALUE));
	}

	@Test
	public void resolveStudyLevelData_withSeasonVarTextVariable() {
		final MeasurementVariable seasonMeasurementVariable =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_VAR_TEXT.getId(),
				SEASON_CATEGORY_VALUE);

		final String expectedSeason = seasonDataResolver.resolveStudyLevelData(Arrays.asList(seasonMeasurementVariable));
		assertThat(expectedSeason, is(SEASON_CATEGORY_VALUE));
	}

	@Test
	public void resolveStudyLevelData_withSeasonVarVariable() {
		final MeasurementVariable seasonMeasurementVariable =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_VAR.getId(),
				SEASON_CATEGORY_VALUE);

		final String expectedSeason = seasonDataResolver.resolveStudyLevelData(Arrays.asList(seasonMeasurementVariable));
		assertThat(expectedSeason, is(SEASON_CATEGORY_VALUE));
	}

	@Test
	public void resolveStudyLevelData_withNumericSeasonVarVariable() {
		final MeasurementVariable seasonMeasurementVariable =
			MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_VAR.getId(),
				SEASON_CATEGORY_ID);
		seasonMeasurementVariable.setPossibleValues(Arrays.asList(this.valueReferenceTestDataInitializer
			.createValueReference(Integer.parseInt(SEASON_CATEGORY_ID), SEASON_CATEGORY_VALUE)));

		final String expectedSeason = seasonDataResolver.resolveStudyLevelData(Arrays.asList(seasonMeasurementVariable));
		assertThat(expectedSeason, is(SEASON_CATEGORY_VALUE));
	}

	@Test
	public void resolveStudyLevelData_withNoSeasonVariable() {
		final String expectedSeason = seasonDataResolver.resolveStudyLevelData(new ArrayList<>());
		assertThat(expectedSeason, is(""));
	}

	@Test
	public void resolvePlotLevelData_withSeasonMonthVariable() {
		this.mockAndVerifyResolvePlotLevelData(TermId.SEASON_MONTH, SEASON_MONTH_VALUE, null);
	}

	@Test
	public void resolvePlotLevelData_withSeasonVarTextVariable() {
		this.mockAndVerifyResolvePlotLevelData(TermId.SEASON_VAR_TEXT, SEASON_CATEGORY_VALUE, null);
	}

	@Test
	public void resolvePlotLevelData_withSeasonVarVariable() {
		this.mockAndVerifyResolvePlotLevelData(TermId.SEASON_VAR, SEASON_CATEGORY_VALUE, null);
	}

	@Test
	public void resolvePlotLevelData_withNumericSeasonVarVariable() {
		final List<ValueReference> possibleValues = Arrays.asList(this.valueReferenceTestDataInitializer
			.createValueReference(Integer.parseInt(SEASON_CATEGORY_ID), SEASON_CATEGORY_VALUE));
		this.mockAndVerifyResolvePlotLevelData(TermId.SEASON_VAR, SEASON_CATEGORY_VALUE, possibleValues);
	}

	@Test
	public void resolvePlotLevelData_withNoSeasonVariable() {
		final ObservationUnitData firstInstanceObservation = new ObservationUnitData(TermId.TRIAL_INSTANCE_FACTOR.getId(), "1");
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(TermId.TRIAL_INSTANCE_FACTOR.name(), firstInstanceObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setEnvironmentVariables(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrailInstanceObservation()).thenReturn(observationUnitRow);

		final MeasurementVariable firstInstanceMeasurementVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), firstInstanceMeasurementVariable);

		this.seasonDataResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);

		Mockito.verify(source).setSeason("");
	}

	private void mockAndVerifyResolvePlotLevelData(final TermId measurementVariableTermId, final String observationValue,
		final List<ValueReference> measurementVariablePossibleValues) {
		final MeasurementVariable firstInstanceSeasonMeasurementVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(measurementVariableTermId.getId(), null);
		firstInstanceSeasonMeasurementVariable.setPossibleValues(measurementVariablePossibleValues);
		final ObservationUnitData firstInstanceSeasonObservation =
			new ObservationUnitData(measurementVariableTermId.getId(), observationValue);

		final ObservationUnitData firstInstanceObservation = new ObservationUnitData(TermId.TRIAL_INSTANCE_FACTOR.getId(), "1");
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		environmentVariables.put(measurementVariableTermId.name(), firstInstanceSeasonObservation);
		environmentVariables.put(TermId.TRIAL_INSTANCE_FACTOR.name(), firstInstanceObservation);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setEnvironmentVariables(environmentVariables);

		final NewAdvancingSource source = Mockito.mock(NewAdvancingSource.class);
		Mockito.when(source.getTrailInstanceObservation()).thenReturn(observationUnitRow);

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId = new HashMap<>();
		plotDataVariablesByTermId.put(measurementVariableTermId.getId(), firstInstanceSeasonMeasurementVariable);

		final MeasurementVariable firstInstanceMeasurementVariable = MeasurementVariableTestDataInitializer
			.createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		plotDataVariablesByTermId.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), firstInstanceMeasurementVariable);

		this.seasonDataResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);

		Mockito.verify(source).setSeason(observationValue);
	}

	@Test
	public void testGetSeasonNameValueIsCategoricalId() {
		final List<ValueReference> possibleValues = this.createPossibleValues();

		assertThat(SEASON_NAME_WET, is(this.seasonDataResolver.getSeasonName(String.valueOf(SEASON_ID_WET), possibleValues)));
		assertThat(SEASON_NAME_DRY, is(this.seasonDataResolver.getSeasonName(String.valueOf(SEASON_ID_DRY), possibleValues)));
	}

	@Test
	public void testGetSeasonNameValueIsCategoricalDescription() {
		final List<ValueReference> possibleValues = this.createPossibleValues();

		assertThat(SEASON_NAME_WET, is(this.seasonDataResolver.getSeasonName(SEASON_DESCRIPTION_DRY, possibleValues)));
		assertThat(SEASON_NAME_DRY, is(this.seasonDataResolver.getSeasonName(SEASON_DESCRIPTION_WET, possibleValues)));
	}

	@Test
	public void testGetSeasonNamePossibleValuesIsNullOrEmpty() {
		assertThat(SEASON_NAME_WET, is(this.seasonDataResolver.getSeasonName(SEASON_NAME_WET, null)));
		assertThat(SEASON_NAME_WET, is(this.seasonDataResolver.getSeasonName(SEASON_NAME_WET, new ArrayList<>())));
	}

	private List<ValueReference> createPossibleValues() {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(valueReferenceTestDataInitializer.createValueReference(SEASON_ID_WET, SEASON_NAME_WET, SEASON_DESCRIPTION_DRY));
		possibleValues.add(valueReferenceTestDataInitializer.createValueReference(SEASON_ID_DRY, SEASON_NAME_DRY, SEASON_DESCRIPTION_WET));
		return possibleValues;

	}

}
