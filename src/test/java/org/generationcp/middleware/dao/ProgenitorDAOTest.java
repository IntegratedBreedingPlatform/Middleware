package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Progenitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class ProgenitorDAOTest extends IntegrationTestBase {

	private GermplasmDAO germplasmDao;
	private ProgenitorDAO progenitorDao;
	private Germplasm testGermplasm;
	private Progenitor progenitor3;
	private Progenitor progenitor4;
	
	@Before
	public void setup() {
		if (this.progenitorDao == null) {
			this.progenitorDao = new ProgenitorDAO(this.sessionProvder.getSession());
		}
		
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());
		}
		
		if (this.testGermplasm == null) {
			this.testGermplasm = GermplasmTestDataInitializer.createGermplasm(1);
			// set four parents as source
			this.testGermplasm.setGnpgs(4);
			this.germplasmDao.save(this.testGermplasm);

			this.progenitor3 = this.createProgenitor(this.testGermplasm, 3, 13);
			this.progenitor4 = this.createProgenitor(this.testGermplasm, 4, 14);
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
	
	@Test
	public void testGetByGIDAndProgenitorNumber() {
		Assert.assertNull(this.progenitorDao.getByGIDAndProgenitorNumber(this.testGermplasm.getGid(), 1));
		Assert.assertNull(this.progenitorDao.getByGIDAndProgenitorNumber(this.testGermplasm.getGid(), 2));
		
		final Progenitor progenitor3FromDB = this.progenitorDao.getByGIDAndProgenitorNumber(this.testGermplasm.getGid(), 3);
		Assert.assertNotNull(progenitor3FromDB);
		Assert.assertEquals(this.testGermplasm.getGid(), progenitor3FromDB.getGermplasm().getGid());
		Assert.assertEquals(this.progenitor3.getProgenitorGid(), progenitor3FromDB.getProgenitorGid());
		Assert.assertEquals(3, progenitor3FromDB.getProgenitorNumber().intValue());
		
		final Progenitor progenitor4FromDB = this.progenitorDao.getByGIDAndProgenitorNumber(this.testGermplasm.getGid(), 4);
		Assert.assertNotNull(progenitor4FromDB);
		Assert.assertEquals(this.testGermplasm.getGid(), progenitor4FromDB.getGermplasm().getGid());
		Assert.assertEquals(this.progenitor4.getProgenitorGid(), progenitor4FromDB.getProgenitorGid());
		Assert.assertEquals(4, progenitor4FromDB.getProgenitorNumber().intValue());
	}

	@Test
	public void updateProgenitor() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasmDao.save(germplasm1);

		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasmDao.save(germplasm2);

		final Progenitor progenitor = this.createProgenitor(germplasm1, 1, germplasm2.getGid());
		final Progenitor actualProgenitor =
			this.progenitorDao.getByGIDAndProgenitorNumber(germplasm1.getGid(), progenitor.getProgenitorNumber());
		Assert.assertThat(actualProgenitor.getProgenitorGid(), is(germplasm2.getGid()));
		Assert.assertNull(actualProgenitor.getModifiedBy());
		Assert.assertNull(actualProgenitor.getModifiedDate());

		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasmDao.save(germplasm3);

		progenitor.setProgenitorNumber(1);
		progenitor.setGermplasm(germplasm3);
		this.progenitorDao.save(progenitor);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		this.sessionProvder.getSession().refresh(progenitor);

		final Progenitor byGIDAndProgenitorNumber = this.progenitorDao.getByGIDAndProgenitorNumber(germplasm3.getGid(), 1);
		Assert.assertThat(byGIDAndProgenitorNumber.getModifiedBy(), is(this.findAdminUser()));
		Assert.assertNotNull(byGIDAndProgenitorNumber.getModifiedDate());
	}

	private Progenitor createProgenitor(final Germplasm germplasm, final Integer number, final Integer gid) {
		final Progenitor progenitor = new Progenitor(germplasm, number, gid);
		this.progenitorDao.save(progenitor);

		Assert.assertThat(progenitor.getCreatedBy(), is(this.findAdminUser()));
		Assert.assertNotNull(progenitor.getCreatedDate());
		Assert.assertNull(progenitor.getModifiedBy());
		Assert.assertNull(progenitor.getModifiedDate());

		return progenitor;
	}

}
