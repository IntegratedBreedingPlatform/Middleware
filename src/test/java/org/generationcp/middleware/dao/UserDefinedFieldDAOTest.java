package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.NameTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserDefinedFieldTestDataInitializer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;



public class UserDefinedFieldDAOTest extends IntegrationTestBase {
	
	private UserDefinedFieldDAO userDefinedFieldDao;
	private AttributeDAO attributeDao;
	private NameDAO nameDao;
	private GermplasmDAO germplasmDao;
	private List<Integer> gids;
	private List<UserDefinedField> nameTypes;
	
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
		
		if (this.nameDao == null) {
			this.nameDao = new NameDAO(session);
		}
		
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(session);
		}
		
		if (this.gids == null) {
			setupTestData();
		}
	}

	private void setupTestData() {
		final Germplasm germplasm1 =
				GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Germplasm germplasm2 =
				GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmDao.save(germplasm1);
		this.germplasmDao.save(germplasm2);
		this.gids = Arrays.asList(germplasm1.getGid(), germplasm2.getGid());
		
		final UserDefinedField nameType1 = new UserDefinedField(null, "NAMES", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1, 1, 20180909, null);
		final UserDefinedField nameType2 = new UserDefinedField(null, "NAMES", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1, 1, 20180909, null);
		final UserDefinedField nameType3 = new UserDefinedField(null, "NAMES", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1, 1, 20180909, null);
		final UserDefinedField nameType4 = new UserDefinedField(null, "NAMES", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100), 1, 1, 20180909, null);
		this.userDefinedFieldDao.save(nameType1);
		this.userDefinedFieldDao.save(nameType2);
		this.userDefinedFieldDao.save(nameType3);
		this.userDefinedFieldDao.save(nameType4);
		this.nameTypes = Arrays.asList(nameType1, nameType2, nameType3, nameType4);
		
		final Name name1 = NameTestDataInitializer.createName(nameType1.getFldno(), germplasm1.getGid(), RandomStringUtils.randomAlphabetic(100));
		final Name name2 = NameTestDataInitializer.createName(nameType2.getFldno(), germplasm2.getGid(), RandomStringUtils.randomAlphabetic(100));
		this.nameDao.save(name1);
		this.nameDao.save(name2);

		// Delete a name
		final Name deletedName = NameTestDataInitializer.createName(nameType4.getFldno(), germplasm2.getGid(), RandomStringUtils.randomAlphabetic(100));
		deletedName.setNstat(9);
		this.nameDao.save(deletedName);
	}

	@Test
	public void testGetNameTypesByGIDList() {
		final List<UserDefinedField> nameTypesByGID = this.userDefinedFieldDao.getNameTypesByGIDList(this.gids);
		Assert.assertNotNull(nameTypesByGID);
		Assert.assertEquals(2, nameTypesByGID.size());
		Assert.assertTrue(nameTypesByGID.contains(this.nameTypes.get(0)));
		Assert.assertTrue(nameTypesByGID.contains(this.nameTypes.get(1)));
		Assert.assertFalse(nameTypesByGID.contains(this.nameTypes.get(2)));
		Assert.assertFalse(nameTypesByGID.contains(this.nameTypes.get(3)));
	}

	@Test
	public void testGetByFieldTableNameAndFTypeAndFName() {
		final UserDefinedField udfld = UserDefinedFieldTestDataInitializer.createUserDefinedField("NAMES", "NAME", "FNAME12345");
		this.userDefinedFieldDao.save(udfld);
		final List<UserDefinedField> userDefinedFields = this.userDefinedFieldDao.getByFieldTableNameAndFTypeAndFName(udfld.getFtable(), udfld.getFtype(), udfld.getFname());
		Assert.assertNotNull(userDefinedFields);
		Assert.assertFalse(userDefinedFields.isEmpty());
		for(final UserDefinedField userDefinedField: userDefinedFields) {
			Assert.assertEquals(udfld.getFtable(), userDefinedField.getFtable());
			Assert.assertEquals(udfld.getFtype(), userDefinedField.getFtype());
			Assert.assertEquals(udfld.getFname(), userDefinedField.getFname());
		}
	}

}
