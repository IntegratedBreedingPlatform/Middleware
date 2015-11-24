package org.generationcp.middleware.pojos.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Test;


public class SingleGermplasmCrossElementTest {

	@Test
	public void testToStringWhenPreferredNameIsPresent() {
		SingleGermplasmCrossElement sgcElement = new SingleGermplasmCrossElement();

		Germplasm testGermplasm = new Germplasm();
		testGermplasm.setGid(1);
		final Name preferredName = new Name();
		preferredName.setNval("CML502");
		testGermplasm.setPreferredName(preferredName);
		sgcElement.setGermplasm(testGermplasm);

		Assert.assertEquals(preferredName.getNval(), sgcElement.toString());
	}

	@Test
	public void testToStringWhenPreferredNameIsNotPresent() {
		SingleGermplasmCrossElement sgcElement = new SingleGermplasmCrossElement();

		Germplasm testGermplasm = new Germplasm();
		testGermplasm.setGid(1);
		sgcElement.setGermplasm(testGermplasm);
		Assert.assertEquals(testGermplasm.getGid().toString(), sgcElement.toString());
	}

	@Test
	public void testToStringWhenGermplasmIsNotPresent() {
		SingleGermplasmCrossElement sgcElement = new SingleGermplasmCrossElement();
		Assert.assertEquals("Unknown", sgcElement.toString());
	}

}
