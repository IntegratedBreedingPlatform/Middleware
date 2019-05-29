package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentDesignTypeTest {

	@Test
	public void testGetTermIdByDesignTypeId() {
		assertEquals(TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), ExperimentDesignType.getTermIdByDesignTypeId(0, false));
		assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(), ExperimentDesignType.getTermIdByDesignTypeId(1, false));
		assertEquals(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(), ExperimentDesignType.getTermIdByDesignTypeId(1, true));
		assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(), ExperimentDesignType.getTermIdByDesignTypeId(2, false));
		assertEquals(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), ExperimentDesignType.getTermIdByDesignTypeId(2, true));
		assertEquals(TermId.OTHER_DESIGN.getId(), ExperimentDesignType.getTermIdByDesignTypeId(3, false));
		assertEquals(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), ExperimentDesignType.getTermIdByDesignTypeId(4, false));
		assertEquals(TermId.ENTRY_LIST_ORDER.getId(), ExperimentDesignType.getTermIdByDesignTypeId(5, false));
		assertEquals(TermId.P_REP.getId(), ExperimentDesignType.getTermIdByDesignTypeId(6, false));
		assertEquals(0, ExperimentDesignType.getTermIdByDesignTypeId(7, false));
	}

	@Test
	public void testGetDesignTypeItemByTermId() {
		assertEquals(
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));
		assertEquals(
			ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));
		assertEquals(
			ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));
		assertEquals(
			ExperimentDesignType.ROW_COL,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));
		assertEquals(
			ExperimentDesignType.ROW_COL,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));
		assertEquals(ExperimentDesignType.CUSTOM_IMPORT, ExperimentDesignType.getDesignTypeItemByTermId(TermId.OTHER_DESIGN.getId()));
		assertEquals(
			ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()));
		assertEquals(
			ExperimentDesignType.ENTRY_LIST_ORDER,
			ExperimentDesignType.getDesignTypeItemByTermId(TermId.ENTRY_LIST_ORDER.getId()));
		assertEquals(ExperimentDesignType.P_REP, ExperimentDesignType.getDesignTypeItemByTermId(TermId.P_REP.getId()));
		assertEquals(null, ExperimentDesignType.getDesignTypeItemByTermId(1010101));
	}

	@Test
	public void testDesignTypeFromString() {

		assertEquals(
			ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK,
			ExperimentDesignType.getExperimentDesignTypeByBVDesignName(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvDesignName()));
		assertEquals(
			ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK,
			ExperimentDesignType.getExperimentDesignTypeByBVDesignName(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvDesignName()));
		assertEquals(
			ExperimentDesignType.ROW_COL,
			ExperimentDesignType.getExperimentDesignTypeByBVDesignName(ExperimentDesignType.ROW_COL.getBvDesignName()));
		assertEquals(
			ExperimentDesignType.P_REP,
			ExperimentDesignType.getExperimentDesignTypeByBVDesignName(ExperimentDesignType.P_REP.getBvDesignName()));
		assertEquals(
			ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK,
			ExperimentDesignType.getExperimentDesignTypeByBVDesignName(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvDesignName()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDesignTypeFromStringException() {

		// This will throw an exception because an empty string doesn't match any of the constants in ExperimentDesignType.
		ExperimentDesignType.getExperimentDesignTypeByBVDesignName("Any Name");

	}

}
