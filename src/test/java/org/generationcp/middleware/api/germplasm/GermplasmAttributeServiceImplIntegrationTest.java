package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GermplasmAttributeServiceImplIntegrationTest  extends IntegrationTestBase {

	public static final String ATTRIBUTE_VALUE = "Stat Acc";
	public static final String ATTRIBUTE_CODE = "STATUS_ACC";
	public static final String ATTRIBUTE_TYPE = "PASSPORT";
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
	public void testCreateGermplasmAttribute() throws ParseException {
		final Integer createAttributeId = createAttribute();
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(createAttributeId, attribute.getAid());
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
	}

	@Test
	public void testUpdateGermplasmAttribute() throws ParseException{
		final Integer createAttributeId = this.createAttribute();
		Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
		final Date newDate = Util.parseDate("20210317", Util.DATE_AS_NUMBER_FORMAT);
		final GermplasmAttributeRequestDto dto = new GermplasmAttributeRequestDto("new value", ATTRIBUTE_CODE, ATTRIBUTE_TYPE,
			newDate, 1);
		this.germplasmAttributeService.updateGermplasmAttribute(createAttributeId, dto);

		attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(dto.getValue(), attribute.getAval());
		Assert.assertEquals(dto.getLocationId(), attribute.getLocationId());
		Assert.assertEquals(dto.getDate(), attribute.getAdate());
	}

	@Test
	public void testDeleteGermplasmAttribute() throws  ParseException{
		final Integer createAttributeId = this.createAttribute();
		this.germplasmAttributeService.deleteGermplasmAttribute(createAttributeId);
		Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertNull(attribute);
	}

	@Test
	public void testGetGermplasmAttributeDtos() throws ParseException{
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

	Integer createAttribute() throws  ParseException {
		final Date date = Util.parseDate("20210316", Util.DATE_AS_NUMBER_FORMAT);
		final GermplasmAttributeRequestDto dto = new GermplasmAttributeRequestDto(ATTRIBUTE_VALUE, ATTRIBUTE_CODE, ATTRIBUTE_TYPE,
			date, LOCATION_ID);
		return this.germplasmAttributeService.createGermplasmAttribute(this.germplasm.getGid(), dto, 1);
	}


}
