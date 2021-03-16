package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GermplasmAttributeServiceImplIntegrationTest  extends IntegrationTestBase {

	private static final String ATTRIBUTE_VALUE = "Stat Acc";
	private static final String ATTRIBUTE_CODE = "STATUS_ACC";
	private static final String ATTRIBUTE_TYPE = "PASSPORT";
	public static final Integer LOCATION_ID = 0;
	private DaoFactory daoFactory;

	private Germplasm germplasm;

	@Autowired
	private GermplasmAttributeService germplasmAttributeService;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.germplasm = this.createGermplasm();
	}

	@Test
	public void testCreateGermplasmAttribute() {
		final Integer createAttributeId = this.createAttribute();
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(createAttributeId, attribute.getAid());
		Assert.assertEquals(this.germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
	}

	@Test
	public void testUpdateGermplasmAttribute() {
		final Integer createAttributeId = this.createAttribute();
		Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(this.germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
		final GermplasmAttributeRequestDto dto = new GermplasmAttributeRequestDto("new value", ATTRIBUTE_CODE, ATTRIBUTE_TYPE,
			"20210317", 1);
		this.germplasmAttributeService.updateGermplasmAttribute(createAttributeId, dto);

		attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(this.germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(dto.getValue(), attribute.getAval());
		Assert.assertEquals(dto.getLocationId(), attribute.getLocationId());
		Assert.assertEquals("20210317", attribute.getAdate().toString());
	}

	@Test
	public void testDeleteGermplasmAttribute() {
		final Integer createAttributeId = this.createAttribute();
		this.germplasmAttributeService.deleteGermplasmAttribute(createAttributeId);
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertNull(attribute);
	}

	@Test
	public void testGetGermplasmAttributeDtos() {
		final Integer createdAttribute = this.createAttribute();
		final List<GermplasmAttributeDto> germplasmAttributeDtos = this.germplasmAttributeService.getGermplasmAttributeDtos(
			this.germplasm.getGid(), ATTRIBUTE_TYPE);
		final List<GermplasmAttributeDto> filteredDtos = germplasmAttributeDtos.stream().filter(dto -> dto.getId().equals(createdAttribute))
			.collect(Collectors.toList());
		Assert.assertFalse(CollectionUtils.isEmpty(filteredDtos));
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = new Germplasm(null, 1, -1, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, null, null, null);

		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private Integer createAttribute() {
		final GermplasmAttributeRequestDto dto = new GermplasmAttributeRequestDto(ATTRIBUTE_VALUE, ATTRIBUTE_CODE, ATTRIBUTE_TYPE,
			"20210316", LOCATION_ID);
		return this.germplasmAttributeService.createGermplasmAttribute(this.germplasm.getGid(), dto, 1);
	}


}
