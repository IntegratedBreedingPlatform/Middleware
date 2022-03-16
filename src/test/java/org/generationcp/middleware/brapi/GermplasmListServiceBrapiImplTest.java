package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.GermplasmListServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmListSearchRequestDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListExternalReference;
import org.generationcp.middleware.service.api.GermplasmListDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GermplasmListServiceBrapiImplTest extends IntegrationTestBase {

	private final String LIST_NAME = RandomStringUtils.random(10);
	private final String LIST_DESCRIPTION = RandomStringUtils.random(10);
	private static final String PROGRAM_UUID = RandomStringUtils.random(10);
	private Integer userId;
	private Integer germplasmListId;

	@Autowired
	GermplasmListServiceBrapi germplasmListServiceBrapi;

	@Autowired
	private GermplasmListManager germplasmListManager;

	private DaoFactory daoFactory;
	private GermplasmListTestDataInitializer germplasmListTestDataInitializer;


	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.userId = this.findAdminUser();
		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		final GermplasmList germplasmListParent = this.germplasmListTestDataInitializer
			.createGermplasmList(LIST_NAME, userId, LIST_DESCRIPTION, null, 1,
				PROGRAM_UUID);
		this.germplasmListId = this.germplasmListManager.addGermplasmList(germplasmListParent);
	}

	@Test
	public void testCountGermplasmListDTOs() {
		final GermplasmListSearchRequestDTO requestDTO = new GermplasmListSearchRequestDTO();
		requestDTO.setListDbId(this.germplasmListId.toString());
		Assert.assertEquals(1, (int) this.germplasmListServiceBrapi.countGermplasmListDTOs(requestDTO));
	}

	@Test
	public void testSearchGermplasmListDTOs() {
		final GermplasmListExternalReference germplasmListExternalReference = new GermplasmListExternalReference();
		germplasmListExternalReference.setList(new GermplasmList(this.germplasmListId));
		germplasmListExternalReference.setReferenceId(RandomStringUtils.random(10));
		germplasmListExternalReference.setSource(RandomStringUtils.random(10));
		this.daoFactory.getGermplasmListExternalReferenceDAO().save(germplasmListExternalReference);
		this.sessionProvder.getSession().flush();

		final GermplasmListSearchRequestDTO requestDTO = new GermplasmListSearchRequestDTO();
		requestDTO.setListDbId(this.germplasmListId.toString());
		requestDTO.setListName(LIST_NAME);
		requestDTO.setExternalReferenceID(germplasmListExternalReference.getReferenceId());
		requestDTO.setExternalReferenceSource(germplasmListExternalReference.getSource());

		final List<GermplasmListDTO> lists = this.germplasmListServiceBrapi.searchGermplasmListDTOs(requestDTO, null);
		Assert.assertEquals(1, lists.size());
		final GermplasmListDTO listDTO = lists.get(0);
		Assert.assertEquals(LIST_NAME, listDTO.getListName());
		Assert.assertEquals(LIST_DESCRIPTION, listDTO.getListDescription());
		Assert.assertEquals(userId.toString(), listDTO.getListOwnerPersonDbId());
		Assert.assertEquals("0", listDTO.getListSize().toString());
		Assert.assertEquals(1, listDTO.getExternalReferences().size());
		final ExternalReferenceDTO externalReferenceDTO = listDTO.getExternalReferences().get(0);
		Assert.assertEquals(germplasmListExternalReference.getReferenceId(), externalReferenceDTO.getReferenceID());
		Assert.assertEquals(germplasmListExternalReference.getSource(), externalReferenceDTO.getReferenceSource());

	}
}
