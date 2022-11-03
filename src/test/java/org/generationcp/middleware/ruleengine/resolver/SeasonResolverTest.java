package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.data.initializer.ValueReferenceTestDataInitializer;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementData;
import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

public class SeasonResolverTest {


	private ValueReferenceTestDataInitializer valueReferenceTestDataInitializer;

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	private static final Integer SEASON_CATEGORY_ID = 10290;
	private static final String SEASON_CATEGORY_NAME_VALUE = "1";
	private static final String SEASON_CATEGORY_DESCRIPTION_VALUE = "Dry Season";
	public static final String DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES = "Description not found in possible values";
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);

		final Variable seasonVariable = new Variable();
		final Scale seasonScale = new Scale();
		final TermSummary seasonCategory = new TermSummary(SEASON_CATEGORY_ID, SEASON_CATEGORY_NAME_VALUE, SEASON_CATEGORY_DESCRIPTION_VALUE);
		seasonScale.addCategory(seasonCategory);
		seasonVariable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(ArgumentMatchers.eq(PROGRAM_UUID),
			ArgumentMatchers.eq(TermId.SEASON_VAR.getId()), ArgumentMatchers.eq(true))).thenReturn(seasonVariable);
		this.valueReferenceTestDataInitializer = new ValueReferenceTestDataInitializer();
	}

	@Test
	public void testResolveForNurseryWithSeasonVariableAndValue() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(new StudyTypeDto("N"));
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		seasonMV.setValue(SEASON_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(seasonMV));

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				new ArrayList<>(), new HashMap<>());

		final String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in Nursery settings.",
				SEASON_CATEGORY_NAME_VALUE, season);

	}

	@Test
	public void testResolveForNurseryWithSeasonVariableButNoValue() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		// Variable presnet but no value

		workbook.setConditions(Lists.newArrayList(seasonMV));

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				new ArrayList<>(), new HashMap<>());

		final String season = seasonResolver.resolve();

		final SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		final String currentYearAndMonth = formatter.format(new java.util.Date());

		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable is present but has no value.",
				currentYearAndMonth, season);
	}

	@Test
	public void testResolveForNurseryWithoutSeasonVariable() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				new ArrayList<>(), new HashMap<>());

		final String season = seasonResolver.resolve();

		final SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		final String currentYearAndMonth = formatter.format(new java.util.Date());

		Assert.assertEquals("Season should be defaulted to current year and month when Crop_season_Code variable is not present.",
				currentYearAndMonth,
				season);
	}

	@Test
	public void testResolveForTrialWithSeasonVariableAndValue() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		final MeasurementData instance1SeasonMD = new MeasurementData();
		instance1SeasonMD.setValue(SEASON_CATEGORY_DESCRIPTION_VALUE);
		instance1SeasonMD.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		final MeasurementVariable firstInstanceMeasurementVariable = new MeasurementVariable();
		firstInstanceMeasurementVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData firstInstanceMeasurementData = new MeasurementData();
		firstInstanceMeasurementData.setValue("1");
		firstInstanceMeasurementData.setMeasurementVariable(firstInstanceMeasurementVariable);

		final MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(firstInstanceMeasurementData, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation).getVariables().values(), getMeasurementVariableByTermId(trialInstanceObservation));

		final String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in environment level settings.",
				SEASON_CATEGORY_NAME_VALUE, season);
	}

	@Test
	public void testResolveForStudyWithSeasonVariableButNoValue() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		final MeasurementData instance1SeasonMD = new MeasurementData();
		// Variable present but has no value
		instance1SeasonMD.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		final MeasurementVariable firstInstanceMeasurementVariable = new MeasurementVariable();
		firstInstanceMeasurementVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData firstInstanceMeasurementData = new MeasurementData();
		firstInstanceMeasurementData.setValue("1");
		firstInstanceMeasurementData.setMeasurementVariable(firstInstanceMeasurementVariable);

		final MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(firstInstanceMeasurementData, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation).getVariables().values(), getMeasurementVariableByTermId(trialInstanceObservation));

		final String season = seasonResolver.resolve();

		final SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		final String currentYearAndMonth = formatter.format(new java.util.Date());

		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable in environment level settings, is present but has no value.",
				currentYearAndMonth, season);
	}

	@Test
	public void testResolveForStudyWithoutSeasonVariable() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		final String currentYearAndMonth = formatter.format(new java.util.Date());

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				new ArrayList<>(), new HashMap<>());

		final String season = seasonResolver.resolve();
		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable is not present in environment level settings.",
				currentYearAndMonth, season);
	}

	@Test
	public void testGetValueFromStudyInstanceMeasurementDataSeasonDesscriptionIsPresentInPossibleValues() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		final MeasurementData firstInstanceSeasonMeasurementData = new MeasurementData();
		firstInstanceSeasonMeasurementData.setValue(SEASON_CATEGORY_DESCRIPTION_VALUE);
		firstInstanceSeasonMeasurementData.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		final MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(firstInstanceSeasonMeasurementData));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation).getVariables().values(), getMeasurementVariableByTermId(trialInstanceObservation));

		Assert.assertEquals("The method should return the Season Name, not the Season Description.", SEASON_CATEGORY_NAME_VALUE,
			seasonResolver.getValueFromObservationUnitData(fromMeasurementData(firstInstanceSeasonMeasurementData)));

	}

	@Test
	public void testGetValueFromStudyInstanceMeasurementDataSeasonDesscriptionDoesNotExistInPossibleValues() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		final MeasurementData firstInstanceSeasonMeasurementData = new MeasurementData();
		firstInstanceSeasonMeasurementData.setValue(DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES);
		firstInstanceSeasonMeasurementData.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		final MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(firstInstanceSeasonMeasurementData));
		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation).getVariables().values(), getMeasurementVariableByTermId(trialInstanceObservation));

		Assert.assertEquals("The method should return the Season Measurement Data value as it is since the value is not found in possible values.",
			DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES,
			seasonResolver.getValueFromObservationUnitData(fromMeasurementData(firstInstanceSeasonMeasurementData)));

	}

	@Test
	public void testFindValueReferenceByDescriptionPossibleValues() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				new ArrayList<>(), new HashMap<>());

		final Optional<ValueReference> result1 = seasonResolver.findValueReferenceByDescription(SEASON_CATEGORY_DESCRIPTION_VALUE, null);
		Assert.assertFalse(result1.isPresent());

		final Optional<ValueReference> result2 = seasonResolver.findValueReferenceByDescription(SEASON_CATEGORY_DESCRIPTION_VALUE, this.createTestPossibleValuesForSeasonVariable());
		Assert.assertTrue(result2.isPresent());

		final Optional<ValueReference> result3 = seasonResolver.findValueReferenceByDescription(DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES, this.createTestPossibleValuesForSeasonVariable());
		Assert.assertFalse(result3.isPresent());


	}

	@Test
	public void testResolveForStudyWithSeasonVariableConditions() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		firstInstanceSeasonMeasurementVariable.setValue(SEASON_CATEGORY_DESCRIPTION_VALUE);

		final MeasurementVariable firstInstanceMeasurementVariable = new MeasurementVariable();
		firstInstanceMeasurementVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		firstInstanceMeasurementVariable.setValue("1");
		
		final List<MeasurementVariable> conditions = new ArrayList<>();
		conditions.add(firstInstanceSeasonMeasurementVariable);
		conditions.add(firstInstanceMeasurementVariable);
		workbook.setConditions(conditions);

		final SeasonResolver seasonResolver =
			new SeasonResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				new ArrayList<>(), new HashMap<>());

		final String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in environment level settings.",
				SEASON_CATEGORY_NAME_VALUE, season);
	}
	
	private List<ValueReference> createTestPossibleValuesForSeasonVariable() {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(this.valueReferenceTestDataInitializer.createValueReference(SEASON_CATEGORY_ID, SEASON_CATEGORY_NAME_VALUE, SEASON_CATEGORY_DESCRIPTION_VALUE));
		return possibleValues;
	}

	protected static Map<Integer, MeasurementVariable> getMeasurementVariableByTermId(final MeasurementRow trialInstanceObservation) {
		if (trialInstanceObservation == null) {
            return new HashMap<>();
		}
		return Maps.uniqueIndex(trialInstanceObservation.getMeasurementVariables(), new Function<MeasurementVariable, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final MeasurementVariable measurementVariable) {
				return measurementVariable.getTermId();
			}
		});
	}
}
