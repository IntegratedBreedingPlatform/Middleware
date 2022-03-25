package org.generationcp.middleware.brapi;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.GermplasmListServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.list.GermplasmListImportRequestDTO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmListSearchRequestDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListExternalReference;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.GermplasmListDTO;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GermplasmListServiceBrapiImplTest extends IntegrationTestBase {

	private final String LIST_NAME = RandomStringUtils.randomAlphanumeric(10);
	private final String LIST_DESCRIPTION = RandomStringUtils.randomAlphanumeric(10);
	private static final String PROGRAM_UUID = RandomStringUtils.randomAlphanumeric(10);
	private Integer userId;
	private Integer germplasmListId;

	@Autowired
	GermplasmListServiceBrapi germplasmListServiceBrapi;

	@Autowired
	private GermplasmListManager germplasmListManager;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DaoFactory daoFactory;


	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(daoFactory);
		this.userId = this.findAdminUser();
		GermplasmListTestDataInitializer germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		final GermplasmList germplasmList = germplasmListTestDataInitializer
			.createGermplasmList(LIST_NAME, this.userId, LIST_DESCRIPTION, null, 1,
				PROGRAM_UUID);
		this.germplasmListId = this.germplasmListManager.addGermplasmList(germplasmList);
	}

	@Test
	public void testCountGermplasmListDTOs() {
		final GermplasmListSearchRequestDTO requestDTO = new GermplasmListSearchRequestDTO();
		requestDTO.setListDbIds(Collections.singletonList(this.germplasmListId.toString()));
		Assert.assertEquals(1, (int) this.germplasmListServiceBrapi.countGermplasmListDTOs(requestDTO));
	}

	@Test
	public void testSearchGermplasmListDTOs() {
		final GermplasmListExternalReference germplasmListExternalReference = new GermplasmListExternalReference();
		germplasmListExternalReference.setList(new GermplasmList(this.germplasmListId));
		germplasmListExternalReference.setReferenceId(RandomStringUtils.randomAlphanumeric(10));
		germplasmListExternalReference.setSource(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmListExternalReferenceDAO().save(germplasmListExternalReference);
		this.sessionProvder.getSession().flush();

		final GermplasmListSearchRequestDTO requestDTO = new GermplasmListSearchRequestDTO();
		requestDTO.setListDbIds(Collections.singletonList(this.germplasmListId.toString()));
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

	@Test
	public void testSaveGermplasmListDTOs() {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(Integer.MIN_VALUE);
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), cropType);
		final Germplasm savedGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());

		final List<GermplasmListImportRequestDTO> importRequestDTOS = new ArrayList<>();
		final GermplasmListImportRequestDTO importRequestDTO = new GermplasmListImportRequestDTO();
		importRequestDTO.setListName(RandomStringUtils.randomAlphanumeric(10));
		importRequestDTO.setListDescription(RandomStringUtils.randomAlphanumeric(10));
		importRequestDTO.setListOwnerPersonDbId(userId.toString());
		importRequestDTO.setDateCreated("2022-03-03");
		importRequestDTO.setData(new ArrayList<>());
		importRequestDTO.getData().add(savedGermplasm.getGermplasmUUID());

		final ExternalReferenceDTO externalReferenceDTO = new ExternalReferenceDTO();
		externalReferenceDTO.setReferenceSource(RandomStringUtils.randomAlphanumeric(50));
		externalReferenceDTO.setReferenceID(RandomStringUtils.randomAlphanumeric(50));
		importRequestDTO.setExternalReferences(Collections.singletonList(externalReferenceDTO));
		importRequestDTOS.add(importRequestDTO);

		final List<GermplasmListDTO> germplasmListDTOList = this.germplasmListServiceBrapi.saveGermplasmListDTOs(importRequestDTOS);
		Assert.assertEquals(1, germplasmListDTOList.size());
		Assert.assertEquals(importRequestDTO.getListName(), germplasmListDTOList.get(0).getListName());
		Assert.assertEquals(importRequestDTO.getListDescription(), germplasmListDTOList.get(0).getListDescription());
		Assert.assertEquals(importRequestDTO.getDateCreated(),
			Util.formatDateAsStringValue(germplasmListDTOList.get(0).getDateCreated(), Util.FRONTEND_DATE_FORMAT));
		Assert.assertEquals(importRequestDTO.getListOwnerPersonDbId(), germplasmListDTOList.get(0).getListOwnerPersonDbId());
		Assert.assertEquals(1, germplasmListDTOList.get(0).getExternalReferences().size());
		Assert.assertEquals(1, germplasmListDTOList.get(0).getListSize().intValue());
		final ExternalReferenceDTO savedExternalReferenceDTO = germplasmListDTOList.get(0).getExternalReferences().get(0);
		Assert.assertEquals(externalReferenceDTO.getReferenceID(), savedExternalReferenceDTO.getReferenceID());
		Assert.assertEquals(externalReferenceDTO.getReferenceSource(), savedExternalReferenceDTO.getReferenceSource());
	}
}
