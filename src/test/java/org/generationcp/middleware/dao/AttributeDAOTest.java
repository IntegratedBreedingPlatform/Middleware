package org.generationcp.middleware.dao;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class AttributeDAOTest extends IntegrationTestBase {

	private UserDefinedFieldDAO userDefinedFieldDao;
	private AttributeDAO attributeDao;
	private GermplasmDAO germplasmDao;
	private List<Integer> gids;
	private List<String> guids;
	private List<UserDefinedField> testAttributeTypes;
	private Integer previousAttributeTypesCount = 0;
	private Attribute attribute1;
	private Attribute attribute2;
	private Attribute attribute3;

	@Before
	public void setup() {
		final Session session = this.sessionProvder.getSession();

		if (this.userDefinedFieldDao == null) {
			this.userDefinedFieldDao = new UserDefinedFieldDAO(session);
		}

		if (this.attributeDao == null) {
			this.attributeDao = new AttributeDAO();
			this.attributeDao.setSession(session);
		}

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(session);
		}

		if (CollectionUtils.isEmpty(this.gids) || CollectionUtils.isEmpty(this.guids)) {
			final List<UserDefinedField> existingAttributeTypes = this.attributeDao.getAttributeTypes();
			if (existingAttributeTypes != null && !existingAttributeTypes.isEmpty()) {
				this.previousAttributeTypesCount = existingAttributeTypes.size();
			}
			this.setupTestData();
		}
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

		final UserDefinedField attributeType1 =
			new UserDefinedField(null, "ATRIBUTS", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20),
				RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1,
				1, 20180909, null);
		final UserDefinedField attributeType2 =
			new UserDefinedField(null, "ATRIBUTS", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20),
				RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1,
				1, 20180909, null);
		final UserDefinedField attributeType3 =
			new UserDefinedField(null, "ATRIBUTS", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20),
				RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1,
				1, 20180909, null);
		this.userDefinedFieldDao.save(attributeType1);
		this.userDefinedFieldDao.save(attributeType2);
		this.userDefinedFieldDao.save(attributeType3);
		this.testAttributeTypes = Arrays.asList(attributeType1, attributeType2, attributeType3);

		this.attribute1 =
			new Attribute(null, germplasm1.getGid(), attributeType1.getFldno(), RandomStringUtils.randomAlphabetic(100), null, null, null);
		this.attribute2 =
			new Attribute(null, germplasm1.getGid(), attributeType2.getFldno(), RandomStringUtils.randomAlphabetic(100), null, null, null);
		this.attribute3 =
			new Attribute(null, germplasm2.getGid(), attributeType1.getFldno(), RandomStringUtils.randomAlphabetic(100), null, null, null);

		this.attributeDao.save(this.attribute1);
		this.attributeDao.save(this.attribute2);
		this.attributeDao.save(this.attribute3);
	}

	@Test
	public void testGetAttributeTypes() {
		final List<UserDefinedField> attributeTypes = this.attributeDao.getAttributeTypes();
		Assert.assertNotNull(attributeTypes);
		Assert.assertEquals(this.previousAttributeTypesCount + this.testAttributeTypes.size(), attributeTypes.size());
	}

	@Test
	public void testGetAttributeValuesByTypeAndGIDList() {
		List<Attribute> attributes =
			this.attributeDao.getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(0).getFldno(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(2, attributes.size());

		attributes = this.attributeDao.getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(1).getFldno(), this.gids);
		Assert.assertNotNull(attributes);
		Assert.assertEquals(1, attributes.size());

		attributes = this.attributeDao.getAttributeValuesByTypeAndGIDList(this.testAttributeTypes.get(2).getFldno(), this.gids);
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
	public void testGetAttribute() {
		final String fcode = this.testAttributeTypes.get(0).getFcode();
		Assert.assertNotNull(this.attributeDao.getAttribute(this.gids.get(0), fcode));
		Assert.assertNotNull(this.attributeDao.getAttribute(this.gids.get(1), fcode));
		Assert.assertNull(this.attributeDao.getAttribute(this.gids.get(2), fcode));
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
	public void testSearchAttributes() {
		final UserDefinedField passportAttribute =
			new UserDefinedField(null, UDTableType.ATRIBUTS_ATTRIBUTE.getTable(), UDTableType.ATRIBUTS_PASSPORT.getType(),
				RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100),
				RandomStringUtils.randomAlphabetic(100), 1, 1, 20180909, null);
		this.userDefinedFieldDao.save(passportAttribute);
		final List<org.generationcp.middleware.api.attribute.AttributeDTO> attributeDTOList =
			this.attributeDao.searchAttributes(passportAttribute.getFcode());

		Assert.assertEquals(1, attributeDTOList.size());
		Assert.assertEquals(passportAttribute.getFcode(), attributeDTOList.get(0).getCode());
		Assert.assertEquals(passportAttribute.getFname(), attributeDTOList.get(0).getName());

	}
}
