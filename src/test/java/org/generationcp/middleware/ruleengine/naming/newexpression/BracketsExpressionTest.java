
package org.generationcp.middleware.ruleengine.naming.newexpression;

import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BracketsExpressionTest extends TestExpression {

	BracketsExpression dut = new BracketsExpression();

	@Test
	public void testBracketsNonCross() {
		String testRootName = "CMLI452";
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData(testRootName, "-", null, null, null, false);
		source.setRootName(testRootName);
		List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder(source.getRootName() + BracketsExpression.KEY));

		this.dut.apply(builders, source, null);

		Assert.assertEquals(testRootName, builders.get(0).toString());
	}

	@Test
	public void testBracketsCross() {
		String testRootName = "CMLI452 X POSI105";
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData(testRootName, "-", null, null, null, false);
		source.setRootName(testRootName);
		source.setRootNameType(GermplasmNameType.CROSS_NAME.getUserDefinedFieldID());

		List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder(source.getRootName() + BracketsExpression.KEY));

		this.dut.apply(builders, source, null);

		Assert.assertEquals("(" + testRootName + ")", builders.get(0).toString());
	}

}
