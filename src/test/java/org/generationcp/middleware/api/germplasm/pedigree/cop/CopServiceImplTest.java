package org.generationcp.middleware.api.germplasm.pedigree.cop;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CopServiceImplTest extends IntegrationTestBase {

	private static final BTypeEnum BTYPE_DEFAULT = BTypeEnum.CROSS_FERTILIZING;

	private DaoFactory daoFactory;

	private Germplasm P3;
	private Germplasm Q2;
	private Germplasm E0;
	private Germplasm F0;
	private Germplasm E0F0;
	private Germplasm B1;
	private Germplasm A0;
	private Germplasm A0B1;
	private Germplasm Z2P1;
	private Germplasm Z2;
	private Germplasm R2P1;
	private Germplasm R2;
	private Germplasm Q2P2;
	private Germplasm Q2P1;
	private Germplasm B1R2;
	private Germplasm P3P4;
	private Germplasm P3P3;
	private Germplasm P3P2;
	private Germplasm P3P1;

	private BTypeEnum btype = BTYPE_DEFAULT;

	@Autowired
	private CopService copService;

	@Before
	public void setup() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.createPedigree();
		this.btype = BTYPE_DEFAULT;
	}

	/**
	 * @see CopCalculation
	 */
	@Test
	public void testCoefficientOfParentage_BaseCase() {
		this.btype = BTypeEnum.SELF_FERTILIZING_F4;

		// TODO refactor, use api with list of gids params
		assertThat(this.copService.coefficientOfParentage(this.E0.getGid(), this.F0.getGid(), this.btype), is(0.0));
		assertThat(this.copService.coefficientOfInbreeding(this.B1.getGid(), this.btype), is(15 / 16.0));
		assertThat(this.copService.coefficientOfParentage(this.B1.getGid(), this.B1.getGid(), this.btype), is(31 / 32.0));
		assertThat(this.copService.coefficientOfInbreeding(this.Z2.getGid(), this.btype), is(3 / 4.0));
		assertThat(this.copService.coefficientOfParentage(this.R2.getGid(), this.Q2.getGid(), this.btype), is(7 / 8.0));
		assertThat(this.copService.coefficientOfParentage(this.B1.getGid(), this.Q2.getGid(), this.btype), is(31 / 64.0));
		assertThat(this.copService.coefficientOfParentage(this.P3.getGid(), this.Q2.getGid(), this.btype), is(87 / 128.0));
	}

	/**
	 * @see CopCalculation
	 */
	private void createPedigree() {
		this.E0 = createGermplasm("E0", 0, 0, 0);
		this.F0 = createGermplasm("F0", 0, 0, 0);
		this.E0F0 = createGermplasm("E0F0", 2, E0.getGid(), F0.getGid());
		this.B1 = createGermplasm("B1", -1, E0F0.getGid(), 0);
		this.A0 = createGermplasm("A0", 0, 0, 0);
		this.A0B1 = createGermplasm("A0B1", 2, A0.getGid(), B1.getGid());
		this.Z2P1 = createGermplasm("Z2P1", -1, A0B1.getGid(), A0B1.getGid());
		this.Z2 = createGermplasm("Z2", -1, A0B1.getGid(), Z2P1.getGid());
		this.R2P1 = createGermplasm("R2P1", -1, Z2.getGid(), Z2.getGid());
		this.R2 = createGermplasm("R2", -1, Z2.getGid(), R2P1.getGid());
		this.Q2P2 = createGermplasm("Q2P2", -1, Z2.getGid(), Z2.getGid());
		this.Q2P1 = createGermplasm("Q2P1", -1, Z2.getGid(), Q2P2.getGid());
		this.Q2 = createGermplasm("Q2", -1, Z2.getGid(), Q2P1.getGid());
		this.B1R2 = createGermplasm("B1R2", 2, B1.getGid(), R2.getGid());
		this.P3P4 = createGermplasm("P3P4", -1, B1R2.getGid(), B1R2.getGid());
		this.P3P3 = createGermplasm("P3P3", -1, B1R2.getGid(), P3P4.getGid());
		this.P3P2 = createGermplasm("P3P2", -1, B1R2.getGid(), P3P3.getGid());
		this.P3P1 = createGermplasm("P3P1", -1, B1R2.getGid(), P3P2.getGid());
		this.P3 = createGermplasm("P3", -1, B1R2.getGid(), P3P1.getGid());
	}

	@Test
	public void testCoefficientOfParentage_SameParents() {
		final Germplasm p1 = this.createGermplasm("P1", 0, 0, 0);
		final Germplasm p2 = this.createGermplasm("P2", 0, 0, 0);
		final Germplasm c1 = this.createGermplasm("C1", 2, p1.getGid(), p2.getGid());
		final Germplasm c2 = this.createGermplasm("C2", 2, p1.getGid(), p2.getGid());

		assertThat(this.copService.coefficientOfParentage(c1.getGid(), c2.getGid(), this.btype), is(1/4d));

	}

	@Test
	public void testCoefficientOfParentage_CrossWithParent() {
		final Germplasm p1 = this.createGermplasm("P1", 0, 0, 0);
		final Germplasm p2 = this.createGermplasm("P2", 0, 0, 0);
		final Germplasm c1 = this.createGermplasm("C1", 2, p1.getGid(), p2.getGid());

		assertThat(this.copService.coefficientOfParentage(c1.getGid(), p1.getGid(), this.btype), is(1/4d));
	}

	@Test
	public void testCoefficientOfParentage_CrossWithGrandParents() {
		final Germplasm p1 = this.createGermplasm("P1", 0, 0, 0);
		final Germplasm p2 = this.createGermplasm("P2", 0, 0, 0);
		final Germplasm p3 = this.createGermplasm("P3", 0, 0, 0);
		final Germplasm p4 = this.createGermplasm("P4", 0, 0, 0);
		final Germplasm c1 = this.createGermplasm("C1", 2, p1.getGid(), p2.getGid());
		final Germplasm c2 = this.createGermplasm("C2", 2, p3.getGid(), p4.getGid());
		final Germplasm d1 = this.createGermplasm("D1", 2, c1.getGid(), c2.getGid());

		assertThat(this.copService.coefficientOfParentage(d1.getGid(), p1.getGid(), this.btype), is(1/8d));
	}


	private Germplasm createGermplasm(final String name, final int gnpgs, final int gpid1, final int gpid2) {
		final Name preferredName = new Name();
		preferredName.setNval(name);
		final Germplasm germplasm = new Germplasm(null, 1, gnpgs, gpid1, gpid2, 0, 0, 0, 0, 0, 0, preferredName, null, null);
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);
		return germplasm;
	}
}