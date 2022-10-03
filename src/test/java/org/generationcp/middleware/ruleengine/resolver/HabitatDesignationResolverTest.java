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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.generationcp.middleware.ruleengine.resolver.SeasonResolverTest.getMeasurementVariableByTermId;
import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

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

		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(new StudyTypeDto("N"));
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TermId.HABITAT_DESIGNATION.getId());
		measurementVariable.setValue(HABITAT_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(measurementVariable));

		final MeasurementRow trialInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final StudyTypeDto studyType = workbook.getStudyDetails().getStudyType();

		final HabitatDesignationResolver habitatDesignationResolver =
			new HabitatDesignationResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation), getMeasurementVariableByTermId(trialInstanceObservation));
		final String designation = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in Nursery settings.",
				HABITAT_CATEGORY_VALUE, designation);

	}

	@Test
	public void testResolveForTrialWithHabitatVariableAndValue() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1HabitatMV = new MeasurementVariable();
		instance1HabitatMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		final MeasurementData instance1Habitat = new MeasurementData();
		instance1Habitat.setValue(HABITAT_CATEGORY_VALUE);
		instance1Habitat.setMeasurementVariable(instance1HabitatMV);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		final MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(instance1MD, instance1Habitat));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));
		
		final StudyTypeDto studyType = workbook.getStudyDetails().getStudyType();

		final HabitatDesignationResolver habitatDesignationResolver =
			new HabitatDesignationResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation), getMeasurementVariableByTermId(trialInstanceObservation));
		final String season = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in environment level settings.",
				HABITAT_CATEGORY_VALUE, season);
	}
	
	@Test
	public void testResolveForStudyWithHabitatConditions() {
		final Workbook workbook = new Workbook();
		final StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyTypeDto.getTrialDto());
		workbook.setStudyDetails(studyDetails);

		final MeasurementVariable instance1HabitatMV = new MeasurementVariable();
		instance1HabitatMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		instance1HabitatMV.setValue(HABITAT_CATEGORY_VALUE);

		final MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");
		
		final MeasurementRow trialInstanceObservation = null;

		final List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();
		conditions.add(instance1MV);
		conditions.add(instance1HabitatMV);
		workbook.setConditions(conditions );

		final StudyTypeDto studyType = workbook.getStudyDetails().getStudyType();

		final HabitatDesignationResolver habitatDesignationResolver =
			new HabitatDesignationResolver(this.ontologyVariableDataManager, workbook.getConditions(),
				fromMeasurementRow(trialInstanceObservation), getMeasurementVariableByTermId(trialInstanceObservation));
		final String season = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in environment level settings.",
				HABITAT_CATEGORY_VALUE, season);
	}
}
