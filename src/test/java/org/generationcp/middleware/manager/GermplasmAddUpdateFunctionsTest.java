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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmAddUpdateFunctionsTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager manager;

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
		g.setLgid(Integer.valueOf(1));
		// g.setUserId(Integer.valueOf(527));
		g.setUserId(Integer.valueOf(1));
		g.setReferenceId(Integer.valueOf(1));
		g.setDeleted(Boolean.FALSE);

		Name n = new Name();
		n.setLocationId(Integer.valueOf(9000));
		n.setNdate(Integer.valueOf(20120412));
		n.setNval("Kevin 64");
		n.setReferenceId(Integer.valueOf(1));
		n.setTypeId(Integer.valueOf(1));
		n.setUserId(Integer.valueOf(1));

		Integer addedGid = this.manager.addGermplasm(g, n);

		Debug.println(0, "Germplasm added: " + addedGid);

		Germplasm oldG = this.manager.getGermplasmByGID(addedGid);
		Name name = this.manager.getNameByGIDAndNval(addedGid, n.getNval(), GetGermplasmByNameModes.NORMAL);

		Assert.assertNotNull(oldG.getGid());
		Assert.assertEquals(g.getLocationId(), oldG.getLocationId());
		Assert.assertEquals(n.getNval(), name.getNval());

		// UPDATE
		List<Germplasm> gList = new ArrayList<Germplasm>();
		oldG.setGdate(3000);
		gList.add(oldG);
		this.manager.updateGermplasm(gList);
		Germplasm newG = this.manager.getGermplasmByGID(addedGid);
		Assert.assertEquals(newG.getGdate(), oldG.getGdate());

		// update the GID via grplce
		newG.setGrplce(addedGid);
		gList.clear();
		gList.add(newG);
		this.manager.updateGermplasm(gList);
		Germplasm newerG = this.manager.getGermplasmByGID(newG.getGid());
		Assert.assertEquals(newerG, newG);
		Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasm(" + addedGid + ")");

	}

}
