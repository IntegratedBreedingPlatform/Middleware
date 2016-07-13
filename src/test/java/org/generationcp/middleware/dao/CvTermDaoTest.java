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
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CvTermDaoTest extends IntegrationTestBase {

	private static CVDao cvDao;
	private static CVTermDao dao;

	@Before
	public void setUp() throws Exception {
		cvDao = new CVDao();
		cvDao.setSession(this.sessionProvder.getSession());
		dao = new CVTermDao();
		dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetByName() throws Exception {

		CV cv = new CV();
		cv.setName("TestCV");
		cv.setDefinition("Test CV description");

		cvDao.save(cv);

		CVTerm term = new CVTerm();
		term.setName("Test term");
		term.setDefinition("Test description");
		term.setCv(cv.getCvId());
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		dao.save(term);

		CVTerm cvTerm = dao.getByName(term.getName());

		Assert.assertNotNull(cvTerm);
		Assert.assertEquals(term.getCv(), cvTerm.getCv());
		Assert.assertEquals(term.getName(), cvTerm.getName());
		Assert.assertEquals(term.getDefinition(), cvTerm.getDefinition());
	}

	@Test
	public void testGetByNameAndCvId() throws Exception {

		CV cv = new CV();
		cv.setName("TestCV");
		cv.setDefinition("Test CV description");

		cvDao.save(cv);

		CVTerm term = new CVTerm();
		term.setName("Test term");
		term.setDefinition("Test description");
		term.setCv(cv.getCvId());
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		dao.save(term);

		CVTerm cvTerm = dao.getByNameAndCvId(term.getName(), cv.getCvId());

		Assert.assertNotNull(cvTerm);
		Assert.assertEquals(term.getCvTermId(), cvTerm.getCvTermId());
		Assert.assertEquals(term.getCv(), cvTerm.getCv());
		Assert.assertEquals(term.getName(), cvTerm.getName());
		Assert.assertEquals(term.getDefinition(), cvTerm.getDefinition());
		Assert.assertEquals(term.isObsolete(), cvTerm.isObsolete());
		Assert.assertEquals(term.isRelationshipType(), cvTerm.isRelationshipType());
	}

}
