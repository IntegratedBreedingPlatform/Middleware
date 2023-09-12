package org.generationcp.middleware.api.germplasm.pedigree.cop;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
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
		this.E0 = this.createGermplasm("E0", 0, 0, 0);
		this.F0 = this.createGermplasm("F0", 0, 0, 0);
		this.E0F0 = this.createGermplasm("E0F0", 2, this.E0.getGid(), this.F0.getGid());
		this.B1 = this.createGermplasm("B1", -1, this.E0F0.getGid(), 0);
		this.A0 = this.createGermplasm("A0", 0, 0, 0);
		this.A0B1 = this.createGermplasm("A0B1", 2, this.A0.getGid(), this.B1.getGid());
		this.Z2P1 = this.createGermplasm("Z2P1", -1, this.A0B1.getGid(), this.A0B1.getGid());
		this.Z2 = this.createGermplasm("Z2", -1, this.A0B1.getGid(), this.Z2P1.getGid());
		this.R2P1 = this.createGermplasm("R2P1", -1, this.Z2.getGid(), this.Z2.getGid());
		this.R2 = this.createGermplasm("R2", -1, this.Z2.getGid(), this.R2P1.getGid());
		this.Q2P2 = this.createGermplasm("Q2P2", -1, this.Z2.getGid(), this.Z2.getGid());
		this.Q2P1 = this.createGermplasm("Q2P1", -1, this.Z2.getGid(), this.Q2P2.getGid());
		this.Q2 = this.createGermplasm("Q2", -1, this.Z2.getGid(), this.Q2P1.getGid());
		this.B1R2 = this.createGermplasm("B1R2", 2, this.B1.getGid(), this.R2.getGid());
		this.P3P4 = this.createGermplasm("P3P4", -1, this.B1R2.getGid(), this.B1R2.getGid());
		this.P3P3 = this.createGermplasm("P3P3", -1, this.B1R2.getGid(), this.P3P4.getGid());
		this.P3P2 = this.createGermplasm("P3P2", -1, this.B1R2.getGid(), this.P3P3.getGid());
		this.P3P1 = this.createGermplasm("P3P1", -1, this.B1R2.getGid(), this.P3P2.getGid());
		this.P3 = this.createGermplasm("P3", -1, this.B1R2.getGid(), this.P3P1.getGid());
	}

	@Test
	public void testCoefficientOfParentage_SameParents() {
		final Germplasm p1 = this.createGermplasm("P1", 0, 0, 0);
		final Germplasm p2 = this.createGermplasm("P2", 0, 0, 0);
		final Germplasm c1 = this.createGermplasm("C1", 2, p1.getGid(), p2.getGid());
		final Germplasm c2 = this.createGermplasm("C2", 2, p1.getGid(), p2.getGid());

		assertThat(this.copService.coefficientOfParentage(c1.getGid(), c2.getGid(), this.btype), is(1 / 4d));

	}

	@Test
	public void testCoefficientOfParentage_CrossWithParent() {
		final Germplasm p1 = this.createGermplasm("P1", 0, 0, 0);
		final Germplasm p2 = this.createGermplasm("P2", 0, 0, 0);
		final Germplasm c1 = this.createGermplasm("C1", 2, p1.getGid(), p2.getGid());

		assertThat(this.copService.coefficientOfParentage(c1.getGid(), p1.getGid(), this.btype), is(1 / 4d));
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

		assertThat(this.copService.coefficientOfParentage(d1.getGid(), p1.getGid(), this.btype), is(1 / 8d));
	}

	@Test
	public void testCoefficientOfInbreeding_UnknownSource() {
		final Germplasm p1 = this.createGermplasm("P1", 0, 0, 0);
		final Germplasm p2 = this.createGermplasm("P2", -1, p1.getGid(), p1.getGid());
		final Germplasm p3 = this.createGermplasm("P3", -1, p1.getGid(), p2.getGid());
		final Germplasm p4 = this.createGermplasm("P4", -1, p1.getGid(), p3.getGid());
		final Germplasm pn = this.createGermplasm("P5-Unknown-Source", -1, p4.getGid(), 0);

		assertThat(this.copService.coefficientOfInbreeding(pn.getGid(), BTypeEnum.SELF_FERTILIZING),
			is(BTypeEnum.SELF_FERTILIZING.getValue()));
		assertThat(this.copService.coefficientOfInbreeding(pn.getGid(), BTypeEnum.SELF_FERTILIZING_F4),
			is(BTypeEnum.SELF_FERTILIZING_F4.getValue()));
	}

	@Test
	public void testCoefficientOfInbreeding_KnownSource() {
		final Germplasm p01 = this.createGermplasm("P01", 0, 0, 0);
		final Germplasm p02 = this.createGermplasm("P02", 0, 0, 0);
		final Germplasm p1 = this.createGermplasm("P1", 2, p01.getGid(), p02.getGid());
		final Germplasm p2 = this.createGermplasm("P2", -1, p1.getGid(), p1.getGid());
		final Germplasm p3 = this.createGermplasm("P3", -1, p1.getGid(), p2.getGid());
		final Germplasm p4 = this.createGermplasm("P4", -1, p1.getGid(), p3.getGid());
		final Germplasm p5 = this.createGermplasm("P5", -1, p1.getGid(), p4.getGid());
		final Germplasm p6 = this.createGermplasm("P6", -1, p1.getGid(), p5.getGid());

		assertThat(this.copService.coefficientOfInbreeding(p6.getGid(), BTypeEnum.SELF_FERTILIZING), is(31 / 32d));
	}

	/**
	 * <pre>
	 *   AA  BB  CC  DD
	 *     EE      FF
	 *         GG
	 *
	 * gid1 gid2 cop
	 * "AA" "AA" 1
	 * "BB" "AA" 0
	 * "BB" "BB" 1
	 * "CC" "AA" 0
	 * "CC" "BB" 0
	 * "CC" "CC" 1
	 * "DD" "AA" 0
	 * "DD" "BB" 0
	 * "DD" "CC" 0
	 * "DD" "DD" 1
	 * "EE" "AA" 0.5
	 * "EE" "BB" 0.5
	 * "EE" "CC" 0
	 * "EE" "DD" 0
	 * "EE" "EE" 0.5
	 * "FF" "AA" 0
	 * "FF" "BB" 0
	 * "FF" "CC" 0.5
	 * "FF" "DD" 0.5
	 * "FF" "EE" 0
	 * "FF" "FF" 0.5
	 * "GG" "AA" 0.25
	 * "GG" "BB" 0.25
	 * "GG" "CC" 0.25
	 * "GG" "DD" 0.25
	 * "GG" "EE" 0.25
	 * "GG" "FF" 0.25
	 * "GG" "GG" 0.5
	 * </pre>
	 *
	 */
	@Test
	public void testCase_SelfFertilizingF2() {
		this.btype = BTypeEnum.SELF_FERTILIZING;

		final Germplasm aa = this.createGermplasm("AA", 0, 0, 0);
		final Germplasm bb = this.createGermplasm("BB", 0, 0, 0);
		final Germplasm cc = this.createGermplasm("CC", 0, 0, 0);
		final Germplasm dd = this.createGermplasm("DD", 0, 0, 0);
		final Germplasm ee = this.createGermplasm("EE", 2, aa.getGid(), bb.getGid());
		final Germplasm ff = this.createGermplasm("FF", 2, cc.getGid(), dd.getGid());
		final Germplasm gg = this.createGermplasm("GG", 2, ee.getGid(), ff.getGid());

		assertThat(this.copService.coefficientOfParentage(aa.getGid(), aa.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(bb.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(bb.getGid(), bb.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(cc.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(cc.getGid(), bb.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(cc.getGid(), cc.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), bb.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), cc.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), dd.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), aa.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), bb.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), cc.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), dd.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), ee.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), bb.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), cc.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), dd.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), ee.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), ff.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), aa.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), bb.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), cc.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), dd.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), ee.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), ff.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), gg.getGid(), this.btype), is(1 / 2d));
	}

	@Test
	public void testCase_SelfFertilizingF2WithIncorrectProgenitorNumberForF0() {
		this.btype = BTypeEnum.SELF_FERTILIZING;

		final Germplasm aa = this.createGermplasm("AA", -1, 0, 0);
		final Germplasm bb = this.createGermplasm("BB", -1, 0, 0);
		final Germplasm cc = this.createGermplasm("CC", -1, 0, 0);
		final Germplasm dd = this.createGermplasm("DD", -1, 0, 0);
		final Germplasm ee = this.createGermplasm("EE", 2, aa.getGid(), bb.getGid());
		final Germplasm ff = this.createGermplasm("FF", 2, cc.getGid(), dd.getGid());
		final Germplasm gg = this.createGermplasm("GG", 2, ee.getGid(), ff.getGid());

		assertThat(this.copService.coefficientOfParentage(aa.getGid(), aa.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(bb.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(bb.getGid(), bb.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(cc.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(cc.getGid(), bb.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(cc.getGid(), cc.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), bb.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), cc.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(dd.getGid(), dd.getGid(), this.btype), is(1d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), aa.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), bb.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), cc.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), dd.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ee.getGid(), ee.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), aa.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), bb.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), cc.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), dd.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), ee.getGid(), this.btype), is(0d));
		assertThat(this.copService.coefficientOfParentage(ff.getGid(), ff.getGid(), this.btype), is(1 / 2d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), aa.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), bb.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), cc.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), dd.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), ee.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), ff.getGid(), this.btype), is(1 / 4d));
		assertThat(this.copService.coefficientOfParentage(gg.getGid(), gg.getGid(), this.btype), is(1 / 2d));
	}


	private Germplasm createGermplasm(final String name, final int gnpgs, final int gpid1, final int gpid2) {
		final Name preferredName = new Name();
		preferredName.setNval(name);
		final Germplasm germplasm = new Germplasm(null, gnpgs, gpid1, gpid2, 0, 0, 0, 0, 0, preferredName, null, new Method(1));
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);
		return germplasm;
	}
}
