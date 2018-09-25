/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameDAOTest extends IntegrationTestBase {

	private static final String PREFERRED_NAME = "PREFERRED";
	private static final String NOT_PREFERRED_NAME = "NOT PREFFERED";
	private static final String UNCATEGORIZED_NAME = "UNCATEGORIZED";
	private static final String DELETED_NAME = "DELETED";
	private static NameDAO nameDAO;
	private static GermplasmDAO germplasmDAO;

	@Before
	public void setUp() throws Exception {
		final Session session = this.sessionProvder.getSession();
		NameDAOTest.nameDAO = new NameDAO();
		NameDAOTest.nameDAO.setSession(session);
		NameDAOTest.germplasmDAO = new GermplasmDAO();
		NameDAOTest.germplasmDAO.setSession(session);
	}

	@Test
	public void testGetByGIDWithFilters() throws Exception {
		final int dateIntValue = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		final Germplasm germplasm = this.createGermplasmTestData(dateIntValue);
		NameDAOTest.germplasmDAO.save(germplasm);
		final Name notPreferredName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 2, NameDAOTest.NOT_PREFERRED_NAME,
						GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(notPreferredName);
		final Name uncategorizedName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 0, NameDAOTest.UNCATEGORIZED_NAME,
						GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(uncategorizedName);
		final Name preferredName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 1, NameDAOTest.PREFERRED_NAME, GermplasmNameType.LINE_NAME);
		NameDAOTest.nameDAO.save(preferredName);
		final Name deletedName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 9, NameDAOTest.DELETED_NAME, GermplasmNameType.LINE_NAME);
		NameDAOTest.nameDAO.save(deletedName);
		final List<Name> names = NameDAOTest.nameDAO.getByGIDWithFilters(germplasm.getGid(), 0, null);
		Assert.assertNotNull("The list should not be empty", names);
		Assert.assertEquals("Given the test data, there should be 3 items in the list.", 3, names.size());
		Assert.assertTrue("The first name should always be the preferred name if it exists.",
				names.get(0).getNval().equals(NameDAOTest.PREFERRED_NAME));
		Assert.assertTrue("Given the test data, the second name should be " + NameDAOTest.NOT_PREFERRED_NAME, names.get(1).getNval()
				.equals(NameDAOTest.NOT_PREFERRED_NAME));
		Assert.assertTrue("Given the test data, the third name should be " + NameDAOTest.UNCATEGORIZED_NAME,
				names.get(2).getNval().equals(NameDAOTest.UNCATEGORIZED_NAME));

	}

	@Test
	public void testGetByGIDWithFiltersOfNstat() throws Exception {
		final int dateIntValue = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		final Germplasm germplasm = this.createGermplasmTestData(dateIntValue);
		NameDAOTest.germplasmDAO.save(germplasm);
		final Name notPreferredName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 2, NameDAOTest.NOT_PREFERRED_NAME,
						GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(notPreferredName);
		final Name preferredName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 1, NameDAOTest.PREFERRED_NAME, GermplasmNameType.LINE_NAME);
		NameDAOTest.nameDAO.save(notPreferredName);
		NameDAOTest.nameDAO.save(preferredName);
		final List<Name> names = NameDAOTest.nameDAO.getByGIDWithFilters(germplasm.getGid(), 2, null);
		Assert.assertNotNull("The list should not be empty", names);
		Assert.assertEquals("Given the test data, there should only be 1 item in the list.", 1, names.size());
		Assert.assertTrue("Given the test data, the first name should be " + NameDAOTest.NOT_PREFERRED_NAME,
				names.get(0).getNval().equals(NameDAOTest.NOT_PREFERRED_NAME));
	}

	@Test
	public void testGetByGIDWithFiltersOfNtype() throws Exception {
		final int dateIntValue = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		final Germplasm germplasm = this.createGermplasmTestData(dateIntValue);
		NameDAOTest.germplasmDAO.save(germplasm);
		final Name notPreferredName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 2, NameDAOTest.NOT_PREFERRED_NAME,
						GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(notPreferredName);
		final Name preferredName =
				this.createNameTestData(dateIntValue, germplasm.getGid(), 1, NameDAOTest.PREFERRED_NAME, GermplasmNameType.LINE_NAME);
		NameDAOTest.nameDAO.save(preferredName);
		final List<Name> names = NameDAOTest.nameDAO.getByGIDWithFilters(germplasm.getGid(), null, GermplasmNameType.LINE_NAME);
		Assert.assertNotNull("The list should not be empty", names);
		Assert.assertEquals("Given the test data, there should only be 1 item in the list.", 1, names.size());
		Assert.assertTrue("Given the test data, the first name should be " + NameDAOTest.PREFERRED_NAME,
				names.get(0).getNval().equals(NameDAOTest.PREFERRED_NAME));
	}
	
	@Test
	public void testGetNamesByTypeAndGIDList() {
		final Germplasm germplasm1 = this.createGermplasmTestData(20190910);
		final Germplasm germplasm2 = this.createGermplasmTestData(20190914);
		NameDAOTest.germplasmDAO.save(germplasm1);
		NameDAOTest.germplasmDAO.save(germplasm2);
		final Name germplasm1Name1 = this.createNameTestData(20190910, germplasm1.getGid(), 0, RandomStringUtils.randomAlphabetic(20),
				GermplasmNameType.DERIVATIVE_NAME);
		final Name germplasm1Name2 = this.createNameTestData(20190910, germplasm1.getGid(), 0, RandomStringUtils.randomAlphabetic(20),
				GermplasmNameType.LINE_NAME);
		final Name germplasm2Name1 = this.createNameTestData(20190910, germplasm2.getGid(), 0, RandomStringUtils.randomAlphabetic(20),
				GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(germplasm1Name1);
		NameDAOTest.nameDAO.save(germplasm1Name2);
		NameDAOTest.nameDAO.save(germplasm2Name1);
		
		List<Name> names = NameDAOTest.nameDAO.getNamesByTypeAndGIDList(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(),
				Arrays.asList(germplasm1.getGid(), germplasm2.getGid()));
		Assert.assertNotNull(names);
		Assert.assertEquals(2, names.size());
		
		names = NameDAOTest.nameDAO.getNamesByTypeAndGIDList(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(),
				Arrays.asList(germplasm1.getGid(), germplasm2.getGid()));
		Assert.assertNotNull(names);
		Assert.assertEquals(1, names.size());
		
		names = NameDAOTest.nameDAO.getNamesByTypeAndGIDList(GermplasmNameType.CROSS_NAME.getUserDefinedFieldID(),
				Arrays.asList(germplasm1.getGid(), germplasm2.getGid()));
		Assert.assertNotNull(names);
		Assert.assertTrue(names.isEmpty());
	}

	private Germplasm createGermplasmTestData(final int dateIntValue) {
		final Germplasm germplasm = new Germplasm();
		germplasm.setUserId(1);
		germplasm.setLocationId(0);
		germplasm.setGdate(dateIntValue);
		germplasm.setMethodId(1);
		germplasm.setGnpgs(0);
		germplasm.setGpid1(0);
		germplasm.setGpid2(0);
		germplasm.setLgid(0);
		germplasm.setGrplce(0);
		germplasm.setReferenceId(0);
		germplasm.setMgid(0);
		return germplasm;
	}

	public Name createNameTestData(final int dateIntValue, final int gid, final Integer nStat, final String nameValue,
			final GermplasmNameType nType) {
		final Name name = new Name();
		name.setGermplasmId(gid);
		name.setTypeId(nType.getUserDefinedFieldID());
		name.setUserId(1);
		name.setNval(nameValue);
		name.setLocationId(0);
		name.setNdate(dateIntValue);
		name.setReferenceId(0);
		name.setNstat(nStat);
		return name;
	}

}
