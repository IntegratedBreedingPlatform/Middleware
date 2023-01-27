package org.generationcp.middleware.dao;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
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

		this.germplasmStudySourceFirst =
			this.integrationTestDataInitializer.addGermplasmStudySource(this.study, plot, geolocation, "111", "222");
		this.germplasmStudySourceSecond =
			this.integrationTestDataInitializer.addGermplasmStudySource(this.study, plot, geolocation, "333", "444");

		this.sessionProvder.getSession().flush();
	}

	@Test
	public void testGetGermplasmStudySourceList() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);
		final List<GermplasmStudySourceDto> germplasmStudySourceDtos =
			this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceSearchRequest, null);
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
		Assert.assertEquals("Afghanistan", germplasmStudySourceDtoFirst.getBreedingLocationName());
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
		Assert.assertEquals("Afghanistan", germplasmStudySourceDtoSecond.getBreedingLocationName());
		Assert.assertEquals("1 - Afghanistan", germplasmStudySourceDtoSecond.getTrialInstance());
		Assert.assertEquals(333, germplasmStudySourceDtoSecond.getPlotNumber().intValue());
		Assert.assertEquals(444, germplasmStudySourceDtoSecond.getReplicationNumber().intValue());
		Assert.assertEquals(20150101, germplasmStudySourceDtoSecond.getGermplasmDate().intValue());

	}

	@Test
	public void testCountGermplasmStudySourceList() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final long count = this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(germplasmStudySourceSearchRequest);
		Assert.assertEquals(2l, count);
	}

	@Test
	public void testOrderAscending() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);
		final List<Integer> gids = new LinkedList<>();
		for (final GermplasmStudySourceDto dto : this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, 1, Sort.Direction.ASC, "gid"))) {
			gids.add(dto.getGid());
		}

		Assert.assertTrue(Ordering.natural().isOrdered(gids));

	}

	@Test
	public void testOrderDescending() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);
		final List<Integer> gids = new LinkedList<>();
		for (final GermplasmStudySourceDto dto : this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, 1, Sort.Direction.DESC, "gid"))) {
			gids.add(dto.getGid());
		}

		Assert.assertTrue(Ordering.natural().reverse().isOrdered(gids));

	}

	@Test
	public void testPagination() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());

		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, 1)).size());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(1, 1)).size());
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(2, 1)).size());
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, 2)).size());
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO()
			.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(1, 2)).size());
	}

	@Test
	public void testFilterByGid() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setGidList(Collections.singletonList(this.germplasmStudySourceFirst.getGermplasm().getGid()));
		Assert.assertEquals(1,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setGidList(Collections.singletonList(Integer.MAX_VALUE));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByGroupId() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setGroupIdList(Collections.singletonList(this.germplasmStudySourceFirst.getGermplasm().getMgid()));
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setGroupIdList(Collections.singletonList(Integer.MAX_VALUE));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByLocation() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setBreedingLocationName("Afghanistan");
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setBreedingLocationName("Unknown Place");
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByTrialInstance() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setTrialInstanceList(Arrays.asList("1", "2"));
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setTrialInstanceList(Collections.singletonList("2"));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

	}

	@Test
	public void testFilterByPlotNumber() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setPlotNumberList(Arrays.asList(111, 333));
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setPlotNumberList(Collections.singletonList(999));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

	}

	@Test
	public void testFilterByReplicatesNumber() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setReplicationNumberList(Arrays.asList(222, 444));
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setReplicationNumberList(Collections.singletonList(999));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

	}

	@Test
	public void testFilterByGermplasmDate() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setGermplasmDateList(Collections.singletonList(20150101));
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setGermplasmDateList(Collections.singletonList(20160102));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByDesignation() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setDesignation("Name " + this.germplasmStudySourceFirst.getGermplasm().getGid());
		Assert.assertEquals(1,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setDesignation("Name " + this.germplasmStudySourceSecond.getGermplasm().getGid());
		Assert.assertEquals(1,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setDesignation("Some Name");
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByBreedingMethodAbbrevation() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setBreedingMethodAbbreviation("UGM");
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setBreedingMethodAbbreviation("AAA");
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByBreedingMethodName() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setBreedingMethodName("Unknown generative method");
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setBreedingMethodName("Unknown generative method 1111");
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

	}

	@Test
	public void testFilterByBreedingMethodType() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setBreedingMethodType("GEN");
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setBreedingMethodType("MAN");
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testFilterByLotsCount() {
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest = new GermplasmStudySourceSearchRequest();
		germplasmStudySourceSearchRequest.setStudyId(this.study.getProjectId());
		final GermplasmStudySourceSearchRequest.Filter filter = new GermplasmStudySourceSearchRequest.Filter();
		germplasmStudySourceSearchRequest.setFilter(filter);

		filter.setNumberOfLotsList(Collections.singletonList(0));
		Assert.assertEquals(2,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));

		filter.setNumberOfLotsList(Collections.singletonList(1));
		Assert.assertEquals(0,
			this.daoFactory.getGermplasmStudySourceDAO()
				.getGermplasmStudySourceList(germplasmStudySourceSearchRequest, new PageRequest(0, Integer.MAX_VALUE)).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(
			germplasmStudySourceSearchRequest));
	}

	@Test
	public void testGetByGids() {
		final List<GermplasmStudySource> result = this.daoFactory.getGermplasmStudySourceDAO().getByGids(
			Sets.newHashSet(this.germplasmStudySourceFirst.getGermplasm().getGid(),
				this.germplasmStudySourceSecond.getGermplasm().getGid()));

		Assert.assertEquals(result.get(0), this.germplasmStudySourceFirst);
		Assert.assertEquals(result.get(1), this.germplasmStudySourceSecond);
	}

}
