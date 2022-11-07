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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NameDAOTest extends IntegrationTestBase {

	private static final String PREFERRED_NAME = "PREFERRED";
	private static final String NOT_PREFERRED_NAME = "NOT PREFFERED";
	private static final String UNCATEGORIZED_NAME = "UNCATEGORIZED";
	private static final String DELETED_NAME = "DELETED";
	private static NameDAO nameDAO;
	private static GermplasmDAO germplasmDAO;

	@Before
	public void setUp() throws Exception {
		NameDAOTest.nameDAO = new NameDAO(this.sessionProvder.getSession());
		NameDAOTest.germplasmDAO = new GermplasmDAO(this.sessionProvder.getSession());
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
	public void testGetNamesByGidsAndPrefixes() {
		final Germplasm germplasm = this.createGermplasmTestData(20190910);
		NameDAOTest.germplasmDAO.save(germplasm);
		final Name name1 = this.createNameTestData(20190910, germplasm.getGid(), 0, "PREF 001",
			GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(name1);
		final Name name2 = this.createNameTestData(20190910, germplasm.getGid(), 0, "REF 001",
			GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(name2);
		final List<String> names = NameDAOTest.nameDAO.getNamesByGidsAndPrefixes(Collections.singletonList(germplasm.getGid()), Collections.singletonList("PREF"));
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(name1.getNval(), names.get(0));

	}

	@Test
	public void testCheckIfMatches() {
		final Germplasm germplasm1 = this.createGermplasmTestData(20190910);
		NameDAOTest.germplasmDAO.save(germplasm1);
		final Name name1 = this.createNameTestData(20190910, germplasm1.getGid(), 0, "SKSKSKSKSPREF 001",
			GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(name1);
		Assert.assertTrue(NameDAOTest.nameDAO.checkIfMatches(name1.getNval()));

		final Germplasm germplasm2 = this.createGermplasmTestData(20190910);
		germplasm2.setDeleted(true);
		NameDAOTest.germplasmDAO.save(germplasm2);
		final Name name2 = this.createNameTestData(20190910, germplasm2.getGid(), 0, "SKSKSKSKSPREF 002",
			GermplasmNameType.DERIVATIVE_NAME);
		NameDAOTest.nameDAO.save(name2);
		Assert.assertFalse(NameDAOTest.nameDAO.checkIfMatches(name2.getNval()));
	}

	private Germplasm createGermplasmTestData(final int dateIntValue) {
		final Germplasm germplasm = new Germplasm();
		germplasm.setLocationId(0);
		germplasm.setGdate(dateIntValue);
		germplasm.setMethod(new Method(1));
		germplasm.setGnpgs(0);
		germplasm.setGpid1(0);
		germplasm.setGpid2(0);
		germplasm.setGrplce(0);
		germplasm.setReferenceId(0);
		germplasm.setMgid(0);
		return germplasm;
	}

	public Name createNameTestData(final int dateIntValue, final int gid, final Integer nStat, final String nameValue,
			final GermplasmNameType nType) {
		final Name name = new Name();
		name.setGermplasm(new Germplasm(gid));
		name.setTypeId(nType.getUserDefinedFieldID());
		name.setNval(nameValue);
		name.setLocationId(0);
		name.setNdate(dateIntValue);
		name.setReferenceId(0);
		name.setNstat(nStat);
		return name;
	}

}
