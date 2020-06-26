package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
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
		this.sessionProvder.getSession().flush();
		final List<String> treatmentFactorValues = this.experimentPropertyDao.getTreatmentFactorValues(1001, 1002, 1);
		assertEquals(1, treatmentFactorValues.size());
		assertEquals("Value", treatmentFactorValues.get(0));
	}

	@Test
	public void testGetFieldMapLabels() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.SEASON_VAR.getId(), "10101", 1);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.TRIAL_LOCATION.getId(), "India", 2);

		final ExperimentModel experimentModel =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		this.testDataInitializer.createTestStock(this.study, experimentModel);
		this.testDataInitializer.addExperimentProp(experimentModel, TermId.REP_NO.getId(), RandomStringUtils.randomNumeric(5), 2);
		this.testDataInitializer.addExperimentProp(experimentModel, TermId.BLOCK_NO.getId(), RandomStringUtils.randomNumeric(5), 3);
		this.testDataInitializer.addExperimentProp(experimentModel, TermId.RANGE_NO.getId(), RandomStringUtils.randomNumeric(5), 4);
		this.testDataInitializer.addExperimentProp(experimentModel, TermId.COLUMN_NO.getId(), RandomStringUtils.randomNumeric(5), 5);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<FieldMapDatasetInfo> fieldMapDatasetInfos = this.experimentPropertyDao.getFieldMapLabels(this.study.getProjectId());
		assertEquals(1, fieldMapDatasetInfos.size());
		assertEquals(1, fieldMapDatasetInfos.get(0).getTrialInstances().size());
		final FieldMapTrialInstanceInfo fieldMapTrialInstanceInfo = fieldMapDatasetInfos.get(0).getTrialInstances().get(0);
		final FieldMapLabel fieldMapLabel = fieldMapTrialInstanceInfo.getFieldMapLabel(experimentModel.getNdExperimentId());

		assertEquals("India", fieldMapTrialInstanceInfo.getSiteName());
		assertEquals(experimentModel.getNdExperimentId(), fieldMapLabel.getExperimentId());
		assertNotNull(fieldMapLabel.getObsUnitId());
		assertEquals(1, fieldMapLabel.getEntryNumber().intValue());
		assertEquals(experimentModel.getStock().getName(), fieldMapLabel.getGermplasmName());
		assertNotNull(fieldMapLabel.getRep());
		assertNotNull(fieldMapLabel.getBlockNo());
		assertNotNull(fieldMapLabel.getColumn());
		assertNotNull(fieldMapLabel.getRange());
		assertNotNull(fieldMapLabel.getGid());
		assertEquals("General", fieldMapLabel.getSeason().getLabel());
		assertEquals("Study1", fieldMapLabel.getStudyName());
		assertEquals(1, fieldMapLabel.getPlotNo().intValue());

	}

	@Test
	public void testGetAllFieldMapsInBlockByTrialInstanceId() {

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.SEASON_VAR.getId(), "10101", 1);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.TRIAL_LOCATION.getId(), "India", 2);
		this.testDataInitializer.addGeolocationProp(geolocation, TermId.BLOCK_ID.getId(), "1234", 3);

		final ExperimentModel experimentModel =
			this.testDataInitializer.createTestExperiment(this.plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		this.testDataInitializer.createTestStock(this.study, experimentModel);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();

		final List<FieldMapInfo> fieldMapInfos1 = this.experimentPropertyDao
			.getAllFieldMapsInBlockByTrialInstanceId(this.study.getProjectId(), geolocation.getLocationId(), 1234);

		assertEquals(1, fieldMapInfos1.size());
		assertEquals(1, fieldMapInfos1.get(0).getDatasets().size());

		final List<FieldMapInfo> fieldMapInfos2 = this.experimentPropertyDao
			.getAllFieldMapsInBlockByTrialInstanceId(this.study.getProjectId(), geolocation.getLocationId(), 9999);
		assertEquals(0, fieldMapInfos2.size());

	}

}
