package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PhenotypeSaverTest extends IntegrationTestBase {
	private PhenotypeSaver phenotypeSaver;

	private ExperimentModelSaver experimentModelSaver;

	private PhenotypeDao phenotypeDao;

	private ExperimentDao experimentDao;

	@Autowired
	private StudyDataManager studyDataManager;

	@Before
	public void  setUp() {
		if(this.phenotypeSaver == null) {
			this.phenotypeSaver = new PhenotypeSaver(this.sessionProvder);
		}

		if (this.phenotypeDao == null) {
			this.phenotypeDao = new PhenotypeDao(this.sessionProvder.getSession());
		}

		if (this.experimentDao == null) {
			this.experimentDao = new ExperimentDao(this.sessionProvder.getSession());
		}

		if(this.experimentModelSaver == null) {
			this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
		}

	}

	@Test
	public void testSaveOrUpdate() {
		final VariableList factors = new VariableList();
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);

		//Save the experiment
		final CropType crop = new CropType();
		crop.setUseUUID(true);
		experimentModelSaver.addExperiment(crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);

		final ExperimentModel experiment = this.experimentDao.getExperimentByProjectIdAndLocation(1, values.getLocationId());

		final Phenotype toBeSaved = new Phenotype();
		toBeSaved.setValue("999");
		toBeSaved.setObservableId(1001);

		this.phenotypeSaver.saveOrUpdate(experiment.getNdExperimentId(), toBeSaved);
		Phenotype phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", phenotype.getValue());

		toBeSaved.setValue("1000");
		this.phenotypeSaver.saveOrUpdate(experiment.getNdExperimentId(), toBeSaved);
		phenotype = this.phenotypeDao.getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("1000", phenotype.getValue());
	}
}
