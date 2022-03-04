package org.generationcp.middleware.dao;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.brapi.v2.attribute.AttributeValueDto;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeValueSearchRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.AttributeExternalReference;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AttributeDAOTest extends IntegrationTestBase {

	private List<Integer> gids;
	private List<Germplasm> germplasms;
	private List<CVTerm> testAttributeTypes;
	private Attribute attribute1;
	private Attribute attribute2;
	private Attribute attribute3;

	private static final String NOTE_ATTRIBUTE = "NOTE_AA_text";
	private CropType cropType;

	private DaoFactory daoFactory;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private Pageable pageable;

	@Before
	public void setup() {
		final Session session = this.sessionProvder.getSession();

		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);
		}

		if (CollectionUtils.isEmpty(this.gids)) {
			this.cropType = new CropType();
			this.cropType.setUseUUID(false);
			this.setupTestData();
		}

		this.pageable = Mockito.mock(Pageable.class);
		Mockito.when(this.pageable.getPageSize()).thenReturn(10);
		Mockito.when(this.pageable.getPageNumber()).thenReturn(0);
	}

	private void setupTestData() {
		this.germplasms = new ArrayList<>();
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm1.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm2.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.germplasmTestDataGenerator.addGermplasm(germplasm2, germplasm2.getPreferredName(), this.cropType);
		final Germplasm germplasm3 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm3.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.germplasms.add(this.daoFactory.getGermplasmDao().save(germplasm1));
		this.germplasms.add(this.daoFactory.getGermplasmDao().save(germplasm2));
		this.germplasms.add(this.daoFactory.getGermplasmDao().save(germplasm3));
		this.gids = Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid());

		final CVTerm attributeType1 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);
		final CVTerm attributeType2 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);
		final CVTerm attributeType3 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);

		this.daoFactory.getCvTermDao().save(attributeType1);
		this.daoFactory.getCvTermDao().save(attributeType2);
		this.daoFactory.getCvTermDao().save(attributeType3);
		this.testAttributeTypes = Arrays.asList(attributeType1, attributeType2, attributeType3);

		this.attribute1 =
			new Attribute(null, germplasm1.getGid(), attributeType1.getCvTermId(), RandomStringUtils.randomAlphabetic(100), null, null,
				null,
				null);
		this.attribute2 =
			new Attribute(null, germplasm1.getGid(), attributeType2.getCvTermId(), RandomStringUtils.randomAlphabetic(100), null, null,
				null,
				null);
		this.attribute3 = this.saveAttribute(germplasm2, NOTE_ATTRIBUTE, RandomStringUtils.randomAlphabetic(100));

		this.daoFactory.getAttributeDAO().saveOrUpdate(this.attribute1);
		this.daoFactory.getAttributeDAO().saveOrUpdate(this.attribute2);
		this.daoFactory.getAttributeDAO().saveOrUpdate(this.attribute3);
	}

	@Test
	public void testGetAttributeValuesByTypeAndGIDList() {
		List<Attribute> attributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(0).getCvTermId(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());

		attributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(1).getCvTermId(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());

		attributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(2).getCvTermId(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertTrue(attributes.isEmpty());
	}

	@Test
	public void testGetByGID() {
		List<Attribute> attributes = this.daoFactory.getAttributeDAO().getByGID(this.gids.get(0));
		Assert.assertNotNull(attributes);
		Assert.assertEquals(2, attributes.size());

		attributes = this.daoFactory.getAttributeDAO().getByGID(this.gids.get(1));
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());

		attributes = this.daoFactory.getAttributeDAO().getByGID(this.gids.get(2));
		Assert.assertNotNull(attributes);
		Assert.assertTrue(attributes.isEmpty());
	}

	@Test
	public void testGetAttributesByGUIDAndAttributeIds() {
		final List<AttributeDTO> attributes = this.daoFactory.getAttributeDAO()
			.getAttributesByGUIDAndAttributeIds(
				String.valueOf(this.germplasms.get(0).getGermplasmUUID()), Lists.newArrayList(String.valueOf(this.attribute1.getTypeId())),
				null);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());
	}

	@Test
	public void testGetAttributeValues() {
		final Germplasm germplasm = this.germplasms.get(1);

		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setGermplasmDbIds(Collections.singletonList(germplasm.getGermplasmUUID()));
		final List<AttributeValueDto> result = this.daoFactory.getAttributeDAO().getAttributeValueDtos(request, this.pageable, "1");

		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.stream().allMatch(cVTerm -> cVTerm.getAttributeName().equalsIgnoreCase(NOTE_ATTRIBUTE.toUpperCase())));
		Assert.assertEquals(NOTE_ATTRIBUTE, result.get(0).getAttributeName());
		Assert.assertEquals(this.attribute3.getAval(), result.get(0).getValue());
		Assert.assertEquals(germplasm.getGermplasmUUID(), result.get(0).getGermplasmDbId());
		Assert.assertEquals(Integer.toString(this.attribute3.getTypeId()), result.get(0).getAttributeDbId());
		Assert.assertEquals(Integer.toString(this.attribute3.getAid()), result.get(0).getAttributeValueDbId());
		Assert.assertEquals(germplasm.getPreferredName().getNval(), result.get(0).getGermplasmName());
		Assert.assertEquals(Collections.singletonMap(AttributeDAO.ADDTL_INFO_LOCATION, "1"), result.get(0).getAdditionalInfo());
	}

	@Test
	public void testCountAttributeValues_FilterByGermplasmDbId() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();

		request.setGermplasmDbIds(Collections.singletonList(this.germplasms.get(0).getGermplasmUUID()));
		Assert.assertEquals(2, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));

		request.setGermplasmDbIds(Collections.singletonList(this.germplasms.get(1).getGermplasmUUID()));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));

		request.setGermplasmDbIds(Collections.singletonList(this.germplasms.get(2).getGermplasmUUID()));
		Assert.assertEquals(0, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByValidDatatype() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setAttributeValueDbIds(Collections.singletonList(Integer.toString(this.attribute3.getAid())));
		request.setDataTypes(Collections.singletonList("Text"));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByInvalidDatatype() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setAttributeValueDbIds(Collections.singletonList(Integer.toString(this.attribute3.getAid())));
		request.setDataTypes(Collections.singletonList("text"));
		Assert.assertEquals(0, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByAttributeDbId() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setAttributeDbIds(Collections.singletonList(Integer.toString(this.attribute1.getTypeId())));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByAttributeValueDbId() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setAttributeValueDbIds(Collections.singletonList(Integer.toString(this.attribute1.getAid())));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByAttributeName() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setAttributeNames(Collections.singletonList(this.testAttributeTypes.get(0).getName()));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByTraitClass() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setAttributeValueDbIds(Collections.singletonList(Integer.toString(this.attribute3.getAid())));
		request.setTraitClasses(Collections.singletonList("Passport"));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByGermplasmName() {
		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setGermplasmNames(Collections.singletonList(this.germplasms.get(1).getPreferredName().getNval()));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	@Test
	public void testCountAttributeValues_FilterByExternalReference() {
		final AttributeExternalReference attributeExternalReference = new AttributeExternalReference();
		attributeExternalReference.setAttribute(this.attribute1);
		attributeExternalReference.setSource(RandomStringUtils.randomAlphabetic(200));
		attributeExternalReference.setReferenceId(RandomStringUtils.randomAlphabetic(500));
		this.daoFactory.getAttributeExternalReferenceDao().save(attributeExternalReference);

		AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setExternalReferenceIDs(Collections.singletonList(attributeExternalReference.getReferenceId()));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));

		request = new AttributeValueSearchRequestDto();
		request.setExternalReferenceSources(Collections.singletonList(attributeExternalReference.getSource()));
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().countAttributeValueDtos(request, "1"));
	}

	private Attribute saveAttribute(final Germplasm germplasm, final String attributeType, final String value) {
		CVTerm cvTerm =
			this.daoFactory.getCvTermDao().getByNameAndCvId(attributeType, CvId.VARIABLES.getId());

		if (cvTerm == null) {
			cvTerm = new CVTerm(null, CvId.VARIABLES.getId(), attributeType, attributeType, null, 0, 0, false);
			this.daoFactory.getCvTermDao().save(cvTerm);
		}

		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(germplasm.getGid());
		attribute.setTypeId(cvTerm.getCvTermId());
		attribute.setAval(value);
		attribute.setAdate(germplasm.getGdate());
		attribute.setLocationId(1);

		this.daoFactory.getAttributeDAO().saveOrUpdate(attribute);
		return attribute;
	}

}
