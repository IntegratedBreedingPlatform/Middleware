package org.generationcp.middleware.util;

import org.junit.Assert;
import org.junit.Test;

public class StringsUtilTest {

	@Test
	public void testRemoveBracesForSuppliedString() {

		//Should remove <> braces
		String strInput = "<DENT>";
		String strOutput = StringUtil.removeBraces(strInput);
		Assert.assertEquals("Expected DENT but got " + strOutput, "DENT", strOutput);

		//Should remove () braces
		strInput = "(DENT)";
		strOutput = StringUtil.removeBraces(strInput);
		Assert.assertEquals("Expected DENT but got " + strOutput, "DENT", strOutput);

		//Should not remove any characters
		strInput = "DENT";
		strOutput = StringUtil.removeBraces(strInput);
		Assert.assertEquals("Expected DENT but got " + strOutput, "DENT", strOutput);

	}
}
