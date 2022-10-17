package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.collect.Lists;
import org.generationcp.middleware.ContextHolder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.generationcp.middleware.ruleengine.resolver.SeasonResolverTest.getMeasurementVariableByTermId;
import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

public class ProjectPrefixResolverTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	private static final Integer PROJECT_CATEGORY_ID = 3001;
	private static final String PROJECT_CATEGORY_VALUE = "Project_Prefix";
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);

		final Variable variable = new Variable();
		final Scale seasonScale = new Scale();
		final TermSummary categories = new TermSummary(PROJECT_CATEGORY_ID, PROJECT_CATEGORY_VALUE, PROJECT_CATEGORY_VALUE);
		seasonScale.addCategory(categories);
		variable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(ArgumentMatchers.eq(PROGRAM_UUID),
			ArgumentMatchers.eq(TermId.PROJECT_PREFIX.getId()), ArgumentMatchers.eq(true))).thenReturn(variable);
	}

	@Test
	public void testResolveForNurseryWithProgramVariableAndValue() {

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getNurseryDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TermId.PROJECT_PREFIX.getId());
		measurementVariable.setValue(PROJECT_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(measurementVariable));

		final MeasurementRow trialInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final StudyTypeDto studyType = workbook.getStudyDetails().getStudyType();

		final ProjectPrefixResolver projectPrefixResolver =
			new ProjectPrefixResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation), getMeasurementVariableByTermId(trialInstanceObservation));
		final String program = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in Nursery settings.",
				PROJECT_CATEGORY_VALUE, program);

	}

	@Test
	public void testResolveForTrialWithProgramVariableAndValue() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1ProgramMV = new MeasurementVariable();
		instance1ProgramMV.setTermId(TermId.PROJECT_PREFIX.getId());
		final MeasurementData instance1ProgramMD = new MeasurementData();
		instance1ProgramMD.setValue(PROJECT_CATEGORY_VALUE);
		instance1ProgramMD.setMeasurementVariable(instance1ProgramMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(instance1MD, instance1ProgramMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		final ProjectPrefixResolver projectPrefixResolver =
			new ProjectPrefixResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation), getMeasurementVariableByTermId(trialInstanceObservation));
		final String season = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in environment level settings.",
				PROJECT_CATEGORY_VALUE, season);
	}
	
	@Test
	public void testResolveForStudyWithProgramVariableConditions() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1ProgramMV = new MeasurementVariable();
		instance1ProgramMV.setTermId(TermId.PROJECT_PREFIX.getId());
		instance1ProgramMV.setValue(PROJECT_CATEGORY_VALUE);
		
		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");
		
		final MeasurementRow trialInstanceObservation = null;
		
		final List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();
		conditions.add(instance1MV);
		conditions.add(instance1ProgramMV);
		workbook.setConditions(conditions );

		final StudyTypeDto studyType = workbook.getStudyDetails().getStudyType();

		final ProjectPrefixResolver projectPrefixResolver =
			new ProjectPrefixResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation), getMeasurementVariableByTermId(trialInstanceObservation));
		final String season = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in environment level settings.",
				PROJECT_CATEGORY_VALUE, season);
	}
}
