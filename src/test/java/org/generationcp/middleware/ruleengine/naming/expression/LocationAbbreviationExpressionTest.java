
package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;

import java.util.List;

public class LocationAbbreviationExpressionTest extends TestExpression {

	private static final String DESIGNATION = "GERMPLASM_TEST";

	@Test
	public void testLabbrAsPrefix() {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, "[LABBR]", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testLabbrAsSuffix() {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, ":", null, null, "[LABBR]", true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testNoLabbr() {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, null, null, "[LABBR]", true, 2);
		source.setLocationAbbreviation(null);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData(DESIGNATION, null, "[labbr]", null, null, true, 2);
		List<StringBuilder> values = this.createInitialValues(DESIGNATION, source);
		expression.apply(values, source, null);
		System.out.println("process code is in lower case");
		this.printResult(values, source);
	}

}
