package org.generationcp.middleware.ruleengine.generator;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BreedersCrossIDGeneratorTest {

	private static final String PROJECT_PREFIX_CATEGORY_VALUE = "Project_Prefix";

	private static final String HABITAT_DESIGNATION_CATEGORY_VALUE = "Habitat_Designation";

	private static final String SEASON_CATEGORY_VALUE = "Dry Season";

	@InjectMocks
	private BreedersCrossIDGenerator breedersCrossIDGenerator;

	@Before
	public void setUp() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX]-[HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX]-[HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		breedersCrossIDGenerator.setGermplasmNamingProperties(germplasmNamingProperties);
	}

	@Test
	public void testGenerateBreedersCrossIDStudy() {

		final List<ObservationUnitData> observations = new ArrayList<>();
		observations.add(new ObservationUnitData(TermId.LOCATION_ABBR.getId(), "IND"));
		observations.add(new ObservationUnitData(TermId.PROJECT_PREFIX.getId(), PROJECT_PREFIX_CATEGORY_VALUE));
		observations.add(new ObservationUnitData(TermId.HABITAT_DESIGNATION.getId(), HABITAT_DESIGNATION_CATEGORY_VALUE));
		observations.add(new ObservationUnitData(TermId.SEASON_VAR.getId(), SEASON_CATEGORY_VALUE));
		observations.add(new ObservationUnitData(TermId.TRIAL_INSTANCE_FACTOR.getId(), "1"));

		final Method breedingMethod = new Method();
		breedingMethod.setMname("Single cross");
		breedingMethod.setSnametype(5);
		breedingMethod.setPrefix("pre");
		breedingMethod.setSeparator("-");
		breedingMethod.setCount("[CIMCRS]");
		breedingMethod.setSuffix("suff");

		final MeasurementVariable instance1SeasonMV = new MeasurementVariable();
		instance1SeasonMV.setTermId(TermId.SEASON_VAR.getId());
		instance1SeasonMV.setName(TermId.SEASON_VAR.name());

		final MeasurementVariable instance1InstanceNumberMV = new MeasurementVariable();
		instance1InstanceNumberMV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1InstanceNumberMV.setName(TermId.TRIAL_INSTANCE_FACTOR.name());

		final String expectedBreedersCrossId = PROJECT_PREFIX_CATEGORY_VALUE + "-" + HABITAT_DESIGNATION_CATEGORY_VALUE + "-"
			+ SEASON_CATEGORY_VALUE + "-IND";

		final List<MeasurementVariable> studyEnvironmentVariables = new ArrayList<>();
		studyEnvironmentVariables.add(instance1InstanceNumberMV);

		AdvanceContext.setStudyEnvironmentVariables(studyEnvironmentVariables);
		AdvanceContext.setLocationsNamesByIds(new HashMap<>());

		final HashMap<Integer, MeasurementVariable> environmentVariablesByTermId = new HashMap<>();
		environmentVariablesByTermId.put(TermId.SEASON_VAR.getId(), instance1SeasonMV);
		AdvanceContext.setEnvironmentVariablesByTermId(environmentVariablesByTermId);

		final String actualBreedersCrossId =
			this.breedersCrossIDGenerator.generateBreedersCrossID(observations);

		Assert.assertEquals(expectedBreedersCrossId, actualBreedersCrossId);
	}
}
