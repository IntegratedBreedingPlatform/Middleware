package org.generationcp.middleware.dao;

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
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceRequest;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceSearchDto;
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
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final List<StudyGermplasmSourceDto> studyGermplasmSourceDtos =
			this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters);
		final StudyGermplasmSourceDto studyGermplasmSourceDtoFirst = studyGermplasmSourceDtos.get(0);
		final StudyGermplasmSourceDto studyGermplasmSourceDtoSecond = studyGermplasmSourceDtos.get(1);

		Assert.assertEquals(this.germplasmStudySourceFirst.getGermplasm().getGid(), studyGermplasmSourceDtoFirst.getGid());
		Assert.assertEquals(this.germplasmStudySourceFirst.getGermplasm().getMgid(), studyGermplasmSourceDtoFirst.getGroupId());
		Assert
			.assertEquals("Name " + this.germplasmStudySourceFirst.getGermplasm().getGid(), studyGermplasmSourceDtoFirst.getDesignation());
		Assert.assertEquals(this.germplasmStudySourceFirst.getGermplasm().getMgid(), studyGermplasmSourceDtoFirst.getGroupId());
		Assert.assertEquals("UGM", studyGermplasmSourceDtoFirst.getBreedingMethodAbbrevation());
		Assert.assertEquals("Unknown generative method", studyGermplasmSourceDtoFirst.getBreedingMethodName());
		Assert.assertEquals("GEN", studyGermplasmSourceDtoFirst.getBreedingMethodType());
		Assert.assertEquals("Afghanistan", studyGermplasmSourceDtoFirst.getLocation());
		Assert.assertEquals("1", studyGermplasmSourceDtoFirst.getTrialInstance());
		Assert.assertEquals(111, studyGermplasmSourceDtoFirst.getPlotNumber().intValue());
		Assert.assertEquals(222, studyGermplasmSourceDtoFirst.getReplicationNumber().intValue());
		Assert.assertEquals(20150101, studyGermplasmSourceDtoFirst.getGermplasmDate().intValue());

		Assert.assertEquals(this.germplasmStudySourceSecond.getGermplasm().getGid(), studyGermplasmSourceDtoSecond.getGid());
		Assert.assertEquals(this.germplasmStudySourceSecond.getGermplasm().getMgid(), studyGermplasmSourceDtoSecond.getGroupId());
		Assert.assertEquals("Name " + this.germplasmStudySourceSecond.getGermplasm().getGid(),
			studyGermplasmSourceDtoSecond.getDesignation());
		Assert.assertEquals(this.germplasmStudySourceSecond.getGermplasm().getMgid(), studyGermplasmSourceDtoSecond.getGroupId());
		Assert.assertEquals("UGM", studyGermplasmSourceDtoSecond.getBreedingMethodAbbrevation());
		Assert.assertEquals("Unknown generative method", studyGermplasmSourceDtoSecond.getBreedingMethodName());
		Assert.assertEquals("GEN", studyGermplasmSourceDtoSecond.getBreedingMethodType());
		Assert.assertEquals("Afghanistan", studyGermplasmSourceDtoSecond.getLocation());
		Assert.assertEquals("1", studyGermplasmSourceDtoSecond.getTrialInstance());
		Assert.assertEquals(333, studyGermplasmSourceDtoSecond.getPlotNumber().intValue());
		Assert.assertEquals(444, studyGermplasmSourceDtoSecond.getReplicationNumber().intValue());
		Assert.assertEquals(20150101, studyGermplasmSourceDtoSecond.getGermplasmDate().intValue());

	}

	@Test
	public void testCountGermplasmStudySourceList() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final long count = this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(searchParameters);
		Assert.assertEquals(2l, count);
	}

	@Test
	public void testPagination() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		final SortedPageRequest sortedPageRequest = new SortedPageRequest();
		sortedPageRequest.setPageNumber(1);
		sortedPageRequest.setPageSize(1);
		searchParameters.setStudyId(this.study.getProjectId());

		searchParameters.setSortedRequest(sortedPageRequest);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());

		sortedPageRequest.setPageNumber(2);
		sortedPageRequest.setPageSize(1);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());

		sortedPageRequest.setPageNumber(3);
		sortedPageRequest.setPageSize(1);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());

		sortedPageRequest.setPageNumber(1);
		sortedPageRequest.setPageSize(2);
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());

		sortedPageRequest.setPageNumber(2);
		sortedPageRequest.setPageSize(2);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
	}

	@Test
	public void testFilterByGid() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setGid(this.germplasmStudySourceFirst.getGermplasm().getGid());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setGid(Integer.MAX_VALUE);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

	@Test
	public void testFilterByGroupId() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setGroupId(this.germplasmStudySourceFirst.getGermplasm().getMgid());
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setGroupId(Integer.MAX_VALUE);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

	@Test
	public void testFilterByLocation() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();

		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setLocation("Afghanistan");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setLocation("Unknown Place");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

	@Test
	public void testFilterByTrialInstance() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setTrialInstance("1");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setTrialInstance("2");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

	}

	@Test
	public void testFilterByPlotNumber() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setPlotNumber(111);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setPlotNumber(333);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setPlotNumber(999);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

	}

	@Test
	public void testFilterByReplicatesNumber() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setReplicationNumber(222);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setReplicationNumber(444);
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setReplicationNumber(999);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

	}

	@Test
	public void testFilterByGermplasmDate() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setGermplasmDate(20150101);
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setGermplasmDate(20160102);
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

	@Test
	public void testFilterByDesignation() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setDesignation("Name " + this.germplasmStudySourceFirst.getGermplasm().getGid());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setDesignation("Name " + this.germplasmStudySourceSecond.getGermplasm().getGid());
		Assert.assertEquals(1, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(1l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setDesignation("Some Name");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

	@Test
	public void testFilterByBreedingMethodAbbrevation() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();

		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setBreedingMethodAbbrevation("UGM");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setBreedingMethodAbbrevation("AAA");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

	@Test
	public void testFilterByBreedingMethodName() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setBreedingMethodName("Unknown generative method");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setBreedingMethodName("Unknown generative method 1111");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

	}

	@Test
	public void testFilterByBreedingMethodType() {
		final StudyGermplasmSourceRequest searchParameters = new StudyGermplasmSourceRequest();
		searchParameters.setStudyId(this.study.getProjectId());
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto = new StudyGermplasmSourceSearchDto();
		searchParameters.setStudyGermplasmSourceSearchDto(studyGermplasmSourceSearchDto);

		studyGermplasmSourceSearchDto.setBreedingMethodType("GEN");
		Assert.assertEquals(2, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(2l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));

		studyGermplasmSourceSearchDto.setBreedingMethodType("MAN");
		Assert.assertEquals(0, this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(searchParameters).size());
		Assert.assertEquals(0l, this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(searchParameters));
	}

}
