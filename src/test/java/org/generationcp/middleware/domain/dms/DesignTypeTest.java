package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

public class DesignTypeTest {

	@Test
	public void testGetTermIdByDesignTypeId() {
		Assert.assertEquals(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), DesignType.getTermIdByDesignTypeId(0, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(), DesignType.getTermIdByDesignTypeId(1, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(), DesignType.getTermIdByDesignTypeId(1, true));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(), DesignType.getTermIdByDesignTypeId(2, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), DesignType.getTermIdByDesignTypeId(2, true));
		Assert.assertEquals(TermId.OTHER_DESIGN.getId(), DesignType.getTermIdByDesignTypeId(3, false));
		Assert.assertEquals(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), DesignType.getTermIdByDesignTypeId(4, false));
		Assert.assertEquals(TermId.ENTRY_LIST_ORDER.getId(), DesignType.getTermIdByDesignTypeId(5, false));
		Assert.assertEquals(TermId.P_REP.getId(), DesignType.getTermIdByDesignTypeId(6, false));
	}

	@Test
	public void testGetDesignTypeItemByTermId() {
		Assert.assertEquals(
			DesignType.RANDOMIZED_COMPLETE_BLOCK,
			DesignType.getDesignTypeItemByTermId(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));
		Assert.assertEquals(
			DesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			DesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));
		Assert.assertEquals(
			DesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			DesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));
		Assert.assertEquals(DesignType.ROW_COL, DesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));
		Assert.assertEquals(
			DesignType.ROW_COL,
			DesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));
		Assert.assertEquals(DesignType.CUSTOM_IMPORT, DesignType.getDesignTypeItemByTermId(TermId.OTHER_DESIGN.getId()));
		Assert.assertEquals(
			DesignType.AUGMENTED_RANDOMIZED_BLOCK,
			DesignType.getDesignTypeItemByTermId(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()));
		Assert.assertEquals(DesignType.ENTRY_LIST_ORDER, DesignType.getDesignTypeItemByTermId(TermId.ENTRY_LIST_ORDER.getId()));
		Assert.assertEquals(DesignType.P_REP, DesignType.getDesignTypeItemByTermId(TermId.P_REP.getId()));
	}

}
