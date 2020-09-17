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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmAddUpdateFunctionsTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager manager;

	@Test
	public void testAddGermplasm() {

		// ADD
		final Germplasm g = new Germplasm();
		g.setGdate(20120412);
		g.setGnpgs(0);
		g.setGpid1(0);
		g.setGpid2(0);
		g.setGrplce(0);
		g.setLocationId(9000);
		g.setMethodId(1);
		g.setMgid(1);
		g.setLgid(1);
		// g.setUserId(Integer.valueOf(527));
		g.setUserId(1);
		g.setReferenceId(1);
		g.setDeleted(Boolean.FALSE);

		final Name n = new Name();
		n.setLocationId(9000);
		n.setNdate(20120412);
		n.setNval("Kevin 64");
		n.setReferenceId(1);
		n.setTypeId(1);
		n.setUserId(1);

		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Integer addedGid = this.manager.addGermplasm(g, n, cropType);

		Debug.println(0, "Germplasm added: " + addedGid);

		final Germplasm oldG = this.manager.getGermplasmByGID(addedGid);
		final Name name = this.manager.getNameByGIDAndNval(addedGid, n.getNval(), GetGermplasmByNameModes.NORMAL);

		Assert.assertNotNull(oldG.getGid());
		Assert.assertEquals(g.getLocationId(), oldG.getLocationId());
		Assert.assertEquals(n.getNval(), name.getNval());
	}

}
