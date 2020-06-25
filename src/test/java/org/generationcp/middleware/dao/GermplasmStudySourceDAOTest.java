package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.study.StudyGermplasmSourceSearchParameters;
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
		final StockModel stockModel = this.integrationTestDataInitializer.createTestStock(this.study);
		final ExperimentModel experimentModel =
			this.integrationTestDataInitializer.createExperimentModel(plot, geolocation, ExperimentType.PLOT.getTermId(), stockModel);

		final GermplasmStudySource germplasmStudySource = new GermplasmStudySource(stockModel.getGermplasm(), this.study, experimentModel,
			GermplasmStudySourceType.ADVANCE);

		this.daoFactory.getGermplasmStudySourceDAO().save(germplasmStudySource);
	}

	@Test
	public void testGetGermplasmStudySourceList() {
		final StudyGermplasmSourceSearchParameters searchParameters = new StudyGermplasmSourceSearchParameters();
		searchParameters.setStudyId(this.study.getProjectId());
		final List<GermplasmStudySource> germplasmStudySourceList =
			this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters);
		final GermplasmStudySource germplasmStudySource = germplasmStudySourceList.get(0);
		Assert.assertNotNull(germplasmStudySource.getExperimentModel());
		Assert.assertNotNull(germplasmStudySource.getGermplasm());
		Assert.assertNotNull(germplasmStudySource.getStudy());
		Assert.assertEquals(GermplasmStudySourceType.ADVANCE, germplasmStudySource.getGermplasmStudySourceType());

	}

	@Test
	public void testCountGermplasmStudySourceList() {
		final StudyGermplasmSourceSearchParameters searchParameters = new StudyGermplasmSourceSearchParameters();
		final long count = this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(searchParameters);
		Assert.assertEquals(1l, count);
	}

}
