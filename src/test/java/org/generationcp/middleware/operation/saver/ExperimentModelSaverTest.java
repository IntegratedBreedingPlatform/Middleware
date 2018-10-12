package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ExperimentModelSaverTest extends IntegrationTestBase {
	private ExperimentModelSaver experimentModelSaver;

	@Before
	public void setUp() throws Exception {
		this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
	}

	@Test
	public void testCreateStudyDesignExperimentProperties() {


		final ExperimentModel experimentModel = new ExperimentModel();
		final VariableList factors = new VariableList();

		factors.add(DMSVariableTestDataInitializer.createVariable(101, "Categorical Name 1", TermId.CATEGORICAL_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(DMSVariableTestDataInitializer.createVariable(102, "999", TermId.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(DMSVariableTestDataInitializer.createVariable(103, "Hello", TermId.CHARACTER_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(DMSVariableTestDataInitializer.createVariable(104, "1", TermId.NUMERIC_VARIABLE.getId(), VariableType.EXPERIMENTAL_DESIGN));
		factors.add(DMSVariableTestDataInitializer.createVariable(105, "Environment", TermId.CHARACTER_VARIABLE.getId(), VariableType.ENVIRONMENT_DETAIL));

		final List<ExperimentProperty> experimentProperties = experimentModelSaver.createTrialDesignExperimentProperties(experimentModel, factors);

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
		final Variable variable = DMSVariableTestDataInitializer.createVariable(variableId, variableValue, TermId.CATEGORICAL_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

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
		final Variable variable = DMSVariableTestDataInitializer.createVariable(variableId, variableValue, TermId.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

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
		final Variable variable = DMSVariableTestDataInitializer.createVariable(variableId, variableValue, TermId.CHARACTER_VARIABLE.getId(), VariableType.TREATMENT_FACTOR);

		final ExperimentProperty experimentProperty = experimentModelSaver.createTrialDesignProperty(experimentModel, variable);

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
		this.experimentModelSaver.addOrUpdateExperiment(1, ExperimentType.TRIAL_ENVIRONMENT, values, "dfg1");
		final ExperimentModel experiment = this.experimentModelSaver.getExperimentDao().getExperimentByProjectIdAndLocation(1, values.getLocationId());
		final Phenotype phenotype = this.experimentModelSaver.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", phenotype.getValue());
	}
}
