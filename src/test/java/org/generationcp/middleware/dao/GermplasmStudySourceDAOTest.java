package org.generationcp.middleware.dao;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.SortedPageRequest;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceRequest;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class GermplasmStudySourceDAOTest extends IntegrationTestBase {

	public static final int BOUND = 10;
	private DaoFactory daoFactory;
	private IntegrationTestDataInitializer integrationTestDataInitializer;
	private DmsProject study;
	private GermplasmStudySource germplasmStudySourceFirst;
	private GermplasmStudySource germplasmStudySourceSecond;

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

		this.germplasmStudySourceFirst = this.addGermplasmStudySource(plot, geolocation, "111", "222");
		this.germplasmStudySourceSecond = this.addGermplasmStudySource(plot, geolocation, "333", "444");

	}

	private GermplasmStudySource addGermplasmStudySource(final DmsProject plot, final Geolocation geolocation, final String plotNumber,
		final String replicationNumber) {
		final StockModel stockModel = this.integrationTestDataInitializer.createTestStock(this.study);

		final ExperimentModel experimentModel =
			this.integrationTestDataInitializer.createExperimentModel(plot, geolocation, ExperimentType.PLOT.getTermId(), stockModel);

		this.integrationTestDataInitializer.addExperimentProp(experimentModel, TermId.PLOT_NO.getId(), plotNumber, 1);
		this.integrationTestDataInitializer.addExperimentProp(experimentModel, TermId.REP_NO.getId(), replicationNumber, 1);

		final GermplasmStudySource germplasmStudySource =
			new GermplasmStudySource(stockModel.getGermplasm(), this.study, experimentModel,
				GermplasmStudySourceType.ADVANCE);
		this.daoFactory.getGermplasmStudySourceDAO().save(germplasmStudySource);

		return germplasmStudySource;
	}

	@Test
	public void testGetGermplasmStudySourceList() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);
		final List<GermplasmStudySourceDto> germplasmStudySourceDtos =
			this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest);
		final GermplasmStudySourceDto germplasmStudySourceDtoFirst = germplasmStudySourceDtos.get(0);
		final GermplasmStudySourceDto germplasmStudySourceDtoSecond = germplasmStudySourceDtos.get(1);

		Assert.assertEquals(this.germplasmStudySourceFirst.getGermplasm().getGid(), germplasmStudySourceDtoFirst.getGid());
		Assert.assertEquals(this.germplasmStudySourceFirst.getGermplasm().getMgid(), germplasmStudySourceDtoFirst.getGroupId());
		Assert
			.assertEquals("Name " + this.germplasmStudySourceFirst.getGermplasm().getGid(), germplasmStudySourceDtoFirst.getDesignation());
		Assert.assertEquals(this.germplasmStudySourceFirst.getGermplasm().getMgid(), germplasmStudySourceDtoFirst.getGroupId());
		Assert.assertEquals("UGM", germplasmStudySourceDtoFirst.getBreedingMethodAbbreviation());
		Assert.assertEquals("Unknown generative method", germplasmStudySourceDtoFirst.getBreedingMethodName());
		Assert.assertEquals("GEN", germplasmStudySourceDtoFirst.getBreedingMethodType());
		Assert.assertEquals("Afghanistan", germplasmStudySourceDtoFirst.getLocation());
		Assert.assertEquals("1 - Afghanistan", germplasmStudySourceDtoFirst.getTrialInstance());
		Assert.assertEquals(111, germplasmStudySourceDtoFirst.getPlotNumber().intValue());
		Assert.assertEquals(222, germplasmStudySourceDtoFirst.getReplicationNumber().intValue());
		Assert.assertEquals(20150101, germplasmStudySourceDtoFirst.getGermplasmDate().intValue());

		Assert.assertEquals(this.germplasmStudySourceSecond.getGermplasm().getGid(), germplasmStudySourceDtoSecond.getGid());
		Assert.assertEquals(this.germplasmStudySourceSecond.getGermplasm().getMgid(), germplasmStudySourceDtoSecond.getGroupId());
		Assert.assertEquals("Name " + this.germplasmStudySourceSecond.getGermplasm().getGid(),
			germplasmStudySourceDtoSecond.getDesignation());
		Assert.assertEquals(this.germplasmStudySourceSecond.getGermplasm().getMgid(), germplasmStudySourceDtoSecond.getGroupId());
		Assert.assertEquals("UGM", germplasmStudySourceDtoSecond.getBreedingMethodAbbreviation());
		Assert.assertEquals("Unknown generative method", germplasmStudySourceDtoSecond.getBreedingMethodName());
		Assert.assertEquals("GEN", germplasmStudySourceDtoSecond.getBreedingMethodType());
		Assert.assertEquals("Afghanistan", germplasmStudySourceDtoSecond.getLocation());
		Assert.assertEquals("1 - Afghanistan", germplasmStudySourceDtoSecond.getTrialInstance());
		Assert.assertEquals(333, germplasmStudySourceDtoSecond.getPlotNumber().intValue());
		Assert.assertEquals(444, germplasmStudySourceDtoSecond.getReplicationNumber().intValue());
		Assert.assertEquals(20150101, germplasmStudySourceDtoSecond.getGermplasmDate().intValue());

	}

	@Test
	public void testCountGermplasmStudySourceList() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final long count = this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(germplasmStudySourceRequest);
		Assert.assertEquals(2l, count);
	}

	@Test
	public void testOrderAscending() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);
		final SortedPageRequest sortedPageRequest = new SortedPageRequest();
		sortedPageRequest.setPageNumber(1);
		sortedPageRequest.setPageSize(1);
		sortedPageRequest.setSortBy("gid");
		sortedPageRequest.setSortOrder("asc");
		germplasmStudySourceRequest.setSortedRequest(sortedPageRequest);

		final List<Integer> gids = new LinkedList<>();
		for (final GermplasmStudySourceDto dto : this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceRequest)) {
			gids.add(dto.getGid());
		}

		Assert.assertTrue(Ordering.natural().isOrdered(gids));

	}

	@Test
	public void testOrderDescending() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);
		final SortedPageRequest sortedPageRequest = new SortedPageRequest();
		sortedPageRequest.setPageNumber(1);
		sortedPageRequest.setPageSize(1);
		sortedPageRequest.setSortBy("gid");
		sortedPageRequest.setSortOrder("desc");
		germplasmStudySourceRequest.setSortedRequest(sortedPageRequest);

		final List<Integer> gids = new LinkedList<>();
		for (final GermplasmStudySourceDto dto : this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceRequest)) {
			gids.add(dto.getGid());
		}

		Assert.assertTrue(Ordering.natural().reverse().isOrdered(gids));

	}

	@Test
	public void testPagination() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);
		final SortedPageRequest sortedPageRequest = new SortedPageRequest();
		sortedPageRequest.setPageNumber(1);
		sortedPageRequest.setPageSize(1);
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());

		germplasmStudySourceRequest.setSortedRequest(sortedPageRequest);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());

		sortedPageRequest.setPageNumber(2);
		sortedPageRequest.setPageSize(1);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());

		sortedPageRequest.setPageNumber(3);
		sortedPageRequest.setPageSize(1);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());

		sortedPageRequest.setPageNumber(1);
		sortedPageRequest.setPageSize(2);
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());

		sortedPageRequest.setPageNumber(2);
		sortedPageRequest.setPageSize(2);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
	}

	@Test
	public void testFilterByGid() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setGid(this.germplasmStudySourceFirst.getGermplasm().getGid());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setGid(Integer.MAX_VALUE);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByGroupId() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setGroupId(this.germplasmStudySourceFirst.getGermplasm().getMgid());
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setGroupId(Integer.MAX_VALUE);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByLocation() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setLocation("Afghanistan");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setLocation("Unknown Place");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByTrialInstance() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setTrialInstance("1");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setTrialInstance("2");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

	}

	@Test
	public void testFilterByPlotNumber() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setPlotNumber(111);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setPlotNumber(333);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setPlotNumber(999);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

	}

	@Test
	public void testFilterByReplicatesNumber() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setReplicationNumber(222);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setReplicationNumber(444);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setReplicationNumber(999);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

	}

	@Test
	public void testFilterByGermplasmDate() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setGermplasmDate(20150101);
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setGermplasmDate(20160102);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByDesignation() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setDesignation("Name " + this.germplasmStudySourceFirst.getGermplasm().getGid());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setDesignation("Name " + this.germplasmStudySourceSecond.getGermplasm().getGid());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setDesignation("Some Name");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByBreedingMethodAbbrevation() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setBreedingMethodAbbreviation("UGM");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setBreedingMethodAbbreviation("AAA");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByBreedingMethodName() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setBreedingMethodName("Unknown generative method");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setBreedingMethodName("Unknown generative method 1111");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

	}

	@Test
	public void testFilterByBreedingMethodType() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setBreedingMethodType("GEN");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setBreedingMethodType("MAN");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

	@Test
	public void testFilterByLotsCount() {
		final GermplasmStudySourceRequest germplasmStudySourceRequest = new GermplasmStudySourceRequest();
		germplasmStudySourceRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceRequest.Filter filter = new GermplasmStudySourceRequest.Filter();
		germplasmStudySourceRequest.setFilter(filter);

		filter.setLots(0);
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));

		filter.setLots(1);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceRequest));
	}

}
