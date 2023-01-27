
package org.generationcp.middleware.ruleengine.generator;

import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
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
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.ruleengine.pojo.ImportedCross;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasmParent;
import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.generationcp.middleware.service.api.dataset.ObservationUnitUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This is more of an integration test (not a pure unit test) of all key code generation pieces for the seed source use case.
 */
@RunWith(MockitoJUnitRunner.class)
public class SeedSourceGeneratorTest {

	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@InjectMocks
	private SeedSourceGenerator seedSourceGenerator;

	@Before
	public void setUp() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setGermplasmOriginStudiesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO][SELECTION_NUMBER]");
		germplasmNamingProperties.setGermplasmOriginStudiesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginStudiesDefault("[NAME]:[LOCATION]:[SEASON]:[PLOTNO]:[PLANT_NO]");

		seedSourceGenerator.setGermplasmNamingProperties(germplasmNamingProperties);

	}

	private void setCurrentCrop(final String crop) {
		ContextHolder.setCurrentCrop(crop);
		ContextHolder.setCurrentProgram(PROGRAM_UUID);

		final Variable seasonVariable = new Variable();
		final Scale seasonScale = new Scale();
		final TermSummary seasonCategory = new TermSummary(TermId.SEASON_DRY.getId(), Season.DRY.getDefinition(), Season.DRY.getDefinition());
		seasonScale.addCategory(seasonCategory);
		seasonVariable.setScale(seasonScale);

		final Map<Integer, Variable> variablesByTermId = new HashMap<>();
		variablesByTermId.put(TermId.SEASON_VAR.getId(), seasonVariable);
		AdvanceContext.setVariablesByTermId(variablesByTermId);
	}

	@Test
	public void testGenerateSeedSourceStudy() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("TestStudy");
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		final int studyId = 1;
		studyDetails.setId(studyId);
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		instance1LocationAbbrMV.setName(TermId.LOCATION_ABBR.name());
		final MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue("IND");
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		final MeasurementVariable instance1SeasonMV = new MeasurementVariable();
		instance1SeasonMV.setTermId(TermId.SEASON_VAR.getId());
		instance1SeasonMV.setName(TermId.SEASON_VAR.name());
		final MeasurementData instance1SeasonMD = new MeasurementData();
		instance1SeasonMD.setValue(Season.DRY.getDefinition());
		instance1SeasonMD.setMeasurementVariable(instance1SeasonMV);

		final MeasurementVariable instance1InstanceNumberMV = new MeasurementVariable();
		instance1InstanceNumberMV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1InstanceNumberMV.setName(TermId.TRIAL_INSTANCE_FACTOR.name());
		final MeasurementData instance1InstanceNumberMD = new MeasurementData();
		instance1InstanceNumberMD.setValue("1");
		instance1InstanceNumberMD.setMeasurementVariable(instance1InstanceNumberMV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1InstanceNumberMD, instance1LocationAbbrMD, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		setCurrentCrop("rice");
		String seedSource = this.seedSourceGenerator
			.generateSeedSource(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)).getVariables().values(), null, "2", "3",
				studyDetails.getStudyName(), null, null, null, instance1Measurements.getMeasurementVariables());
		Assert.assertEquals("TestStudy:IND:Dry season:3:", seedSource);

		// with Plant Number
		seedSource = this.seedSourceGenerator.generateSeedSource(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)).getVariables().values(), null, "2", "3",
			studyDetails.getStudyName(), "4", null, null, instance1Measurements.getMeasurementVariables());
		Assert.assertEquals("TestStudy:IND:Dry season:3:4", seedSource);

		setCurrentCrop("wheat");
		seedSource = this.seedSourceGenerator.generateSeedSource(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)).getVariables().values(), null, "2", "3",
			studyDetails.getStudyName(), null, null, null, instance1Measurements.getMeasurementVariables());
		Assert.assertEquals("IND\\Dry season\\TestStudy\\3", seedSource);

		setCurrentCrop("maize");
		// with selection number
		seedSource = this.seedSourceGenerator.generateSeedSource(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)).getVariables().values(), null, "2", "3",
			studyDetails.getStudyName(), null, null, null, instance1Measurements.getMeasurementVariables());
		Assert.assertEquals("INDDry season-TestStudy-3-2", seedSource);
		// without selection number
		seedSource = this.seedSourceGenerator.generateSeedSource(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)).getVariables().values(), null, null, "3",
			studyDetails.getStudyName(), null, null, null, instance1Measurements.getMeasurementVariables());
		Assert.assertEquals("INDDry season-TestStudy-3", seedSource);
	}
	
	@Test
	public void testGenerateSeedSourceForUnknownPlot() {
		Assert.assertEquals(Name.UNKNOWN, this.seedSourceGenerator.generateSeedSource(new ArrayList<>(),
			null, RandomStringUtils.randomNumeric(2), "0", RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomNumeric(2), null,
			null, null));
	}

	@Test
	public void testGenerateSeedSourceForCross() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("StudyName");
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		studyDetails.setId(1);
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable locationMV = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.LOCATION_ABBR.getId(), "IND");
		final MeasurementVariable seasonMV = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_VAR.getId(), Season.DRY.getDefinition());
		workbook.setConditions(Lists.newArrayList(locationMV, seasonMV));

		setCurrentCrop("maize");
		final List<String> malePlotNos = Arrays.asList("1", "3", "4");
		String generatedSeedSources =
			this.seedSourceGenerator.generateSeedSourceForCross(
				Pair.of(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)), ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1))), Pair.of(workbook.getConditions(), workbook.getConditions()),
				Pair.of(Collections.emptyMap(), Collections.emptyMap()), Pair.of(Collections.emptyMap(), Collections.emptyMap()),
				Pair.of(Collections.emptyList(), Collections.emptyList()), this.getCross(malePlotNos, "2", "StudyName", "StudyName"));
		List<String> expectedResultsString = new ArrayList<>();
		for(final String malePlotNo: malePlotNos) {
			expectedResultsString.add("INDDry season-StudyName-" + malePlotNo);
		}
		String expectedString = "INDDry season-StudyName-2/[" + StringUtils.join(expectedResultsString, ", ") + "]";
		Assert.assertEquals(expectedString, generatedSeedSources);

		setCurrentCrop("rice");
		generatedSeedSources =
			this.seedSourceGenerator.generateSeedSourceForCross(
				Pair.of(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)), ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1))), Pair.of(workbook.getConditions(), workbook.getConditions()),
				Pair.of(Collections.emptyMap(), Collections.emptyMap()), Pair.of(Collections.emptyMap(), Collections.emptyMap()),
				Pair.of(Collections.emptyList(), Collections.emptyList()), this.getCross(malePlotNos, "2", "StudyName", "StudyName"));
		expectedResultsString = new ArrayList<>();
		for(final String malePlotNo: malePlotNos) {
			expectedResultsString.add("StudyName:IND:Dry season:"+malePlotNo+":");
		}
		expectedString = "StudyName:IND:Dry season:2:/[" + StringUtils.join(expectedResultsString, ", ") + "]";
		Assert.assertEquals(expectedString, generatedSeedSources);

		//For scenario where there's only one male plot number
		generatedSeedSources =
			this.seedSourceGenerator.generateSeedSourceForCross(
				Pair.of(ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1)), ObservationUnitUtils.fromMeasurementRow(workbook.getTrialObservationByTrialInstanceNo(1))), Pair.of(workbook.getConditions(), workbook.getConditions()),
				Pair.of(Collections.emptyMap(), Collections.emptyMap()), Pair.of(Collections.emptyMap(), Collections.emptyMap()),
				Pair.of(Collections.emptyList(), Collections.emptyList()), this.getCross(Collections.singletonList("1"), "2", "StudyName", "StudyName"));
		Assert.assertEquals("StudyName:IND:Dry season:2:/StudyName:IND:Dry season:1:", generatedSeedSources);
	}

	@Test
	public void testGenerateSeedSourceForCrossWhereMaleAndFemaleStudyAreDifferent() {
		final Workbook femaleStudyWorkbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("femaleStudyName");
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		studyDetails.setId(1);
		femaleStudyWorkbook.setStudyDetails(studyDetails);

		final MeasurementVariable locationMV = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.LOCATION_ABBR.getId(), "IND");
		final MeasurementVariable seasonMV = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_VAR.getId(), Season.DRY.getDefinition());
		femaleStudyWorkbook.setConditions(Lists.newArrayList(locationMV, seasonMV));

		final Workbook maleStudyWorkbook = new Workbook();
		final StudyDetails maleStudyDetails = new StudyDetails();
		maleStudyDetails.setStudyName("maleStudyName");
		maleStudyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		maleStudyDetails.setId(1);
		maleStudyWorkbook.setStudyDetails(maleStudyDetails);

		final MeasurementVariable malelocationMV = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.LOCATION_ABBR.getId(), "CIMMYT");
		final MeasurementVariable maleSeasonMv = MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.SEASON_VAR.getId(), Season.WET.getDefinition());
		maleStudyWorkbook.setConditions(Lists.newArrayList(malelocationMV, maleSeasonMv));

		setCurrentCrop("maize");
		final List<String> malePlotNos = Arrays.asList("1", "3", "4");
		String generatedSeedSources =
			this.seedSourceGenerator.generateSeedSourceForCross(
				Pair.of(ObservationUnitUtils.fromMeasurementRow(femaleStudyWorkbook.getTrialObservationByTrialInstanceNo(1)), ObservationUnitUtils.fromMeasurementRow(maleStudyWorkbook.getTrialObservationByTrialInstanceNo(1))), Pair.of(femaleStudyWorkbook.getConditions(), maleStudyWorkbook.getConditions()),
				Pair.of(Collections.emptyMap(), Collections.emptyMap()), Pair.of(Collections.emptyMap(), Collections.emptyMap()),
				Pair.of(Collections.emptyList(), Collections.emptyList()),
				this.getCross(malePlotNos, "2", "maleStudyName", "femaleStudyName"));
		List<String> expectedResultsString = new ArrayList<>();
		for(final String malePlotNo: malePlotNos) {
			expectedResultsString.add("CIMMYTWet season-maleStudyName-"+malePlotNo);
		}
		String expectedString = "INDDry season-femaleStudyName-2/[" + StringUtils.join(expectedResultsString, ", ") + "]";
		Assert.assertEquals(expectedString, generatedSeedSources);

		setCurrentCrop("rice");
		generatedSeedSources =
			this.seedSourceGenerator.generateSeedSourceForCross(
				Pair.of(ObservationUnitUtils.fromMeasurementRow(femaleStudyWorkbook.getTrialObservationByTrialInstanceNo(1)), ObservationUnitUtils.fromMeasurementRow(maleStudyWorkbook.getTrialObservationByTrialInstanceNo(1))), Pair.of(femaleStudyWorkbook.getConditions(), maleStudyWorkbook.getConditions()),
				Pair.of(Collections.emptyMap(), Collections.emptyMap()), Pair.of(Collections.emptyMap(), Collections.emptyMap()),
				Pair.of(Collections.emptyList(), Collections.emptyList()),
				this.getCross(malePlotNos, "2", "maleStudyName", "femaleStudyName"));
		expectedResultsString = new ArrayList<>();
		for(final String malePlotNo: malePlotNos) {
			expectedResultsString.add("maleStudyName:CIMMYT:Wet season:"+malePlotNo+":");
		}
		expectedString = "femaleStudyName:IND:Dry season:2:/[" + StringUtils.join(expectedResultsString, ", ") + "]";
		Assert.assertEquals(expectedString, generatedSeedSources);

		//For scenario where there's only one male plot number
		generatedSeedSources =
			this.seedSourceGenerator.generateSeedSourceForCross(
				Pair.of(ObservationUnitUtils.fromMeasurementRow(femaleStudyWorkbook.getTrialObservationByTrialInstanceNo(1)), ObservationUnitUtils.fromMeasurementRow(maleStudyWorkbook.getTrialObservationByTrialInstanceNo(1))), Pair.of(femaleStudyWorkbook.getConditions(), maleStudyWorkbook.getConditions()),
				Pair.of(Collections.emptyMap(), Collections.emptyMap()), Pair.of(Collections.emptyMap(), Collections.emptyMap()),
				Pair.of(Collections.emptyList(), Collections.emptyList()),
				this.getCross(Collections.singletonList("1"), "2", "maleStudyName", "femaleStudyName"));
		Assert.assertEquals("femaleStudyName:IND:Dry season:2:/maleStudyName:CIMMYT:Wet season:1:", generatedSeedSources);
	}

	private ImportedCross getCross(final List<String> malePlotNos, final String femalePlotNo, final String maleStudyName, final String femaleStudyName) {
		final ImportedCross crossInfo = new ImportedCross();
		crossInfo.setFemaleParent(
			new ImportedGermplasmParent(null, null, StringUtils.isEmpty(femalePlotNo) ? null : Integer.valueOf(femalePlotNo),
				femaleStudyName));

		final List<ImportedGermplasmParent> crossMaleParents = new ArrayList<>();
		for (final String malePlotNo : malePlotNos) {
			crossMaleParents.add(
				new ImportedGermplasmParent(null, null, StringUtils.isEmpty(malePlotNo) ? null : Integer.valueOf(malePlotNo),
					maleStudyName));
		}
		crossInfo.setMaleParents(crossMaleParents);
		return crossInfo;
	}

}
