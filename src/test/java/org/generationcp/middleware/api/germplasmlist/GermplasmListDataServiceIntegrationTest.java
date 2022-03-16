package org.generationcp.middleware.api.germplasmlist;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataUpdateViewDTO;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListStaticColumns;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class GermplasmListDataServiceIntegrationTest extends IntegrationTestBase {

	@Autowired
	private GermplasmListDataService germplasmListDataService;

	@Autowired
	private GermplasmListService germplasmListService;

	private DaoFactory daoFactory;
	private IntegrationTestDataInitializer testDataInitializer;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
	}

	@Test
	public void testUpdateGermplasmListDataView_createAndUpdateView_OK() {

		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List ",
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final List<GermplasmListDataUpdateViewDTO> newView = Arrays.asList(
			this.createGermplasmListDataUpdateViewDTO(GermplasmListStaticColumns.GID.getTermId(), GermplasmListColumnCategory.STATIC));

		this.germplasmListDataService.updateGermplasmListDataView(germplasmList.getId(), newView);

		final List<GermplasmListDataView> currentNewView = this.daoFactory.getGermplasmListDataViewDAO().getByListId(germplasmList.getId());
		assertThat(currentNewView, hasSize(1));
		assertThat(currentNewView, CoreMatchers.hasItems(
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.GID.getTermId()))));

		final List<GermplasmListDataUpdateViewDTO> updatedView = Arrays.asList(
			this.createGermplasmListDataUpdateViewDTO(
				GermplasmListStaticColumns.DESIGNATION.getTermId(), GermplasmListColumnCategory.STATIC),
			this.createGermplasmListDataUpdateViewDTO(
				GermplasmListStaticColumns.GERMPLASM_REFERENCE.getTermId(), GermplasmListColumnCategory.STATIC));

		this.germplasmListDataService.updateGermplasmListDataView(germplasmList.getId(), updatedView);

		final List<GermplasmListDataView> currentUpdatedView =
			this.daoFactory.getGermplasmListDataViewDAO().getByListId(germplasmList.getId());
		assertThat(currentUpdatedView, hasSize(2));
		assertThat(currentUpdatedView, CoreMatchers.hasItems(
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.DESIGNATION.getTermId())),
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.GERMPLASM_REFERENCE.getTermId()))));
	}

	@Test
	public void testGetGermplasmListDataDetailList_OK() {

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + RandomStringUtils.randomAlphabetic(10),
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final Germplasm germplasm = new Germplasm(null, 1, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, null, null, null);

		final Germplasm savedGermplasm = this.daoFactory.getGermplasmDao().save(germplasm);

		final Name name = new Name(null, savedGermplasm, 1, 1, "Name", 0, 0, 0);
		this.daoFactory.getNameDao().save(name);

		final GermplasmListData data = new GermplasmListData(null, germplasmList, germplasm.getGid(), 1, "EntryCode 1",
			DataSetupTest.GERMPLSM_PREFIX + 1 + " Source", DataSetupTest.GERMPLSM_PREFIX + 1,
			DataSetupTest.GERMPLSM_PREFIX + "Group A", 0, 0);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(data);

		final CVTerm variable = this.testDataInitializer.createVariableWithScale(DataType.NUMERIC_VARIABLE, VariableType.ENTRY_DETAIL);

		final GermplasmListVariableRequestDto germplasmListVariableRequestDto = new GermplasmListVariableRequestDto();
		germplasmListVariableRequestDto.setVariableId(variable.getCvTermId());
		germplasmListVariableRequestDto.setVariableTypeId(VariableType.ENTRY_DETAIL.getId());

		this.germplasmListService.addVariableToList(germplasmList.getId(), germplasmListVariableRequestDto);

		final String value = "1";
		final GermplasmListObservationRequestDto germplasmListObservationRequestDto1 =
			new GermplasmListObservationRequestDto(data.getListDataId(), variable.getCvTermId(), value, null);
		this.germplasmListService.saveListDataObservation(germplasmList.getId(), germplasmListObservationRequestDto1);

		this.sessionProvder.getSession().flush();
		final List<GermplasmListDataDetail> germplasmListDataDetails =
			this.germplasmListDataService.getGermplasmListDataDetailList(germplasmList.getId());
		Assert.assertEquals(1, germplasmListDataDetails.size());
		Assert.assertEquals(value, germplasmListDataDetails.get(0).getValue());
		Assert.assertEquals(variable.getCvTermId(), germplasmListDataDetails.get(0).getVariableId());
		Assert.assertEquals(data.getListDataId(), germplasmListDataDetails.get(0).getListData().getListDataId());
	}

	private GermplasmListDataUpdateViewDTO createGermplasmListDataUpdateViewDTO(
		final Integer id, final GermplasmListColumnCategory category) {
		final GermplasmListDataUpdateViewDTO dto = new GermplasmListDataUpdateViewDTO();
		dto.setId(id);
		dto.setCategory(category);
		return dto;
	}

}
