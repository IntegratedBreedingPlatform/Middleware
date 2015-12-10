/*******************************************************************************
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

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersonDAOTest extends IntegrationTestBase {

	private PersonDAO dao;
	private Person testPerson;

	@Before
	public void setUp() throws Exception {
		if (this.dao == null) {
			this.dao = new PersonDAO();
			this.dao.setSession(this.sessionProvder.getSession());
		}

		if (this.testPerson == null) {
			this.testPerson = new Person("first", "middle", "last");
			this.testPerson.setId(1);
			this.testPerson.setContact("");
			this.testPerson.setEmail("first@gmail.com");
			this.testPerson.setExtension("");
			this.testPerson.setFax("");
			this.testPerson.setInstituteId(0);
			this.testPerson.setLanguage(0);
			this.testPerson.setNotes("");
			this.testPerson.setPhone("");
			this.testPerson.setPositionName("");
			this.testPerson.setTitle("Mr.");
			this.dao.save(this.testPerson);
		}
	}

	@Test
	public void testGetExistingPersonIds() {
		final List<Integer> personIdsToVerify = Arrays.asList(new Integer[] {12345678, 8765432, this.testPerson.getId()});
		final List<Integer> existingPersonIds = this.dao.getExistingPersonIds(personIdsToVerify);
		Assert.assertFalse(existingPersonIds.isEmpty());
		Assert.assertEquals(1, existingPersonIds.size());
		Assert.assertEquals(this.testPerson.getId(), existingPersonIds.get(0));
	}
}
