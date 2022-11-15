
package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.junit.Test;

import java.util.List;

public class DeprecatedTopLocationAbbreviationExpressionTest extends TestExpression {

	@Test
	public void testLabbrAsPrefix() throws Exception {
		DeprecatedTopLocationAbbreviationExpression expression = new DeprecatedTopLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[TLABBR]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testLabbrAsSuffix() throws Exception {
		DeprecatedTopLocationAbbreviationExpression expression = new DeprecatedTopLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", ":", null, null, "[TLABBR]", true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testNoLabbr() throws Exception {
		DeprecatedTopLocationAbbreviationExpression expression = new DeprecatedTopLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, null, null, "[TLABBR]", true);
		source.setLocationAbbreviation(null);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		DeprecatedTopLocationAbbreviationExpression expression = new DeprecatedTopLocationAbbreviationExpression();
		DeprecatedAdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[tLabbr]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process code is in lower case");
		this.printResult(values, source);
	}

}
