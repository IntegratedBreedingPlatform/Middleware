package org.generationcp.middleware.dao;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.brapi.v2.attribute.AttributeValueDto;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeValueSearchRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeDAOTest extends IntegrationTestBase {

	private AttributeDAO attributeDao;
	private CVTermDao cvTermDao;
	private GermplasmDAO germplasmDao;
	private List<Integer> gids;
	private List<String> guids;
	private List<CVTerm> testAttributeTypes;
	private Attribute attribute1;
	private Attribute attribute2;
	private Attribute attribute3;

	private static final String NOTE_ATTRIBUTE = "NOTE_AA_text";
	private CropType cropType;

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Before
	public void setup() {
		final Session session = this.sessionProvder.getSession();

		this.daoFactory = new DaoFactory(this.sessionProvder);
		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao();
			this.cvTermDao.setSession(session);
		}

		if (this.attributeDao == null) {
			this.attributeDao = new AttributeDAO();
			this.attributeDao.setSession(session);
		}

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(session);
		}

		if (CollectionUtils.isEmpty(this.gids) || CollectionUtils.isEmpty(this.guids)) {
			this.setupTestData();
		}

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
	}

	private void setupTestData() {
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm1.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm2.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		final Germplasm germplasm3 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm3.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.germplasmDao.save(germplasm1);
		this.germplasmDao.save(germplasm2);
		this.germplasmDao.save(germplasm3);
		this.gids = Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid());
		this.guids = Arrays.asList(germplasm1.getGermplasmUUID(), germplasm2.getGermplasmUUID(), germplasm3.getGermplasmUUID());

		final CVTerm attributeType1 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);
		final CVTerm attributeType2 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);
		final CVTerm attributeType3 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);

		this.cvTermDao.save(attributeType1);
		this.cvTermDao.save(attributeType2);
		this.cvTermDao.save(attributeType3);
		this.testAttributeTypes = Arrays.asList(attributeType1, attributeType2, attributeType3);

		this.attribute1 =
			new Attribute(null, germplasm1.getGid(), attributeType1.getCvTermId(), RandomStringUtils.randomAlphabetic(100), null, null,
				null,
				null);
		this.attribute2 =
			new Attribute(null, germplasm1.getGid(), attributeType2.getCvTermId(), RandomStringUtils.randomAlphabetic(100), null, null,
				null,
				null);
		this.attribute3 =
			new Attribute(null, germplasm2.getGid(), attributeType1.getCvTermId(), RandomStringUtils.randomAlphabetic(100), null, null,
				null,
				null);

		this.attributeDao.save(this.attribute1);
		this.attributeDao.save(this.attribute2);
		this.attributeDao.save(this.attribute3);
	}

	@Test
	public void testGetAttributeValuesByTypeAndGIDList() {
		List<Attribute> attributes =
			this.attributeDao.getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(0).getCvTermId(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(2, attributes.size());

		attributes = this.attributeDao.getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(1).getCvTermId(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());

		attributes = this.attributeDao.getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(2).getCvTermId(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertTrue(attributes.isEmpty());
	}

	@Test
	public void testGetByGID() {
		List<Attribute> attributes = this.attributeDao.getByGID(this.gids.get(0));
		Assert.assertNotNull(attributes);
		Assert.assertEquals(2, attributes.size());

		attributes = this.attributeDao.getByGID(this.gids.get(1));
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());

		attributes = this.attributeDao.getByGID(this.gids.get(2));
		Assert.assertNotNull(attributes);
		Assert.assertTrue(attributes.isEmpty());
	}

	@Test
	public void testGetAttributesByGUIDAndAttributeIds() {
		final List<AttributeDTO> attributes = this.attributeDao
			.getAttributesByGUIDAndAttributeIds(
				String.valueOf(this.guids.get(0)), Lists.newArrayList(String.valueOf(this.attribute1.getTypeId())), null);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());
	}

	@Test
	public void testGetAttributeValues() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmGID = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final Map<String, String> fields = new HashMap<>();
		// atributs
		fields.put(NOTE_ATTRIBUTE, "");
		for (final Map.Entry<String, String> attributEntry : fields.entrySet()) {
			final Attribute attribute = this.saveAttribute(germplasm, attributEntry.getKey());
		}

		final AttributeValueSearchRequestDto request = new AttributeValueSearchRequestDto();
		request.setGermplasmDbIds(Collections.singletonList(germplasm.getGermplasmUUID()));
		final List<AttributeValueDto> result = this.daoFactory.getAttributeDAO().getAttributeValueDtos(request, pageable, "1");

		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.stream().allMatch(cVTerm -> cVTerm.getAttributeName().equalsIgnoreCase(NOTE_ATTRIBUTE.toUpperCase())));
		Assert.assertEquals(NOTE_ATTRIBUTE, result.get(0).getAttributeName());
	}

	@Test
	public void testCountAttributeValues() {
	}

	private Attribute saveAttribute(final Germplasm germplasm, final String attributeType) {
		CVTerm cvTerm =
			this.daoFactory.getCvTermDao().getByNameAndCvId(attributeType, CvId.VARIABLES.getId());

		if (cvTerm == null) {
			cvTerm = new CVTerm(null, CvId.VARIABLES.getId(), attributeType, attributeType, null, 0, 0, false);
			this.daoFactory.getCvTermDao().save(cvTerm);
		}

		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(germplasm.getGid());
		attribute.setTypeId(cvTerm.getCvTermId());
		attribute.setAval(RandomStringUtils.randomAlphanumeric(50)); //TODO add this to assertion
		attribute.setAdate(germplasm.getGdate());

		this.germplasmDataDM.addGermplasmAttribute(attribute);
		return attribute;
	}

}
