package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ExperimentPropertyDaoIntegrationTest extends IntegrationTestBase {

	private DmsProjectDao dmsProjectDao;
	private ExperimentPropertyDao experimentPropertyDao;
	private ExperimentModelSaver experimentModelSaver;

	private IntegrationTestDataInitializer testDataInitializer;

	private DmsProject study;
	private DmsProject plot;

	@Before
	public void setUp() {

		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());
		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder);
		this.study = this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.plot = this.testDataInitializer
			.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
	}

	@Test
	public void testGetTreatmentFactorValues() {
		final VariableList factors = new VariableList();
		factors.add(
			DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		factors.add(
			DMSVariableTestDataInitializer.createVariable(1002, "Value", DataType.NUMERIC_VARIABLE.getId(), VariableType.TREATMENT_FACTOR));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);
		//Save the experiment
		this.experimentModelSaver.addOrUpdateExperiment(new CropType(), 1, ExperimentType.STUDY_INFORMATION, values);
		final List<String> treatmentFactorValues = this.experimentPropertyDao.getTreatmentFactorValues(1001, 1002, 1);
		Assert.assertEquals(1, treatmentFactorValues.size());
		Assert.assertEquals("Value", treatmentFactorValues.get(0));
	}

	@Test
	public void testGetFieldMapLabels() {

		this.experimentPropertyDao.getFieldMapLabels(this.study.getProjectId());

	}

	@Test
	public void testGetAllFieldMapsInBlockByTrialInstanceId() {

	}

}
