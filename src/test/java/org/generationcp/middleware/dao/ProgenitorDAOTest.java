package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Progenitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class ProgenitorDAOTest extends IntegrationTestBase {
	
	private GermplasmDAO germplasmDao;
	private ProgenitorDAO progenitorDao;
	private Germplasm testGermplasm;
	private Progenitor progenitor3;
	private Progenitor progenitor4;
	
	@Before
	public void setup() {
		if (this.progenitorDao == null) {
			this.progenitorDao = new ProgenitorDAO();
			this.progenitorDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO();
			this.germplasmDao.setSession(this.sessionProvder.getSession());
		}
		
		if (this.testGermplasm == null) {
			this.testGermplasm = GermplasmTestDataInitializer.createGermplasm(1);
			// set four parents as source
			this.testGermplasm.setGnpgs(4);
			this.germplasmDao.save(this.testGermplasm);
			
			this.progenitor3 = new Progenitor(this.testGermplasm, 3, 13);
			this.progenitorDao.save(progenitor3);
			
			this.progenitor4 = new Progenitor(this.testGermplasm, 4, 14);
			this.progenitorDao.save(progenitor4);
		}
	}
	
	@Test
	public void testGetByGIDAndPID() {
		Assert.assertNull(this.progenitorDao.getByGIDAndPID(this.testGermplasm.getGid(), 1));
		Assert.assertNull(this.progenitorDao.getByGIDAndPID(this.testGermplasm.getGid(), 2));
		
		final Progenitor progenitor3FromDB = this.progenitorDao.getByGIDAndPID(this.testGermplasm.getGid(), this.progenitor3.getProgenitorGid());
		Assert.assertNotNull(progenitor3FromDB);
		Assert.assertEquals(this.testGermplasm.getGid(), progenitor3FromDB.getGermplasm().getGid());
		Assert.assertEquals(this.progenitor3.getProgenitorGid(), progenitor3FromDB.getProgenitorGid());
		Assert.assertEquals(3, progenitor3FromDB.getProgenitorNumber().intValue());
		
		final Progenitor progenitor4FromDB = this.progenitorDao.getByGIDAndPID(this.testGermplasm.getGid(), this.progenitor4.getProgenitorGid());
		Assert.assertNotNull(progenitor4FromDB);
		Assert.assertEquals(this.testGermplasm.getGid(), progenitor4FromDB.getGermplasm().getGid());
		Assert.assertEquals(this.progenitor4.getProgenitorGid(), progenitor4FromDB.getProgenitorGid());
		Assert.assertEquals(4, progenitor4FromDB.getProgenitorNumber().intValue());
	}

}
