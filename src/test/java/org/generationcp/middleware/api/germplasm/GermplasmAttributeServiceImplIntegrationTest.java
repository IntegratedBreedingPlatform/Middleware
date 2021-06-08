package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
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

	private static final String NOTE_ATTRIBUTE = "NOTE_AA_text";
	private static final String ATTRIBUTE_VALUE = RandomStringUtils.randomAlphanumeric(5);
	private static final Integer LOCATION_ID = 1;
	private static final String ATTRIBUTE_DATE = "20210316";

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmAttributeService germplasmAttributeService;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testCreateGermplasmAttribute() {
		final Germplasm germplasm = this.createGermplasm();
		final Integer createAttributeId = this.createAttribute(germplasm.getGid());
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(createAttributeId, attribute.getAid());
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
	}

	@Test
	public void testUpdateGermplasmAttribute() {
		final Germplasm germplasm = this.createGermplasm();
		final Integer createAttributeId = this.createAttribute(germplasm.getGid());
		Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
		final GermplasmAttributeRequestDto dto = new GermplasmAttributeRequestDto(attribute.getTypeId(), "new value", "20210317", 1);
		this.germplasmAttributeService.updateGermplasmAttribute(createAttributeId, dto);

		attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(dto.getValue(), attribute.getAval());
		Assert.assertEquals(dto.getLocationId(), attribute.getLocationId());
		Assert.assertEquals("20210317", attribute.getAdate().toString());
	}

	@Test
	public void testDeleteGermplasmAttribute() {
		final Germplasm germplasm = this.createGermplasm();
		final Integer createAttributeId = this.createAttribute(germplasm.getGid());
		this.germplasmAttributeService.deleteGermplasmAttribute(createAttributeId);
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertNull(attribute);
	}

	@Test
	public void testGetGermplasmAttributeDtos() {
		final Germplasm germplasm = this.createGermplasm();
		final Integer createdAttributeId = this.createAttribute(germplasm.getGid());
		final List<GermplasmAttributeDto> germplasmAttributeDtos = this.germplasmAttributeService.getGermplasmAttributeDtos(
			germplasm.getGid(), VariableType.GERMPLASM_ATTRIBUTE.getId());
		final List<GermplasmAttributeDto> filteredDtos = germplasmAttributeDtos.stream().filter(dto -> dto.getId().equals(createdAttributeId))
			.collect(Collectors.toList());
		Assert.assertFalse(CollectionUtils.isEmpty(filteredDtos));
		final GermplasmAttributeDto germplasmAttributeDto = filteredDtos.get(0);
		Assert.assertEquals(createdAttributeId, germplasmAttributeDto.getId());
		Assert.assertEquals(ATTRIBUTE_VALUE, germplasmAttributeDto.getValue());
		Assert.assertEquals(NOTE_ATTRIBUTE, germplasmAttributeDto.getVariableName());
		//Assert.assertEquals(VariableType.GERMPLASM_ATTRIBUTE.getId(), germplasmAttributeDto.getAttributeType());
		Assert.assertEquals(ATTRIBUTE_DATE, germplasmAttributeDto.getDate());
		Assert.assertEquals(LOCATION_ID, germplasmAttributeDto.getLocationId());
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = new Germplasm(null, 1, -1, 0, 0, 0, 0, 0, 0,
			0, 0, null, null, null);

		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private Integer createAttribute(final Integer germplasmId) {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addName(NOTE_ATTRIBUTE);
		variableFilter.addVariableType(VariableType.GERMPLASM_ATTRIBUTE);
		final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
		final GermplasmAttributeRequestDto dto = new GermplasmAttributeRequestDto(variables.get(0).getId(), ATTRIBUTE_VALUE,
			ATTRIBUTE_DATE, LOCATION_ID);
		return this.germplasmAttributeService.createGermplasmAttribute(germplasmId, dto, this.findAdminUser());
	}


}
