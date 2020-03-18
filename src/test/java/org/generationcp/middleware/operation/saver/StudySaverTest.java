package org.generationcp.middleware.operation.saver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorImplTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StudySaverTest extends IntegrationTestBase {
	private static final String CROP_PREFIX =  RandomStringUtils.randomAlphanumeric(5);

	private StudySaver studySaver;
	private ExperimentModelSaver experimentModelSaver;
	private ExperimentDao experimentDao;

	@Before
	public void setup() {
		this.studySaver = new StudySaver(this.sessionProvder);
		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());
		this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
	}

	@Test
	public void testSaveStudyExperiment() throws Exception {
		final StudyValues values = new StudyValues();
		values.setVariableList(new VariableList());
		values.setGermplasmId(1);
		values.setLocationId(1);

		//Save the experiment
		final CropType crop = new CropType();
		crop.setUseUUID(false);
		crop.setPlotCodePrefix(CROP_PREFIX);
		final ExperimentModel savedStudyExperiment = this.studySaver.saveStudyExperiment(crop, 1, values);
		final ExperimentModel experiment = this.experimentDao.getById(savedStudyExperiment.getNdExperimentId());
		Assert.assertNotNull(experiment.getObsUnitId());
		Assert.assertFalse(experiment.getObsUnitId().matches(ObservationUnitIDGeneratorImplTest.UUID_REGEX));
		Assert.assertEquals(TermId.STUDY_INFORMATION.getId(), experiment.getTypeId().intValue());
	}

}
