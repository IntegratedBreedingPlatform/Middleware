package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.collect.Lists;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

		final Map<Integer, Variable> variablesByTermId = new HashMap<>();
		variablesByTermId.put(TermId.PROJECT_PREFIX.getId(), variable);
		AdvanceContext.setVariablesByTermId(variablesByTermId);
	}

	@Test
	public void testResolveForNurseryWithProgramVariableAndValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TermId.PROJECT_PREFIX.getId());
		measurementVariable.setValue(PROJECT_CATEGORY_ID.toString());

		final ProjectPrefixResolver projectPrefixResolver =
			new ProjectPrefixResolver(this.ontologyVariableDataManager, Lists.newArrayList(measurementVariable),
				new ArrayList<>(), new HashMap<>());
		final String program = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in Nursery settings.",
				PROJECT_CATEGORY_VALUE, program);

	}

	@Test
	public void testResolveForTrialWithProgramVariableAndValue() {

		final MeasurementVariable instance1ProgramMV = new MeasurementVariable();
		instance1ProgramMV.setTermId(TermId.PROJECT_PREFIX.getId());
		final ObservationUnitData instance1ProgramMD = new ObservationUnitData();
		instance1ProgramMD.setValue(PROJECT_CATEGORY_VALUE);
		instance1ProgramMD.setVariableId(TermId.PROJECT_PREFIX.getId());

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final ObservationUnitData instance1MD = new ObservationUnitData();
		instance1MD.setValue("1");
		instance1MD.setVariableId(TermId.TRIAL_INSTANCE_FACTOR.getId());

		final Map<Integer, MeasurementVariable> environmentVariablesByTermId = new HashMap<>();
		environmentVariablesByTermId.put(TermId.PROJECT_PREFIX.getId(), instance1ProgramMV);
		environmentVariablesByTermId.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), instance1MV);

		final ProjectPrefixResolver projectPrefixResolver =
			new ProjectPrefixResolver(this.ontologyVariableDataManager, new ArrayList<>(),
				Arrays.asList(instance1ProgramMD, instance1MD), environmentVariablesByTermId);
		final String season = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in environment level settings.",
				PROJECT_CATEGORY_VALUE, season);
	}
	
	@Test
	public void testResolveForStudyWithProgramVariableConditions() {
		final MeasurementVariable instance1ProgramMV = new MeasurementVariable();
		instance1ProgramMV.setTermId(TermId.PROJECT_PREFIX.getId());
		instance1ProgramMV.setValue(PROJECT_CATEGORY_VALUE);
		
		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");
		
		final List<MeasurementVariable> studyEnvironmentVariables = new ArrayList<>();
		studyEnvironmentVariables.add(instance1MV);
		studyEnvironmentVariables.add(instance1ProgramMV);

		final ProjectPrefixResolver projectPrefixResolver =
			new ProjectPrefixResolver(this.ontologyVariableDataManager, studyEnvironmentVariables,
				new ArrayList<>(), new HashMap<>());
		final String season = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in environment level settings.",
				PROJECT_CATEGORY_VALUE, season);
	}
}
