package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.collect.Lists;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HabitatDesignationResolverTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	private static final Integer HABITAT_CATEGORY_ID = 3002;
	private static final String HABITAT_CATEGORY_VALUE = "Habitat_Designation";
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);

		final Variable variable = new Variable();
		final Scale seasonScale = new Scale();
		final TermSummary categories = new TermSummary(HABITAT_CATEGORY_ID, HABITAT_CATEGORY_VALUE, HABITAT_CATEGORY_VALUE);
		seasonScale.addCategory(categories);
		variable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(ArgumentMatchers.eq(PROGRAM_UUID),
			ArgumentMatchers.eq(TermId.HABITAT_DESIGNATION.getId()), ArgumentMatchers.eq(true))).thenReturn(variable);
	}

	@Test
	public void testResolveForNurseryWithHabitatVariableAndValue() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TermId.HABITAT_DESIGNATION.getId());
		measurementVariable.setValue(HABITAT_CATEGORY_ID.toString());

		final HabitatDesignationResolver habitatDesignationResolver =
			new HabitatDesignationResolver(this.ontologyVariableDataManager, Lists.newArrayList(measurementVariable),
				new ArrayList<>(), new HashMap<>());
		final String designation = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in Nursery settings.",
				HABITAT_CATEGORY_VALUE, designation);

	}

	@Test
	public void testResolveForTrialWithHabitatVariableAndValue() {

		final MeasurementVariable instance1HabitatMV = new MeasurementVariable();
		instance1HabitatMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		final ObservationUnitData instance1Habitat = new ObservationUnitData();
		instance1Habitat.setValue(HABITAT_CATEGORY_VALUE);
		instance1Habitat.setVariableId(TermId.HABITAT_DESIGNATION.getId());

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final ObservationUnitData instance1MD = new ObservationUnitData();
		instance1MD.setValue("1");
		instance1MD.setVariableId(TermId.TRIAL_INSTANCE_FACTOR.getId());

		final Map<Integer, MeasurementVariable> environmentVariablesByTermId = new HashMap<>();
		environmentVariablesByTermId.put(TermId.HABITAT_DESIGNATION.getId(), instance1HabitatMV);
		environmentVariablesByTermId.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), instance1MV);
		
		final HabitatDesignationResolver habitatDesignationResolver =
			new HabitatDesignationResolver(this.ontologyVariableDataManager, new ArrayList<>(),
				Arrays.asList(instance1Habitat, instance1MD), environmentVariablesByTermId);
		final String season = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in environment level settings.",
				HABITAT_CATEGORY_VALUE, season);
	}
	
	@Test
	public void testResolveForStudyWithHabitatConditions() {

		final MeasurementVariable instance1HabitatMV = new MeasurementVariable();
		instance1HabitatMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		instance1HabitatMV.setValue(HABITAT_CATEGORY_VALUE);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");
		
		final List<MeasurementVariable> studyEnvironmentVariables = new ArrayList<>();
		studyEnvironmentVariables.add(instance1MV);
		studyEnvironmentVariables.add(instance1HabitatMV);

		final HabitatDesignationResolver habitatDesignationResolver =
			new HabitatDesignationResolver(this.ontologyVariableDataManager, studyEnvironmentVariables,
				new ArrayList<>(), new HashMap<>());
		final String season = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in environment level settings.",
				HABITAT_CATEGORY_VALUE, season);
	}
}
