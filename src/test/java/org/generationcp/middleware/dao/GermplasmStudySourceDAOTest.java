package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.study.StudyGermplasmSourceDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmSourceRequest;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GermplasmStudySourceDAOTest extends IntegrationTestBase {

	public static final int BOUND = 10;
	private DaoFactory daoFactory;
	private IntegrationTestDataInitializer integrationTestDataInitializer;
	private DmsProject study;
	private StockModel stockModel;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.integrationTestDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		this.study = this.integrationTestDataInitializer
			.createStudy(RandomStringUtils.randomAlphanumeric(BOUND), RandomStringUtils.randomAlphanumeric(BOUND), 1);
		final DmsProject plot = this.integrationTestDataInitializer
			.createDmsProject(RandomStringUtils.randomAlphanumeric(BOUND), RandomStringUtils.randomAlphanumeric(BOUND), this.study,
				this.study,
				DatasetTypeEnum.PLOT_DATA);
		final Geolocation geolocation = this.integrationTestDataInitializer.createInstance(this.study, "1", 1);
		this.stockModel = this.integrationTestDataInitializer.createTestStock(this.study);

		final ExperimentModel experimentModel =
			this.integrationTestDataInitializer.createExperimentModel(plot, geolocation, ExperimentType.PLOT.getTermId(), this.stockModel);

		this.integrationTestDataInitializer.addExperimentProp(experimentModel, TermId.PLOT_NO.getId(), "111", 1);
		this.integrationTestDataInitializer.addExperimentProp(experimentModel, TermId.REP_NO.getId(), "222", 1);

		final GermplasmStudySource germplasmStudySource =
			new GermplasmStudySource(this.stockModel.getGermplasm(), this.study, experimentModel,
				GermplasmStudySourceType.ADVANCE);

		this.daoFactory.getGermplasmStudySourceDAO().save(germplasmStudySource);
	}

	@Test
	public void testGetGermplasmStudySourceList() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final List<StudyGermplasmSourceDto> studyGermplasmSourceDtos =
			this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters);
		final StudyGermplasmSourceDto studyGermplasmSourceDto = studyGermplasmSourceDtos.get(0);

		Assert.assertEquals(this.stockModel.getGermplasm().getGid(), studyGermplasmSourceDto.getGid());
		Assert.assertEquals(this.stockModel.getGermplasm().getMgid(), studyGermplasmSourceDto.getGroupId());
		Assert.assertEquals("Name " + this.stockModel.getGermplasm().getGid(), studyGermplasmSourceDto.getDesignation());
		Assert.assertEquals(this.stockModel.getGermplasm().getMgid(), studyGermplasmSourceDto.getGroupId());
		Assert.assertEquals("UGM", studyGermplasmSourceDto.getBreedingMethodAbbrevation());
		Assert.assertEquals("Unknown generative method", studyGermplasmSourceDto.getBreedingMethodName());
		Assert.assertEquals("GEN", studyGermplasmSourceDto.getBreedingMethodType());
		Assert.assertEquals("Afghanistan", studyGermplasmSourceDto.getLocation());
		Assert.assertEquals("1", studyGermplasmSourceDto.getTrialInstance());
		Assert.assertEquals(111, studyGermplasmSourceDto.getPlotNumber().intValue());
		Assert.assertEquals(222, studyGermplasmSourceDto.getReplicationNumber().intValue());
		Assert.assertEquals(20150101, studyGermplasmSourceDto.getGermplasmDate().intValue());

	}

	@Test
	public void testCountGermplasmStudySourceList() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final long count = this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(searchParameters);
		Assert.assertEquals(1l, count);
	}

}
