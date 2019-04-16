package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

public class DesignTypeItemTest {

	@Test
	public void testGetTermIdByDesignTypeId() {
		Assert.assertEquals(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), DesignTypeItem.getTermIdByDesignTypeId(0, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(), DesignTypeItem.getTermIdByDesignTypeId(1, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(), DesignTypeItem.getTermIdByDesignTypeId(1, true));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(), DesignTypeItem.getTermIdByDesignTypeId(2, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), DesignTypeItem.getTermIdByDesignTypeId(2, true));
		Assert.assertEquals(TermId.OTHER_DESIGN.getId(), DesignTypeItem.getTermIdByDesignTypeId(3, false));
		Assert.assertEquals(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), DesignTypeItem.getTermIdByDesignTypeId(4, false));
		Assert.assertEquals(TermId.ENTRY_LIST_ORDER.getId(), DesignTypeItem.getTermIdByDesignTypeId(5, false));
		Assert.assertEquals(TermId.P_REP.getId(), DesignTypeItem.getTermIdByDesignTypeId(6, false));
	}

	@Test
	public void testGetDesignTypeItemByTermId() {
		Assert.assertEquals(
			DesignTypeItem.RANDOMIZED_COMPLETE_BLOCK,
			DesignTypeItem.getDesignTypeItemByTermId(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));
		Assert.assertEquals(
			DesignTypeItem.RESOLVABLE_INCOMPLETE_BLOCK,
			DesignTypeItem.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));
		Assert.assertEquals(
			DesignTypeItem.RESOLVABLE_INCOMPLETE_BLOCK,
			DesignTypeItem.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));
		Assert.assertEquals(DesignTypeItem.ROW_COL, DesignTypeItem.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));
		Assert.assertEquals(
			DesignTypeItem.ROW_COL,
			DesignTypeItem.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));
		Assert.assertEquals(DesignTypeItem.CUSTOM_IMPORT, DesignTypeItem.getDesignTypeItemByTermId(TermId.OTHER_DESIGN.getId()));
		Assert.assertEquals(
			DesignTypeItem.AUGMENTED_RANDOMIZED_BLOCK,
			DesignTypeItem.getDesignTypeItemByTermId(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()));
		Assert.assertEquals(DesignTypeItem.ENTRY_LIST_ORDER, DesignTypeItem.getDesignTypeItemByTermId(TermId.ENTRY_LIST_ORDER.getId()));
		Assert.assertEquals(DesignTypeItem.P_REP, DesignTypeItem.getDesignTypeItemByTermId(TermId.P_REP.getId()));
	}

}
