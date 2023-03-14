package org.generationcp.middleware.operation.saver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ExperimentModelSaverTest extends IntegrationTestBase {

	private static final String CROP_PREFIX = RandomStringUtils.randomAlphanumeric(5);

	private static final Integer USER_ID = 1;

	private ExperimentModelSaver experimentModelSaver;
	private ExperimentDao experimentDao;
	private PhenotypeDao phenotypeDao;

	@Before
	public void setUp() throws Exception {
		this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
		this.experimentDao = new ExperimentDao(this.sessionProvder.getSession());
		this.phenotypeDao = new PhenotypeDao(this.sessionProvder.getSession());
	}

	@Test
	public void testCreateStudyDesignExperimentProperties() {

		final ExperimentModel experimentModel = new ExperimentModel();
		final VariableList factors = new VariableList();

		factors.add(DMSVariableTestDataInitializer
			.createVariable(101, "Categorical Name 1", TermId.CATEGORICAL_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors
			.add(DMSVariableTestDataInitializer.createVariable(102, "999", TermId.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(
			DMSVariableTestDataInitializer.createVariable(103, "Hello", TermId.CHARACTER_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(
			DMSVariableTestDataInitializer.createVariable(104, "1", TermId.NUMERIC_VARIABLE.getId(), VariableType.EXPERIMENTAL_DESIGN));
		factors.add(DMSVariableTestDataInitializer
			.createVariable(105, "Environment", TermId.CHARACTER_VARIABLE.getId(), VariableType.ENVIRONMENT_DETAIL));

		final List<ExperimentProperty> experimentProperties =
			this.experimentModelSaver.createTrialDesignExperimentProperties(experimentModel, factors);

		Assert.assertEquals(4, experimentProperties.size());

		// Verify that only Study Design Factors are created
		Assert.assertEquals(Integer.valueOf(101), experimentProperties.get(0).getTypeId());
		Assert.assertEquals(Integer.valueOf(102), experimentProperties.get(1).getTypeId());
		Assert.assertEquals(Integer.valueOf(103), experimentProperties.get(2).getTypeId());
		Assert.assertEquals(Integer.valueOf(104), experimentProperties.get(3).getTypeId());

	}

	@Test
	public void testCreateTrialDesignPropertyVariableIsCategorical() {

		final Integer variableId = 101;
		final String variableValue = "Categorical Name 1";

		final ExperimentModel experimentModel = new ExperimentModel();
		final Variable variable = DMSVariableTestDataInitializer
			.createVariable(variableId, variableValue, TermId.CATEGORICAL_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = this.experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

		Assert.assertEquals(String.valueOf(1234), experimentProperty.getValue());
		Assert.assertSame(experimentModel, experimentProperty.getExperiment());
		Assert.assertEquals(variableId, experimentProperty.getTypeId());
		Assert.assertEquals(Integer.valueOf(variable.getVariableType().getRank()), experimentProperty.getRank());

	}

	@Test
	public void testCreateTrialDesignPropertyVariableIsNumeric() {

		final Integer variableId = 101;
		final String variableValue = "20";

		final ExperimentModel experimentModel = new ExperimentModel();
		final Variable variable = DMSVariableTestDataInitializer
			.createVariable(variableId, variableValue, TermId.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = this.experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

		Assert.assertEquals(variableValue, experimentProperty.getValue());
		Assert.assertSame(experimentModel, experimentProperty.getExperiment());
		Assert.assertEquals(variableId, experimentProperty.getTypeId());
		Assert.assertEquals(Integer.valueOf(variable.getVariableType().getRank()), experimentProperty.getRank());

	}

	@Test
	public void testCreateStudyDesignPropertyVariableIsText() {

		final Integer variableId = 101;
		final String variableValue = "Hello";

		final ExperimentModel experimentModel = new ExperimentModel();
		final Variable variable = DMSVariableTestDataInitializer
			.createVariable(variableId, variableValue, TermId.CHARACTER_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = this.experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

		Assert.assertEquals(variableValue, experimentProperty.getValue());
		Assert.assertSame(experimentModel, experimentProperty.getExperiment());
		Assert.assertEquals(variableId, experimentProperty.getTypeId());
		Assert.assertEquals(Integer.valueOf(variable.getVariableType().getRank()), experimentProperty.getRank());

	}

	@Test
	public void testAddOrUpdateExperiment() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);

		//Save the experiment
		final CropType crop = new CropType();
		crop.setUseUUID(false);
		crop.setPlotCodePrefix(CROP_PREFIX);
		this.experimentModelSaver.addOrUpdateExperiment(crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values, USER_ID);
		final ExperimentModel experiment = this.experimentDao.getExperimentByProjectIdAndLocation(1, values.getLocationId());
		Assert.assertNotNull(experiment.getObsUnitId());
		Assert.assertFalse(experiment.getObsUnitId().matches(ObservationUnitIDGeneratorTest.UUID_REGEX));
		final Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", phenotype.getValue());
	}

	@Test
	public void testAddExperiment() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);

		//Save the experiment
		final CropType crop = new CropType();
		crop.setUseUUID(false);
		crop.setPlotCodePrefix(CROP_PREFIX);
		this.experimentModelSaver.addExperiment(crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);
		final ExperimentModel experiment = this.experimentDao.getExperimentByProjectIdAndLocation(1, values.getLocationId());
		Assert.assertNotNull(experiment.getObsUnitId());
		Assert.assertFalse(experiment.getObsUnitId().matches(ObservationUnitIDGeneratorTest.UUID_REGEX));
		final Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", phenotype.getValue());
	}
}
