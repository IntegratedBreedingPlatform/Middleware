package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.shared.RecordAttributeDto;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GermplasmAttributeServiceImplIntegrationTest  extends IntegrationTestBase {

	private static final String NOTE_ATTRIBUTE = "NOTE_AA_text";
	private static final String ATTRIBUTE_VALUE = RandomStringUtils.randomAlphanumeric(5);
	private static final Integer LOCATION_ID = 1;
	private static final String ATTRIBUTE_DATE = "20210316";
	private Integer userId, attributeId;
	private String creationDate;

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmAttributeService germplasmAttributeService;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.userId = this.findAdminUser();
		this.creationDate = "20201212";
		this.attributeId = this.daoFactory.getCvTermDao().getByNameAndCvId(NOTE_ATTRIBUTE, CvId.VARIABLES.getId()).getCvTermId();
	}

	@Test
	public void testCreateGermplasmAttribute() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);
		final Integer createAttributeId = this.createAttribute(germplasm.getGid());
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(createAttributeId, attribute.getAid());
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
	}

	@Test
	public void testUpdateGermplasmAttribute() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);
		final Integer createAttributeId = this.createAttribute(germplasm.getGid());
		Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
		final AttributeRequestDto dto = new AttributeRequestDto(attribute.getTypeId(), "new value", "20210317", 1);
		this.germplasmAttributeService.updateGermplasmAttribute(createAttributeId, dto);

		attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(germplasm.getGid(), attribute.getGermplasmId());
		Assert.assertEquals(dto.getValue(), attribute.getAval());
		Assert.assertEquals(dto.getLocationId(), attribute.getLocationId());
		Assert.assertEquals("20210317", attribute.getAdate().toString());
	}

	@Test
	public void testDeleteGermplasmAttribute() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);
		final Integer createAttributeId = this.createAttribute(germplasm.getGid());
		this.germplasmAttributeService.deleteGermplasmAttribute(createAttributeId);
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(createAttributeId);
		Assert.assertNull(attribute);
	}

	@Test
	public void testGetGermplasmAttributeDtos() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);
		final Integer createdAttributeId = this.createAttribute(germplasm.getGid());
		final List<RecordAttributeDto> germplasmAttributeDtos = this.germplasmAttributeService.getGermplasmAttributeDtos(
			germplasm.getGid(), VariableType.GERMPLASM_ATTRIBUTE.getId(), null);
		final List<RecordAttributeDto> filteredDtos = germplasmAttributeDtos.stream().filter(dto -> dto.getId().equals(createdAttributeId))
			.collect(Collectors.toList());
		Assert.assertFalse(CollectionUtils.isEmpty(filteredDtos));
		final RecordAttributeDto germplasmAttributeDto = filteredDtos.get(0);
		Assert.assertEquals(createdAttributeId, germplasmAttributeDto.getId());
		Assert.assertEquals(ATTRIBUTE_VALUE, germplasmAttributeDto.getValue());
		Assert.assertEquals(NOTE_ATTRIBUTE, germplasmAttributeDto.getVariableName());
		Assert.assertEquals(ATTRIBUTE_DATE, germplasmAttributeDto.getDate());
		Assert.assertEquals(LOCATION_ID, germplasmAttributeDto.getLocationId());
	}

	private Integer createAttribute(final Integer germplasmId) {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addName(NOTE_ATTRIBUTE);
		variableFilter.addVariableType(VariableType.GERMPLASM_ATTRIBUTE);
		final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
		final AttributeRequestDto dto = new AttributeRequestDto(variables.get(0).getId(), ATTRIBUTE_VALUE,
			ATTRIBUTE_DATE, LOCATION_ID);
		return this.germplasmAttributeService.createGermplasmAttribute(germplasmId, dto);
	}

	private Germplasm createGermplasm(final Method method, final String germplasmUUID, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2) {
		return this.createGermplasm(method, germplasmUUID, location, gnpgs, gpid1, gpid2, null);
	}

	private Germplasm createGermplasm(final Method method, final String germplasmUUID, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final Bibref reference) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			0, (location == null) ? 0 : location.getLocid(), Integer.parseInt(this.creationDate), 0,
			0, 0, null, null, method);
		if (StringUtils.isNotEmpty(germplasmUUID)) {
			germplasm.setGermplasmUUID(germplasmUUID);
		}
		germplasm.setBibref(reference);
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvder.getSession().flush();

		assertThat(germplasm.getCreatedBy(), is(this.userId));
		assertNotNull(germplasm.getCreatedBy());
		assertNull(germplasm.getModifiedBy());
		assertNull(germplasm.getModifiedDate());

		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(4).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708);
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

}
