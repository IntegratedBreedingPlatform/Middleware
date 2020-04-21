package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

public class SampleServiceImplTest extends IntegrationTestBase {

	private DmsProjectDao dmsProjectDao;
	private ExperimentDao experimentDao;
	private IntegrationTestDataInitializer testDataInitializer;
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private SampleService sampleService;

	@Before
	public void setUp() {

		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
	}

	@Test
	public void testSampleDetailsDTO() {

		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();
		final DmsProject study =
			this.testDataInitializer.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.testDataInitializer.addProjectProp(study, TermId.SEEDING_DATE.getId(), "", VariableType.STUDY_DETAIL, "20190101", 1);
		this.testDataInitializer.addProjectProp(study, TermId.SEASON_VAR_TEXT.getId(), "", VariableType.STUDY_DETAIL, "Wet", 2);
		this.dmsProjectDao.refresh(study);
		final DmsProject plot =
			this.testDataInitializer.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);

		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		final ExperimentModel experimentModel =
			this.testDataInitializer.createTestExperiment(plot, geolocation, TermId.PLOT_EXPERIMENT.getId(), "1", null);
		this.testDataInitializer.createTestStock(experimentModel);

		final SampleList sampleList = this.testDataInitializer.createTestSampleList("List1", user.getUserid());
		final List<Sample> samples = this.testDataInitializer.addSamples(Arrays.asList(experimentModel), sampleList, user.getUserid());

		final SampleDetailsDTO sampleDetailsDTO = this.sampleService.getSampleObservation(samples.get(0).getSampleBusinessKey());

		Assert.assertEquals("BUSINESS-KEY-List11", sampleDetailsDTO.getSampleBusinessKey());
		Assert.assertEquals("John M Doe", sampleDetailsDTO.getTakenBy());
		Assert.assertEquals("SAMPLE-List1:1", sampleDetailsDTO.getSampleName());
		Assert.assertEquals(experimentModel.getStock().getName(), sampleDetailsDTO.getDesignation());
		Assert.assertEquals(sampleDetailsDTO.getSampleDate(), sampleDetailsDTO.getSampleDate());
		Assert.assertEquals(1, sampleDetailsDTO.getEntryNo().intValue());
		Assert.assertEquals(1, sampleDetailsDTO.getPlotNo().intValue());
		Assert.assertNotNull(sampleDetailsDTO.getGid());
		Assert.assertEquals("20190101", sampleDetailsDTO.getSeedingDate());
		Assert.assertEquals("Wet", sampleDetailsDTO.getSeason());
		Assert.assertEquals(101, sampleDetailsDTO.getLocationDbId().intValue());

	}

}
