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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GermplasmAddUpdateFunctionsTest extends DataManagerIntegrationTest {

	private static GermplasmDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		GermplasmAddUpdateFunctionsTest.manager = DataManagerIntegrationTest.managerFactory.getGermplasmDataManager();
	}

	@Test
	public void testAddAndUpdateGermplasm() throws Exception {

		// ADD
		Germplasm g = new Germplasm();
		g.setGdate(Integer.valueOf(20120412));
		g.setGnpgs(Integer.valueOf(0));
		g.setGpid1(Integer.valueOf(0));
		g.setGpid2(Integer.valueOf(0));
		g.setGrplce(Integer.valueOf(0));
		g.setLocationId(Integer.valueOf(9000));
		g.setMethodId(Integer.valueOf(1));
		g.setMgid(Integer.valueOf(1));
		// g.setUserId(Integer.valueOf(527));
		g.setUserId(Integer.valueOf(1));
		g.setReferenceId(Integer.valueOf(1));

		Name n = new Name();
		n.setLocationId(Integer.valueOf(9000));
		n.setNdate(Integer.valueOf(20120412));
		n.setNval("Kevin 64");
		n.setReferenceId(Integer.valueOf(1));
		n.setTypeId(Integer.valueOf(1));
		n.setUserId(Integer.valueOf(1));

		Integer addedGid = GermplasmAddUpdateFunctionsTest.manager.addGermplasm(g, n);

		Debug.println(0, "Germplasm added: " + addedGid);

		Germplasm oldG = GermplasmAddUpdateFunctionsTest.manager.getGermplasmByGID(addedGid);
		Name name = GermplasmAddUpdateFunctionsTest.manager.getNameByGIDAndNval(addedGid, n.getNval(), GetGermplasmByNameModes.NORMAL);

		Assert.assertNotNull(oldG.getGid());
		Assert.assertEquals(g.getLocationId(), oldG.getLocationId());
		Assert.assertEquals(n.getNval(), name.getNval());

		// UPDATE
		List<Germplasm> gList = new ArrayList<Germplasm>();
		oldG.setGdate(3000);
		gList.add(oldG);
		GermplasmAddUpdateFunctionsTest.manager.updateGermplasm(gList);
		Germplasm newG = GermplasmAddUpdateFunctionsTest.manager.getGermplasmByGID(addedGid);
		Assert.assertEquals(newG.getGdate(), oldG.getGdate());

		// update the GID via grplce
		newG.setGrplce(addedGid);
		gList.clear();
		gList.add(newG);
		GermplasmAddUpdateFunctionsTest.manager.updateGermplasm(gList);
		Germplasm newerG = GermplasmAddUpdateFunctionsTest.manager.getGermplasmByGID(newG.getGid());
		Assert.assertNull(newerG);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testUpdateGermplasm(" + addedGid + ")");

	}

}
