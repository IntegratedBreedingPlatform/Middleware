package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ExperimentPropertyDaoIntegrationTest extends IntegrationTestBase {

	private ExperimentModelSaver experimentModelSaver;

	private ExperimentPropertyDao experimentPropertyDao;

	@Before
	public void setUp() {
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());
		this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
	}

	@Test
	public void testGetTreatmentFactorValues() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(DMSVariableTestDataInitializer.createVariable(1002, "Value", DataType.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);
		//Save the experiment
		this.experimentModelSaver.addOrUpdateExperiment(1, ExperimentType.STUDY_INFORMATION, values);
		final List<String> treatmentFactorValues = this.experimentPropertyDao.getTreatmentFactorValues(1001, 1002, 1);
		Assert.assertEquals(1, treatmentFactorValues.size());
		Assert.assertEquals("Value", treatmentFactorValues.get(0));
	}
}
