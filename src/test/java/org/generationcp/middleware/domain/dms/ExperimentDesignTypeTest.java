package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentDesignTypeTest {

	@Test
	public void testGetTermIdByDesignTypeId() {
		Assert.assertEquals(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), ExperimentDesignType.getTermIdByDesignTypeId(0, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(), ExperimentDesignType.getTermIdByDesignTypeId(1, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(), ExperimentDesignType.getTermIdByDesignTypeId(1, true));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(), ExperimentDesignType.getTermIdByDesignTypeId(2, false));
		Assert.assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), ExperimentDesignType.getTermIdByDesignTypeId(2, true));
		Assert.assertEquals(TermId.OTHER_DESIGN.getId(), ExperimentDesignType.getTermIdByDesignTypeId(3, false));
		Assert.assertEquals(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), ExperimentDesignType.getTermIdByDesignTypeId(4, false));
		Assert.assertEquals(TermId.ENTRY_LIST_ORDER.getId(), ExperimentDesignType.getTermIdByDesignTypeId(5, false));
		Assert.assertEquals(TermId.P_REP.getId(), ExperimentDesignType.getTermIdByDesignTypeId(6, false));
	}

	@Test
	public void testGetDesignTypeItemByTermId() {
		Assert.assertEquals(
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));
		Assert.assertEquals(
			ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));
		Assert.assertEquals(
			ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));
		Assert.assertEquals(ExperimentDesignType.ROW_COL, ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));
		Assert.assertEquals(
			ExperimentDesignType.ROW_COL,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));
		Assert.assertEquals(ExperimentDesignType.CUSTOM_IMPORT, ExperimentDesignType.getDesignTypeItemByTermId(TermId.OTHER_DESIGN.getId()));
		Assert.assertEquals(
			ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()));
		Assert.assertEquals(ExperimentDesignType.ENTRY_LIST_ORDER, ExperimentDesignType.getDesignTypeItemByTermId(TermId.ENTRY_LIST_ORDER.getId()));
		Assert.assertEquals(ExperimentDesignType.P_REP, ExperimentDesignType.getDesignTypeItemByTermId(TermId.P_REP.getId()));
	}

}
