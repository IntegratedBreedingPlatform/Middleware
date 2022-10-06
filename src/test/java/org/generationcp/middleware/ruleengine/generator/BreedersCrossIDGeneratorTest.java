package org.generationcp.middleware.ruleengine.generator;

import com.google.common.collect.Lists;
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
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class BreedersCrossIDGeneratorTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private StudyInstanceService studyInstanceService;

	@Mock
	private DatasetService datasetService;

	private static final Integer PROJECT_PREFIX_CATEGORY_ID = 3001;
	private static final String PROJECT_PREFIX_CATEGORY_VALUE = "Project_Prefix";

	private static final Integer HABITAT_DESIGNATION_CATEGORY_ID = 3002;
	private static final String HABITAT_DESIGNATION_CATEGORY_VALUE = "Habitat_Designation";

	private static final Integer SEASON_CATEGORY_ID = 10290;
	private static final String SEASON_CATEGORY_VALUE = "Dry Season";

	@InjectMocks
	private BreedersCrossIDGenerator breedersCrossIDGenerator;

	private List<StudyInstance> studyInstances;

	@Before
	public void setUp() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX]-[HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX]-[HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		breedersCrossIDGenerator.setGermplasmNamingProperties(germplasmNamingProperties);

		final Project testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType("maize"));

		final Variable projectPrefixVariable = new Variable();
		final Scale projectPrefixScale = new Scale();
		final TermSummary projectPrefixCategory = new TermSummary(PROJECT_PREFIX_CATEGORY_ID, PROJECT_PREFIX_CATEGORY_VALUE,
				PROJECT_PREFIX_CATEGORY_VALUE);
		projectPrefixScale.addCategory(projectPrefixCategory);
		projectPrefixVariable.setScale(projectPrefixScale);

		final Variable habitatDesignationVariable = new Variable();
		final Scale habitatDesignationScale = new Scale();
		final TermSummary habitatDesignationCategory = new TermSummary(HABITAT_DESIGNATION_CATEGORY_ID, HABITAT_DESIGNATION_CATEGORY_VALUE,
				HABITAT_DESIGNATION_CATEGORY_VALUE);
		habitatDesignationScale.addCategory(habitatDesignationCategory);
		habitatDesignationVariable.setScale(habitatDesignationScale);

		final Variable seasonVariable = new Variable();
		final Scale seasonScale = new Scale();
		final TermSummary seasonCategory = new TermSummary(SEASON_CATEGORY_ID, SEASON_CATEGORY_VALUE, SEASON_CATEGORY_VALUE);
		seasonScale.addCategory(seasonCategory);
		seasonVariable.setScale(seasonScale);

		final StudyInstance studyInstance = new StudyInstance();
		studyInstance.setInstanceNumber(1);
		studyInstance.setLocationAbbreviation("ABBR");
		studyInstances = new ArrayList<>();
		studyInstances.add(studyInstance);

	}

	@Test
	public void testGenerateBreedersCrossIDStudy() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("TestStudy");
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		studyDetails.setId(1);
		workbook.setStudyDetails(studyDetails);

		final AdvancingSource source = new AdvancingSource();
		source.setEnvironmentDatasetId(12);

		final MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		instance1LocationAbbrMV.setName(TermId.LOCATION_ABBR.name());
		final MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue("IND");
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		final MeasurementVariable instance1ProjectPrefix = new MeasurementVariable();
		instance1ProjectPrefix.setTermId(TermId.PROJECT_PREFIX.getId());
		instance1ProjectPrefix.setName(TermId.PROJECT_PREFIX.name());
		final MeasurementData instance1ProjectPrefixMD = new MeasurementData();
		instance1ProjectPrefixMD.setValue(PROJECT_PREFIX_CATEGORY_VALUE);
		instance1ProjectPrefixMD.setMeasurementVariable(instance1ProjectPrefix);

		final MeasurementVariable instance1HabitatDesignationMV = new MeasurementVariable();
		instance1HabitatDesignationMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		instance1HabitatDesignationMV.setName(TermId.HABITAT_DESIGNATION.name());
		final MeasurementData instance1HabitatDesignationMD = new MeasurementData();
		instance1HabitatDesignationMD.setValue(HABITAT_DESIGNATION_CATEGORY_VALUE);
		instance1HabitatDesignationMD.setMeasurementVariable(instance1HabitatDesignationMV);

		final MeasurementVariable instance1SeasonMV = new MeasurementVariable();
		instance1SeasonMV.setTermId(TermId.SEASON_VAR.getId());
		instance1SeasonMV.setName(TermId.SEASON_VAR.name());
		final MeasurementData instance1SeasonMD = new MeasurementData();
		instance1SeasonMD.setValue(SEASON_CATEGORY_VALUE);
		instance1SeasonMD.setMeasurementVariable(instance1SeasonMV);

		final MeasurementVariable instance1InstanceNumberMV = new MeasurementVariable();
		instance1InstanceNumberMV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1InstanceNumberMV.setName(TermId.TRIAL_INSTANCE_FACTOR.name());
		final MeasurementData instance1InstanceNumberMD = new MeasurementData();
		instance1InstanceNumberMD.setValue("1");
		instance1InstanceNumberMD.setMeasurementVariable(instance1InstanceNumberMV);

		final MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1InstanceNumberMD, instance1LocationAbbrMD,
				instance1ProjectPrefixMD, instance1HabitatDesignationMD, instance1SeasonMD));

		final Method breedingMethod = new Method();
		breedingMethod.setMname("Single cross");
		breedingMethod.setSnametype(5);
		breedingMethod.setPrefix("pre");
		breedingMethod.setSeparator("-");
		breedingMethod.setCount("[CIMCRS]");
		breedingMethod.setSuffix("suff");

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		final String expectedBreedersCrossId = PROJECT_PREFIX_CATEGORY_VALUE + "-" + HABITAT_DESIGNATION_CATEGORY_VALUE + "-"
				+ SEASON_CATEGORY_VALUE + "-" + instance1LocationAbbrMD.getValue();

		final List<MeasurementVariable> conditions = workbook.getConditions();

		Mockito.doReturn(instance1Measurements.getMeasurementVariables())
			.when(this.datasetService).getObservationSetVariables(anyInt(), ArgumentMatchers.<Integer>anyList());

		Mockito.doReturn(studyInstances).when(this.studyInstanceService).getStudyInstances(1);

		final String actualBreedersCrossId = this.breedersCrossIDGenerator.generateBreedersCrossID(workbook.getStudyDetails().getId(),
			source.getEnvironmentDatasetId(), conditions, fromMeasurementRow(instance1Measurements));

		Assert.assertEquals(expectedBreedersCrossId, actualBreedersCrossId);
	}
}
