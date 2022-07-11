package org.generationcp.middleware.api.inventory;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.shared.AttributeDto;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotAttribute;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotAttributeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LotAttributeServiceImplIntegrationTest extends IntegrationTestBase {

	private static final String TEST_ATTRIBUTE = "TEST_ATTRIBUTE";
	private static final String ATTRIBUTE_VALUE = RandomStringUtils.randomAlphanumeric(5);
	private static final Integer LOCATION_ID = 1;
	private static final String ATTRIBUTE_DATE = "20210316";
	private Integer userId, attributeId;
	private String creationDate;
	private CropType cropType;
	private Lot lot;

	private DaoFactory daoFactory;
	private LotDAO lotDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private static final String GERMPLASM = "GERMPLSM";

	@Autowired
	private LotAttributeService lotAttributeService;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.userId = this.findAdminUser();
		this.creationDate = "20201212";

		this.lotDAO = this.daoFactory.getLotDao();
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);

		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		this.lot =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.lotDAO.save(this.lot);

		this.attributeId = this.createTestVariable().getId();
	}

	@Test
	public void testCreateLotAttribute() {
		final Integer createAttributeId = this.createAttribute(this.lot.getId(), TEST_ATTRIBUTE);
		final LotAttribute attribute = this.daoFactory.getLotAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(createAttributeId, attribute.getAid());
		Assert.assertEquals(this.lot.getId(), attribute.getLotId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
	}

	@Test
	public void testUpdateLotAttribute() {
		final Integer createAttributeId = this.createAttribute(this.lot.getId(), TEST_ATTRIBUTE);
		LotAttribute attribute = this.daoFactory.getLotAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(this.lot.getId(), attribute.getLotId());
		Assert.assertEquals(ATTRIBUTE_VALUE, attribute.getAval());
		Assert.assertEquals(LOCATION_ID, attribute.getLocationId());
		final AttributeRequestDto dto = new AttributeRequestDto(attribute.getTypeId(), "new value", "20210317", 1);
		this.lotAttributeService.updateLotAttribute(createAttributeId, dto);

		attribute = this.daoFactory.getLotAttributeDAO().getById(createAttributeId);
		Assert.assertEquals(this.lot.getId(), attribute.getLotId());
		Assert.assertEquals(dto.getValue(), attribute.getAval());
		Assert.assertEquals(dto.getLocationId(), attribute.getLocationId());
		Assert.assertEquals("20210317", attribute.getAdate().toString());
	}

	@Test
	public void testDeleteLotAttribute() {
		final Integer createAttributeId = this.createAttribute(this.lot.getId(), TEST_ATTRIBUTE);
		this.lotAttributeService.deleteLotAttribute(createAttributeId);
		final LotAttribute attribute = this.daoFactory.getLotAttributeDAO().getById(createAttributeId);
		Assert.assertNull(attribute);
	}

	@Test
	public void testGetLotAttributeDtos() {
		final Integer createdAttributeId = this.createAttribute(this.lot.getId(), TEST_ATTRIBUTE);
		final List<AttributeDto> lotAttributeDtos = this.lotAttributeService.getLotAttributeDtos(
			this.lot.getId(), null);
		final List<AttributeDto> filteredDtos = lotAttributeDtos.stream().filter(dto -> dto.getId().equals(createdAttributeId))
			.collect(Collectors.toList());
		Assert.assertFalse(CollectionUtils.isEmpty(filteredDtos));
		final AttributeDto germplasmAttributeDto = filteredDtos.get(0);
		Assert.assertEquals(createdAttributeId, germplasmAttributeDto.getId());
		Assert.assertEquals(ATTRIBUTE_VALUE, germplasmAttributeDto.getValue());
		Assert.assertEquals(TEST_ATTRIBUTE, germplasmAttributeDto.getVariableName());
		Assert.assertEquals(ATTRIBUTE_DATE, germplasmAttributeDto.getDate());
		Assert.assertEquals(LOCATION_ID, germplasmAttributeDto.getLocationId());
	}

	@Test
	public void testGetLotAttributeVariables() {
		final Integer createAttributeId = this.createAttribute(this.lot.getId(), TEST_ATTRIBUTE);
		final LotAttribute attribute = this.daoFactory.getLotAttributeDAO().getById(createAttributeId);

		final List<Variable> variables =
			this.lotAttributeService.getLotAttributeVariables(Arrays.asList(this.lot.getId()), RandomStringUtils.randomAlphanumeric(10));

		Assert.assertEquals(1, variables.size());
		Assert.assertTrue(variables.stream().allMatch(cVTerm -> cVTerm.getName().equalsIgnoreCase(TEST_ATTRIBUTE.toUpperCase())));
		Assert.assertEquals(TEST_ATTRIBUTE, variables.get(0).getName());
		Assert.assertEquals(TEST_ATTRIBUTE, variables.get(0).getDefinition());
	}

	private Integer createAttribute(final Integer lotId, final String variableName) {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableType(VariableType.INVENTORY_ATTRIBUTE);
		variableFilter.addName(variableName);
		final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
		final AttributeRequestDto dto = new AttributeRequestDto(variables.get(0).getId(), ATTRIBUTE_VALUE,
			ATTRIBUTE_DATE, LOCATION_ID);
		return this.lotAttributeService.createLotAttribute(lotId, dto);
	}

	private Variable createTestVariable() {
		// Create traitVariable
		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(TEST_ATTRIBUTE, TEST_ATTRIBUTE, CvId.VARIABLES);
		final CVTerm property = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		this.daoFactory.getCvTermRelationshipDao().save(scale.getCvTermId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());
		final CVTerm method = this.daoFactory.getCvTermDao().save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);
		final CVTerm numericDataType = this.daoFactory.getCvTermDao().getById(DataType.NUMERIC_VARIABLE.getId());

		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getCvTermId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());

		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(null, cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(),
				VariableType.INVENTORY_ATTRIBUTE.getName(), 0));

		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableId(cvTermVariable.getCvTermId());
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter).values().stream().findFirst().get();

	}
}
