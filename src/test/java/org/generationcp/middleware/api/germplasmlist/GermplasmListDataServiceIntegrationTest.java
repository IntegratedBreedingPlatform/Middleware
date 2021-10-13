package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataUpdateViewDTO;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListStaticColumns;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
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

	private DaoFactory daoFactory;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testSaveGermplasmListDataView_createAndUpdateView_OK() {

		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List ",
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final List<GermplasmListDataUpdateViewDTO> newView = Arrays.asList(
			this.createGermplasmListDataUpdateViewDTO(GermplasmListStaticColumns.ENTRY_NO.getTermId(), GermplasmListColumnCategory.STATIC),
			this.createGermplasmListDataUpdateViewDTO(GermplasmListStaticColumns.GID.getTermId(), GermplasmListColumnCategory.STATIC));

		this.germplasmListDataService.saveGermplasmListDataView(germplasmList.getId(), newView);

		final List<GermplasmListDataView> currentNewView = this.daoFactory.getGermplasmListDataViewDAO().getByListId(germplasmList.getId());
		assertThat(currentNewView, hasSize(2));
		assertThat(currentNewView, CoreMatchers.hasItems(
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.ENTRY_NO.getTermId())),
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.GID.getTermId()))));

		final List<GermplasmListDataUpdateViewDTO> updatedView = Arrays.asList(
			this.createGermplasmListDataUpdateViewDTO(GermplasmListStaticColumns.ENTRY_NO.getTermId(), GermplasmListColumnCategory.STATIC),
			this.createGermplasmListDataUpdateViewDTO(GermplasmListStaticColumns.DESIGNATION.getTermId(), GermplasmListColumnCategory.STATIC),
			this.createGermplasmListDataUpdateViewDTO(GermplasmListStaticColumns.GERMPLASM_REFERENCE.getTermId(), GermplasmListColumnCategory.STATIC));

		this.germplasmListDataService.saveGermplasmListDataView(germplasmList.getId(), updatedView);

		final List<GermplasmListDataView> currentUpdatedView = this.daoFactory.getGermplasmListDataViewDAO().getByListId(germplasmList.getId());
		assertThat(currentUpdatedView, hasSize(3));
		assertThat(currentUpdatedView, CoreMatchers.hasItems(
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.ENTRY_NO.getTermId())),
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.DESIGNATION.getTermId())),
			Matchers.hasProperty("staticId", Matchers.is(GermplasmListStaticColumns.GERMPLASM_REFERENCE.getTermId()))));
	}

	private GermplasmListDataUpdateViewDTO createGermplasmListDataUpdateViewDTO(final Integer id, final GermplasmListColumnCategory category) {
		final GermplasmListDataUpdateViewDTO dto = new GermplasmListDataUpdateViewDTO();
		dto.setId(id);
		dto.setCategory(category);
		return dto;
	}

}
