
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;

import java.util.List;

public class TopLocationAbbreviationExpressionTest extends TestExpression {

	private static final String DESIGNATION = "GERMPLASM_TEST";

	@Test
	public void testLabbrAsPrefix() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, "[TLABBR]", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testLabbrAsSuffix() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, ":", null, null, "[TLABBR]", true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testNoLabbr() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, null, null, "[TLABBR]", true, 2);
		source.setLocationAbbreviation(null);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, "[tLabbr]", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		System.out.println("process code is in lower case");
		this.printResult(values, source);
	}

}
